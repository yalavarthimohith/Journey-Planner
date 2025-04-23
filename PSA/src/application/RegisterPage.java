package application;

import java.util.regex.Pattern;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {
	private UserManager userManager;

	public RegisterPage(UserManager userManager) {
		this.userManager = userManager;
	}

	public void show() {
		Stage registerStage = new Stage();
		registerStage.setTitle("Register Page");

		// UI elements for registration
		Label usernameLabel = new Label("Username:");
		TextField usernameField = new TextField();

		Label emailLabel = new Label("Email:");
		TextField emailField = new TextField();

		Label passwordLabel = new Label("Password:");
		PasswordField passwordField = new PasswordField();

		Label confirmPasswordLabel = new Label("Confirm Password:");
		PasswordField confirmPasswordField = new PasswordField();

		Button registerButton = new Button("Register");

		Region spacer = new Region();
		spacer.setPrefHeight(30);

		// Layout setup
		VBox registerLayout = new VBox(10, usernameLabel, usernameField, emailLabel, emailField, passwordLabel,
				passwordField, confirmPasswordLabel, confirmPasswordField, registerButton);
		registerLayout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

		Scene registerScene = new Scene(registerLayout, 300, 320);
		registerStage.setScene(registerScene);
		registerStage.show();

		// Register button action
		registerButton.setOnAction(e -> {
			String username = usernameField.getText();
			String email = emailField.getText();
			String password = passwordField.getText();
			String confirmPassword = confirmPasswordField.getText();

			// Validate input fields
			if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
			} else if (!validateUsername(username)) {
				showAlert(Alert.AlertType.ERROR, "Validation Error",
						"Username must be at least 3 characters long and contain only letters and numbers.");
			} else if (!validateEmail(email)) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address.");
			} else if (!validatePassword(password)) {
				showAlert(Alert.AlertType.ERROR, "Validation Error",
						"Password must be at least 5 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character.");
			} else if (!password.equals(confirmPassword)) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
			} else {
				boolean success = userManager.registerUser(username, email, password);

				if (success) {
					showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
							"You can now log in with your new account.");
					registerStage.close();
				} else {
					showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username or email already exists.");
				}
			}
		});
	}

	// Username validation - must be at least 3 characters and contain only letters
	// and numbers
	private boolean validateUsername(String username) {
		return username.matches("[a-zA-Z0-9]{3,}");
	}

	// Email validation - simple regex pattern for valid email
	private boolean validateEmail(String email) {
		String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
		return Pattern.matches(emailRegex, email);
	}

	// Password validation - must include uppercase, lowercase, digit, special char,
	// and be at least 5 characters long
	private boolean validatePassword(String password) {
		String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{5,}$";
		return Pattern.matches(passwordRegex, password);
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
