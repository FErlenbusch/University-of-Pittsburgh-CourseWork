package myauction.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import myauction.data_models.KeyValue;
import myauction.data_models.Product;

public class MyAuctionUi extends Application {
	private static final String TITLE = "title";
	private static final int TEXT_INPUT_LENGTH = 200;
	private static final Insets DEFAULT_INSET = new Insets(30, 30, 30, 30);

	private Stage primaryStage;
	private Text errorLabel = new Text("");
	private boolean admin = false;
	private String currentUser;
	private ContextMenu catMenu;
	private Functions functions;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		primaryStage.setTitle("My Auction");
		
		getClass3Screen();
	}

	public void makeStage(GridPane pane) {
		Scene scene = new Scene(pane, 1300, 500);
		scene.getStylesheets().add("MyAuctionStyle.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void getClass3Screen() {
		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Enter Class 3 Credentials:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		Label name = new Label("Username:");
		TextField nameTextField = new TextField();
		nameTextField.setMinWidth(TEXT_INPUT_LENGTH);
		root.add(name, 0, 1);
		root.add(nameTextField, 1, 1);

		Label pass = new Label("Password:");
		PasswordField passTextField = new PasswordField();
		passTextField.setMinWidth(TEXT_INPUT_LENGTH);
		root.add(pass, 0, 2);
		root.add(passTextField, 1, 2);

		Button signInBtn = new Button("Sign In");
		signInBtn.setOnAction(e -> {
			initFunctions(nameTextField.getText(), passTextField.getText());
			if (functions.checkConnection(errorLabel)) {
				errorLabel.setText("");
				getWelcomeScreen();
			}
			else {
				getClass3Screen();
			}});
			
		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().addAll(signInBtn, exitBtn);
		root.add(hbBtn, 1, 3);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}
	
	private void initFunctions(String user, String password) {
		this.functions = new Functions(user, password); 
	}

	private void getWelcomeScreen() {
		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Welcome to My Action!");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		Label name = new Label("Username:");
		TextField nameTextField = new TextField();
		nameTextField.setMinWidth(TEXT_INPUT_LENGTH);
		root.add(name, 0, 1);
		root.add(nameTextField, 1, 1);

		Label pass = new Label("Password:");
		TextField passTextField = new TextField();
		passTextField.setMinWidth(TEXT_INPUT_LENGTH);
		root.add(pass, 0, 2);
		root.add(passTextField, 1, 2);

		Button registerBtn = new Button("Register");
		registerBtn.setOnAction(e -> { 
			errorLabel.setText("");
			getRegisterPage();});

		Button signInBtn = new Button("User Sign In");
		signInBtn.setOnAction(e -> signInUser(nameTextField.getText(), passTextField.getText()));

		Button adminSignInBtn = new Button("Admin Sign In");
		adminSignInBtn.setOnAction(e -> signInAdmin(nameTextField.getText(), passTextField.getText()));

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().addAll(registerBtn, signInBtn, adminSignInBtn, exitBtn);
		root.add(hbBtn, 1, 3);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getRegisterPage() {
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Enter Details to Register a New User:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		HBox usernameBox = new HBox(70);
		usernameBox.setAlignment(Pos.CENTER);
		Label username = new Label("Username:");
		TextField usernameTextField = new TextField();
		usernameTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		usernameBox.getChildren().addAll(username, usernameTextField);
		root.add(usernameBox, 0, i++, 2, 1);

		HBox nameBox = new HBox(100);
		nameBox.setAlignment(Pos.CENTER);
		Label name = new Label("Name:");
		TextField nameTextField = new TextField();
		nameTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		nameBox.getChildren().addAll(name, nameTextField);
		root.add(nameBox, 0, i++, 2, 1);

		HBox addressBox = new HBox(83);
		addressBox.setAlignment(Pos.CENTER);
		Label address = new Label("Address:");
		TextField addressTextField = new TextField();
		addressTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		addressBox.getChildren().addAll(address, addressTextField);
		root.add(addressBox, 0, i++, 2, 1);

		HBox emailBox = new HBox(103);
		emailBox.setAlignment(Pos.CENTER);
		Label email = new Label("Email:");
		TextField emailTextField = new TextField();
		emailTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		emailBox.getChildren().addAll(email, emailTextField);
		root.add(emailBox, 0, i++, 2, 1);

		HBox passBox = new HBox(75);
		passBox.setAlignment(Pos.CENTER);
		Label pass = new Label("Password:");
		TextField passTextField = new TextField();
		passTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		passBox.getChildren().addAll(pass, passTextField);
		root.add(passBox, 0, i++, 2, 1);

		HBox rePassBox = new HBox(15);
		rePassBox.setAlignment(Pos.CENTER);
		Label rePass = new Label("Reenter Password:");
		TextField rePassTextField = new TextField();
		rePassTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		rePassBox.getChildren().addAll(rePass, rePassTextField);
		root.add(rePassBox, 0, i++, 2, 1);

		Button registerBtn;

		if (admin) {
			registerBtn = new Button("Register As User");
		} else {
			registerBtn = new Button("Register");
		}

		registerBtn.setOnAction(
				e -> registerUser(usernameTextField.getText(), nameTextField.getText(), addressTextField.getText(),
						emailTextField.getText(), passTextField.getText(), rePassTextField.getText()));

		Button registerAdminBtn = new Button("Register As Admin");
		registerBtn.setOnAction(
				e -> registerAdmin(usernameTextField.getText(), nameTextField.getText(), addressTextField.getText(),
						emailTextField.getText(), passTextField.getText(), rePassTextField.getText()));

		Button backBtn = new Button("Back");

		if (admin) {
			backBtn.setOnAction(e -> {
				errorLabel.setText(""); 
				getMainMenu();});
		} else {
			backBtn.setOnAction(e -> {
				errorLabel.setText("");
				getWelcomeScreen();});
		}

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);

		if (admin) {
			hbBtn.getChildren().add(registerAdminBtn);
		}

		hbBtn.getChildren().addAll(registerBtn, backBtn, exitBtn);
		root.add(hbBtn, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getMainMenu() {
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Main Menu");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		Button browse = new Button("Browse Auctions");
		browse.setOnAction(e -> getBrowseScreen(null));
		browse.setMinWidth(150);

		Button search = new Button("Search Auctions");
		search.setOnAction(e -> getSearchScreen());
		search.setMinWidth(150);

		Button create = new Button("Create Auction");
		create.setOnAction(e -> getCreateScreen());
		create.setMinWidth(150);

		HBox firstButtons = new HBox(20);
		firstButtons.setAlignment(Pos.CENTER);
		firstButtons.getChildren().addAll(browse, search, create);
		root.add(firstButtons, 0, i++, 2, 1);

		Button bid = new Button("Bid On Auction");
		bid.setOnAction(e -> getBidScreen(null));
		bid.setMinWidth(150);

		Button close = new Button("Close Auction");
		close.setOnAction(e -> getCloseScreen(null));
		close.setMinWidth(150);

		Button suggestions = new Button("Suggested Auctions");
		suggestions.setOnAction(e -> getSuggestionScreen());
		suggestions.setMinWidth(150);

		HBox secondButtons = new HBox(20);
		secondButtons.setAlignment(Pos.CENTER);
		secondButtons.getChildren().addAll(bid, close, suggestions);
		root.add(secondButtons, 0, i++, 2, 1);

		if (admin) {
			Text adminTitle = new Text("Admin functions");
			adminTitle.setId(TITLE);
			root.add(adminTitle, 0, i++, 2, 1);

			Button register = new Button("Register A User");
			register.setOnAction(e -> getRegisterPage());
			register.setMinWidth(150);

			Button update = new Button("Update System Date");
			update.setOnAction(e -> getUpdateDateScreen());
			update.setMinWidth(150);

			Button productStats = new Button("Product Statistics");
			productStats.setOnAction(e -> getProductStatsScreen(null));
			productStats.setMinWidth(150);

			Button stats = new Button("Statistics");
			stats.setOnAction(e -> getStatsScreen());
			stats.setMinWidth(150);

			HBox adminButtons = new HBox(15);
			adminButtons.setAlignment(Pos.CENTER);
			adminButtons.getChildren().addAll(register, update, productStats, stats);
			root.add(adminButtons, 0, i++, 2, 1);
		}

		Button logoutBtn = new Button("Log Out");
		logoutBtn.setOnAction(e -> {
			admin = false;
			getWelcomeScreen();
		});
		logoutBtn.setMinWidth(150);

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));
		exitBtn.setMinWidth(150);

		HBox thirdButtons = new HBox(15);
		thirdButtons.setAlignment(Pos.CENTER);
		thirdButtons.getChildren().addAll(logoutBtn, exitBtn);
		root.add(thirdButtons, 0, i++, 2, 1);

		errorLabel = new Text("");
		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getBrowseScreen(String parent) {
		
		Set<String> cats;

		if (parent == null) {
			cats = functions.getRootCategories(errorLabel);
		} else {
			cats = functions.getSubCategories(errorLabel, parent);
		}

		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Categories To Browse:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		VBox catBox = new VBox(20);
		catBox.setId("pane");
		catBox.setMinWidth(400);
		catBox.setPadding(DEFAULT_INSET);

		for (String cat : cats) {
			Label label = new Label(cat);

			Button subs = new Button("Subcategories");
			subs.setOnAction(e -> {
				errorLabel.setText("");
				getBrowseScreen(cat);});

			Button browse = new Button("Browse");
			browse.setOnAction(e -> {
				errorLabel.setText("");
				getBrowseCategoryScreen(cat, "name");});

			HBox selection = new HBox(20);
			selection.setAlignment(Pos.CENTER_RIGHT);
			selection.getChildren().addAll(label, subs, browse);

			catBox.getChildren().add(selection);
		}

		ScrollPane catPane = new ScrollPane();
		catPane.setBackground(null);
		catPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		catPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		catPane.setMaxWidth(400);
		catPane.setId("pane");
		catPane.setContent(catBox);
		root.add(catPane, 0, 1, 2, 1);

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(main, exitBtn);
		root.add(buttons, 0, 2, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 3, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getBrowseCategoryScreen(String category, String sort) {
		List<Product> products = functions.getProductsInCategory(errorLabel, category, sort);

		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Items in Category: " + category);
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		Button sortNameBtn = new Button("Sort By Name");
		sortNameBtn.setOnAction(e -> {
			errorLabel.setText("");
			getBrowseCategoryScreen(category, "name");});

		Button sortPriceBtn = new Button("Sort By Bid Amount");
		sortPriceBtn.setOnAction(e -> {
			errorLabel.setText("");
			getBrowseCategoryScreen(category, "amount desc");});

		HBox sortBox = new HBox(50);
		sortBox.setAlignment(Pos.CENTER);
		sortBox.getChildren().addAll(sortNameBtn, sortPriceBtn);
		root.add(sortBox, 0, 1, 2, 1);

		GridPane prodBox = new GridPane();
		prodBox.setId("pane");
		prodBox.setMinWidth(400);
		prodBox.setAlignment(Pos.CENTER);
		prodBox.setHgap(30);
		prodBox.setVgap(10);
		prodBox.setPadding(DEFAULT_INSET);
		prodBox.add(new Label("Auction ID"), 0, 0);
		prodBox.add(new Label("Name"), 1, 0);
		prodBox.add(new Label("Description"), 2, 0);
		prodBox.add(new Label("Amount"), 3, 0);

		int i = 1;

		for (Product prod : products) {
			int j = 0;

			prodBox.add(new Label("" + prod.getAuctionId()), j++, i);
			prodBox.add(new Label(prod.getName()), j++, i);
			prodBox.add(new Label(prod.getDescription()), j++, i);
			prodBox.add(new Label("" + prod.getAmount()), j++, i);

			Button bid = new Button("Bid");
			bid.setOnAction(e -> getBidScreen(prod.getAuctionId()));

			prodBox.add(bid, j, i++);
		}

		ScrollPane prodPane = new ScrollPane();
		prodPane.setBackground(null);
		prodPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		prodPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		prodPane.setId("pane");
		prodPane.setContent(prodBox);
		root.add(prodPane, 0, 2, 2, 1);

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(main, exitBtn);
		root.add(buttons, 0, 3, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getSearchScreen() {
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Enter Keywords To Search Products:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++);

		HBox key1Box = new HBox(50);
		key1Box.setAlignment(Pos.CENTER);
		Label key1 = new Label("Keyword 1:");
		TextField key1TextField = new TextField();
		key1TextField.setMaxWidth(TEXT_INPUT_LENGTH);
		key1Box.getChildren().addAll(key1, key1TextField);
		root.add(key1Box, 0, i++);

		HBox key2Box = new HBox(50);
		key2Box.setAlignment(Pos.CENTER);
		Label key2 = new Label("Keyword 2:");
		TextField key2TextField = new TextField();
		key2TextField.setMaxWidth(TEXT_INPUT_LENGTH);
		key2Box.getChildren().addAll(key2, key2TextField);
		root.add(key2Box, 0, i++);

		Button searchBtn = new Button("Search");
		searchBtn.setOnAction(e -> getSearchResultsScreen(key1TextField.getText(), key2TextField.getText(), "name"));

		Button menuBtn = new Button("Main Menu");
		menuBtn.setOnAction(e -> getMainMenu());

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().addAll(searchBtn, menuBtn, exitBtn);
		root.add(hbBtn, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getSearchResultsScreen(String key1, String key2, String sort) {

		List<Product> products = functions.searchForProducts(errorLabel, key1, key2, sort);

		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Search Results:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		Button sortNameBtn = new Button("Sort By Name");
		sortNameBtn.setOnAction(e -> {
			errorLabel.setText("");
			getSearchResultsScreen(key1, key2, "name");});

		Button sortPriceBtn = new Button("Sort By Bid Amount");
		sortPriceBtn.setOnAction(e -> {
			errorLabel.setText("");
			getSearchResultsScreen(key1, key2, "amount desc");});

		HBox sortBox = new HBox(50);
		sortBox.setAlignment(Pos.CENTER);
		sortBox.getChildren().addAll(sortNameBtn, sortPriceBtn);
		root.add(sortBox, 0, 1, 2, 1);

		GridPane prodBox = new GridPane();
		prodBox.setId("pane");
		prodBox.setMinWidth(400);
		prodBox.setAlignment(Pos.CENTER);
		prodBox.setHgap(30);
		prodBox.setVgap(10);
		prodBox.setPadding(DEFAULT_INSET);
		prodBox.add(new Label("Auction ID"), 0, 0);
		prodBox.add(new Label("Name"), 1, 0);
		prodBox.add(new Label("Description"), 2, 0);
		prodBox.add(new Label("Amount"), 3, 0);

		int i = 1;

		for (Product prod : products) {
			int j = 0;

			prodBox.add(new Label("" + prod.getAuctionId()), j++, i);
			prodBox.add(new Label(prod.getName()), j++, i);
			prodBox.add(new Label(prod.getDescription()), j++, i);
			prodBox.add(new Label("" + prod.getAmount()), j, i++);
		}

		ScrollPane prodPane = new ScrollPane();
		prodPane.setBackground(null);
		prodPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		prodPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		prodPane.setId("pane");
		prodPane.setContent(prodBox);
		root.add(prodPane, 0, 2, 2, 1);

		Button search = new Button("Search Again");
		search.setOnAction(e -> {
			errorLabel.setText("");
			getSearchScreen();});

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(50);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(search, main, exitBtn);
		root.add(buttons, 0, 3, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getCreateScreen() {
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Enter Details to Create a Auction:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++);

		HBox nameBox = new HBox(115);
		nameBox.setAlignment(Pos.CENTER);
		Label name = new Label("Name:");
		TextField nameTextField = new TextField();
		nameTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		nameBox.getChildren().addAll(name, nameTextField);
		root.add(nameBox, 0, i++);

		HBox descBox = new HBox(75);
		descBox.setAlignment(Pos.CENTER);
		Label desc = new Label("Description:");
		TextField descTextField = new TextField();
		descTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		descBox.getChildren().addAll(desc, descTextField);
		root.add(descBox, 0, i++);

		HBox sellerBox = new HBox(115);
		sellerBox.setAlignment(Pos.CENTER);
		Label seller = new Label("Seller:");
		TextField sellerTextField = new TextField();
		sellerTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		sellerBox.getChildren().addAll(seller, sellerTextField);

		if (admin) {
			root.add(sellerBox, 0, i++);
		}

		HBox minBox = new HBox(55);
		minBox.setAlignment(Pos.CENTER);
		Label min = new Label("Minimum Price:");
		TextField minTextField = new TextField();
		minTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		minBox.getChildren().addAll(min, minTextField);
		root.add(minBox, 0, i++);

		HBox daysBox = new HBox(15);
		daysBox.setAlignment(Pos.CENTER);
		Label days = new Label("# of Days on Market:");
		TextField daysTextField = new TextField();
		daysTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		daysBox.getChildren().addAll(days, daysTextField);
		root.add(daysBox, 0, i++);

		HBox catBox = new HBox(15);
		catBox.setAlignment(Pos.CENTER_LEFT);
		Label categories = new Label("Categories:");
		Label catList = new Label("");
		Button optionBtn = new Button("Add Category");
		catBox.getChildren().addAll(optionBtn, categories, catList);
		root.add(catBox, 0, i++, 2, 1);

		Set<String> cats = new HashSet<>();
		catMenu = getOptionMenu(cats, catList, null);

		optionBtn.setOnAction(e -> catMenu.show(optionBtn, Side.BOTTOM, 0, 0));

		Button addBtn = new Button("Add Auction");
		addBtn.setOnAction(e -> {
			errorLabel.setText("");
			functions.addAuction(errorLabel, nameTextField.getText(), descTextField.getText(),
				admin ? sellerTextField.getText() : currentUser, Integer.parseInt(minTextField.getText()),
				Integer.parseInt(daysTextField.getText()), cats);});

		Button menuBtn = new Button("Main Menu");
		menuBtn.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().addAll(addBtn, menuBtn, exitBtn);
		root.add(hbBtn, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getBidScreen(Integer id) {
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Bid On a Item:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++);

		HBox bidderBox = new HBox(95);
		bidderBox.setAlignment(Pos.CENTER);
		Label bidder = new Label("Bidder:");
		TextField bidderTextField = new TextField();
		bidderTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		bidderBox.getChildren().addAll(bidder, bidderTextField);

		if (admin) {
			root.add(bidderBox, 0, i++, 2, 1);
		}

		HBox idBox = new HBox(80);
		idBox.setAlignment(Pos.CENTER);
		Label idLabel = new Label("Auction ID:");
		TextField idTextField = new TextField();
		idTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		idBox.getChildren().addAll(idLabel, idTextField);
		root.add(idBox, 0, i++);

		if (id != null) {
			idTextField.setText(id.toString());
		}

		HBox amountBox = new HBox(70);
		amountBox.setAlignment(Pos.CENTER);
		Label amount = new Label("Bid Amount:");
		TextField amountTextField = new TextField();
		amountTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		amountBox.getChildren().addAll(amount, amountTextField);
		root.add(amountBox, 0, i++);

		Button bid = new Button("Bid");
		bid.setOnAction(e -> {
			errorLabel.setText("");
			functions.bidOnItem(errorLabel, Integer.parseInt(idTextField.getText()),
				Integer.parseInt(amountTextField.getText()), admin ? bidderTextField.getText() : currentUser);});

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(50);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(bid, main, exitBtn);
		root.add(buttons, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getCloseScreen(String user) {

		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Your Auctions: ");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		List<Product> products;

		if (admin) {
			if (user != null && !user.isEmpty())
				products = functions.getUsersProducts(errorLabel, user);
			else {
				products = new ArrayList<>();
			}

			HBox sellerBox = new HBox(30);
			Label seller = new Label("User:");
			TextField sellerTextField = new TextField();
			Button sellerBtn = new Button("Get Products For User");

			sellerBtn.setOnAction(e -> {
				errorLabel.setText("");
				getCloseScreen(sellerTextField.getText());});
			sellerTextField.setMaxWidth(TEXT_INPUT_LENGTH);
			sellerBox.getChildren().addAll(seller, sellerTextField, sellerBtn);
			sellerBox.setAlignment(Pos.CENTER);

			root.add(sellerBox, 0, 1, 2, 1);
		} else {
			products = functions.getUsersProducts(errorLabel, currentUser);
		}

		GridPane prodBox = new GridPane();
		prodBox.setId("pane");
		prodBox.setMinWidth(400);
		prodBox.setAlignment(Pos.CENTER);
		prodBox.setHgap(10);
		prodBox.setVgap(10);
		prodBox.setPadding(DEFAULT_INSET);
		prodBox.add(new Label("Auction ID"), 0, 0);
		prodBox.add(new Label("Name"), 1, 0);
		prodBox.add(new Label("Description"), 2, 0);
		prodBox.add(new Label("Amount"), 3, 0);
		prodBox.add(new Label("Status"), 4, 0);

		int i = 1;

		for (Product prod : products) {
			int j = 0;

			prodBox.add(new Label("" + prod.getAuctionId()), j++, i);
			prodBox.add(new Label(prod.getName()), j++, i);
			prodBox.add(new Label(prod.getDescription()), j++, i);
			prodBox.add(new Label("$" + prod.getAmount()), j++, i);
			prodBox.add(new Label(prod.getStatus()), j++, i);

			if (prod.getStatus().equals("under auction")) {
				Button withdraw = new Button("Withdraw");
				withdraw.setOnAction(e -> {
					errorLabel.setText("");
					functions.withdrawProduct(errorLabel, prod.getAuctionId());});
				prodBox.add(withdraw, j++, i);

				KeyValue bid2nd = functions.getSecondHighestBid(errorLabel, prod.getAuctionId());

				if (bid2nd != null) {
					Button sell = new Button("Sell for $" + bid2nd.getValue());
					sell.setOnAction(e -> {
						errorLabel.setText("");
						functions.sellProduct(errorLabel, prod.getAuctionId(), 
							bid2nd.getValue(), bid2nd.getKey());});
					prodBox.add(sell, j, i);
				}
			}

			i++;
		}

		ScrollPane prodPane = new ScrollPane();
		prodPane.setBackground(null);
		prodPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		prodPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		prodPane.setId("pane");
		prodPane.setContent(prodBox);
		root.add(prodPane, 0, 2, 2, 1);

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(main, exitBtn);
		root.add(buttons, 0, 3, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getSuggestionScreen() {
		List<Product> products = functions.provideSuggestions(errorLabel, currentUser);

		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Suggested Products:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		GridPane prodBox = new GridPane();
		prodBox.setId("pane");
		prodBox.setMinWidth(400);
		prodBox.setAlignment(Pos.CENTER);
		prodBox.setHgap(30);
		prodBox.setVgap(10);
		prodBox.setPadding(DEFAULT_INSET);
		prodBox.add(new Label("Auction ID"), 0, 0);
		prodBox.add(new Label("Name"), 1, 0);
		prodBox.add(new Label("Description"), 2, 0);
		prodBox.add(new Label("Amount"), 3, 0);

		int i = 1;

		for (Product prod : products) {
			int j = 0;

			prodBox.add(new Label("" + prod.getAuctionId()), j++, i);
			prodBox.add(new Label(prod.getName()), j++, i);
			prodBox.add(new Label(prod.getDescription()), j++, i);
			prodBox.add(new Label("" + prod.getAmount()), j, i++);
		}

		ScrollPane prodPane = new ScrollPane();
		prodPane.setBackground(null);
		prodPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		prodPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		prodPane.setId("pane");
		prodPane.setContent(prodBox);
		root.add(prodPane, 0, 2, 2, 1);

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(50);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(main, exitBtn);
		root.add(buttons, 0, 3, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getUpdateDateScreen() {
		Date currentDate = new Date(functions.getCurrentDate(errorLabel));

		GridPane root = makeRoot(10, 15);
		int i = 0;

		Text scenetitle = new Text("Current System Date is:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		Text dateDisplay = new Text(currentDate.toString());
		dateDisplay.setId(TITLE);
		root.add(dateDisplay, 0, i++, 2, 1);

		Label yearLabel = new Label("Year");
		Label monthLabel = new Label("Month");
		Label dayLabel = new Label("Day");
		Label hourLabel = new Label("Hour");
		Label minLabel = new Label("Min");
		Label secLabel = new Label("Sec");

		HBox labels = new HBox(40);
		labels.setAlignment(Pos.CENTER);
		labels.getChildren().addAll(yearLabel, monthLabel, dayLabel, hourLabel, minLabel, secLabel);
		root.add(labels, 0, i++, 2, 1);

		ChoiceBox<String> yearChoice = new ChoiceBox<>();
		yearChoice.setMaxHeight(200);
		yearChoice.setItems(FXCollections.observableArrayList("2018", "2019", "2020", "2021", "2022", "2023", "2025"));

		List<String> months = new ArrayList<>();

		for (int month = 1; month < 13; month++) {
			if (month < 10) {
				months.add("0" + month);
			} else {
				months.add("" + month);
			}
		}

		ChoiceBox<String> monthChoice = new ChoiceBox<>();
		monthChoice.setMaxHeight(200);
		monthChoice.setItems(FXCollections.observableArrayList(months));

		List<String> days = new ArrayList<>();

		for (int day = 1; day < 32; day++) {
			if (day < 10) {
				days.add("0" + day);
			} else {
				days.add("" + day);
			}
		}

		ChoiceBox<String> dayChoice = new ChoiceBox<>();
		dayChoice.setMaxHeight(200);
		dayChoice.setItems(FXCollections.observableArrayList(days));

		List<String> hours = new ArrayList<>();

		for (int hour = 0; hour < 25; hour++) {
			if (hour < 10) {
				hours.add("0" + hour);
			} else {
				hours.add("" + hour);
			}
		}

		ChoiceBox<String> hourChoice = new ChoiceBox<>();
		hourChoice.setMaxHeight(200);
		hourChoice.setItems(FXCollections.observableArrayList(hours));

		List<String> mins = new ArrayList<>();

		for (int min = 0; min < 60; min++) {
			if (min < 10) {
				mins.add("0" + min);
			} else {
				mins.add("" + min);
			}
		}

		ChoiceBox<String> minChoice = new ChoiceBox<>();
		minChoice.setMaxHeight(200);
		minChoice.setItems(FXCollections.observableArrayList(mins));

		List<String> secs = new ArrayList<>();

		for (int sec = 0; sec < 60; sec++) {
			if (sec < 10) {
				secs.add("0" + sec);
			} else {
				secs.add("" + sec);
			}
		}

		ChoiceBox<String> secChoice = new ChoiceBox<>();
		secChoice.setMaxHeight(200);
		secChoice.setItems(FXCollections.observableArrayList(secs));

		HBox datePicker = new HBox(15);
		datePicker.setAlignment(Pos.CENTER);
		datePicker.getChildren().addAll(yearChoice, monthChoice, dayChoice, hourChoice, minChoice, secChoice);
		root.add(datePicker, 0, i++, 2, 1);

		Button changeBtn = new Button("Change Date");
		changeBtn.setOnAction(e -> {
			errorLabel.setText("");
			functions.setDate(errorLabel, yearChoice.getValue() + "/" + monthChoice.getValue() + "/" 
					+ dayChoice.getValue() + " " + hourChoice.getValue() + ":" + minChoice.getValue() 
					+ ":" + secChoice.getValue());});

		Button menuBtn = new Button("Main Menu");
		menuBtn.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().addAll(changeBtn, menuBtn, exitBtn);
		root.add(hbBtn, 0, i++, 2, 1);

		errorLabel = new Text("");
		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getProductStatsScreen(String login) {
		List<Product> products;

		if (login == null) {
			products = functions.getStatsAllProducts(errorLabel);
		} else {
			products = functions.getStatsUserProducts(errorLabel, login);
		}

		GridPane root = makeRoot(10, 20);

		Text scenetitle = new Text("Product Stats");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, 0, 2, 1);

		HBox loginBox = new HBox(70);
		loginBox.setAlignment(Pos.CENTER);
		Label loginLabel = new Label("Filter by User");
		TextField loginTextField = new TextField();
		loginTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		Button loginBtn = new Button("Sort By Bid Amount");
		loginBtn.setOnAction(e -> {
			errorLabel.setText("");
			getProductStatsScreen(loginTextField.getText());});
		loginBox.getChildren().addAll(loginLabel, loginTextField, loginBtn);
		root.add(loginBox, 0, 1, 2, 1);

		GridPane prodBox = new GridPane();
		prodBox.setId("pane");
		prodBox.setMinWidth(400);
		prodBox.setAlignment(Pos.CENTER);
		prodBox.setHgap(30);
		prodBox.setVgap(10);
		prodBox.setPadding(DEFAULT_INSET);
		prodBox.add(new Label("Auction ID"), 0, 0);
		prodBox.add(new Label("Name"), 1, 0);
		prodBox.add(new Label("Description"), 2, 0);
		prodBox.add(new Label("Status"), 3, 0);
		prodBox.add(new Label("Highest Amount/Sell Price"), 4, 0);
		prodBox.add(new Label("Seller"), 5, 0);
		prodBox.add(new Label("Buyer"), 6, 0);

		int i = 1;

		for (Product prod : products) {
			int j = 0;

			prodBox.add(new Label("" + prod.getAuctionId()), j++, i);
			prodBox.add(new Label(prod.getName()), j++, i);
			prodBox.add(new Label(prod.getDescription()), j++, i);
			prodBox.add(new Label(prod.getStatus()), j++, i);
			prodBox.add(new Label("$" + prod.getAmount()), j++, i);
			prodBox.add(new Label(prod.getSeller()), j++, i);
			prodBox.add(new Label(prod.getBuyer()), j, i++);
		}

		ScrollPane prodPane = new ScrollPane();
		prodPane.setBackground(null);
		prodPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		prodPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		prodPane.setId("pane");
		prodPane.setContent(prodBox);
		root.add(prodPane, 0, 2, 2, 1);

		Button main = new Button("Main Menu");
		main.setOnAction(e -> {
			errorLabel.setText("");
			getMainMenu();});

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(main, exitBtn);
		root.add(buttons, 0, 3, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, 4, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getStatsScreen() {
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Statistics");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		HBox monthBox = new HBox(70);
		monthBox.setAlignment(Pos.CENTER);
		Label month = new Label("Enter # of Months Desired:");
		TextField monthTextField = new TextField();
		monthTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		monthBox.getChildren().addAll(month, monthTextField);
		root.add(monthBox, 0, i++, 2, 1);

		HBox countBox = new HBox(70);
		countBox.setAlignment(Pos.CENTER);
		Label count = new Label("Enter # of Results Desired:");
		TextField countTextField = new TextField();
		countTextField.setMaxWidth(TEXT_INPUT_LENGTH);
		countBox.getChildren().addAll(count, countTextField);
		root.add(countBox, 0, i++, 2, 1);

		Button volLeaf = new Button("Top Volume of Leaf Categories");
		volLeaf.setOnAction(e -> getVolLeafScreen(Integer.parseInt(monthTextField.getText()),
				Integer.parseInt(countTextField.getText())));
		volLeaf.setMinWidth(100);

		Button volRoot = new Button("Top Volume of Root Categories");
		volRoot.setOnAction(e -> getVolRootScreen(Integer.parseInt(monthTextField.getText()),
				Integer.parseInt(countTextField.getText())));
		volRoot.setMinWidth(100);

		Button bidders = new Button("Most Active Bidders");
		bidders.setOnAction(e -> getHighestBidderScreen(Integer.parseInt(monthTextField.getText()),
				Integer.parseInt(countTextField.getText())));
		bidders.setMinWidth(100);

		Button buyers = new Button("Most Active Buyers");
		buyers.setOnAction(e -> getHighestBuyerScreen(Integer.parseInt(monthTextField.getText()),
				Integer.parseInt(countTextField.getText())));
		buyers.setMinWidth(100);

		HBox firstButtons = new HBox(20);
		firstButtons.setAlignment(Pos.CENTER);
		firstButtons.getChildren().addAll(volLeaf, volRoot, bidders, buyers);
		root.add(firstButtons, 0, i++, 2, 1);

		Button main = new Button("Main Menu");
		main.setOnAction(e -> getMainMenu());

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(50);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(main, exitBtn);
		root.add(buttons, 0, i++, 2, 1);

		errorLabel = new Text("");
		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getVolLeafScreen(int months, int count) {
		List<KeyValue> list = functions.highestVolumeLeafs(errorLabel, months, count);
		
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Highest Volume Leaf Categories:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		VBox catBox = new VBox(20);
		catBox.setId("pane");
		catBox.setMinWidth(400);
		catBox.setPadding(DEFAULT_INSET);

		HBox labels = new HBox(20);
		labels.setAlignment(Pos.CENTER_RIGHT);
		labels.getChildren().addAll(new Label("Category"), new Label("Volume"));
		catBox.getChildren().add(labels);
		
		while (!list.isEmpty()) {
			KeyValue entry = list.remove(0);

			Label category = new Label(entry.getKey());
			Label volume = new Label(entry.getValue().toString());

			HBox selection = new HBox(70);
			selection.setAlignment(Pos.CENTER_RIGHT);
			selection.getChildren().addAll(category, volume);

			catBox.getChildren().add(selection);
		}

		ScrollPane catPane = new ScrollPane();
		catPane.setBackground(null);
		catPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		catPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		catPane.setMaxWidth(400);
		catPane.setId("pane");
		catPane.setContent(catBox);
		root.add(catPane, 0, i++, 2, 1);

		Button stats = new Button("Other Stats");
		stats.setOnAction(e -> getStatsScreen());

		Button main = new Button("Main Menu");
		main.setOnAction(e -> getMainMenu());

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(stats, main, exitBtn);
		root.add(buttons, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getVolRootScreen(int months, int count) {
		List<KeyValue> list = functions.highestVolumeRoots(errorLabel, months, count);

		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Highest Volume Root Categories:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		VBox catBox = new VBox(20);
		catBox.setId("pane");
		catBox.setMinWidth(400);
		catBox.setPadding(DEFAULT_INSET);

		HBox labels = new HBox(20);
		labels.setAlignment(Pos.CENTER_RIGHT);
		labels.getChildren().addAll(new Label("Category"), new Label("Volume"));
		catBox.getChildren().add(labels);
		
		while (!list.isEmpty()) {
			KeyValue entry = list.remove(0);

			Label category = new Label(entry.getKey());
			Label volume = new Label(entry.getValue().toString());

			HBox selection = new HBox(70);
			selection.setAlignment(Pos.CENTER_RIGHT);
			selection.getChildren().addAll(category, volume);

			catBox.getChildren().add(selection);
		}

		ScrollPane catPane = new ScrollPane();
		catPane.setBackground(null);
		catPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		catPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		catPane.setMaxWidth(400);
		catPane.setId("pane");
		catPane.setContent(catBox);
		root.add(catPane, 0, i++, 2, 1);

		Button stats = new Button("Other Stats");
		stats.setOnAction(e -> getStatsScreen());

		Button main = new Button("Main Menu");
		main.setOnAction(e -> getMainMenu());

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(stats, main, exitBtn);
		root.add(buttons, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getHighestBidderScreen(int months, int count) {
		List<KeyValue> list = functions.mostActiveBidders(errorLabel, months, count);
		
		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Most Active Bidders:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		VBox catBox = new VBox(20);
		catBox.setId("pane");
		catBox.setMinWidth(300);
		catBox.setPadding(DEFAULT_INSET);

		HBox labels = new HBox(20);
		labels.setAlignment(Pos.CENTER_RIGHT);
		labels.getChildren().addAll(new Label("Category"), new Label("Bids"));
		catBox.getChildren().add(labels);
		
		while (!list.isEmpty()) {
			KeyValue entry = list.remove(0);

			Label category = new Label(entry.getKey());
			Label volume = new Label(entry.getValue().toString());

			HBox selection = new HBox(40);
			selection.setAlignment(Pos.CENTER_RIGHT);
			selection.getChildren().addAll(category, volume);

			catBox.getChildren().add(selection);
		}

		ScrollPane catPane = new ScrollPane();
		catPane.setBackground(null);
		catPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		catPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		catPane.setMaxWidth(300);
		catPane.setMinWidth(300);
		catPane.setId("pane");
		catPane.setContent(catBox);
		root.add(catPane, 0, i++, 2, 1);

		Button stats = new Button("Other Stats");
		stats.setOnAction(e -> getStatsScreen());

		Button main = new Button("Main Menu");
		main.setOnAction(e -> getMainMenu());

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(stats, main, exitBtn);
		root.add(buttons, 0, i++, 2, 1);

		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	public void getHighestBuyerScreen(int months, int count) {
		List<KeyValue> list = functions.mostActiveBuyers(errorLabel, months, count);

		GridPane root = makeRoot(10, 20);
		int i = 0;

		Text scenetitle = new Text("Most Active Buyers:");
		scenetitle.setId(TITLE);
		root.add(scenetitle, 0, i++, 2, 1);

		VBox catBox = new VBox(20);
		catBox.setId("pane");
		catBox.setMinWidth(300);
		catBox.setPadding(DEFAULT_INSET);

		HBox labels = new HBox(20);
		labels.setAlignment(Pos.CENTER_RIGHT);
		labels.getChildren().addAll(new Label("Category"), new Label("Spent"));
		catBox.getChildren().add(labels);
		
		while (!list.isEmpty()) {
			KeyValue entry = list.remove(0);

			Label category = new Label(entry.getKey());
			Label volume = new Label(entry.getValue().toString());

			HBox selection = new HBox(50);
			selection.setAlignment(Pos.CENTER_RIGHT);
			selection.getChildren().addAll(category, volume);

			catBox.getChildren().add(selection);
		}

		ScrollPane catPane = new ScrollPane();
		catPane.setBackground(null);
		catPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		catPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		catPane.setMaxWidth(300);
		catPane.setMinWidth(300);
		catPane.setId("pane");
		catPane.setContent(catBox);
		root.add(catPane, 0, i++, 2, 1);

		Button stats = new Button("Other Stats");
		stats.setOnAction(e -> getStatsScreen());

		Button main = new Button("Main Menu");
		main.setOnAction(e -> getMainMenu());

		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(e -> System.exit(0));

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(stats, main, exitBtn);
		root.add(buttons, 0, i++, 2, 1);

		errorLabel = new Text("");
		errorLabel.setId("error");
		root.add(errorLabel, 0, i, 2, 1);
		GridPane.setHalignment(errorLabel, HPos.CENTER);

		makeStage(root);
	}

	private ContextMenu getOptionMenu(Set<String> build, Label label, String parent) {
		ContextMenu menu = new ContextMenu();

		Set<String> cats = functions.getRootCategories(errorLabel);

		for (String cat : cats) {
			if (functions.isLeafCategory(errorLabel, cat)) {
				MenuItem item = new MenuItem(cat);

				item.setOnAction(e -> {
					build.add(cat);
					label.setText("");
					catMenu = getOptionMenu(build, label, null);

					for (String s : build) {
						label.setText(label.getText() + s + ", ");
					}

					label.setText(label.getText().substring(0, label.getText().length() - 1));
				});

				menu.getItems().add(item);
			} else {
				menu.getItems().add(getSubMenu(build, label, cat));
			}
		}

		return menu;
	}

	private Menu getSubMenu(Set<String> build, Label label, String parent) {
		Menu menu = new Menu(parent);

		Set<String> cats = functions.getSubCategories(errorLabel, parent);

		for (String cat : cats) {
			if (functions.isLeafCategory(errorLabel, cat)) {
				MenuItem item = new MenuItem(cat);

				item.setOnAction(e -> {
					build.add(cat);
					label.setText("");
					catMenu = getOptionMenu(build, label, null);

					for (String s : build) {
						label.setText(label.getText() + s + ", ");
					}

					label.setText(label.getText().substring(0, label.getText().length() - 1));
				});

				menu.getItems().add(item);
			} else {
				menu.getItems().add(getSubMenu(build, label, cat));
			}
		}

		return menu;
	}

	public void signInUser(String username, String password) {
		admin = false;

		if (functions.login(errorLabel, admin, username, password)) {
			currentUser = username;
			errorLabel.setText("");
			getMainMenu();
		} else {
			currentUser = "";
			errorLabel.setText("Invalid Login Credentials!");
			getWelcomeScreen();
		}
	}

	public void signInAdmin(String username, String password) {
		admin = true;

		if (functions.login(errorLabel, admin, username, password)) {
			currentUser = username;
			errorLabel.setText("");
			getMainMenu();
		} else {
			admin = false;
			currentUser = "";
			errorLabel.setText("Invalid Login Credentials!");
			getWelcomeScreen();
		}
	}

	public void registerUser(String username, String name, String address, String email, String pass, String pass2) {

		if (pass.equals(pass2)) {
			if (functions.addCustomer(errorLabel, username, pass, name, address, email)) {
				if (admin) {
					errorLabel.setText("Successfully Registered User!");
				} else {
					errorLabel.setText("");
					getMainMenu();
				}
			}
		} else {
			errorLabel.setText("Passwords do not match!");
		}
	}

	public void registerAdmin(String username, String name, String address, String email, String pass, String pass2) {

		if (pass.equals(pass2)) {
			if (functions.addCustomer(errorLabel, username, pass, name, address, email)) {
				if (admin) {
					errorLabel.setText("Successfully Registered Admin!");
				} else {
					getMainMenu();
				}
			}
		} else {
			errorLabel.setText("Passwords do not match!");
		}
	}

	private GridPane makeRoot(double hGap, double vGap) {
		GridPane root = new GridPane();

		root.setAlignment(Pos.CENTER);
		root.setHgap(hGap);
		root.setVgap(vGap);
		root.setPadding(DEFAULT_INSET);

		return root;
	}
}
