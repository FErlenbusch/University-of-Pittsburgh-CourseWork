import java.util.*;
import java.io.*;


// Wrapper class to hold car information
class Car {
	String vin;
	String make;
	String model;
	double price;
	double mileage;
	String color;

	public Car(String vin, String make, String model, double price, double mileage, String color) {
		this.vin = vin;
		this.make = make;
		this.model = model;
		this.price = price;
		this.mileage = mileage;
		this.color = color;
	}
}


// MinHeap hold's all data for the priority queue heap implementation. 
// Priority queue arrays are an array representation of a binary tree.
class MinHeap {
	Car[] pqPrice;		// Priority queue array for lowest price
	Car[] pqMileage;	// Priority queue array for lowest mileage
	HashMap<String, Integer> priceMap;		// Indirection for price priority queue
	HashMap<String, Integer> mileageMap;	// Indirection for mileage priority queue
	int n;		// number of items stored in the heap.

	// initalize the heap arrays with an array size of 10
	public MinHeap() {
		pqPrice = new Car[10];
		pqMileage = new Car[10];
		priceMap = new HashMap<String, Integer>();
		mileageMap = new HashMap<String, Integer>();
		n = 0;
	}

	// add inserts new car at the end of each array, O(1), then swims up until it's in it's 
	// in it's correct place, O(logn). Worst case is when it tries to add to a full array, 
	// then it must resize the array, O(n), average case is O(logn).
	public void add(Car car) {
		if (n == pqPrice.length) {
			resize();
		}

		pqPrice[n] = car; 
		pqMileage[n] = car; 
		priceMap.put(car.vin, n);
		mileageMap.put(car.vin, n);

		if (n > 0) {
			swimPrice(n);
			swimMileage(n);
		}

		n++;
	}

	// remove gets the index/removes the item to be removed from the indirections, O(1), puts the last item
	// in the heap into it's place, O(1), then sinks it to it's proper placement in the queue, O(logn).
	public void remove(String vin) {
		int iPrice = priceMap.remove(vin);
		int iMileage = mileageMap.remove(vin);

		pqPrice[iPrice] = pqPrice[n-1];
		pqMileage[iMileage] = pqMileage[n-1];
		pqPrice[n] = null;
		pqMileage[n] = null;
		n--;

		priceMap.replace(pqPrice[iPrice].vin, iPrice);
		mileageMap.replace(pqMileage[iMileage].vin, iMileage);

		sinkPrice(iPrice);
		sinkMileage(iMileage);
	}

	// find gets the index of the item to be found from the indirection and returns the item, O(1).
	public Car find(String vin) {
		int i = priceMap.get(vin);
		return pqPrice[i];
	}

	// update removes the car from the heap, O(logn), then add's the updated information back in, O(logn).
	public void update(Car car) {
		remove(car.vin);
		add(car);
	}

	// resize gets called when the priority queue's arrays are full. resize doubles the array capacities, 
	// and copies the data from the old arrays into the new ones, O(n).
	public void resize() {
		Car[] temp1 = new Car[n * 2];
		Car[] temp2 = new Car[n * 2];

		for (int i = 0; i < n; i++) {
			temp1[i] = pqPrice[i];
			temp2[i] = pqMileage[i];
		}

		pqPrice = temp1;
		pqMileage = temp2;
	}

	// swim looks at the parent node of the item that's swimming, O(1), and determins if it's larger than it. 
	// If it is then it swaps places, O(1), this repeats until it's in it's correct priority placement in the
	// heap occuring at most the height of the tree, O(logn).
	public void swimPrice(int i) {
		int parent = (int) Math.floor((i - 1) / 2);

		if (pqPrice[i].price < pqPrice[parent].price) {
			Car temp = pqPrice[i];
			pqPrice[i] = pqPrice[parent];
			pqPrice[parent] = temp;
			priceMap.replace(pqPrice[i].vin, i);
			priceMap.replace(pqPrice[parent].vin, parent);
			swimPrice(parent);
		}
	}

	// swim looks at the parent node of the item that's swimming, O(1), and determines if it's larger than it. 
	// If it is then it swaps places, O(1), this repeats until it's in it's correct priority placement in the
	// heap occuring at most the height of the tree, O(logn).
	public void swimMileage(int i) {
		int parent = (int) Math.floor((i - 1) / 2);

		if (pqMileage[i].mileage < pqMileage[parent].mileage) {
			Car temp = pqMileage[i];
			pqMileage[i] = pqMileage[parent];
			pqMileage[parent] = temp;
			mileageMap.replace(pqMileage[i].vin, i);
			mileageMap.replace(pqMileage[parent].vin, parent);
			swimMileage(parent);
		}
	}

	// sink looks at the left child of the item that's sinking, O(1), and determines if it's smaller than it.
	// If it is then it swaps places, O(1), if it isn't then it looks at the right child and makes the same 
	// determination, this repeates until it's in it's correct placement in the heap occuring at most the height
	// of the tree, O(logn).
	public void sinkPrice(int i) {
		int left = 2 * i + 1;
		int right = 2 * i + 2;

		if (left < n && pqPrice[i].price > pqPrice[left].price) {
			Car temp = pqPrice[i];
			pqPrice[i] = pqPrice[left];
			pqPrice[left] = temp;
			priceMap.replace(pqPrice[i].vin, i);
			priceMap.replace(pqPrice[left].vin, left);
			sinkPrice(left);
		}

		if (right < n && pqPrice[i].price > pqPrice[right].price) {
			Car temp = pqPrice[i];
			pqPrice[i] = pqPrice[right];
			pqPrice[right] = temp;
			priceMap.replace(pqPrice[i].vin, i);
			priceMap.replace(pqPrice[right].vin, right);
			sinkPrice(right);
		}
	}

	// sink looks at the left child of the item that's sinking, O(1), and determines if it's smaller than it.
	// If it is then it swaps places, O(1), if it isn't then it looks at the right child and makes the same 
	// determination, this repeates until it's in it's correct placement in the heap occuring at most the height
	// of the tree, O(logn).
	public void sinkMileage(int i) {
		int left = 2 * i + 1;
		int right = 2 * i + 2;

		if (left < n && pqMileage[i].mileage > pqMileage[left].mileage) {
			Car temp = pqMileage[i];
			pqMileage[i] = pqMileage[left];
			pqMileage[left] = temp;
			mileageMap.replace(pqMileage[i].vin, i);
			mileageMap.replace(pqMileage[left].vin, left);
			sinkMileage(left);
		}

		if (right < n && pqMileage[i].mileage > pqMileage[right].mileage) {
			Car temp = pqMileage[i];
			pqMileage[i] = pqMileage[right];
			pqMileage[right] = temp;
			mileageMap.replace(pqMileage[i].vin, i);
			mileageMap.replace(pqMileage[right].vin, right);
			sinkMileage(right);
		}
	}

	// minPrice returns the first index of the min price priority queue O(1)
	public Car minPrice() {
		return pqPrice[0];
	}

	// minMileage returns the first index of the min mileage priority queue O(1)
	public Car minMileage() {
		return pqMileage[0];
	}

	// minPriceMakeModel uses a recursive search algorithm modeled on the underlaying Binary Search Tree
	// the priority queue heap is based on. It see's if the root is the make and model desired and returns
	// it if it is, then splits and looks at each of it's child nodes in the same manner repeating for each
	// branch down the tree until it finds the first occuring desired node. If two nodes on the same level
	// match the search critera then it returns the one with higher priority, average case is O(logn), worst
	// is O(n) if it has to examine each node before it finds a match.
	public Car minPriceMakeModel(String make, String model, int i) {
		int left = 2 * i + 1;
		int right = 2 * i + 2;
		Car leftCar = null;
		Car rightCar = null;

		if (pqPrice[i].make.equals(make) && pqPrice[i].model.equals(model)) {
			return pqPrice[i];
		}

		if (left < n) {
			leftCar = minPriceMakeModel(make, model, left);
		}

		if (right < n) {
			rightCar = minPriceMakeModel(make, model, right);
		}

		if (leftCar != null && rightCar != null) {

			if (leftCar.price <= rightCar.price) {
				return leftCar;
			} 
			else {
				return rightCar;
			}
		}
		else if (leftCar != null) {
			return leftCar;
		}
		else if (rightCar != null) {
			return rightCar;
		}
		else {
			return null;
		}
	}

	// minMileageMakeModel uses a recursive search algorithm modeled on the underlaying Binary Search Tree
	// the priority queue heap is based on. It see's if the root is the make and model desired and returns
	// it if it is, then splits and looks at each of it's child nodes in the same manner repeating for each
	// branch down the tree until it finds the first occuring desired node. If two nodes on the same level
	// match the search critera then it returns the one with higher priority, average case is O(logn), worst
	// is O(n) if it has to examine each node before it finds a match.
	public Car minMileageMakeModel(String make, String model, int i) {
		int left = 2 * i + 1;
		int right = 2 * i + 2;
		Car leftCar = null;
		Car rightCar = null;

		if (pqMileage[i].make.equals(make) && pqMileage[i].model.equals(model)) {
			return pqMileage[i];
		}

		if (left < n) {
			leftCar = minMileageMakeModel(make, model, left);
		}

		if (right < n) {
			rightCar = minMileageMakeModel(make, model, right);
		}

		if (leftCar != null && rightCar != null) {

			if (leftCar.mileage <= rightCar.mileage) {
				return leftCar;
			} 
			else {
				return rightCar;
			}
		}
		else if (leftCar != null) {
			return leftCar;
		}
		else if (rightCar != null) {
			return rightCar;
		}
		else {
			return null;
		}
	}
}

// provides an interface for a user to interact with the heap. 
public class CarTracker {

	private static Scanner UserIn = new Scanner(System.in);
	private static MinHeap Heap = new MinHeap();

	public static void main(String[] args) throws IOException {

		carsInit();

		System.out.println("Welcome to Car Tracker!");

		startMenu();
	}

	public static void carsInit() throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader("cars.txt"));

		String str = reader.readLine();

		while ((str = reader.readLine()) != null ) {
			String[] carInfo = str.split(":");

			Car car = new Car(carInfo[0], carInfo[1], carInfo[2], Double.parseDouble(carInfo[3]), 
				Double.parseDouble(carInfo[4]), carInfo[5]);
			Heap.add(car);
		}

		reader.close();
	}

	public static void startMenu() {

		int choice = 0;

		System.out.println("\nWhat would you like to do? (Enter number of choice)");
		System.out.println("\t(1) Add a Car \t (2) Update a Car \t (3) Remove a Car from Consideration");
		System.out.println("\t(4) Retrive Lowest Priced Car \t (5) Retrieve the Lowest Mileage Car");
		System.out.println("\t(6) Retrieve the Lowest Priced Car by Make and Model");
		System.out.println("\t(7) Retrieve the Lowest Mileage Car by Make and Model");
		System.out.println("\t(8) Exit Program");

		choice = UserIn.nextInt();
		UserIn.nextLine();

		switch (choice) {
			case 1: addCar();
					break;
			case 2: updateCar();
					break;
			case 3: removeCar();
					break;
			case 4: getLowestPriced();
					break;
			case 5: getLowestMileage();
					break;
			case 6: getLowestPricedMakeModel();
					break;
			case 7: getLowestMileageMakeModel();
					break;
			case 8: System.out.println("\nGoodbye!");
					break;
			default: System.out.println("\nInvalid Choice!");
					startMenu();
		}
	}

	public static void addCar() {

		String[] carInfo = new String[6];

		System.out.println("\nEnter the VIN number of the car:");
		carInfo[0] = UserIn.nextLine();

		System.out.println("Enter the car's make:");
		carInfo[1] = UserIn.nextLine();

		System.out.println("Enter the car's model:");
		carInfo[2] = UserIn.nextLine();

		System.out.println("Enter the car's price:");
		carInfo[3] = UserIn.nextLine();

		System.out.println("Enter the car's mileage:");
		carInfo[4] = UserIn.nextLine();

		System.out.println("Enter the car's color:");
		carInfo[5] = UserIn.nextLine();

		Car car = new Car(carInfo[0], carInfo[1], carInfo[2], Double.parseDouble(carInfo[3]), 
			Double.parseDouble(carInfo[4]), carInfo[5]);
		Heap.add(car);

		startMenu();
	}

	public static void updateCar() {

		System.out.println("\nEnter the VIN number of the car you'd like to update:");
		Car car = Heap.find(UserIn.nextLine());

		if (car == null) {
			System.out.println("\nCar doesn't exist in the database!");
		}
		else {
			System.out.println("\nWhat would you like to update?");
			System.out.println("\t(1) The Price \t (2) The Mileage \t (3) the Color");

			int choice = UserIn.nextInt();
			UserIn.nextLine();

			switch (choice) {
				case 1: System.out.println("Enter the new price:");
						car.price = UserIn.nextDouble();
						Heap.update(car);
						break;
				case 2: System.out.println("Enter the new mileage:");
						car.mileage = UserIn.nextDouble();
						Heap.update(car);
						break;
				case 3: System.out.println("Enter the new color:");
						car.color = UserIn.nextLine();
						Heap.update(car);
						break;
				default: System.out.println("\nInvalid Entry!");
			}
		}

		startMenu();
	}

	public static void removeCar() {

		System.out.println("\nEnter the VIN number of the car you'd like to remove:");
		Car car = Heap.find(UserIn.nextLine());

		if (car == null) {
			System.out.println("\nCar doesn't exist in the database!");
		}
		else {
			Heap.remove(car.vin);
		}

		startMenu();
	}

	public static void getLowestPriced() {

		Car car = Heap.minPrice();
		System.out.println("\nThe car with the lowest price:");
		printCar(car);
		startMenu();
	}

	public static void getLowestMileage() {

		Car car = Heap.minMileage();
		System.out.println("\nThe car with the lowest mileage:");
		printCar(car); 
		startMenu();
	}

	public static void getLowestPricedMakeModel() {

		System.out.println("\nEnter the make:");
		String make = UserIn.nextLine();

		System.out.println("\nEnter the model:");
		String model = UserIn.nextLine();

		Car car = Heap.minPriceMakeModel(make, model, 0);

		if (car == null) {
			System.out.println("\nA " + make + " " + model + " doesn't exist in the Database!");
		}
		else {
			System.out.println("The lowest priced " + make + " " + model + " is:");
			printCar(car);
		}

		startMenu();
	}

	public static void getLowestMileageMakeModel() {

		System.out.println("\nEnter the make:");
		String make = UserIn.nextLine();

		System.out.println("\nEnter the model:");
		String model = UserIn.nextLine();

		Car car = Heap.minMileageMakeModel(make, model, 0);

		if (car == null) {
			System.out.println("\nA " + make + " " + model + " doesn't exist in the Database!");
		}
		else {
			System.out.println("The " + make + " " + model + " with the lowest mileage is:");
			printCar(car);
		}

		startMenu();
	}

	public static void printCar(Car car) {
		System.out.println("\tVIN Number:\t" + car.vin);
		System.out.println("\tMake:\t\t" + car.make);
		System.out.println("\tModel:\t\t" + car.model);
		System.out.format("\tPrice:\t\t%.2f\n", car.price);
		System.out.format("\tMileage:\t%.2f\n", car.mileage);
		System.out.println("\tColor:\t\t" + car.color);
	}
}