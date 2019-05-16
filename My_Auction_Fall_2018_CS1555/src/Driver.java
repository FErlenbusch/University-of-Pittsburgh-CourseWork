import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;
import javafx.scene.text.Text;
import myauction.data_models.Product;
import myauction.data_models.KeyValue;
import myauction.ui.Functions;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// javafx.scene.text.Text object
//getText()

public class Driver {

	public Text error;
	public Functions functions;
	public String DBuser;
	public String DBpassword;
	public String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";

	public static void main(String[] args) {
    System.out.println("Starting auction!");
		Driver d = new Driver();
		d.runDriver();
	}

  public void runDriver() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter DB username:");
    DBuser = sc.next();
		System.out.println("Please enter DB password:");
    DBpassword = sc.next();

		System.out.println("Please maximize your terminal window to best display output");

    openDBConnection(DBuser, DBpassword);
		logInAdmin("admin", "password");
		logInUser("DinoMan", "password");
		addNewCustomer("testlogin", "testpass", "Sandy", "123 Lane", "sandy@email.com");
		addNewAdmin("newAdlogin", "testpass22", "RandyAdmin", "12333 Lane", "randy@email.com");
		getBidsPlaced("builderbob", 2);
		getProdCount("Antiques", 2);
		Set<String> categories = new HashSet<>();
		doAddAuction("Object", "The best thing ever", "builderbob", 200, 5, categories);
		getDate();
		setNewDate("2018/12/12 23:59:59");
		getBuyingAmount("builderbob", 2);
		getRootCategories();
		getSubCategories("Sports");
		isLeafCategory("Sports");
		isLeafCategory("BaseBall");
		getProductsInCategory("Sports", "name");
		searchForProducts("Gold", "Ring", "name");
		getStatsAllProducts();
		getStatsAllProducts("builderbob");
		bidOnItem(4, 200, "builderbob");
		getUsersProducts("builderbob");
		getSecondHighestBid(4);
		sellProduct(4, 200, "builderbob");
		withdrawProduct(5);
		mostActiveBidders(2, 3);
		mostActiveBuyers(3, 2);
		highestVolumeLeafs(2, 3);
		highestVolumeRoots(2, 3);
		provideSuggestions("builderbob");
  }

	public void openDBConnection(String user, String password) {
		functions = new Functions(user, password);
		error = new Text();
	}

  public void logInAdmin(String username, String password) {
		System.out.println();
		System.out.println("Logging is as admin: " + username + " with password: " + password);
    boolean wasSuccessful = functions.login(error, true, username, password);
		System.out.println("Admin login worked: " + wasSuccessful);
  }

  public void logInUser(String username, String password) {
		System.out.println();
		System.out.println("Logging is as user: " + username + " with password: " + password);
    boolean wasSuccessful = functions.login(error, false, username, password);
		System.out.println("User login worked: " + wasSuccessful);
	}

	// Works if login is unique - running same params 2x will not work!
  public void addNewCustomer(String login, String pass, String name, String address, String email) {
		System.out.println();
		System.out.println("Adding new customer named " + name);
		queryCustomers();
		boolean wasSuccessful = functions.addCustomer(error, login, pass, name, address, email);
		System.out.println("Adding new customer was successful: " + wasSuccessful);
		queryCustomers();
	}

  public void addNewAdmin(String login, String pass, String name, String address, String email) {
		System.out.println();
		System.out.println("Adding admin named " + name);
		queryAdmins();
    boolean wasSuccessful = functions.addAdministrator(error, login, pass, name, address, email);
		System.out.println("Adding new admin was successful: " + wasSuccessful);
		queryAdmins();
	}

	public void getBidsPlaced(String username, int months) {
		System.out.println();
		System.out.println("Getting number of bids placed by " + username + " in the past " + months + " months");
		int numBids = functions.bidsUserPlaced(error, username, months);
		System.out.println("Bids: " + numBids);
	}

  public void getProdCount(String category, int months) {
		System.out.println();
		System.out.println("Getting number of products in category " + category + " in the last " + months + "months");
		int numProds = functions.productCount(error, category, months);
		System.out.println("Products: " + numProds);
  }

	public void doAddAuction(String name, String desc, String seller, int minPrice, int daysOpen, Set<String> categories) {
		System.out.println();
		System.out.println("Adding auction with name: " + name + ", seller: " + seller + ", minPrice: " + minPrice);
		queryAuctions();
		functions.addAuction(error, name, desc, seller, minPrice, daysOpen, categories);
		System.out.println("Auction was added!");
		queryAuctions();
	}

	public void getDate() {
		System.out.println();
		long date = functions.getCurrentDate(error);
		System.out.println("Date: " + date);
	}

	public void setNewDate(String newDate) {
		System.out.println();
		System.out.println("Setting new system date of " + newDate);
		functions.setDate(error, newDate);
		System.out.println("New date was set!");
	}

	public void getBuyingAmount(String username, int months) {
		System.out.println();
		System.out.println("Getting how much user " + username + " spent on products in the past " + months + " months");
		int amount = functions.buyingAmount(error, username, months);
		if (amount <= 0 ) {
			System.out.println(username + " bought no products");
		} else {
			System.out.println("Amount: " + amount);
		}
	}

	public void getRootCategories() {
		System.out.println();
		Set<String> result = functions.getRootCategories(error);
		System.out.println("All root categories: " + result);
	}

	public void getSubCategories(String parent) {
		System.out.println();
		Set<String> result = functions.getSubCategories(error, parent);
		System.out.println("Sub categories of parent category " + parent + ": " + result);
	}

	public void isLeafCategory(String parent) {
		System.out.println();
		boolean isLeaf = functions.isLeafCategory(error, parent);
		System.out.println(parent + " is Leaf: " + isLeaf);
	}

	// Need to print out products correctly, doesn't do that yet
	public void getProductsInCategory(String category, String order) {
		System.out.println();
		System.out.println("Products in category " + category + ": ");
		List<Product> products = functions.getProductsInCategory(error, category, order);
		System.out.println(printProdHeader() + products);
	}

	public void searchForProducts(String key1, String key2, String order) {
		System.out.println();
		List<Product> products = functions.searchForProducts(error, key1, key2, order);
		System.out.println("Products found in search with params:" + key1 + " " + key2 + ": " + printProdHeader() + products);
	}

	public void getStatsAllProducts() {
		System.out.println();
		List<Product> products = functions.getStatsAllProducts(error);
		System.out.println("All product stats: " + printProdHeader() + products);
	}

	public void getStatsAllProducts(String login) {
		System.out.println();
		List<Product> products = functions.getStatsUserProducts(error, login);
		System.out.println("Product stats for user " + login + ": " + printProdHeader() + products);
	}

	public void bidOnItem(int id, int amount, String bidder) {
		System.out.println();
		System.out.println("Placing a bid");
		functions.bidOnItem(error, id, amount, bidder);
		System.out.println("Bid was placed by bidder " + bidder + " on product where id = " + id + " for $" + amount);
	}

	public void getUsersProducts(String login) {
		System.out.println();
		List<Product> products = functions.getUsersProducts(error, login);
		System.out.println("User's products: " + printProdHeader() + products);
	}

	public String printProdHeader() {
		String prodString = "\n";
		prodString = prodString + "Name";
		prodString = prodString + "\tDescription";
		prodString = prodString + "\tSeller";
		prodString = prodString + "\tMinimum Price";
		prodString = prodString + "\tDays";
		prodString = prodString + "\tStatus";
		prodString = prodString + "\tBuyer";
		prodString = prodString + "\tSell Date";
		prodString = prodString + "\tAmount";
		return prodString;
	}

	public void getSecondHighestBid(int id) {
		System.out.println();
		KeyValue bid = functions.getSecondHighestBid(error, id);
		System.out.println("Second highest bid on product where product id = " + id + ": " + bid);
	}

	public void sellProduct(int id, int amount, String bidder) {
		System.out.println();
		System.out.println("Selling Product where product ID = " + id + " to bidder " + bidder);
		functions.sellProduct(error, id, amount, bidder);
		System.out.println("Product was sold");
	}

	public void withdrawProduct(int id) {
		System.out.println();
		System.out.println("Withdrawing product where product ID = " + id);
		functions.withdrawProduct(error, id);
		System.out.println("Product was withdrawn");
	}

	public void mostActiveBidders(int months, int k) {
		System.out.println();
		List<KeyValue> bidders = functions.mostActiveBidders(error, months, k);
		System.out.println("Top " + k + " Most Active Bidders in the past " + months + " months: " + bidders);
	}

	public void mostActiveBuyers(int months, int k) {
		System.out.println();
		List<KeyValue> buyers = functions.mostActiveBuyers(error, months, k);
		System.out.println("Top " + k + " Most Active Buyers in the past " + months + " months: ");
		if (buyers == null || buyers.isEmpty()) {
			System.out.println("No Buyers in DB");
		} else {
			System.out.println(buyers);
		}
	}

	public void highestVolumeLeafs(int months, int k) {
		System.out.println();
		List<KeyValue> leafs = functions.highestVolumeLeafs(error, months, k);
		System.out.println("Highest Volume Leafs: " + leafs);
	}

	public void highestVolumeRoots(int months, int k) {
		System.out.println();
		List<KeyValue> roots = functions.highestVolumeRoots(error, months, k);
		System.out.println("Highest Volume Roots: " + roots);
	}

	public void provideSuggestions(String customer) {
		System.out.println();
		List<Product> suggestions = functions.provideSuggestions(error, customer);
		System.out.println("Suggestions for " + customer + ": " + printProdHeader() + suggestions);
	}

	public void queryProducts() {
		Set<String> cats = new HashSet<>();

		String query = "SELECT * FROM Product";

		try (Connection conn = DriverManager.getConnection(url, DBuser, DBpassword);
				Statement stmt = conn.createStatement()) {

			ResultSet result = stmt.executeQuery(query);

			while (result.next()) {
				cats.add(result.getString("name"));
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		System.out.println("All Products: ");
		System.out.println(cats);
	}

	public void queryCustomers() {
		Set<String> cats = new HashSet<>();

		String query = "SELECT * FROM Customer";

		try (Connection conn = DriverManager.getConnection(url, DBuser, DBpassword);
				Statement stmt = conn.createStatement()) {

			ResultSet result = stmt.executeQuery(query);

			while (result.next()) {
				cats.add(result.getString("name"));
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		System.out.println("Customer Names: ");
		System.out.println(cats);
	}

	public void queryAdmins() {
		Set<String> cats = new HashSet<>();

		String query = "SELECT * FROM Administrator";

		try (Connection conn = DriverManager.getConnection(url, DBuser, DBpassword);
				Statement stmt = conn.createStatement()) {

			ResultSet result = stmt.executeQuery(query);

			while (result.next()) {
				cats.add(result.getString("name"));
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		System.out.println("Administrator Names: ");
		System.out.println(cats);
	}

	public void queryAuctions() {
		//Set<String> cats = new HashSet<>();
		String cats = "";

		String query = "SELECT * FROM Product";

		try (Connection conn = DriverManager.getConnection(url, DBuser, DBpassword);
				Statement stmt = conn.createStatement()) {

			ResultSet result = stmt.executeQuery(query);

			while (result.next()) {
				cats = cats + "\n" + result.getString("name");
				cats = cats + "\t" + result.getString("seller");
				cats = cats + "\t" + result.getString("min_price");
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		System.out.println("Product Names: ");
		System.out.println("\nName \tSeller \tMin Price");
		System.out.println(cats);
	}
}
