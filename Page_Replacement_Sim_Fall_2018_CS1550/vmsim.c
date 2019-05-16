#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <getopt.h>


struct access_struct *arr_access = NULL;    // array holding the contents of the trace file
struct frame_struct *arr_frame = NULL;      // array holding the current frame table
struct list_node *fifo_head = NULL;         // head of a linked list for FIFO 
long fifo_count = 0;                        // count of fifo linked list size
long access_count = 0;                      // number of records in the trace file
long frame_count = 0;                       // size of the frame table
long method = 0;                            // 1 = opt | 2= clock | 3 = fifo | 4 = nru
long refresh = 0;                           // refresh period for NRU 
long faults = 0;                            // count of faults in the simulation
long writes = 0;                            // count of disk writes in the simulation
long clocks = 0;                            // current index for Clock
char *name;                                 // name of algorithm being used

// struct to hold the trace file contents
struct access_struct {
    unsigned long index;
    unsigned char mode;
};

// struct to hold the frame table
struct frame_struct {
    unsigned long index;
    unsigned long dirty;
    unsigned long refed;
    long time;
};

// struct for a linked list for FIFO and NRU
struct list_node {
	long frame;
	struct list_node *next;
};

// calculates the next time interval for Clock
long next(long current, unsigned long index) {
    if (current < access_count) {
    	long i;
        // searches the trace file contents from current line accessed to the future 
        // for the next time a given process enters the frame table;
        for (i = current + 1; i < access_count; i++) {
            if (index == arr_access[i].index) { 
            	return i; // if found return the index in the trace file
            }
        }
    }

    return -1; // returns -1 if process never enters the frame table again
}

// swaps out an incoming page for the page being written to memory;
void swapPage(long i, unsigned long index, unsigned char mode) {
    arr_frame[i].index = index;
    
    if (arr_frame[i].dirty) { 
        writes++; 
    }

    if (mode == 'R') { 
        arr_frame[i].dirty = 0; 
    }
    else if (mode == 'W') { 
        arr_frame[i].dirty = 1; 
    }
}

// how to handle page faults via OPT
void opt(long current, unsigned long index, unsigned char mode) {
    long max = 0;
    long j = 0;
    long i;
    
    // search through frame table for page that will be called the furthest in the future.
    for (i = 0; i < frame_count; i++) {
        // if a page's time is less than the current time, and not -1, 
        // find the next times it's called after the current time
    	if (arr_frame[i].time <= current && arr_frame[i].time >= 0) {
    		arr_frame[i].time = next(current, arr_frame[i].index);
    	}

        // note the furthest in the future page.
        if (arr_frame[i].time > max) {
            max = arr_frame[i].time;
            j = i;
        }

        // if a page never enters the table again, just get rid of it and stop looking
        if (arr_frame[i].time == -1) {
        	j = i;
        	break;
        }
    }

    // swap page being evicted out with incoming one;
    swapPage(j, index, mode);

    // calculate the next time the new page is going to show up again.
    arr_frame[j].time = next(current, index);
}

// how to handle page faults via Clock
void clock(unsigned long index, unsigned char mode) {
    long i = clocks;                    // set index to last clock position

    while (1) {
        // if reference bit is 0
        if (!arr_frame[i].refed) {
            // swap unreferenced page out for incoming one
            swapPage(i, index, mode);
            // set it's reference bit to 1
            arr_frame[i].refed = 1;

            break;
        }
        // if reference bit is 1 set it to 0
        arr_frame[i].refed = 0; 

        // increment to next position of clock;
        i = (i + 1) % frame_count;
    }

    // increment clock to where it will start on the next page fault
    clocks = (i + 1) % frame_count;
}

// remove from head of linked list and return it's value
long popList(struct list_node **head) {
	struct list_node *out = *head;
	long frame = 0;

    // if the head is not null, pop it off and set the head 
    // to the next item in the list
	if (*head != NULL) {
		frame = out->frame;
		
		*head = out->next;
		
		free(out);
	}
    // return the value of the head of the linked list
	return frame; 
}

// add item to the end of a linked list
void addToList(struct list_node **head, long frame) {
	struct list_node *new_node = (struct list_node*)malloc(sizeof(struct list_node));
	struct list_node *last = *head;

    // initilize node being added to the list
	new_node->frame = frame;
	new_node->next = NULL;

    // if the list is empty start it and return
	if (*head == NULL) {
		*head = new_node;
		return;
	}
	
    // if not the end of the list find the end
	while (last->next != NULL) {
		last = last->next; 
	}

    // add the new node to the end of the list
	last->next = new_node;
}

// clear a linked list from memory completely
void freeList(struct list_node **head) {
	while (*head != NULL) {
		struct list_node *out = *head;
		*head = out->next;
		free(out);
	}
}

// how to handle page faults via FIFO
void fifo(unsigned long index, unsigned char mode) {
    // if frame table is full run FIFO
	if (fifo_count == frame_count) {
        // pop the node off the thead of list
		long frame = popList(&fifo_head);

        // swap out the page being evicted for the incoming page
        swapPage(frame, index, mode);

        // add the new page to the end of the list
	    addToList(&fifo_head, frame);
	}
	else {
        // if frame table isn't full, add the new page to the end of the list
		addToList(&fifo_head, fifo_count);
		fifo_count++;
	}
}

// gets a random page from a list for NRU
long getRandFromList(struct list_node **bucket, long max) {
	struct list_node *temp = *bucket;     
	int n = rand() % max;          // random number between zero and size of bucket
	long i;
	long frame = 0;
	
    // get to the node in the list to get value of page being removed from the list
	for (i = 0; i < n; i++) {
		frame = temp->frame;
		*bucket = temp->next;
		temp = *bucket;
	}

    // return page to be evicted from page table
	return frame;
}

// how to handle page faults via NRU
void nru(unsigned long index, unsigned char mode) {
	struct list_node *bucket1 = NULL, *bucket2 = NULL;
	struct list_node *bucket3 = NULL, *bucket4 = NULL;
	long count1 = 0, count2 = 0, count3 = 0, count4 = 0;
	long i;

    // sort pages in the page table to appropriate NRU classes
	for (i = 0; i < frame_count; i++) {
		if (!arr_frame[i].refed && !arr_frame[i].dirty) {
			addToList(&bucket1, i);
			count1++;
		}
		else if(!arr_frame[i].refed && arr_frame[i].dirty) {
			addToList(&bucket2, i);
			count2++;
		}
		else if(arr_frame[i].refed && !arr_frame[i].dirty) {
			addToList(&bucket3, i);
			count3++;
		}
		else if(arr_frame[i].refed && arr_frame[i].dirty) {
			addToList(&bucket4, i);
			count4++;
		}
	}

    // find the lowest order class that isn't empty and get random 
    // page to be evicted from that class
	if (count1 > 0) {
		i = getRandFromList(&bucket1, count1);
	}
	else if (count2 > 0) {
		i = getRandFromList(&bucket2, count2);
	}
	else if (count3 > 0) {
		i = getRandFromList(&bucket3, count3);
	}
	else if (count4 > 0) {
		i = getRandFromList(&bucket4, count4);
	}

    // free the memory for all the buckets
	freeList(&bucket1);
	freeList(&bucket2);
	freeList(&bucket3);
	freeList(&bucket4);

    // swap page being evicted with incoming page
    swapPage(i, index, mode);
}

// determines if input arguments are valid
int validArgs(char *args[]) {
	frame_count = atoi(args[2]);           // get page table size

	if (frame_count <= 0) {
        printf("Error: Number of frames must be greater than 0!\n");
    }

    if (strcmp(args[1], "-n") != 0 || strcmp(args[3], "-a") != 0 ||
    			frame_count <= 0) { 
    	return 0; 
    }

    return 1;
}

// determine which algorithm is being used, and set appropriate paramaeters
int validMethod(long count, char *args[]) {
	if (strcmp(args[4], "opt") == 0) { 
    	method = 1;
    	name = "OPT";
    }
    else if (strcmp(args[4], "clock") == 0) { 
    	method = 2; 
    	name = "Clock";
    }
    else if (strcmp(args[4], "fifo") == 0) { 
        method = 3;
        name = "FIFO";
    }
    else if (strcmp(args[4], "nru") == 0) { 
        // make sure refresh value for NRU is supplied
        if (count == 5) {
            printf("Error: Must give refresh argument to run with aging method\n");
            return 0;
        }

        method = 4; 
        name = "NRU";
        
        refresh = atoi(args[5]);        // get refresh value

        // make sure refresh value is not negative
        if (refresh <= 0) {
            printf("Error: Refresh value must be greater than 0!\n");
            return 0;
        }
    }
    else { 
        // if not one of the 4 methods covered is indicated
        printf("Error: Invalid method!\n");
        return 0; 
    }

    return 1;
}

// validate the command line input
int validInput(long count, char *args[]) {
	if (count < 5 || count > 6) {
        printf("Error: Invalid number of arguments!\n");
        return 0;
    }

    if (!validArgs(args) || !validMethod(count, args)) {
    	return 0;
    } 

    return 1;
}

// loads the trace file into the program
void loadTraceFile(char *fileName) {	
    unsigned long address = 0;
    unsigned char mode = 0;

	FILE *file = fopen(fileName, "rb");

    if (!file) { 
    	exit(1); 
    }

    // get number of entries in trace file
    while (fscanf(file, "%x %c", &address, &mode) == 2) { 
    	access_count++;
    }
    
    rewind(file);

    // allocate memory for data structure holding the trace file
    arr_access = (struct access_struct*)malloc(access_count * sizeof(struct access_struct));

    if (!arr_access) { 
    	exit(1); 
    }

    long i = 0;
    // load in the trace file to data structure
    while (fscanf(file, "%x %c", &address, &mode) == 2) {
        arr_access[i].index = address >> 12;
        arr_access[i].mode = mode;
        i++;
    }

    fclose(file);
}

// initialize the page table
void initFrame() {
	arr_frame = (struct frame_struct*)malloc(frame_count * sizeof(struct frame_struct));

    if (!arr_frame) { 
    	exit(1); 
    }

    long i; 

    // intialize page table entries to empty
    for (i = 0; i < frame_count; i++) {
        arr_frame[i].dirty = 0;
        arr_frame[i].refed = 0;
        arr_frame[i].time = 0;
    }
}

// detects if incoming page is not in the page table
long detectPageFault(unsigned long index, long current) {
	long i;

    // search page table if incoming page is present return it's index
	for (i = 0; i < frame_count; i++) {
        if (arr_frame[i].index == index) {
            return i;
        }
    }

    // return negative, incoming page is not in page table
    return -1;
}

// clears the reference on all entries of the page table for NRU
void clearReferenceBit() {
	long i;

    for (i = 0; i < frame_count; i++) { 
    	arr_frame[i].refed = 0; 
    }
}

// if incoming page doesn't cause a page fault do various actions for appropriate algorithm
// currently being used
void noPageFault(long frame, long current, unsigned long index, unsigned char mode) {
	arr_frame[frame].index = index;

    if (mode == 'W') { 
    	arr_frame[frame].dirty = 1; 
    }

    if (method == 1) {  // if OPT is being used, find the next time the page enters the page table
    	arr_frame[frame].time = next(current, index); 
    }
    else if (method == 2) {     // if Clock is being used set it's reference bit to 1
    	arr_frame[frame].refed = 1; 
    }
    else if (method == 4) {     // if NRU is being used
        // if current time is a refresh interval clear reference bits in page table
    	if (current % refresh == 0) {
    		clearReferenceBit();
    	}

        // set incoming page's reference bit to 1
    	arr_frame[frame].refed = 1;   
    } 
}

// if incoming page causes a page fault start page fault handling for appropriate 
// algorithm currently being used
void pageFault(long current, unsigned long index, unsigned char mode) {
    faults++;       // increment the number of page faults that have occured

	if (method == 1) { 
    	opt(current, index, mode); 
    }
    else if (method == 2) { 
    	clock(index, mode); 
    }
    else if (method == 3) {
    	fifo(index, mode); 
    }
    else if (method == 4) {
    	nru(index, mode); 
    }
}

// runs the simulation
void runSim() {
	long i; 

    for (i = 0; i < access_count; i++) {
        unsigned long index = arr_access[i].index;
        unsigned char mode = arr_access[i].mode;


        long frame = detectPageFault(index, i);
        // >= 0 for not page fault, -1 for page fault
        if (frame >= 0) {
            noPageFault(frame, i, index, mode);
        }
        else {
            pageFault(i, index, mode);
        }
    }
}

// outputs program's stats after simulation completes
void printStats() {
	printf("Algorithm: %s\n", name);
    printf("Number of frames:\t%d\n", frame_count);
    printf("Total memory accesses:\t%d\n", access_count);
    printf("Total page faults:\t%d\n", faults);
    printf("Total writes to disk:\t%d\n", writes);
}

// free's memory that needs freeing before exiting the program
void freeMemory() {
	if (method == 3) {
		freeList(&fifo_head);
	}
	free(arr_frame);
    free(arr_access);
}

int main(int argc, char *argv[]) {
    // validate input
    if (!validInput(argc - 1, argv)) {
        printf("Usage: %s -n <numframes> -a <opt|clock|fifo|nru> [-r refresh] <tracefile>\n", argv[0]);
        exit(1);
    }

    loadTraceFile(argv[argc - 1]); // load the given trace file

    initFrame();        // initalize the page table
    
    runSim();           // run the simulation

    printStats();       // print stats after completion

    freeMemory();       // free memory being used

    return 0;           // end program
}

