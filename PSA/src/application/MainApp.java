package application;

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class MainApp extends Application {

	private UserManager userManager = new UserManager();
	private RouteManager routeManager;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Journey Planner Application");

		// Initialize the ComboBoxes
		ComboBox<String> sourceComboBox = new ComboBox<>();
		ComboBox<String> destinationComboBox = new ComboBox<>();
		sourceComboBox.setPrefWidth(500);
		destinationComboBox.setPrefWidth(500);

		routeManager = new RouteManager(sourceComboBox, destinationComboBox);
		addInitialRoutes();
		routeManager.loadRoutesFromFile();

		// UI elements for source, destination, mode of transportation, and route
		// selection
		Label sourceLabel = new Label("Source:");
		sourceComboBox.getItems().addAll(routeManager.getSourceLocations());

		Label destinationLabel = new Label("Destination:");
		destinationComboBox.getItems().addAll(routeManager.getDestinationLocations());

		Label modeLabel = new Label("Mode of Transportation:");
		ComboBox<String> modeComboBox = new ComboBox<>();
		modeComboBox.getItems().addAll("Bus", "Car", "Heavy Vehicles");
		modeComboBox.setPrefWidth(500);

		Label routeLabel = new Label("Select Route:");
		ComboBox<String> routeComboBox = new ComboBox<>();
		routeComboBox.getItems().addAll("Shortest Route", "Least Traffic Route", "Cost-Optimal Route");
		routeComboBox.setPrefWidth(500);

		Button searchRouteButton = new Button("Search Route");
		Button loginButton = new Button("Login");
		Button registerButton = new Button("Register");

		TextArea resultArea = new TextArea();
		resultArea.setEditable(false);

		// Layout for the form (excluding Search button)
		VBox formLayout = new VBox(10, sourceLabel, sourceComboBox, destinationLabel, destinationComboBox, modeLabel,
				modeComboBox, routeLabel, routeComboBox);
		formLayout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

		// Separate layout for the Search button to center it
		HBox searchButtonLayout = new HBox(searchRouteButton);
		searchButtonLayout.setAlignment(Pos.CENTER);
		searchButtonLayout.setPadding(new Insets(0, 0, 10, 0));

		// Layout for Login and Register buttons
		HBox buttonLayout = new HBox(10, loginButton, registerButton);
		buttonLayout.setAlignment(Pos.TOP_RIGHT);
		buttonLayout.setStyle("-fx-padding: 10 0 0 0;");

		VBox topLayout = new VBox(10, buttonLayout);
		topLayout.setPadding(new Insets(10, 20, 20, 20));

		// Combine formLayout and searchButtonLayout
		VBox centerLayout = new VBox(10, formLayout, searchButtonLayout);
		centerLayout.setAlignment(Pos.TOP_CENTER);

		// BorderPane to organize the overall layout
		BorderPane mainLayout = new BorderPane();
		mainLayout.setTop(topLayout);
		mainLayout.setCenter(centerLayout);
		mainLayout.setBottom(resultArea);

		// Scene setup
		Scene mainScene = new Scene(mainLayout, 500, 600);
		primaryStage.setScene(mainScene);
		primaryStage.show();

		// Action Handlers
		loginButton.setOnAction(e -> openLoginPage());
		registerButton.setOnAction(e -> openRegisterPage());

		// Action handler for the Search Route button
		searchRouteButton.setOnAction(e -> {
			// Validate inputs
			String source = sourceComboBox.getValue();
			String destination = destinationComboBox.getValue();
			String mode = modeComboBox.getValue();
			String route = routeComboBox.getValue();

			if (source == null || source.isEmpty()) {
				showAlert("Error", "Please select a source.");
			} else if (destination == null || destination.isEmpty()) {
				showAlert("Error", "Please select a destination.");
			} else if (mode == null || mode.isEmpty()) {
				showAlert("Error", "Please select a mode of transportation.");
			} else if (route == null || route.isEmpty()) {
				showAlert("Error", "Please select a route.");
			} else {
				searchRouteWithFixedIntermediates(source, destination, route, mode, resultArea);
			}
		});
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void openManageRoutesPage() {
		ManageRoutesPage manageRoutesPage = new ManageRoutesPage(routeManager);
		manageRoutesPage.show();
	}

	// Helper method to display alerts
	private void showAlert1(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void addInitialRoutes() {

		// East Lexington - Burlington
		routeManager.addRoute(new Route("Burlington", "East Lexington", 5.0, "Car", 10.0, 6.0), true);
		routeManager.addRoute(new Route("East Lexington", "Burlington", 5.0, "Bus", 1.0, 6.0), true);
		routeManager.addRoute(new Route("East Lexington", "Alewife", 4.0, "Car", 8.0, 5.0), true);
		routeManager.addRoute(new Route("Alewife", "East Lexington", 4.0, "Bus", 8.0, 5.0), true);

		// Davis - Alewife
		routeManager.addRoute(new Route("Alewife", "Davis", 2.5, "Bus", 3.0, 4.0), true);
		routeManager.addRoute(new Route("Davis", "Alewife", 2.5, "Car", 3.0, 4.0), true);

		// Davis - Porter
		routeManager.addRoute(new Route("Davis", "Porter", 2.0, "Car", 4.0, 7.0), true);
		routeManager.addRoute(new Route("Porter", "Davis", 2.0, "Heavy Vehicles", 4.0, 7.0), true);

		// Porter - Science Park
		routeManager.addRoute(new Route("Porter", "Science Park", 6.0, "Heavy Vehicles", 15.0, 10.0), true);
		routeManager.addRoute(new Route("Science Park", "Porter", 6.0, "Bus", 15.0, 10.0), true);

		// Alewife - Porter
		routeManager.addRoute(new Route("Alewife", "Porter", 3.0, "Bus", 5.0, 3.0), true);
		routeManager.addRoute(new Route("Porter", "Alewife", 3.0, "Car", 5.0, 3.0), true);

		// Union Square - Brickbottom
		routeManager.addRoute(new Route("Union Sq", "Brickbottom", 1.5, "By walk", 1.0, 18.0), true);
		routeManager.addRoute(new Route("Brickbottom", "Union Sq", 1.5, "By walk", 1.0, 18.0), true);

		// Brickbottom - Haymarket
		routeManager.addRoute(new Route("Brickbottom", "Haymarket", 3.5, "Bus", 4.0, 7.0), true);
		routeManager.addRoute(new Route("Haymarket", "Brickbottom", 3.5, "Car", 4.0, 7.0), true);

		// Danvers - Salem
		routeManager.addRoute(new Route("Danvers", "Salem", 5.5, "Bus", 6.0, 11.0), true);
		routeManager.addRoute(new Route("Salem", "Danvers", 5.5, "Car", 6.0, 11.0), true);

		// Lynn - Riverworks
		routeManager.addRoute(new Route("Lynn", "Riverworks", 4.0, "Car", 8.0, 4.0), true);
		routeManager.addRoute(new Route("Riverworks", "Lynn", 4.0, "Heavy Vehicles", 8.0, 4.0), true);

		// Inter-line connections
		routeManager.addRoute(new Route("Porter", "Union Sq", 4.5, "Bus", 5.0, 1.0), true);
		routeManager.addRoute(new Route("Union Sq", "Porter", 4.5, "Car", 5.0, 1.0), true);
		routeManager.addRoute(new Route("Haymarket", "Oak Island", 7.5, "Car", 12.0, 4.0), true);
		routeManager.addRoute(new Route("Oak Island", "Haymarket", 7.5, "By walk", 12.0, 4.0), true);

		// Additional routes
		routeManager.addRoute(new Route("Oak Island", "Riverworks", 2.5, "Bus", 2.0, 6.0), true);
		routeManager.addRoute(new Route("Riverworks", "Oak Island", 2.5, "Bus", 2.0, 6.0), true);

		routeManager.addRoute(new Route("East Lexington", "Central Square", 6.0, "Car", 14.0, 8.0), true);
		routeManager.addRoute(new Route("Central Square", "East Lexington", 6.0, "Bus", 14.0, 8.0), true);

		routeManager.addRoute(new Route("Union Sq", "Central Square", 3.5, "Car", 7.0, 4.5), true);
		routeManager.addRoute(new Route("Central Square", "Union Sq", 3.5, "Bus", 7.0, 4.5), true);

		routeManager.addRoute(new Route("Lynn", "Salem", 3.0, "Car", 5.0, 2.5), true);
		routeManager.addRoute(new Route("Salem", "Lynn", 3.0, "Bus", 5.0, 2.5), true);

		routeManager.addRoute(new Route("Porter", "Harvard Square", 2.0, "Car", 3.5, 2.0), true);
		routeManager.addRoute(new Route("Harvard Square", "Porter", 2.0, "Bus", 3.5, 2.0), true);

		routeManager.addRoute(new Route("Harvard Square", "Central Square", 1.5, "Car", 2.0, 5.0), true);
		routeManager.addRoute(new Route("Central Square", "Harvard Square", 1.5, "Bus", 2.0, 5.0), true);

		routeManager.addRoute(new Route("Harvard Square", "Kendall Square", 2.5, "Car", 4.0, 3.5), true);
		routeManager.addRoute(new Route("Kendall Square", "Harvard Square", 2.5, "Bus", 4.0, 3.5), true);

		routeManager.addRoute(new Route("Kendall Square", "Alewife", 4.0, "Car", 6.0, 3.0), true);
		routeManager.addRoute(new Route("Alewife", "Kendall Square", 4.0, "Bus", 6.0, 3.0), true);

		routeManager.addRoute(new Route("Brickbottom", "Union Sq", 1.5, "Bus", 1.0, 5.0), true);
		routeManager.addRoute(new Route("Union Sq", "Brickbottom", 1.5, "Bus", 1.0, 5.0), true);

		// Burlington - East Lexington
		routeManager.addRoute(new Route("Burlington", "East Lexington", 5.0, "Heavy Vehicles", 15.0, 8.0), true);
		routeManager.addRoute(new Route("East Lexington", "Burlington", 5.0, "Heavy Vehicles", 15.0, 8.0), true);

		// East Lexington - Alewife
		routeManager.addRoute(new Route("East Lexington", "Alewife", 4.0, "Heavy Vehicles", 10.0, 6.0), true);
		routeManager.addRoute(new Route("Alewife", "East Lexington", 4.0, "Heavy Vehicles", 10.0, 6.0), true);

		// Davis - Alewife
		routeManager.addRoute(new Route("Alewife", "Davis", 2.5, "Heavy Vehicles", 6.0, 5.0), true);
		routeManager.addRoute(new Route("Davis", "Alewife", 2.5, "Heavy Vehicles", 6.0, 5.0), true);

		// Davis - Porter
		routeManager.addRoute(new Route("Davis", "Porter", 2.0, "Heavy Vehicles", 5.5, 8.0), true);
		routeManager.addRoute(new Route("Porter", "Davis", 2.0, "Heavy Vehicles", 5.5, 8.0), true);

		// Porter - Science Park
		routeManager.addRoute(new Route("Porter", "Science Park", 6.0, "Heavy Vehicles", 18.0, 12.0), true);
		routeManager.addRoute(new Route("Science Park", "Porter", 6.0, "Heavy Vehicles", 18.0, 12.0), true);

		// Alewife - Porter
		routeManager.addRoute(new Route("Alewife", "Porter", 3.0, "Heavy Vehicles", 7.0, 4.0), true);
		routeManager.addRoute(new Route("Porter", "Alewife", 3.0, "Heavy Vehicles", 7.0, 4.0), true);

		// Brickbottom - Haymarket
		routeManager.addRoute(new Route("Brickbottom", "Haymarket", 3.5, "Heavy Vehicles", 6.0, 8.0), true);
		routeManager.addRoute(new Route("Haymarket", "Brickbottom", 3.5, "Heavy Vehicles", 6.0, 8.0), true);

		// Danvers - Salem
		routeManager.addRoute(new Route("Danvers", "Salem", 5.5, "Heavy Vehicles", 9.0, 12.0), true);
		routeManager.addRoute(new Route("Salem", "Danvers", 5.5, "Heavy Vehicles", 9.0, 12.0), true);

		// Lynn - Riverworks
		routeManager.addRoute(new Route("Lynn", "Riverworks", 4.0, "Heavy Vehicles", 10.0, 5.0), true);
		routeManager.addRoute(new Route("Riverworks", "Lynn", 4.0, "Heavy Vehicles", 10.0, 5.0), true);
		routeManager.addRoute(new Route("Science Park", "Salem", 11.0, "Heavy Vehicles", 25.0, 14.0), true);

		// Inter-line connections
		routeManager.addRoute(new Route("Porter", "Union Sq", 4.5, "Heavy Vehicles", 7.5, 2.0), true);
		routeManager.addRoute(new Route("Union Sq", "Porter", 4.5, "Heavy Vehicles", 7.5, 2.0), true);
		routeManager.addRoute(new Route("Haymarket", "Oak Island", 7.5, "Heavy Vehicles", 15.0, 6.0), true);
		routeManager.addRoute(new Route("Oak Island", "Haymarket", 7.5, "Heavy Vehicles", 15.0, 6.0), true);

		// Additional routes
		routeManager.addRoute(new Route("Oak Island", "Porter", 2.5, "Heavy Vehicles", 4.0, 8.0), true);
		routeManager.addRoute(new Route("Central Square", "Oak Island", 2.5, "Heavy Vehicles", 4.0, 8.0), true);

		routeManager.addRoute(new Route("East Lexington", "Central Square", 6.0, "Heavy Vehicles", 18.0, 10.0), true);
		routeManager.addRoute(new Route("Central Square", "East Lexington", 6.0, "Heavy Vehicles", 18.0, 10.0), true);

		routeManager.addRoute(new Route("Union Sq", "Central Square", 3.5, "Heavy Vehicles", 9.0, 6.0), true);
		routeManager.addRoute(new Route("Central Square", "Union Sq", 3.5, "Heavy Vehicles", 9.0, 6.0), true);

		routeManager.addRoute(new Route("Lynn", "Salem", 3.0, "Heavy Vehicles", 7.0, 4.0), true);
		routeManager.addRoute(new Route("Salem", "Lynn", 3.0, "Heavy Vehicles", 7.0, 4.0), true);

		routeManager.addRoute(new Route("Porter", "Harvard Square", 2.0, "Heavy Vehicles", 4.5, 3.0), true);
		routeManager.addRoute(new Route("Harvard Square", "Porter", 2.0, "Heavy Vehicles", 4.5, 3.0), true);

		routeManager.addRoute(new Route("Harvard Square", "Central Square", 1.5, "Heavy Vehicles", 3.0, 6.0), true);
		routeManager.addRoute(new Route("Central Square", "Harvard Square", 1.5, "Heavy Vehicles", 3.0, 6.0), true);

		routeManager.addRoute(new Route("Harvard Square", "Kendall Square", 2.5, "Heavy Vehicles", 5.5, 4.5), true);
		routeManager.addRoute(new Route("Kendall Square", "Harvard Square", 2.5, "Heavy Vehicles", 5.5, 4.5), true);

		routeManager.addRoute(new Route("Kendall Square", "Alewife", 4.0, "Heavy Vehicles", 8.0, 5.0), true);
		routeManager.addRoute(new Route("Alewife", "Kendall Square", 4.0, "Heavy Vehicles", 8.0, 5.0), true);

		routeManager.addRoute(new Route("Brickbottom", "Union Sq", 1.5, "Heavy Vehicles", 2.0, 6.0), true);
		routeManager.addRoute(new Route("Union Sq", "Brickbottom", 1.5, "Heavy Vehicles", 2.0, 6.0), true);

		routeManager.addRoute(new Route("Science Park", "Riverworks", 7.0, "Bus", 9.0, 6.5), true);
		routeManager.addRoute(new Route("Riverworks", "Science Park", 7.0, "Bus", 9.0, 6.5), true);

		// New locations and expanded routes

		// Cambridgeport - East Lexington
		routeManager.addRoute(new Route("Cambridgeport", "East Lexington", 8.0, "Car", 16.0, 9.0), true);
		routeManager.addRoute(new Route("Cambridgeport", "East Lexington", 8.0, "Bus", 14.0, 8.5), true);
		routeManager.addRoute(new Route("Cambridgeport", "East Lexington", 8.0, "Heavy Vehicles", 18.0, 10.5), true);

		// Cambridgeport - Kendall Square
		routeManager.addRoute(new Route("Cambridgeport", "Kendall Square", 2.5, "Car", 5.0, 3.0), true);
		routeManager.addRoute(new Route("Cambridgeport", "Kendall Square", 2.5, "Bus", 4.5, 2.5), true);
		routeManager.addRoute(new Route("Cambridgeport", "Kendall Square", 2.5, "Heavy Vehicles", 6.0, 3.5), true);

		// Medford - Arlington
		routeManager.addRoute(new Route("Medford", "Arlington", 5.5, "Car", 11.0, 7.0), true);
		routeManager.addRoute(new Route("Medford", "Arlington", 5.5, "Bus", 10.0, 6.5), true);
		routeManager.addRoute(new Route("Medford", "Arlington", 5.5, "Heavy Vehicles", 13.0, 7.5), true);

		// Medford - Somerville
		routeManager.addRoute(new Route("Medford", "Somerville", 4.0, "Car", 8.0, 5.0), true);
		routeManager.addRoute(new Route("Medford", "Somerville", 4.0, "Bus", 7.5, 4.5), true);
		routeManager.addRoute(new Route("Medford", "Somerville", 4.0, "Heavy Vehicles", 10.0, 5.5), true);

		// Somerville - Cambridgeport
		routeManager.addRoute(new Route("Somerville", "Cambridgeport", 6.5, "Car", 13.0, 7.5), true);
		routeManager.addRoute(new Route("Somerville", "Cambridgeport", 6.5, "Bus", 12.0, 7.0), true);
		routeManager.addRoute(new Route("Somerville", "Cambridgeport", 6.5, "Heavy Vehicles", 15.0, 8.5), true);

		// Arlington - Newton
		routeManager.addRoute(new Route("Arlington", "Newton", 9.0, "Car", 18.0, 10.0), true);
		routeManager.addRoute(new Route("Arlington", "Newton", 9.0, "Bus", 16.0, 9.5), true);
		routeManager.addRoute(new Route("Arlington", "Newton", 9.0, "Heavy Vehicles", 20.0, 11.5), true);

		// Newton - Davis
		routeManager.addRoute(new Route("Newton", "Davis", 7.5, "Car", 15.0, 8.5), true);
		routeManager.addRoute(new Route("Newton", "Davis", 7.5, "Bus", 14.0, 8.0), true);
		routeManager.addRoute(new Route("Newton", "Davis", 7.5, "Heavy Vehicles", 18.0, 9.5), true);

		// Newton - Kendall Square
		routeManager.addRoute(new Route("Newton", "Kendall Square", 10.0, "Car", 20.0, 11.0), true);
		routeManager.addRoute(new Route("Newton", "Kendall Square", 10.0, "Bus", 18.0, 10.5), true);
		routeManager.addRoute(new Route("Newton", "Kendall Square", 10.0, "Heavy Vehicles", 22.0, 12.5), true);

		// Somerville - Kendall Square
		routeManager.addRoute(new Route("Somerville", "Kendall Square", 3.0, "Car", 6.0, 3.5), true);
		routeManager.addRoute(new Route("Somerville", "Kendall Square", 3.0, "Bus", 5.5, 3.0), true);
		routeManager.addRoute(new Route("Somerville", "Kendall Square", 3.0, "Heavy Vehicles", 7.0, 4.0), true);

		// Somerville - Brickbottom
		routeManager.addRoute(new Route("Somerville", "Brickbottom", 2.0, "Car", 4.0, 2.5), true);
		routeManager.addRoute(new Route("Somerville", "Brickbottom", 2.0, "Bus", 3.5, 2.0), true);
		routeManager.addRoute(new Route("Somerville", "Brickbottom", 2.0, "Heavy Vehicles", 5.0, 3.0), true);

		// Medford - Newton
		routeManager.addRoute(new Route("Medford", "Newton", 8.0, "Car", 16.0, 9.0), true);
		routeManager.addRoute(new Route("Medford", "Newton", 8.0, "Bus", 14.0, 8.5), true);
		routeManager.addRoute(new Route("Medford", "Newton", 8.0, "Heavy Vehicles", 18.0, 10.0), true);

		// Arlington - East Lexington
		routeManager.addRoute(new Route("Arlington", "East Lexington", 4.5, "Car", 9.0, 5.0), true);
		routeManager.addRoute(new Route("Arlington", "East Lexington", 4.5, "Bus", 8.0, 4.5), true);
		routeManager.addRoute(new Route("Arlington", "East Lexington", 4.5, "Heavy Vehicles", 10.0, 6.0), true);

		routeManager.addRoute(new Route("Science Park", "Somerville", 5.0, "Heavy Vehicles", 12.0, 8.0), true);
		routeManager.addRoute(new Route("Somerville", "Science Park", 5.0, "Heavy Vehicles", 12.0, 8.0), true);

		// Burlington - Cambridgeport
		routeManager.addRoute(new Route("Burlington", "Cambridgeport", 10.0, "Car", 20.0, 12.0), true);
		routeManager.addRoute(new Route("Burlington", "Cambridgeport", 10.0, "Bus", 18.0, 11.5), true);
		routeManager.addRoute(new Route("Burlington", "Cambridgeport", 10.0, "Heavy Vehicles", 24.0, 13.0), true);

		// Arlington - Harvard Square
		routeManager.addRoute(new Route("Arlington", "Harvard Square", 6.5, "Car", 13.0, 8.0), true);
		routeManager.addRoute(new Route("Arlington", "Harvard Square", 6.5, "Bus", 12.0, 7.5), true);
		routeManager.addRoute(new Route("Arlington", "Harvard Square", 6.5, "Heavy Vehicles", 15.0, 9.0), true);

		// Medford - Kendall Square
		routeManager.addRoute(new Route("Medford", "Kendall Square", 7.0, "Car", 14.0, 8.5), true);
		routeManager.addRoute(new Route("Medford", "Kendall Square", 7.0, "Bus", 13.0, 8.0), true);
		routeManager.addRoute(new Route("Medford", "Kendall Square", 7.0, "Heavy Vehicles", 16.0, 9.5), true);

		// East Lexington - Brickbottom
		routeManager.addRoute(new Route("East Lexington", "Brickbottom", 5.5, "Car", 11.0, 6.5), true);
		routeManager.addRoute(new Route("East Lexington", "Brickbottom", 5.5, "Bus", 10.5, 6.0), true);
		routeManager.addRoute(new Route("East Lexington", "Brickbottom", 5.5, "Heavy Vehicles", 13.0, 7.5), true);

		// Salem - Somerville
		routeManager.addRoute(new Route("Salem", "Somerville", 9.5, "Car", 19.0, 10.5), true);
		routeManager.addRoute(new Route("Salem", "Somerville", 9.5, "Bus", 17.0, 9.5), true);
		routeManager.addRoute(new Route("Salem", "Somerville", 9.5, "Heavy Vehicles", 22.0, 11.5), true);

		// Science Park - Newton
		routeManager.addRoute(new Route("Science Park", "Newton", 12.0, "Car", 24.0, 14.0), true);
		routeManager.addRoute(new Route("Science Park", "Newton", 12.0, "Bus", 22.0, 13.0), true);
		routeManager.addRoute(new Route("Science Park", "Newton", 12.0, "Heavy Vehicles", 28.0, 15.5), true);

		// Lynn - Kendall Square
		routeManager.addRoute(new Route("Lynn", "Kendall Square", 8.0, "Car", 16.0, 9.0), true);
		routeManager.addRoute(new Route("Lynn", "Kendall Square", 8.0, "Bus", 14.0, 8.5), true);
		routeManager.addRoute(new Route("Lynn", "Kendall Square", 8.0, "Heavy Vehicles", 20.0, 10.5), true);

		// Oak Island - Davis
		routeManager.addRoute(new Route("Oak Island", "Davis", 7.0, "Car", 14.0, 8.0), true);
		routeManager.addRoute(new Route("Oak Island", "Davis", 7.0, "Bus", 13.0, 7.5), true);
		routeManager.addRoute(new Route("Oak Island", "Davis", 7.0, "Heavy Vehicles", 18.0, 9.5), true);

		// Arlington - Somerville
		routeManager.addRoute(new Route("Arlington", "Somerville", 4.5, "Car", 9.0, 5.5), true);
		routeManager.addRoute(new Route("Arlington", "Somerville", 4.5, "Bus", 8.5, 5.0), true);
		routeManager.addRoute(new Route("Arlington", "Somerville", 4.5, "Heavy Vehicles", 11.0, 6.0), true);

		// Harvard Square - Riverworks
		routeManager.addRoute(new Route("Harvard Square", "Riverworks", 10.0, "Car", 20.0, 11.0), true);
		routeManager.addRoute(new Route("Harvard Square", "Riverworks", 10.0, "Bus", 18.0, 10.5), true);
		routeManager.addRoute(new Route("Harvard Square", "Riverworks", 10.0, "Heavy Vehicles", 24.0, 12.5), true);
	}

	private void searchRouteWithFixedIntermediates(String source, String destination, String selectedRoute,
			String vehicleType, TextArea resultArea) {
		if (source.isEmpty() || destination.isEmpty() || selectedRoute == null || vehicleType == null) {
			showAlert1(Alert.AlertType.ERROR, "Search Failed", "Please fill in all fields.");
		}

		else {
			// Define two fixed intermediate nodes
			List<String> intermediateList = Arrays.asList("East Lexington", "Science Park");

			if ("Shortest Route".equals(selectedRoute)) {
				ShortestRoute shortestRouteFinder = new ShortestRoute(routeManager);
				List<String> path = shortestRouteFinder.findShortestRoute(source, intermediateList, destination,
						vehicleType);
				path = trimPathToDestination(path, destination);
				if (!path.isEmpty() && path.get(path.size() - 1).equals(destination)) {
					displayRouteResultGetShortestPath(resultArea, source, destination, vehicleType, path,
							shortestRouteFinder.getRouteTraffic(path, vehicleType),
							shortestRouteFinder.getRouteDistance(path, vehicleType),
							shortestRouteFinder.getRouteCost(path, vehicleType), "Shortest");
				} else {
					resultArea.setText("No route found.");
				}
			} else if ("Cost-Optimal Route".equals(selectedRoute)) {
				CostOptimalRoute costOptimalRouteFinder = new CostOptimalRoute(routeManager);
				List<String> path = costOptimalRouteFinder.findCostOptimalRoute(source, intermediateList, destination,
						vehicleType);
				path = trimPathToDestination(path, destination);
				if (!path.isEmpty() && path.get(path.size() - 1).equals(destination)) {
					displayRouteResultGetCostOptimal(resultArea, source, destination, vehicleType, path,
							costOptimalRouteFinder.getRouteTraffic(path, vehicleType),
							costOptimalRouteFinder.getRouteDistance(path, vehicleType),
							costOptimalRouteFinder.getRouteCost(path, vehicleType), "Cost-Optimal");
				} else {
					resultArea.setText("No route found.");
				}
			} else if ("Least Traffic Route".equals(selectedRoute)) {
				LeastTrafficRoute leastTrafficRouteFinder = new LeastTrafficRoute(routeManager);
				List<String> path = leastTrafficRouteFinder.findLeastTrafficRoute(source, intermediateList, destination,
						vehicleType);
				path = trimPathToDestination(path, destination);
				if (!path.isEmpty() && path.get(path.size() - 1).equals(destination)) {
					displayRouteResultLeastTraffic(resultArea, source, destination, vehicleType, path,
							leastTrafficRouteFinder.getRouteTraffic(path, vehicleType),
							leastTrafficRouteFinder.getRouteDistance(path, vehicleType),
							leastTrafficRouteFinder.getRouteCost(path, vehicleType), "Least Traffic");
				} else {
					resultArea.setText("No route found.");
				}
			} else {
				resultArea.setText("Route type not supported yet.");
			}

		}
	}

	private List<String> trimPathToDestination(List<String> path, String destination) {
		if (path.contains(destination)) {
			return path.subList(0, path.indexOf(destination) + 1);
		}
		return path;
	}

	private void displayRouteResultGetShortestPath(TextArea resultArea, String source, String destination,
			String vehicleType, List<String> path, double traffic, double distance, double cost, String routeType) {
		if (path.isEmpty()) {
			resultArea.setText("No route found.");
			return;
		}

		StringBuilder result = new StringBuilder(routeType + " route from ").append(source).append(" to ")
				.append(destination).append(" for ").append(vehicleType).append(":\n").append("- Distance: ")
				.append(distance).append(" km\n").append("- Total Cost: $").append(cost).append("\n")
				.append("- Traffic Time: ").append(traffic).append(" hrs\n").append("Route path: ");

		for (int i = 0; i < path.size(); i++) {
			result.append(path.get(i));
			if (i < path.size() - 1)
				result.append(" -> ");
		}

		resultArea.setWrapText(true);
		resultArea.setText(result.toString());
	}

	private void displayRouteResultGetCostOptimal(TextArea resultArea, String source, String destination,
			String vehicleType, List<String> path, double traffic, double distance, double cost, String routeType) {
		if (path.isEmpty()) {
			resultArea.setText("No route found.");
			return;
		}

		StringBuilder result = new StringBuilder(routeType + " route from ").append(source).append(" to ")
				.append(destination).append(" for ").append(vehicleType).append(":\n").append("- Total Cost: $")
				.append(cost).append("\n").append("- Distance: ").append(distance).append(" km\n")
				.append("- Traffic Time: ").append(traffic).append(" hrs\n").append("Route path: ");

		for (int i = 0; i < path.size(); i++) {
			result.append(path.get(i));
			if (i < path.size() - 1)
				result.append(" -> ");
		}

		resultArea.setWrapText(true);
		resultArea.setText(result.toString());
	}

	private void displayRouteResultLeastTraffic(TextArea resultArea, String source, String destination,
			String vehicleType, List<String> path, double routeTraffic, double routeDistance, double routeCost,
			String routeType) {
		if (path.isEmpty()) {
			resultArea.setText("No route found.");
		} else {

			StringBuilder result = new StringBuilder(routeType + " route from ").append(source).append(" to ")
					.append(destination).append(" for ").append(vehicleType).append(":\n")

					.append("- Traffic Time: ").append(routeTraffic).append(" hrs").append("\n").append("- Distance: ")
					.append(routeDistance).append(" km ").append("\n").append("- Total Cost: $").append(routeCost)
					.append("\n").append("Route path: ");

			for (int i = 0; i < path.size(); i++) {
				result.append(path.get(i));
				if (i < path.size() - 1)
					result.append(" -> ");
			}
			resultArea.setWrapText(true);
			resultArea.setText(result.toString());
		}
	}

	private void openLoginPage() {

		// Define a callback for successful login
		Runnable onLoginSuccess = this::openManageRoutesPage;

		// Create a new LoginPage instance
		LoginPage loginPage = new LoginPage(userManager, onLoginSuccess);

		// Show the login page
		loginPage.show();
	}

	private void openRegisterPage() {
		// Create a new RegisterPage instance
		RegisterPage registerPage = new RegisterPage(userManager);

		// Show the register page
		registerPage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
