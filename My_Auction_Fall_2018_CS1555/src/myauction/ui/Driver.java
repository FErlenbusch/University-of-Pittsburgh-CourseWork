package myauction.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
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
import myauction.data_models.Product;


// javafx.scene.text.Text object
//getText()

public class Driver {


	public static void main(String[] args) {
    System.out.println("Hello World");
    openDBConnection("cws24", "3831607");
	}

  public static void runDriver() {
    // openDBConnection
    String user = "cws24";
    String password = "3831607";
    
  }

	public static void openDBConnection(String user, String password) {
		Functions functions = new Functions(user, password);
		System.out.println("Hey Connie");
	}

  public void logInAdmin(String username, String password) {
    //boolean wasSuccessful = functions.login(error, true, username, password);
  }

  public void logInUser(String username, String password) {
    //boolean wasSuccessful = functions.login(error, false, username, password);
  }

  public void addNewCustomer(String login, String pass, String name, String address, String email) {
    //boolean wasSuccessful = functions.addCustomer(error, login, pass, name, address, email);
  }

  public void addNewAdmin(String login, String pass, String name, String address, String email) {
    //boolean wasSuccessful = functions.addAdministrator(error, login, pass, name, address, email);
  }

  public void getProdCount(String category, int months) {

  }

}
