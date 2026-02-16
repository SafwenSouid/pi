package controllers;

import entities.Utilisateur;
import services.ServiceUtilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class PatientEditProfileController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNaissanceField;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordStatusLabel;

    @FXML private Button uploadImageButton;
    @FXML private Button removeImageButton;
    @FXML private Label imageFileNameLabel;
    @FXML private Label avatarInitials;
    @FXML private ImageView profileImageView;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    @FXML private Label emailErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmErrorLabel;
    @FXML private Label ageErrorLabel;

    private File profileImageFile;
    private Utilisateur currentUser;
    private SessionManager sessionManager = SessionManager.getInstance();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        // Cacher les labels d'erreur
        hideErrorLabels();

        // Récupérer l'utilisateur connecté
        currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
        }

        // Configuration des validateurs
        setupValidation();

        // Charger la photo de profil
        loadProfileImage();
    }

    private void hideErrorLabels() {
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);
        phoneErrorLabel.setVisible(false);
        phoneErrorLabel.setManaged(false);
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);
        confirmErrorLabel.setVisible(false);
        confirmErrorLabel.setManaged(false);
        ageErrorLabel.setVisible(false);
        ageErrorLabel.setManaged(false);
        passwordStatusLabel.setVisible(false);
        passwordStatusLabel.setManaged(false);
    }

    private void loadUserData() {
        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
            telephoneField.setText(currentUser.getNumeroTel());

            if (currentUser.getDateNaissance() != null) {
                dateNaissanceField.setValue(currentUser.getDateNaissance());
            }

            // Initiales pour l'avatar
            String initials = currentUser.getPrenom().substring(0, 1).toUpperCase() +
                    currentUser.getNom().substring(0, 1).toUpperCase();
            avatarInitials.setText(initials);
        }
    }

    private void loadProfileImage() {
        try {
            String uploadDir = "uploads/profiles/";
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif"};
            File imageFile = null;

            for (String ext : extensions) {
                imageFile = new File(uploadDir + currentUser.getIdUser() + ext);
                if (imageFile.exists()) {
                    break;
                }
                imageFile = null;
            }

            if (imageFile != null && imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
                profileImageView.setVisible(true);
                avatarInitials.setVisible(false);
                imageFileNameLabel.setText(imageFile.getName());
            } else {
                profileImageView.setVisible(false);
                avatarInitials.setVisible(true);
                imageFileNameLabel.setText("Aucune photo");
            }
        } catch (Exception e) {
            profileImageView.setVisible(false);
            avatarInitials.setVisible(true);
        }
    }

    private void setupValidation() {
        // ========== VALIDATION EMAIL EN TEMPS RÉEL ==========
        emailField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.isEmpty()) {
                emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                emailErrorLabel.setText("L'email est obligatoire");
                emailErrorLabel.setVisible(true);
                emailErrorLabel.setManaged(true);
            } else if (!newValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                emailErrorLabel.setText("Format d'email invalide");
                emailErrorLabel.setVisible(true);
                emailErrorLabel.setManaged(true);
            } else if (newValue.length() > 150) {
                emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                emailErrorLabel.setText("L'email ne peut pas dépasser 150 caractères");
                emailErrorLabel.setVisible(true);
                emailErrorLabel.setManaged(true);
            } else {
                emailField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                emailErrorLabel.setVisible(false);
                emailErrorLabel.setManaged(false);
            }
        });

        // ========== VALIDATION TÉLÉPHONE EN TEMPS RÉEL ==========
        telephoneField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.isEmpty()) {
                telephoneField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                phoneErrorLabel.setText("Le téléphone est obligatoire");
                phoneErrorLabel.setVisible(true);
                phoneErrorLabel.setManaged(true);
            } else if (!newValue.matches("^[0-9+\\s-]{10,}$")) {
                telephoneField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                phoneErrorLabel.setText("Format invalide (10 chiffres minimum)");
                phoneErrorLabel.setVisible(true);
                phoneErrorLabel.setManaged(true);
            } else {
                telephoneField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                phoneErrorLabel.setVisible(false);
                phoneErrorLabel.setManaged(false);
            }
        });

        // ========== VALIDATION DATE NAISSANCE ==========
        dateNaissanceField.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                ageErrorLabel.setText("La date de naissance est obligatoire");
                ageErrorLabel.setVisible(true);
                ageErrorLabel.setManaged(true);
            } else {
                LocalDate birthDate = newValue;
                LocalDate today = LocalDate.now();
                int age = Period.between(birthDate, today).getYears();

                if (age < 18) {
                    dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    ageErrorLabel.setText("Vous devez avoir au moins 18 ans");
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

        // ========== VALIDATION NOUVEAU MOT DE PASSE ==========
        newPasswordField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                if (newValue.length() < 6) {
                    newPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    passwordErrorLabel.setText("Minimum 6 caractères");
                    passwordErrorLabel.setVisible(true);
                    passwordErrorLabel.setManaged(true);
                } else if (!newValue.matches(".*[A-Z].*")) {
                    newPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    passwordErrorLabel.setText("Doit contenir une majuscule");
                    passwordErrorLabel.setVisible(true);
                    passwordErrorLabel.setManaged(true);
                } else if (!newValue.matches(".*[0-9].*")) {
                    newPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    passwordErrorLabel.setText("Doit contenir un chiffre");
                    passwordErrorLabel.setVisible(true);
                    passwordErrorLabel.setManaged(true);
                } else {
                    newPasswordField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                    passwordErrorLabel.setVisible(false);
                    passwordErrorLabel.setManaged(false);
                }
            } else {
                newPasswordField.setStyle("");
                passwordErrorLabel.setVisible(false);
                passwordErrorLabel.setManaged(false);
            }
            validatePasswordConfirmation();
        });

        // ========== VALIDATION CONFIRMATION MOT DE PASSE ==========
        confirmPasswordField.textProperty().addListener((obs, old, newValue) -> {
            validatePasswordConfirmation();
        });

        // ========== VALIDATION NOM ==========
        nomField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty() && newValue.length() >= 2) {
                nomField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
            } else if (!newValue.isEmpty()) {
                nomField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            } else {
                nomField.setStyle("");
            }
        });

        // ========== VALIDATION PRÉNOM ==========
        prenomField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty() && newValue.length() >= 2) {
                prenomField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
            } else if (!newValue.isEmpty()) {
                prenomField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            } else {
                prenomField.setStyle("");
            }
        });
    }

    private void validatePasswordConfirmation() {
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (!newPass.isEmpty() && !confirmPass.isEmpty()) {
            if (newPass.equals(confirmPass)) {
                confirmPasswordField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                confirmErrorLabel.setVisible(false);
                confirmErrorLabel.setManaged(false);
            } else {
                confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                confirmErrorLabel.setText("Les mots de passe ne correspondent pas");
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
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());

        if (file != null) {
            if (file.length() > 2 * 1024 * 1024) {
                showAlert(Alert.AlertType.ERROR, "Fichier trop volumineux",
                        "L'image ne doit pas dépasser 2 Mo.");
                return;
            }

            profileImageFile = file;
            imageFileNameLabel.setText(file.getName());
            imageFileNameLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: 600;");

            // Afficher l'image
            Image image = new Image(file.toURI().toString());
            profileImageView.setImage(image);
            profileImageView.setVisible(true);
            avatarInitials.setVisible(false);
        }
    }

    @FXML
    private void handleRemoveImage() {
        profileImageFile = null;
        profileImageView.setImage(null);
        profileImageView.setVisible(false);
        avatarInitials.setVisible(true);
        imageFileNameLabel.setText("Aucune photo");
        imageFileNameLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-style: italic;");

        // Supprimer le fichier
        deleteProfileImage(currentUser.getIdUser());
    }

    @FXML
    private void handleSave() {
        // Réinitialiser les styles
        resetStyles();

        // VALIDATION COMPLÈTE
        if (!validateAllFields()) {
            return;
        }

        try {
            // Vérifier l'ancien mot de passe si un nouveau est fourni
            if (!newPasswordField.getText().isEmpty()) {
                if (!verifyCurrentPassword()) {
                    showAlert(Alert.AlertType.ERROR, "Mot de passe incorrect",
                            "Le mot de passe actuel est incorrect.");
                    currentPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    return;
                }
            }

            // Vérifier si l'email existe déjà (sauf si c'est le même)
            if (!emailField.getText().trim().equalsIgnoreCase(currentUser.getEmail())) {
                if (isEmailExists(emailField.getText().trim())) {
                    showAlert(Alert.AlertType.ERROR, "Email déjà utilisé",
                            "Cet email est déjà associé à un autre compte.");
                    emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    return;
                }
            }

            // Mettre à jour les informations
            currentUser.setNom(nomField.getText().trim().toUpperCase());
            currentUser.setPrenom(prenomField.getText().trim());
            currentUser.setEmail(emailField.getText().trim().toLowerCase());
            currentUser.setNumeroTel(formatTelephone(telephoneField.getText().trim()));
            currentUser.setDateNaissance(dateNaissanceField.getValue());

            // Mettre à jour le mot de passe si changé
            if (!newPasswordField.getText().isEmpty()) {
                currentUser.setMotDePasse(newPasswordField.getText());
            }

            // Sauvegarder dans la base de données
            serviceUtilisateur.modifier(currentUser);

            // Upload de l'image si changée
            if (profileImageFile != null) {
                uploadProfileImage(currentUser.getIdUser());
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Votre profil a été mis à jour avec succès !");

            // Retour au dashboard
            handleBack();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean verifyCurrentPassword() {
        return currentUser.getMotDePasse().equals(currentPasswordField.getText());
    }

    private boolean validateAllFields() {
        // Validation Nom
        if (nomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "Le nom est obligatoire.");
            nomField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (nomField.getText().trim().length() < 2) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom doit contenir au moins 2 caractères.");
            nomField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation Prénom
        if (prenomField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "Le prénom est obligatoire.");
            prenomField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (prenomField.getText().trim().length() < 2) {
            showAlert(Alert.AlertType.ERROR, "Prénom invalide", "Le prénom doit contenir au moins 2 caractères.");
            prenomField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation Email
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "L'email est obligatoire.");
            emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Format d'email invalide.");
            emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation Téléphone
        String tel = telephoneField.getText().trim();
        if (tel.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "Le téléphone est obligatoire.");
            telephoneField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!tel.matches("^[0-9+\\s-]{10,}$")) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Format de téléphone invalide (10 chiffres minimum).");
            telephoneField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation Date de naissance
        if (dateNaissanceField.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "La date de naissance est obligatoire.");
            dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        LocalDate birthDate = dateNaissanceField.getValue();
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();

        if (age < 18) {
            showAlert(Alert.AlertType.ERROR, "Âge minimum", "Vous devez avoir au moins 18 ans.");
            dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (age > 120) {
            showAlert(Alert.AlertType.ERROR, "Âge invalide", "Veuillez vérifier votre date de naissance.");
            dateNaissanceField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation changement mot de passe
        if (!newPasswordField.getText().isEmpty()) {
            // Vérifier que l'ancien mot de passe est fourni
            if (currentPasswordField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe requis",
                        "Veuillez entrer votre mot de passe actuel.");
                currentPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return false;
            }

            String password = newPasswordField.getText();
            if (password.length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe trop court",
                        "Le mot de passe doit contenir au moins 6 caractères.");
                newPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return false;
            }
            if (!password.matches(".*[A-Z].*")) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe faible",
                        "Le mot de passe doit contenir au moins une majuscule.");
                newPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return false;
            }
            if (!password.matches(".*[0-9].*")) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe faible",
                        "Le mot de passe doit contenir au moins un chiffre.");
                newPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return false;
            }

            // Validation confirmation
            if (confirmPasswordField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Confirmation requise",
                        "Veuillez confirmer votre nouveau mot de passe.");
                confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return false;
            }
            if (!password.equals(confirmPasswordField.getText())) {
                showAlert(Alert.AlertType.ERROR, "Mots de passe différents",
                        "Les mots de passe ne correspondent pas.");
                confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                return false;
            }
        }

        return true;
    }

    private boolean isEmailExists(String email) throws SQLException {
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getIdUser() != currentUser.getIdUser()) {
                return true;
            }
        }
        return false;
    }

    private String formatTelephone(String telephone) {
        return telephone.replaceAll("\\s+", "").replaceAll("-", "").replaceAll("\\+", "00");
    }

    private void uploadProfileImage(int userId) {
        try {
            String uploadDir = "uploads/profiles/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Supprimer les anciennes images
            deleteProfileImage(userId);

            // Sauvegarder la nouvelle image
            String extension = profileImageFile.getName().substring(
                    profileImageFile.getName().lastIndexOf("."));
            String newFileName = userId + extension;
            String filePath = uploadDir + newFileName;

            Files.copy(profileImageFile.toPath(),
                    new File(filePath).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteProfileImage(int userId) {
        try {
            String uploadDir = "uploads/profiles/";
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif"};

            for (String ext : extensions) {
                File file = new File(uploadDir + userId + ext);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon espace - MENSOS");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        handleBack();
    }

    private void resetStyles() {
        nomField.setStyle("");
        prenomField.setStyle("");
        emailField.setStyle("");
        telephoneField.setStyle("");
        dateNaissanceField.setStyle("");
        currentPasswordField.setStyle("");
        newPasswordField.setStyle("");
        confirmPasswordField.setStyle("");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}