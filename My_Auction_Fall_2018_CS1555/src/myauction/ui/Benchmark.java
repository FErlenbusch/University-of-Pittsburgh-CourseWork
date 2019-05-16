package myauction.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.text.Text;

import myauction.data_models.Product;
import myauction.data_models.KeyValue;

// javafx.scene.text.Text object
//getText()

public class Benchmark {

	public Text error;
	public Functions functions;

	public static void main(String[] args) {
    System.out.println("Benchmark File is now running:");
		Benchmark b1 = new Benchmark();
		b1.runBenchmark();
	}

  public void runBenchmark() {
    // openDBConnection
    String user = "cws24";
    String password = "3831607";
    
    openDBConnection(user, password);
	

    //TESTING setNewDate Function + getDate Function
    setNewDate("2018/01/01 12:59:59");
    getDate();
    setNewDate("2018/02/02 03:44:59");
    getDate();
    setNewDate("2018/03/03 14:09:59");
    getDate();
    setNewDate("2018/04/04 22:19:59");
    getDate();
    setNewDate("2018/05/05 20:49:59");
    getDate();
    setNewDate("2018/06/06 23:43:59");
    getDate();
    setNewDate("2018/07/07 02:22:39");
    getDate();
    setNewDate("2018/08/08 23:59:52");
    getDate();
    setNewDate("2018/09/09 07:21:33");
    getDate();
    setNewDate("1994/10/21 01:13:59");
    getDate();
    setNewDate("2018/11/11 04:32:55");
    getDate();
    setNewDate("2018/12/12 11:35:12");
    getDate();
    System.out.println();

   
 
	//ADD NEW ADMINISTRATOR ACCOUNT INTO TABLE  - 5
	addNewAdmin("admin2", "root2", "A-Connie", "131 Boss Lane", "boss@gmail.com");
	addNewAdmin("admin3", "root3", "A-Roisin", "255 Ceo Lane", "ceo@gmail.com");
	addNewAdmin("professor", "root4", "Chrysanthis", "323 Sennot Square", "prof@gmail.com");
	addNewAdmin("ta", "root5", "Xiaozhong", "987 Sennot Square", "ta@gmail.com");
	addNewAdmin("advisor", "root6", "Ramirez", "3334 Sennot Square", "advisor@gmail.com");
	System.out.println();

	//TESTING logInAdmin Function - 6
	logInAdmin("admin", "root");
	logInAdmin("admin2", "root2");
	logInAdmin("admin3", "root3");
	logInAdmin("professor", "root4");
	logInAdmin("ta", "root5");
	logInAdmin("advisor", "root6");
	System.out.println();

	//ADD NEW USER ACCOUNTS INTO CUSTOMER TABLE - 10 
	addNewCustomer("marypops", "marypass", "Mary Poppins", "17 CherryTree Lane", "mary@email.com");
	addNewCustomer("sherlock", "sherpass", "Sherlock Holmes", "54 Baker Street", "sherlock@email.com");
	addNewCustomer("janeaust", "janepass", "Jane Austin", "3482 Pemberly Lane", "jane@email.com");
	addNewCustomer("saintnick", "santapass", "Santa Claus", "01 North Pole", "santa@email.com");
	addNewCustomer("grinch", "grinchpass", "The Grinch", "837 Snowflake Drive", "grinch@email.com");
	addNewCustomer("edsheer", "edpass", "Ed Sheeran", "383 Photograph Avenue", "ed@email.com");
	addNewCustomer("kimkard", "kimpass", "Kim Kardashian", "2342 Hollywood Blvd", "kim@email.com");
	addNewCustomer("bobama", "obamapass", "Barack Obama", "55 White House", "barack@email.com");
	addNewCustomer("jfallon", "jimmypass", "Jimmy Fallon", "878 Rockefeller Lane", "jimmy@email.com");
	addNewCustomer("ngraham", "nortenpass", "Norten Graham", "232 London Bridges", "norteny@email.com");
	System.out.println();

	//TESTING logInUser Function - 13
	logInUser("builderbob", "bobpass");
	logInUser("cindywreck", "cindypass");
	logInUser("panamajack","jackpass");
	logInUser("marypops", "marypass");
	logInUser("sherlock", "sherpass");
	logInUser("janeaust", "janepass");
	logInUser("saintnick", "santapass");
	logInUser("grinch", "grinchpass");
	logInUser("edsheer", "edpass");
	logInUser("kimkard", "kimpass");
	logInUser("bobama", "obamapass");
	logInUser("jfallon", "jimmypass");
	logInUser("ngraham", "nortenpass");
	System.out.println();
	

	//PRINTING CATEGORIES AND SUBCATEGORIES - call as many categories/subcategories there are
	Set<String> roots = getRootCategories();
	List<String> list = new ArrayList<>(roots);
	
	while (!list.isEmpty()) {
		String root = list.remove(0);
		
		System.out.println("SubCategories of: " + root);
		Set<String> leaves = getSubCategories(root);
		
		for (String leaf : leaves) {
			if (!functions.isLeafCategory(error, leaf)) {
				list.add(leaf);
			}
		}
	}
	
	
	//TESTING DOADDAUCTION - 10
	Set<String> categories = new HashSet<String>(list);
	doAddAuction("Black Leather Glove", "Worn by Babe Ruth.", "sherlock", 800, 3, categories);
	doAddAuction("Steelers Helmet", "1944 Steelers Helmet", "edsheer", 440, 2, categories);
	doAddAuction("Iphone 8", "Color - Black, Memory - 32 GB", "kimkard", 950, 90, categories);
	doAddAuction("Iphone 3", "Color - Silver, Memory - 8 GB", "jfallon", 1833, 4, categories);
	doAddAuction("Iphone X", "Color - Red, Memory 96 GB", "ngraham", 333, 8, categories);
	doAddAuction("Samsung Galaxy 8", "Color - Black, Refurbished", "saintnick", 80, 320, categories);
	doAddAuction("Tiffany Diamond Ring", "14-carat diamond ring", "kimkard", 8932, 2, categories);
	doAddAuction("JFK-private journals", "6 books written during term", "bobama", 2311, 6, categories);
	doAddAuction("Dell Monitor", "20-inch monitor", "grinch", 14, 1, categories);
	doAddAuction("pencil sharpener", "blue sharpener", "janeaust", 3, 20, categories);
	System.out.println();

	//TESTING searchForProducts Function - 8: 2 fails/2 success
	searchForProducts("Dell", "Iphone", "name");
	searchForProducts("Signed", "Baseball", "name");
	searchForProducts("Gold", "Ring", "name");
	searchForProducts("Gold", "Baseball", "name");
	searchForProducts("Antique", "Knife", "name");
	searchForProducts("2012", "Pro", "name");
	searchForProducts("Red", "Stapler", "name");
	searchForProducts("Signed", "Football", "name");
	System.out.println();

	//TESTING getBuyingAmount function -5 
	getBuyingAmount("builderbob", 5);
	getBuyingAmount("cindywreck", 1);
	getBuyingAmount("panamajack", 5);
	getBuyingAmount("jfallen", 5);
	getBuyingAmount("bobama", 4);
	System.out.println();

	//TESTING getBidsPlaced function - 6
	getBidsPlaced("builderbob", 2);
	getBidsPlaced("cindywreck", 5);
	getBidsPlaced("panamajack", 3);
	getBidsPlaced("sherlock", 3);
	getBidsPlaced("marypops", 3);
	getBidsPlaced("grinch", 5);
	getBidsPlaced("edsheer", 6);
	System.out.println();
	
	//TESTING getProdCount function - 5
	getProdCount("Antiques", 5);
	getProdCount("Sports", 5);
	getProdCount("Jewlery", 5);
	getProdCount("Electronics", 5);
	getProdCount("Office Supplies", 5);
	getProdCount("Office Supplies", 5);
	getProdCount("Football", 6);
	getProdCount("Baseball", 6);


	//TESTING getProductsInCategory function - 5
	getProductsInCategory("Jewlery", "name");
	getProductsInCategory("Antiques", "name");
	getProductsInCategory("Electronics", "name");
	getProductsInCategory("Office Supplies", "name");
	getProductsInCategory("FootBall", "name");
	getProductsInCategory("Sports", "name");
	getProductsInCategory("Phones", "name");
	System.out.println();

	//TESTING getStatsAllProducts Functions - no params -1 
	getStatsAllProducts();
	System.out.println();

	//TESTING getStatsAllProducts(login) Functions - 5
	getStatsAllProducts("builderbob");
	getStatsAllProducts("cindywreck");
	getStatsAllProducts("panamajack");
	getStatsAllProducts("bobama");
	getStatsAllProducts("saintnick");
	System.out.println();

	//TESTING bidOnItem function - 12
	bidOnItem(5, 1500, "cindywreck");
	bidOnItem(5, 2000, "panamajack");
	bidOnItem(5, 3000, "cindywreck");
	bidOnItem(4, 200, "cindywreck");
	bidOnItem(4, 400, "builderbob");
	bidOnItem(4, 1000, "cindywreck");
	bidOnItem(1, 300, "builderbob");
	bidOnItem(1, 600, "cindywreck");
	bidOnItem(2, 3000, "cindywreck");
	bidOnItem(2, 8444, "panamajack");
	bidOnItem(3, 642, "builderbob");
	bidOnItem(5, 5000, "panamajack");
	bidOnItem(6, 2000, "panamajack");
	bidOnItem(6, 3000, "cindywreck");
	bidOnItem(5, 5000, "panamajack");
	bidOnItem(5, 6000, "cindywreck");
	System.out.println();

	//TESTING getUsersProducts function - 5
	getUsersProducts("builderbob");
	getUsersProducts("cindywreck");
	getUsersProducts("panamajack");
	getUsersProducts("bobama");
	getUsersProducts("edsheer");
	System.out.println();

	//TESTING getUsersProducts function - 5
	getSecondHighestBid(1);
	getSecondHighestBid(2);
	getSecondHighestBid(3);
	getSecondHighestBid(4);
	getSecondHighestBid(5);
	getSecondHighestBid(6);
	System.out.println();


	//TESTING getUsersProducts function - 5
	sellProduct(1, 120, "cindywreck");
	sellProduct(2, 450, "panamajack");
	sellProduct(3, 150, "panamajack");
	sellProduct(4, 15, "builderbob");
	sellProduct(5, 1100, "panamajack");
	sellProduct(6, 1500, "cindywreck");
	System.out.println();

	//TESTING getUsersProducts function - 6
	withdrawProduct(1);
	withdrawProduct(2);
	withdrawProduct(3);
	withdrawProduct(4);
	withdrawProduct(5);
	withdrawProduct(6);
	System.out.println();

	//TESTING mostActiveBidders function - 5
	mostActiveBidders(1, 1);
	mostActiveBidders(1, 2);
	mostActiveBidders(2, 1);
	mostActiveBidders(2, 2);
	mostActiveBidders(2, 3);
	System.out.println();

	//TESTING mostActiveBuyers function - 5
	mostActiveBuyers(1, 1);
	mostActiveBuyers(1, 2);
	mostActiveBuyers(2, 1);
	mostActiveBuyers(2, 2);
	mostActiveBuyers(2, 3);
	System.out.println();

	//TESTING highestVolumeLeafs function - 5
	highestVolumeLeafs(1, 1);
	highestVolumeLeafs(1, 2);
	highestVolumeLeafs(5, 1);
	highestVolumeLeafs(2, 2);
	highestVolumeLeafs(2, 3);
	System.out.println();

	//TESTING highestVolumeRoots function - 5
	highestVolumeRoots(1, 1);
	highestVolumeRoots(1, 2);
	highestVolumeRoots(5, 1);
	highestVolumeRoots(2, 2);
	highestVolumeRoots(2, 3);
	System.out.println();

	//Testing provideSuggestions Function - 5
	provideSuggestions("builderbob");
	provideSuggestions("cindywreck");
	provideSuggestions("panamajack");
	provideSuggestions("bobama");
	provideSuggestions("edsheer");
	System.out.println();
  }

	public void openDBConnection(String user, String password) {
		functions = new Functions(user, password);
		error = new Text();
	}

	//only logs in the first one
  public void logInAdmin(String username, String password) {
    boolean wasSuccessful = functions.login(error, true, username, password);
		System.out.println("Function logInAdmin: Logged in - " + username + " : " + wasSuccessful);
  }

  //done - successful
  public void logInUser(String username, String password) {
    boolean wasSuccessful = functions.login(error, false, username, password);
		System.out.println("Function logInUser: Logged in - " + username + " : " + wasSuccessful);
	}
	//done - successful
	// Works if login is unique - running same params 2x will not work
  public void addNewCustomer(String login, String pass, String name, String address, String email) {
		boolean wasSuccessful = functions.addCustomer(error, login, pass, name, address, email);
		System.out.println("Function addNewCustomer: Added - " + name +  " :  " + wasSuccessful);
	}

	// Does not work, idk why?? - not an issue with parameters
  public void addNewAdmin(String login, String pass, String name, String address, String email) {
    boolean wasSuccessful = functions.addAdministrator(error, login, pass, name, address, email);
		System.out.println("Function addNewAdmin: Added - " + name + " : " + wasSuccessful);
		
	}
	//done
	public void getBidsPlaced(String username, int months) {
		int numBids = functions.bidsUserPlaced(error, username, months);
		System.out.println("Number of Bids by " + username + ": " + numBids);
		
	}
	//done
  	public void getProdCount(String category, int months) {
		int numProds = functions.productCount(error, category, months);
		System.out.println("Product Count for " + category + ": " + numProds);
	
  	}
  	//done
	public void doAddAuction(String name, String desc, String seller, int minPrice, int daysOpen, Set<String> categories) {
		functions.addAuction(error, name, desc, seller, minPrice, daysOpen, categories);
		System.out.println("Function doAddAuction: Added item: " + name);

	}

	//done
	public void getDate() {
		functions.getCurrentDate(error);
		System.out.println("Function getDate - success");
		
	}

	//done
	public void setNewDate(String newDate) {
		functions.setDate(error, newDate);
		System.out.println("Function setNewDate - new date: " + newDate);
		
	}

	//done
	public void getBuyingAmount(String username, int months) {
		int amount = functions.buyingAmount(error, username, months);
		System.out.println("Function getBuyingAmount: " + username +" buying Amount: " + amount);
		
	}
	//done
	public Set<String> getRootCategories() {
		Set<String> result = functions.getRootCategories(error);
		System.out.println("Root categories: " + result);
		System.out.println();
		
		return result;
	}
	//done
	public Set<String> getSubCategories(String parent) {
		Set<String> result = functions.getSubCategories(error, parent);
		
		System.out.println("Sub categories: " + result);
		System.out.println();
		
		return result;
	}
	//done
	public void isLeafCategory(String parent) {
		boolean isLeaf = functions.isLeafCategory(error, parent);
		System.out.println("Is Leaf: " + isLeaf);
		System.out.println();
	}

	//done
	public void getProductsInCategory(String category, String order) {
		List<Product> products = functions.getProductsInCategory(error, category, order);
		System.out.println("Products in category " + category + ": ");
		for(int i=0; i< products.size(); i++){
			System.out.println(products.get(i));
		}
	
	}

	//done
	public void searchForProducts(String key1, String key2, String order) {
		List<Product> products = functions.searchForProducts(error, key1, key2, order);
		System.out.print("Function searchForProducts - keys: " + key1 + ", " + key2 + " products: ");
		for (int i=0; i < products.size(); i++){
			System.out.print(products.get(i));
		}
		System.out.println();
	}
	// done
	public void getStatsAllProducts() {
		List<Product> products = functions.getStatsAllProducts(error);
		System.out.println("All product stats: " + products);
		
	}
	//done 
	public void getStatsAllProducts(String login) {
		List<Product> products = functions.getStatsUserProducts(error, login);
		System.out.println("All product stats for " + login + ": " + products);
		
	}
	//done
	public void bidOnItem(int id, int amount, String bidder) {
		functions.bidOnItem(error, id, amount, bidder);
		System.out.println("Function bidItem: " + bidder + " bid on ItemID " + id );
		System.out.println();
	}
	//done
	public void getUsersProducts(String login) {
		List<Product> products = functions.getUsersProducts(error, login);
		System.out.println("Function getUsersProducts: " + login + "'s products: " + products);
		
	}

	//done
	public void getSecondHighestBid(int id) {
		KeyValue bid = functions.getSecondHighestBid(error, id);
		System.out.println("Item " + id + ":  Second Highest Bidder: " + bid.getKey() + 
				"      Second highest bid: " + bid.getValue());
		
	}

	//done
	public void sellProduct(int id, int amount, String bidder) {
		functions.sellProduct(error, id, amount, bidder);
		System.out.println("Function sellProduct:  Item " + id + " sold to " + bidder);
	}
	//DONE
	public void withdrawProduct(int id) {
		functions.withdrawProduct(error, id);
		System.out.println("Function withdrawProduct: withdrew item - " + id);
		System.out.println();
	}
	//done
	public void mostActiveBidders(int months, int k) {
		List<KeyValue> bidders = functions.mostActiveBidders(error, months, k);
		System.out.println("Function mostActiveBidders: Most Active Bidders: " + bidders);
		
	}
	//done
	public void mostActiveBuyers(int months, int k) {
		List<KeyValue> buyers = functions.mostActiveBuyers(error, months, k);
		System.out.println("Function mostActiveBuyers: Most Active Buyers: " + buyers);
	
	}
	//done
	public void highestVolumeLeafs(int months, int k) {
		List<KeyValue> leafs = functions.highestVolumeLeafs(error, months, k);
		System.out.println("Highest Volume Leafs: " + leafs);
		System.out.println();
	}
	//done 
	public void highestVolumeRoots(int months, int k) {
		List<KeyValue> roots = functions.highestVolumeRoots(error, months, k);
		System.out.println("Highest Volume Roots: " + roots);
		System.out.println();
	}

	public void provideSuggestions(String customer) {
		List<Product> suggestions = functions.provideSuggestions(error, customer);
		System.out.println("Suggestions for " + customer + ": " + suggestions);
	}
}
