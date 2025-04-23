package application;

import javafx.scene.control.TextField;

public interface ManageRoutesPageInterface {
    void show();
    void addRoute(TextField sourceField, TextField destinationField, TextField distanceField, String mode, TextField costField, TextField trafficField);
    void clearFields(TextField sourceField, TextField destinationField, TextField distanceField, TextField costField, TextField trafficField);
    void showAlert(javafx.scene.control.Alert.AlertType alertType, String title, String message);
}
