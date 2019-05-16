#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/mman.h>
#include <asm/unistd.h>


// exact copy of the sem_node struct being used in sys.c so trafficsim.c knows what it's using.
// sem_node is a struct to implement a linked list of processes to keep order of processes being put to sleep and
// 		being woken up.
struct sem_node {
	struct sem_node* next; 
	struct task_struct* info;
};

// exact copy of the cs1550_sem struct being used in sys.c so trafficsim.c knows what it's using using.
// cs1550 is a counting semaphore using a linked list to track processes being put to sleep and being woken up.
struct cs1550_sem {
	int value;
	struct sem_node* head;
	struct sem_node* tail; 
};

int buffer_size = 10; 		// Size of each queue's buffer
int* start_time; 			// To keep track of time in simulation
int* car_count;				// keep track of car numbering
int* available;				// variable to tell consumer what to consume 0 = nothing 1 = south 2 = north

void* memory;				// address of memory being allocated for all the semaphores

// semaphores for south queue
struct cs1550_sem* south_empty;
struct cs1550_sem* south_full;

// semaphores for north queue
struct cs1550_sem* north_empty;
struct cs1550_sem* north_full;

// semaphore for mutex
struct cs1550_sem* mutex;

// Calls cs1550 down syscall
void down(struct cs1550_sem* sem) {
	syscall(__NR_cs1550_down, sem);
}

// Calls cs1550 up syscall
void up(struct cs1550_sem* sem) {
	syscall(__NR_cs1550_up, sem);
}

// Initalizes a cs1550 semaphore
struct cs1550_sem* init_cs1550_sem(void* sem_location, int value) {
	// intialize semaphore struct
	struct cs1550_sem* sem;
	
	// set it's location in memory
	sem = sem_location;
	
	// set it's initial values
	sem->value = value;
	sem->head = NULL;
	sem->tail = NULL;
	
	return sem;
}

// Intializes memory for semaphores and creates them with approriate memory locations and values
void init_sem_memory() {
	// Initalize memory for south queue semaphores
	memory = (void*) mmap(NULL, (int) sizeof(struct cs1550_sem) * 5, 
		PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);	

	// Intialize south semaphores
	south_empty = init_cs1550_sem(memory, buffer_size);
	south_full = init_cs1550_sem(south_empty + sizeof(struct cs1550_sem), 0);

	// Intialize north semaphores
	north_empty = init_cs1550_sem(south_full + sizeof(struct cs1550_sem), buffer_size);
	north_full = init_cs1550_sem(north_empty + sizeof(struct cs1550_sem), 0);

	// Intialize mutex
	mutex = init_cs1550_sem(north_full + sizeof(struct cs1550_sem), 1);
}

// Used to determine if another car follows a car being added to the queue. 
// 		Based on 80% chance of car following another
int car_follows() {
	int n = rand() % 10; // get a random number from 0 - 9

	if (n < 8) {	// if random number is 0 - 7 return 1
		return 1; 
	}

	return 0;		// random number is 8 or 9 return 0
}

void producer(int* queue, char dir, struct cs1550_sem* empty, struct cs1550_sem* full) {
	int i = 0; 	// the current index in the queue for this producer
	
	while (1) { 
		down(empty); 	// call down on the empty semaphore for this producer to see if we can add a car
		down(mutex); 	// call down on this mutex to be able to do it's critical section when it's its turn

		int car = *car_count; 			// get the current car number to being added to the queue
		queue[i % buffer_size] = car;  	// add the car to this producers queue

		// Generate output for car showing up in queue
		printf("Car %d coming from the %c direction arrived in the queue at time %d.\n", 
			car, dir, ((int)time(NULL) - *start_time));

		i += 1;					// increment index;
		*car_count += 1;		// increment car count;

		// if no cars were in the queues tell consumer which queue to pull from first.
		if (*available == 0 && dir == 'S') {
			*available = 1;
		}
		else if (*available == 0 && dir == 'N') {
			*available = 2;
		}

		int to_sleep = 0;		// flag to tell producer to sleep. 

		// if a car doesn't follow the previous one or the queue is full 
		// tell producer to sleep after semaphores are released
		if (!car_follows() || empty->value == 0) {
			to_sleep = 1;
		}

		up(mutex); 		// done with critical secion so release the mutex
		up(full);		// call up on this producer's full semaphore to tell the consumer a car was added

		// determine if another car is following the car that just showed up, it not sleep for 20 seconds.
		if (to_sleep) {
			sleep(20);
		}
	}
}

void consumer(int* south_queue, int* north_queue) {
	int i = 0;				// index variable for the index currently being used
	int south_i = 0;		// current index in the south queue
	int north_i = 0;		// current index in the north queue
	int sleeping = 1;		// flag variable for if the flagger is sleeping

	struct cs1550_sem* full;		// variable for the full semaphore that's actively being used
	struct cs1550_sem* empty;		// variable for the empty semaphore that's actively being used
	int* queue;						// variable for the queue that's actively being used
	char dir;						// variable for the direction currently being used

	while (1) {
		down(mutex);		// call down on this mutex to be able to do it's critical section when it's its turn

		// if a car is available to be passed through, pass them through, else give up the mutex.
		if (*available != 0) {
			if (*available == 1) {			// if a car is available in the south queue, set variables to consume from the south
				full = south_full;
				empty = south_empty;
				queue = south_queue;
				dir = 'S';
				i = south_i;
			}
			else if (*available == 2) {		// if a car is available in the north queue, set variables to consume from the north
				full = north_full;
				empty = north_empty;
				queue = north_queue;
				dir = 'N';
				i = north_i;
			}

 			down(full); 	// call down on the full semaphore to consume when a car is present in the active queue

			int car = queue[i % buffer_size];	// get the car from the given queue that will be allowed to pass flagger

			// if flagger is sleeping, wake them up
			if (sleeping) {
				// output car waking up flagger.
				printf("Car %d coming from the %c direction, blew their horn at time %d.\n", 
					car, dir, ((int)time(NULL) - *start_time));
				// output flagger waking up
				printf("The flagperson is now awake.\n");
				sleeping = 0; 		// flagger is awake now.
			}

			// output car being flagged through.
			printf("Car %d coming from the %c direction left the construction zone at time %d.\n", 
				car, dir, ((int)time(NULL) - *start_time));

			// incement index to the next car in appropriate queue to be flagged through
			if (dir == 'S') {
				south_i += 1;
			}
			else if(dir == 'N') {
				north_i += 1;
			}

			up(empty);	// call up on the empty semaphore to tell the appropriate producer a car was removed

			// change the available variable if the opposing queue is full, or this queue is empty and there's
			// still cars in the opposing queue, or both queue's are empty.
			if (dir == 'S' && (north_full->value == buffer_size 
								|| (south_full->value == 0 && north_full->value > 0))) {
				*available = 2;
			}
			else if (dir == 'N' && (south_full->value == buffer_size 
								|| (north_full->value == 0 && south_full->value > 0))) {
				*available = 1;
			}
			else if (south_full->value == 0 && north_full->value == 0) {
				*available = 0;
			}

			up(mutex);		// done with critical secion so release the mutex
			sleep(2);		// sleep for 2 seconds to allow car to pass
		}
		else if (!sleeping) {	// if there are no cars and the flagger is awake, put him to sleep and release the mutex
			printf("The flagperson is now asleep.\n");		// output flagger going to sleep.
			sleeping = 1;		// set flag for the flagger being asleep to them being awake. 
			up(mutex);			// done with critical secion so release the mutex
		}
		else {	
			up(mutex);			// done with critical secion so release the mutex
		}
	}
}

int main(int argc, char* argv[]) {
	srand(time(NULL));		// initilize the seed of the random number generator for this program

	// allocate memory for program's time counter and car counter.
	int* counters = (int*) mmap(NULL, sizeof(int) * 3, 
		PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);

	start_time = counters;						// assign memory location to time counter
	*start_time = (int)time(NULL);				// initilize time counter's value

	car_count = start_time + sizeof(int);		// assign memory location to car counter
	*car_count = 1;								// initilize car counter's value

	available = car_count + sizeof(int);		// assign memory location to avaiable
	*available = 0;								// initilize available's value

	// Initalize memory and semaphores for north and south queues
	init_sem_memory();

	// Initalize memory for production and consumtion of cars in south queue;
	int* south_queue = (int*) mmap(NULL, buffer_size * sizeof(int), 
		PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);

	// create south producer
	if (fork() == 0) {
		producer(south_queue, 'S', south_empty, south_full);
	}

	// Initalize memory for production and consumtion of cars in north queue;
	int* north_queue = (int*) mmap(NULL, buffer_size * sizeof(int), 
		PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, 0, 0);

	// create north producer
	if (fork() == 0) {
		producer(north_queue, 'N', north_empty, north_full);
	}

	// create consumer/flagger
	if (fork() == 0) {
		consumer(south_queue, north_queue);
	}

	// set the parent of the producers and the consumer to wait until it's children are done. 
	int status;
	wait(&status); 

	return 0;
}
