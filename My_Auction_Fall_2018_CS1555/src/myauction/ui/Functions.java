package myauction.ui;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.text.Text;
import myauction.data_models.KeyValue;
import myauction.data_models.Product;
import oracle.jdbc.driver.OracleDriver;

public class Functions {
	private static String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
	private String user;
	private String password;
	
	public Functions(String user, String password) {
		this.user = user;
		this.password = password;
		
		try {
			DriverManager.registerDriver(new OracleDriver());
		} 
		catch (SQLException e) {
			System.out.println(e.toString());
		}
	}
	
	public boolean checkConnection(Text error) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			return true;
		}
		catch (SQLException e) {
			error.setText("Invalid Credentials");
		}
		
		return false;
	}
	
	public boolean login(Text error, boolean admin, String login, String pass) {
		
		String query;
		
		if (admin) {
			query = "SELECT login FROM Administrator WHERE login = ? AND password = ?";
		}
		else {
			query = "SELECT login FROM Customer WHERE login = ? AND password = ?";
		}
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setString(1, login);
			stmt.setString(2, pass);
			
			ResultSet result = stmt.executeQuery();
			
			if (result.next()) {
				return result.getString("login").equals(login);
			}
		}
		catch (SQLException e) {
			if (admin) {
				error.setText("Could not login admin: " + e.toString());
			}
			else {
				error.setText("Could not login user: " + e.toString());
			}
		}
		
		return false;
	}

	public boolean addCustomer(Text error, String login, String pass, String name, String address,
			String email) {

		String insert = "INSERT INTO CUSTOMER(LOGIN, PASSWORD, NAME, ADDRESS, EMAIL) VALUES(?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(insert)) {

			stmt.setString(1, login);
			stmt.setString(2, pass);
			stmt.setString(3, name);
			stmt.setString(4, address);
			stmt.setString(5, email);

			stmt.executeUpdate();

			return true;
		} 
		catch (SQLException e) {
			error.setText("Cannot Create Customer: " + e.toString());
		}

		return false;
	}

	public boolean addAdministrator(Text error, String login, String password, String name, String address,
			String email) {

		String insert = "INSERT INTO ADMINISTRATOR(LOGIN, PASSWORD, NAME, ADDRESS, EMAIL) VALUES(?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(insert)) {

			stmt.setString(1, login);
			stmt.setString(2, password);
			stmt.setString(3, name);
			stmt.setString(4, address);
			stmt.setString(5, email);

			return true;
		} 
		catch (SQLException e) {
			error.setText("Cannot Create Admin: " + e.toString());
		}

		return false;
	}

	public int bidsUserPlaced(Text error, String username, int months) {
		String call = "{? = call func_bidCount(?, ?)}";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				CallableStatement stmt = conn.prepareCall(call)) {

			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, username);
			stmt.setInt(3, months);
			stmt.execute();

			return stmt.getInt(1);
		} 
		catch (SQLException e) {
			error.setText("Error getting bids user placed: " + e.toString());
		}

		return 0;
	}
	
	public int productCount(Text error, String category, int months) {
		String call = "{? = call func_productCount(?, ?)}";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				CallableStatement stmt = conn.prepareCall(call)) {

			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, months);
			stmt.setString(3, category);
			stmt.execute();

			return stmt.getInt(1);
		} 
		catch (SQLException e) {
			error.setText("Error getting product count of category: " + e.toString());
		}

		return 0;
	}
	
	public void addAuction(Text error, String name, String desc, String seller, 
			int minPrice, int daysOpen, Set<String> categories) {
		
		String call = "begin proc_putProduct(?, ?, ?, ?, ?, category_list(";
		
		int i = 0;
		
		for (String cat : categories) {
			if (isLeafCategory(error, cat)) {
				if (i == 0) {
					call += "?";
				}
				else {
					call += ", ?";
				}
				
				i++;
			}
			else {
				error.setText(cat + " is not a leaf category!");
				return;
			}
		}
		
		call += "), ?); end;";
		
		String query = "Select * From Product Order By auction_id desc Fetch First 1 Rows Only";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				CallableStatement stmt = conn.prepareCall(call);
				Statement stmt2 = conn.createStatement()) {
			i = 1;
			
			stmt.setString(i++, name);
			stmt.setString(i++, desc);
			stmt.setString(i++, seller);
			stmt.setInt(i++, minPrice);
			stmt.setInt(i++, daysOpen);

			for (String s : categories) {
				stmt.setString(i++, s);
			}
			
			stmt.setInt(i, categories.size());
			stmt.execute();
			
			ResultSet result = stmt2.executeQuery(query);
			
			if (result.next() && result.getString("name").equals(name) && 
					result.getString("seller").equals(seller) &&
					result.getInt("min_price") == minPrice &&
					result.getInt("number_of_days") == daysOpen) {
				error.setText("Add Auction Successful!");
			}
			else {
				error.setText("Add Auction Failed!");
			}
		} 
		catch (SQLException e) {
			error.setText("Error adding auction: " + e.toString());
		}
	}

	public long getCurrentDate(Text error) {

		String call = "{? = call func_currentDate()}";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				CallableStatement stmt = conn.prepareCall(call)) {

			stmt.registerOutParameter(1, Types.DATE);
			stmt.execute();

			return stmt.getDate(1).getTime();
		} 
		catch (SQLException e) {
			error.setText("Error getting current date: " + e.toString());
		}

		return -1L;
	}
	
	public void setDate(Text error, String newDate) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		String update = "Update ourSysDate Set c_date = ?";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(update)) {
			
			Date date; date = new Date(sdf.parse(newDate).getTime());
			
			stmt.setDate(1, date);
			
			ResultSet result = stmt.executeQuery();
			
			if (result.next() && result.getDate("c_date").equals(date)) {
				error.setText("Date Successfully Updated!");
			}
			else {
				error.setText("Date Not Updated!");
			}
			
		} catch (Exception e) {
			error.setText("Error with date: " + e.toString());
		}
	}
	
	public int buyingAmount(Text error, String username, int months) {

		String call = "{? = call func_buyingAmount(?, ?)}";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				CallableStatement stmt = conn.prepareCall(call)) {

			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, username);
			stmt.setInt(3, months);
			stmt.execute();
			
			return stmt.getInt(1);
		} 
		catch (SQLException e) {
			error.setText("Error running the sample queries.  Machine Error: " + e.toString());
		} 

		return -1;
	}
	
	public Set<String> getRootCategories(Text error) {
		Set<String> cats = new HashSet<>();
		
		String query = "SELECT * FROM Category WHERE parent_category IS NULL";
	  
		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement()) {
			
			ResultSet result = stmt.executeQuery(query);
			
			while (result.next()) {
				cats.add(result.getString("name"));
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		
		return cats;
	}
	
	public Set<String> getSubCategories(Text error, String parent) {
		
		Set<String> cats = new HashSet<>();
		
		String query = "SELECT * FROM Category WHERE parent_category = ?";
	  
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setString(1, parent);
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				cats.add(result.getString("name"));
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		
		return cats;
	}
	
	public boolean isLeafCategory(Text error, String parent) {
		// Get's all the children of the given category.
		String query = "SELECT * FROM Category WHERE parent_category = ?";
		  
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setString(1, parent);
			
			ResultSet result = stmt.executeQuery();
			
			if (!result.next()) { // If the category has no children (ie. a leaf)
				return true;
			}
		}
		catch (SQLException e) {
			error.setText("Error In Retrieving Categories: " + e.toString());
		}
		
		return false;
	}
	
	public List<Product> getProductsInCategory(Text error, String category, String order) {
		
		List<Product> products = new ArrayList<>();
		
		String query = "SELECT * FROM Product WHERE status = 'under auction' AND auction_id IN ("
				+ "SELECT auction_id FROM BelongsTo WHERE category IN ("
				+ "SELECT name FROM Category START WITH name = ? "
				+ "CONNECT BY PRIOR name = parent_category)) Order By " + order;
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setString(1, category);
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				Product prod = new Product(result.getInt("auction_id"), result.getString("name"),
						result.getString("description"), result.getString("seller"), 
						result.getInt("min_price"), result.getInt("number_of_days"), 
						result.getString("status"), result.getString("buyer"), 
						result.getDate("sell_date"), result.getInt("amount"));
				
				products.add(prod);
			}
		}
		catch (SQLException e) {
			error.setText("Error in finding Products: " + e.toString());
		}
		
		return products;
	}
	
	public List<Product> searchForProducts(Text error, String key1, String key2, String order) {
		List<Product> products = new ArrayList<>();
		
		String query = "SELECT * FROM Product WHERE description LIKE ? AND description LIKE ?"
				+ " Order By " + order;
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setString(1, "%" + key1 + "%");
			stmt.setString(2, "%" + key2 + "%");
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				Product prod = new Product(result.getInt("auction_id"), result.getString("name"),
						result.getString("description"), result.getString("seller"), 
						result.getInt("min_price"), result.getInt("number_of_days"), 
						result.getString("status"), result.getString("buyer"), 
						result.getDate("sell_date"), result.getInt("amount"));
				
				products.add(prod);
			}
		}
		catch (SQLException e) {
			error.setText("Error in searching for products:" + e.toString());
		}
		
		return products;
	}
	
	public List<Product> getStatsAllProducts(Text error) {
		
		List<Product> products = new ArrayList<>();

    	String query = "WITH HighBids(id, bid) AS (SELECT auction_id, MAX(amount) FROM BidLog GROUP BY auction_id), " + 
    			"HighBidders(id, bidder) AS (SELECT h.id, b.bidder FROM HighBids h INNER JOIN BidLog b " + 
    			"ON b.auction_id = h.id AND b.amount = h.bid) SELECT p.auction_id, p.name, p.description, p.seller, " + 
    			"p.min_price, p.number_of_days, p.status, b.bidder, p.sell_date, p.amount FROM Product p " + 
    			"LEFT OUTER JOIN HighBidders b ON p.auction_id = b.id ORDER BY p.auction_id DESC";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement()) {
      		
			ResultSet result = stmt.executeQuery(query);
      		
			while (result.next()) {
				
		        Product productItem = new Product(
		            result.getInt("auction_id"), 
		            result.getString("name"),
					result.getString("description"), 
		            result.getString("seller"),
					result.getInt("min_price"), 
		            result.getInt("number_of_days"),
					result.getString("status"), 
		            result.getString("bidder"),
					result.getDate("sell_date"), 
		            result.getInt("amount"));

		        products.add(productItem);
			}
		}
		catch (SQLException e) {
			error.setText("Error in finding Products: " + e.toString());
		}
	    
	    return products;
 	}
	
	public List<Product> getStatsUserProducts(Text error, String login) {
		
		List<Product> products = new ArrayList<>();

    	String query = "WITH HighBids(id, bid) AS (SELECT auction_id, MAX(amount) FROM BidLog GROUP BY auction_id), " + 
    			"HighBidders(id, bidder) AS (SELECT h.id, b.bidder FROM HighBids h INNER JOIN BidLog b " + 
    			"ON b.auction_id = h.id AND b.amount = h.bid) SELECT p.auction_id, p.name, p.description, p.seller, " + 
    			"p.min_price, p.number_of_days, p.status, b.bidder, p.sell_date, p.amount FROM Product p LEFT OUTER " +
    			"JOIN HighBidders b ON p.auction_id = b.id WHERE seller = ? ORDER BY p.auction_id DESC";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
      		
			stmt.setString(1, login);
			
			ResultSet result = stmt.executeQuery();
      		
			while (result.next()) {
				
		        Product productItem = new Product(
		            result.getInt("auction_id"), 
		            result.getString("name"),
					result.getString("description"), 
		            result.getString("seller"),
					result.getInt("min_price"), 
		            result.getInt("number_of_days"),
					result.getString("status"), 
		            result.getString("bidder"),
					result.getDate("sell_date"), 
		            result.getInt("amount"));

		        products.add(productItem);
			}
		}
		catch (SQLException e) {
			error.setText("Error in finding Products: " + e.toString());
		}
	    
	    return products;
 	}
	
	public void bidOnItem(Text error, int id, int amount, String bidder) {
		
		String query = "SELECT amount CASE WHEN amount >= ? then 't' else 'f' END as canBid"
				+ "FROM Product WHERE auction_id = ?";
		String insert = "INSERT INTO BidLog values(0, ?, ?, null, ?)";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query);
				PreparedStatement stmt2 = conn.prepareStatement(insert)) {
			
			stmt.setInt(1, amount);
			stmt.setInt(2, id);
			
			ResultSet result = stmt.executeQuery();
			
			if (result.next() && result.getString("CanBid").equals("f")) {
				error.setText("Bid amount is not greater than the current bid:" + 
						result.getInt("amount"));
			}
			else {
				stmt2.setInt(1, id);
				stmt2.setString(2, bidder);
				stmt2.setInt(3, amount);
		
				if (stmt2.executeUpdate() == 1) {
					error.setText("Bid was successful!");
				}
				else {
					error.setText("Bid was not successful!");
				}
			}
		}
		catch (SQLException e) {
			error.setText("Error in bidding on product:" + e.toString());
		}
	}
	
	public List<Product> getUsersProducts(Text error, String login) {
		List<Product> products = new ArrayList<>();
		
		String query = "SELECT * FROM Product WHERE seller = ?";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setString(1, login);
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				Product prod = new Product(result.getInt("auction_id"), result.getString("name"),
						result.getString("description"), result.getString("seller"), 
						result.getInt("min_price"), result.getInt("number_of_days"), 
						result.getString("status"), result.getString("buyer"), 
						result.getDate("sell_date"), result.getInt("amount"));
				
				products.add(prod);
			}
		}
		catch (SQLException e) {
			error.setText("Error in retrieving your products:" + e.toString());
		}
		
		return products;
	}
	
	public KeyValue getSecondHighestBid(Text error, int id) {
		
		String query = "SELECT amount, bidder FROM (SELECT amount, bidder FROM BidLog WHERE auction_id = ? ORDER BY"
				+ " amount DESC FETCH FIRST 2 ROWS ONLY) ORDER BY amount FETCH FIRST ROW ONLY";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setInt(1, id);
			
			ResultSet result = stmt.executeQuery();
			
			if (result.next()) {
				return new KeyValue(result.getString("bidder"), result.getInt("amount"));
			}
		}
		catch (SQLException e) {
			error.setText("Error in getting 2nd highest bid:" + e.toString());
		}
		
		return null;
	}
	
	public void sellProduct(Text error, int id, int amount, String bidder) {
		
		String update = "UPDATE Product SET status = 'sold', amount = ?, "
				+ "sell_date = func_currentDate(), buyer = ?"
				+ "WHERE auction_id = ?";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(update)) {
			
			stmt.setInt(1, amount);
			stmt.setString(2, bidder);
			stmt.setInt(3, id);
			
			if (stmt.executeUpdate() == 1) {
				error.setText("Auction successfully sold!");
			}
			else {
				conn.rollback();
				error.setText("Auction could not be sold!");
			}
		}
		catch (SQLException e) {
			error.setText("Error in selling product:" + e.toString());
		}
	}
	
	public void withdrawProduct(Text error, int id) {
		
		String update = "UPDATE Product SET status = 'withdrawn' WHERE auction_id = ?";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(update)) {
			
			stmt.setInt(1, id);
			
			if (stmt.executeUpdate() == 1) {
				error.setText("Auction successfully withdrawn!");
			}
			else {
				conn.rollback();
				error.setText("Auction could not be withdrawn!");
			}
		}
		catch (SQLException e) {
			error.setText("Error in withdrawing product:" + e.toString());
		}
	}
	
	public List<KeyValue> mostActiveBidders(Text error, int months, int k) {
		
		List<KeyValue> bidders = new ArrayList<>();
		
		String query = "SELECT login, func_bidCount(login, ?) AS cnt FROM Customer "
				+ "ORDER BY cnt DESC, login FETCH FIRST ? ROWS ONLY";

	    try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
	    	
	    	stmt.setInt(1, months);
	    	stmt.setInt(2, k);
	    	
	    	ResultSet result = stmt.executeQuery();
	    	
	    	while (result.next()) {
	    		bidders.add(new KeyValue(result.getString("login"), result.getInt("cnt")));
	    	}
	    } catch(SQLException e) {
	    	error.setText("Error getting active bidders: " + e.toString());
	    }
		
	    return bidders;
	}
	
	public List<KeyValue> mostActiveBuyers(Text error, int months, int k) {
		
		List<KeyValue> buyers = new ArrayList<>();
		
		String query = "SELECT login, func_buyingAmount(login, ?) AS cnt FROM Customer "
				+ "ORDER BY cnt DESC NULLS LAST, login FETCH FIRST ? ROWS ONLY";

	    try (Connection conn = DriverManager.getConnection(url, user, password);
	    		PreparedStatement stmt = conn.prepareStatement(query)) {
	    	
	    	stmt.setInt(1, months);
	    	stmt.setInt(2, k);
	    	
	    	ResultSet result = stmt.executeQuery();
	    	
	    	while (result.next()) {
	    		buyers.add(new KeyValue(result.getString("login"), result.getInt("cnt")));
	    	}
	    } catch(SQLException e) {
	    	error.setText("Error getting active buyers: " + e.toString());
	    }
		
	    return buyers;
	}
	
	public List<KeyValue> highestVolumeLeafs(Text error, int months, int k) {
				
		String query = "WITH Leaves(name) AS ( "
						+ "SELECT name FROM category WHERE name NOT IN ( " 
							+ "SELECT DISTINCT parent_category FROM category "
							+ "WHERE parent_category IS NOT NULL)) "
					 + "Select name, func_productCount(?, name) as cnt "
					 + "FROM Leaves ORDER BY cnt DESC, name "
					 + "FETCH FIRST ? ROWS ONLY";
		
		List<KeyValue> leafs = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setInt(1, months);
			stmt.setInt(2, k);
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				leafs.add(new KeyValue(result.getString("name"), result.getInt("cnt")));
			}
		}
		catch (SQLException e) {
			error.setText("Error in geting statistic:" + e.toString());
		}
		
	    return leafs;
	}
	
	public List<KeyValue> highestVolumeRoots(Text error, int months, int k) {
		
		String query = "WITH ProdCats(name, parent, cnt) AS (SELECT c.name, c.parent_category, func_productCount(?, c.name) as cnt " + 
				"FROM Category c LEFT JOIN BelongsTo p ON c.name = p.category GROUP BY c.name, c.parent_category), RootCnts(name, cnt) " + 
				"AS (SELECT CONNECT_BY_ROOT name root, cnt FROM ProdCats CONNECT BY PRIOR name = parent START WITH parent IS NULL) " + 
				"SELECT DISTINCT name, SUM(cnt) AS sum FROM RootCnts GROUP BY name ORDER BY sum DESC, name FETCH FIRST ? ROWS ONLY ";
		
		List<KeyValue> leafs = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.setInt(1, months);
			stmt.setInt(2, k);
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				leafs.add(new KeyValue(result.getString("name"), result.getInt("sum")));
			}
		}
		catch (SQLException e) {
			error.setText("Error in geting statistic:" + e.toString());
		}
		
	    return leafs;
	}
	
	public List<Product> provideSuggestions(Text error, String customer) {
		
		List<Product> products = new ArrayList<>();
		
		String query = "WITH UserBids(auction) AS ( SELECT DISTINCT auction_id  FROM BidLog WHERE bidder = ?), " + 
				"BidFriends(friend) AS ( SELECT DISTINCT bidder FROM BidLog WHERE auction_id NOT IN (Select * FROM UserBids) " + 
				"AND bidder <> ?), FriendsBids(auction) AS ( SELECT DISTINCT b.auction_id FROM BidLog b, BidFriends f " + 
				"WHERE b.bidder IN (SELECT * FROM BidFriends) AND b.auction_id NOT IN (SELECT * FROM UserBids)), " + 
				"FriendCnt (auction, cnt) AS ( SELECT a.auction, COUNT(DISTINCT b.bidder) FROM BidLog b INNER JOIN FriendsBids a " + 
				"ON b.auction_id = a.auction GROUP BY a.auction) SELECT * FROM Product p INNER JOIN FriendCnt f " + 
				"ON p.auction_id = f.auction WHERE p.status = 'under auction' ORDER BY f.cnt DESC";
		
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
		
			stmt.setString(1, customer);
			stmt.setString(2, customer);
  
			ResultSet result = stmt.executeQuery();
		
			while (result.next()) {
				Product prod = new Product(result.getInt("auction_id"), result.getString("name"),
						result.getString("description"), result.getString("seller"),
						result.getInt("min_price"), result.getInt("number_of_days"),
						result.getString("status"), result.getString("buyer"),
						result.getDate("sell_date"), result.getInt("amount"));
				products.add(prod);
			}
		} 
		catch( SQLException e) {
		      error.setText("Error running the sample queries.  Machine Error: " + e.toString());
		}
		
		return products;
	}
}
