
package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ManageRoutesPage {
	private RouteManager routeManager;

	public ManageRoutesPage(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

	public void show() {
		Stage manageRoutesStage = new Stage();
		manageRoutesStage.setTitle("Add New Route");

		// UI elements for source, destination, mode of transportation, and traffic
		Label sourceLabel = new Label("Source:");
		TextField sourceField = new TextField();

		Label destinationLabel = new Label("Destination:");
		TextField destinationField = new TextField();

		Label distanceLabel = new Label("Distance (km):");
		TextField distanceField = new TextField();

		Label modeLabel = new Label("Mode of Transportation:");
		ComboBox<String> modeComboBox = new ComboBox<>();
		modeComboBox.getItems().addAll("Bus", "Car", "Heavy Vehicles");

		Label costLabel = new Label("Cost:");
		TextField costField = new TextField();

		Label trafficLabel = new Label("Traffic (hours):");
		TextField trafficField = new TextField();

		Button addRouteButton = new Button("Add Route");
		Button logoutButton = new Button("Logout");

		VBox formLayout = new VBox(10, sourceLabel, sourceField, destinationLabel, destinationField, distanceLabel,
				distanceField, modeLabel, modeComboBox, costLabel, costField, trafficLabel, trafficField);
		formLayout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

		VBox buttonLayout = new VBox(10, addRouteButton);
		buttonLayout.setAlignment(Pos.CENTER);
		buttonLayout.setPadding(new Insets(10, 0, 20, 0));

		// Layout for logout button (top-right)
		HBox logoutLayout = new HBox(logoutButton);
		logoutLayout.setAlignment(Pos.TOP_RIGHT);
		logoutLayout.setStyle("-fx-padding: 10 20 10 20;");

		// Combine everything into a VBox for the layout
		VBox mainLayout = new VBox(10, logoutLayout, formLayout, buttonLayout);

		// Scene setup
		Scene mainScene = new Scene(mainLayout, 500, 600);
		manageRoutesStage.setScene(mainScene);
		manageRoutesStage.show();

		// Action Handlers
		addRouteButton.setOnAction(e -> addRoute(sourceField, destinationField, distanceField, modeComboBox.getValue(),
				costField, trafficField));

		logoutButton.setOnAction(e -> manageRoutesStage.close());
	}

	// Method to handle route addition logic
	private void addRoute(TextField sourceField, TextField destinationField, TextField distanceField, String mode,
			TextField costField, TextField trafficField) {
		try {
			// Validate and parse inputs
			double distanceValue = Double.parseDouble(distanceField.getText());
			double costValue = Double.parseDouble(costField.getText());
			double trafficValue = Double.parseDouble(trafficField.getText());

			// Check for empty fields
			if (sourceField.getText().isEmpty() || destinationField.getText().isEmpty() || mode == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
				return;
			}

			// Create and add the route
			Route newRoute = new Route(sourceField.getText(), destinationField.getText(), distanceValue, mode,
					costValue, trafficValue);
			routeManager.addRoute(newRoute, true);
			showAlert(Alert.AlertType.INFORMATION, "Success", "Route added successfully!");

			// Clear fields after adding route
			clearFields(sourceField, destinationField, distanceField, costField, trafficField);

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numeric values.");
		}
	}

	// Method to clear fields
	private void clearFields(TextField sourceField, TextField destinationField, TextField distanceField,
			TextField costField, TextField trafficField) {
		sourceField.clear();
		destinationField.clear();
		distanceField.clear();
		costField.clear();
		trafficField.clear();
	}

	// Utility method to show alerts
	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
