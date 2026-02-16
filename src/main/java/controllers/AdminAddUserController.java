package controllers;

import entities.Utilisateur;
import services.ServiceUtilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class AdminAddUserController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNaissanceField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField motDePasseField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox actifCheckBox;

    @FXML private Label emailErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label ageErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmErrorLabel;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private AdminDashboardController adminController;
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("patient", "psy", "coach de vie", "admin");
        roleComboBox.setValue("patient");
        actifCheckBox.setSelected(true);
        dateNaissanceField.setValue(LocalDate.now().minusYears(30));
        setupValidation();
        hideErrorLabels();
    }

    public void setAdminController(AdminDashboardController controller) {
        this.adminController = controller;
    }

    private void hideErrorLabels() {
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);
        phoneErrorLabel.setVisible(false);
        phoneErrorLabel.setManaged(false);
        ageErrorLabel.setVisible(false);
        ageErrorLabel.setManaged(false);
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);
        confirmErrorLabel.setVisible(false);
        confirmErrorLabel.setManaged(false);
    }

    private void setupValidation() {
        // Validation email
        emailField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                emailErrorLabel.setVisible(true);
                emailErrorLabel.setManaged(true);
            } else if (!newValue.isEmpty()) {
                emailField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                emailErrorLabel.setVisible(false);
                emailErrorLabel.setManaged(false);
            } else {
                emailField.setStyle("");
                emailErrorLabel.setVisible(false);
                emailErrorLabel.setManaged(false);
            }
        });

        // Validation téléphone
        telephoneField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^[0-9+\\s-]{10,}$")) {
                telephoneField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                phoneErrorLabel.setVisible(true);
                phoneErrorLabel.setManaged(true);
            } else if (!newValue.isEmpty()) {
                telephoneField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                phoneErrorLabel.setVisible(false);
                phoneErrorLabel.setManaged(false);
            } else {
                telephoneField.setStyle("");
                phoneErrorLabel.setVisible(false);
                phoneErrorLabel.setManaged(false);
            }
        });

        // Validation date naissance
        dateNaissanceField.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue != null) {
                int age = Period.between(newValue, LocalDate.now()).getYears();
                if (age < 18) {
                    dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    ageErrorLabel.setText("Âge minimum 18 ans");
                    ageErrorLabel.setVisible(true);
                    ageErrorLabel.setManaged(true);
                } else if (age > 120) {
                    dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    ageErrorLabel.setText("Âge invalide");
                    ageErrorLabel.setVisible(true);
                    ageErrorLabel.setManaged(true);
                } else {
                    dateNaissanceField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                    ageErrorLabel.setVisible(false);
                    ageErrorLabel.setManaged(false);
                }
            }
        });

        // Validation mot de passe
        motDePasseField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                if (newValue.length() < 6) {
                    motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    passwordErrorLabel.setText("Minimum 6 caractères");
                    passwordErrorLabel.setVisible(true);
                    passwordErrorLabel.setManaged(true);
                } else if (!newValue.matches(".*[A-Z].*")) {
                    motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    passwordErrorLabel.setText("Doit contenir une majuscule");
                    passwordErrorLabel.setVisible(true);
                    passwordErrorLabel.setManaged(true);
                } else if (!newValue.matches(".*[0-9].*")) {
                    motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    passwordErrorLabel.setText("Doit contenir un chiffre");
                    passwordErrorLabel.setVisible(true);
                    passwordErrorLabel.setManaged(true);
                } else {
                    motDePasseField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                    passwordErrorLabel.setVisible(false);
                    passwordErrorLabel.setManaged(false);
                }
            } else {
                motDePasseField.setStyle("");
                passwordErrorLabel.setVisible(false);
                passwordErrorLabel.setManaged(false);
            }
            validatePasswordConfirmation();
        });

        // Validation confirmation
        confirmPasswordField.textProperty().addListener((obs, old, newValue) -> {
            validatePasswordConfirmation();
        });
    }

    private void validatePasswordConfirmation() {
        String pass = motDePasseField.getText();
        String confirm = confirmPasswordField.getText();

        if (!pass.isEmpty() && !confirm.isEmpty()) {
            if (pass.equals(confirm)) {
                confirmPasswordField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                confirmErrorLabel.setVisible(false);
                confirmErrorLabel.setManaged(false);
            } else {
                confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                confirmErrorLabel.setVisible(true);
                confirmErrorLabel.setManaged(true);
            }
        } else {
            confirmPasswordField.setStyle("");
            confirmErrorLabel.setVisible(false);
            confirmErrorLabel.setManaged(false);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }

        try {
            // Vérifier si l'email existe déjà
            if (isEmailExists(emailField.getText().trim())) {
                showAlert(Alert.AlertType.ERROR, "Email déjà utilisé",
                        "Cet email est déjà associé à un compte.");
                emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return;
            }

            // Créer le nouvel utilisateur
            Utilisateur user = new Utilisateur(
                    nomField.getText().trim().toUpperCase(),
                    prenomField.getText().trim(),
                    emailField.getText().trim().toLowerCase(),
                    telephoneField.getText().trim(),
                    dateNaissanceField.getValue(),
                    roleComboBox.getValue(),
                    motDePasseField.getText()
            );
            user.setActif(actifCheckBox.isSelected());

            serviceUtilisateur.ajouter(user);

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "L'utilisateur a été ajouté avec succès.");

            // Fermer la fenêtre
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ajouter l'utilisateur: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Validation nom
        if (nomField.getText().trim().isEmpty() || nomField.getText().trim().length() < 2) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom doit contenir au moins 2 caractères.");
            return false;
        }

        // Validation prénom
        if (prenomField.getText().trim().isEmpty() || prenomField.getText().trim().length() < 2) {
            showAlert(Alert.AlertType.ERROR, "Prénom invalide", "Le prénom doit contenir au moins 2 caractères.");
            return false;
        }

        // Validation email
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Email requis", "L'email est obligatoire.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Format d'email invalide.");
            return false;
        }

        // Validation téléphone
        String tel = telephoneField.getText().trim();
        if (tel.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Téléphone requis", "Le téléphone est obligatoire.");
            return false;
        }
        if (!tel.matches("^[0-9+\\s-]{10,}$")) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Format de téléphone invalide (10 chiffres minimum).");
            return false;
        }

        // Validation date naissance
        if (dateNaissanceField.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Date requise", "La date de naissance est obligatoire.");
            return false;
        }

        LocalDate birthDate = dateNaissanceField.getValue();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            showAlert(Alert.AlertType.ERROR, "Âge minimum", "L'utilisateur doit avoir au moins 18 ans.");
            return false;
        }

        // Validation rôle
        if (roleComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Rôle requis", "Veuillez sélectionner un rôle.");
            return false;
        }

        // Validation mot de passe
        String password = motDePasseField.getText();
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe requis", "Le mot de passe est obligatoire.");
            return false;
        }
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe trop court", "Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe faible", "Le mot de passe doit contenir au moins une majuscule.");
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe faible", "Le mot de passe doit contenir au moins un chiffre.");
            return false;
        }

        // Validation confirmation
        if (confirmPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Confirmation requise", "Veuillez confirmer le mot de passe.");
            return false;
        }
        if (!password.equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Confirmation", "Les mots de passe ne correspondent pas.");
            return false;
        }

        return true;
    }

    private boolean isEmailExists(String email) throws SQLException {
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}