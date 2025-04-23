package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {
	private UserManager userManager;
	private Runnable onLoginSuccess;

	public LoginPage(UserManager userManager, Runnable onLoginSuccess) {
		this.userManager = userManager;
		this.onLoginSuccess = onLoginSuccess;
	}

	public void show() {
		Stage loginStage = new Stage();
		loginStage.setTitle("Login Page");

		// UI elements for login
		Label usernameLabel = new Label("Username:");
		TextField usernameField = new TextField();

		Label passwordLabel = new Label("Password:");
		PasswordField passwordField = new PasswordField();

		Button loginButton = new Button("Login");

		// Layout setup
		VBox loginLayout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton);
		loginLayout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

		Scene loginScene = new Scene(loginLayout, 300, 200);
		loginStage.setScene(loginScene);
		loginStage.show();

		// Login button action
		loginButton.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();

			if (userManager.authenticateUser(username, password)) {
				loginStage.close();
				onLoginSuccess.run();
			} else {
				showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
			}
		});
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
