package controllers;

import entities.Cv;
import entities.Utilisateur;
import services.ServiceCv;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class RegisterController {

    @FXML private TextField prenomField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField numeroTelField;
    @FXML private DatePicker dateNaissanceField;
    @FXML private PasswordField motDePasseField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private VBox patientCard;
    @FXML private VBox coachCard;
    @FXML private VBox psyCard;
    @FXML private VBox cvUploadPanel;

    @FXML private Button uploadCVButton;
    @FXML private Label cvFileNameLabel;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;

    // ✅ AJOUTER CES DEUX LIGNES POUR LE LOGO
    @FXML private ImageView leftLogoImageView;
    @FXML private ImageView rightLogoImageView;

    private File cvFile;
    private String selectedRole = "patient";
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private ServiceCv serviceCv = new ServiceCv();

    @FXML
    public void initialize() {
        selectRolePatient();
        cvUploadPanel.setVisible(false);
        cvUploadPanel.setManaged(false);
        dateNaissanceField.setPromptText("jj/mm/aaaa");

        // ✅ AJOUTER CETTE LIGNE POUR CHARGER LE LOGO
        loadLogo();

        // Validation en temps réel
        setupRealTimeValidation();
    }

    // ✅ AJOUTER CETTE MÉTHODE COMPLÈTE
    private void loadLogo() {
        try {
            String userHome = System.getProperty("user.home");
            String logoPath = userHome + "\\Desktop\\logo.png";  // Chemin vers votre logo sur le bureau

            File logoFile = new File(logoPath);

            if (logoFile.exists()) {
                Image logo = new Image(logoFile.toURI().toString());

                if (leftLogoImageView != null) {
                    leftLogoImageView.setImage(logo);
                }

                if (rightLogoImageView != null) {
                    rightLogoImageView.setImage(logo);
                }

                System.out.println("✅ Logo chargé depuis: " + logoPath);
            } else {
                System.err.println("❌ Fichier logo non trouvé: " + logoPath);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du logo: " + e.getMessage());
        }
    }

    private void setupRealTimeValidation() {
        // Email validation en direct
        emailField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                if (newValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    emailField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                } else {
                    emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                }
            }
        });

        // Téléphone validation
        numeroTelField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                if (newValue.matches("^[0-9+\\s-]{10,}$")) {
                    numeroTelField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                } else {
                    numeroTelField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                }
            }
        });

        // Mot de passe validation
        motDePasseField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                if (newValue.length() >= 6) {
                    motDePasseField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                } else {
                    motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                }
            }
        });

        // Confirmation mot de passe
        confirmPasswordField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty() && !motDePasseField.getText().isEmpty()) {
                if (newValue.equals(motDePasseField.getText())) {
                    confirmPasswordField.setStyle("-fx-border-color: #27AE60; -fx-border-width: 2;");
                } else {
                    confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                }
            }
        });
    }

    @FXML
    private void selectRolePatient() {
        selectedRole = "patient";
        updateRoleCardStyles();
        cvUploadPanel.setVisible(false);
        cvUploadPanel.setManaged(false);
    }

    @FXML
    private void selectRoleCoach() {
        selectedRole = "coach de vie";
        updateRoleCardStyles();
        cvUploadPanel.setVisible(true);
        cvUploadPanel.setManaged(true);
    }

    @FXML
    private void selectRolePsy() {
        selectedRole = "psy";
        updateRoleCardStyles();
        cvUploadPanel.setVisible(true);
        cvUploadPanel.setManaged(true);
    }

    private void updateRoleCardStyles() {
        patientCard.getStyleClass().remove("role-card-selected");
        coachCard.getStyleClass().remove("role-card-selected");
        psyCard.getStyleClass().remove("role-card-selected");

        switch(selectedRole) {
            case "patient":
                patientCard.getStyleClass().add("role-card-selected");
                break;
            case "coach de vie":
                coachCard.getStyleClass().add("role-card-selected");
                break;
            case "psy":
                psyCard.getStyleClass().add("role-card-selected");
                break;
        }
    }

    @FXML
    private void handleUploadCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner votre CV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );

        cvFile = fileChooser.showOpenDialog(uploadCVButton.getScene().getWindow());

        if (cvFile != null) {
            if (cvFile.length() > 5 * 1024 * 1024) { // 5MB max
                showAlert(Alert.AlertType.ERROR, "Fichier trop volumineux",
                        "Le CV ne doit pas dépasser 5 Mo.");
                cvFile = null;
                cvFileNameLabel.setText("Aucun fichier sélectionné");
                return;
            }
            cvFileNameLabel.setText(cvFile.getName());
            cvFileNameLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: 600;");
        }
    }

    @FXML
    private void handleRegister() {
        resetStyles();

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

            // ✅ CONSTRUCTEUR AVEC DATE DE NAISSANCE
            Utilisateur user = new Utilisateur(
                    nomField.getText().trim().toUpperCase(),
                    prenomField.getText().trim(),
                    emailField.getText().trim().toLowerCase(),
                    formatTelephone(numeroTelField.getText().trim()),
                    dateNaissanceField.getValue(),
                    selectedRole,
                    motDePasseField.getText()
            );

            serviceUtilisateur.ajouter(user);

            Utilisateur createdUser = getUtilisateurByEmail(user.getEmail());

            if (createdUser != null) {
                if (selectedRole.equals("patient")) {
                    showAlert(Alert.AlertType.INFORMATION, "Bienvenue !",
                            "Compte créé avec succès ! Vous allez maintenant passer le quiz d'évaluation.");
                    openQuizInterface(createdUser.getIdUser());
                } else {
                    // ✅ UPLOADER LE CV DANS LE DOSSIER ET LA BASE DE DONNÉES
                    uploadCVToDatabase(createdUser.getIdUser());
                    showAlert(Alert.AlertType.INFORMATION, "Félicitations !",
                            "Votre compte a été créé avec succès ! Votre CV a été enregistré et sera examiné par un administrateur.");
                    redirectToLogin();
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ NOUVELLE MÉTHODE - UPLOAD CV AVEC STOCKAGE FICHIER + BASE DE DONNÉES
    private void uploadCVToDatabase(int userId) {
        if (cvFile == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun fichier CV sélectionné.");
            return;
        }

        try {
            // 1. Créer le dossier uploads/cv/ s'il n'existe pas
            String uploadDir = "uploads/cv/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. Générer un nom de fichier unique
            String originalFileName = cvFile.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = "cv_" + userId + "_" + System.currentTimeMillis() + extension;
            String filePath = uploadDir + newFileName;

            // 3. Copier le fichier vers le dossier uploads
            File destFile = new File(filePath);
            Files.copy(cvFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 4. Créer l'objet Cv
            Cv cv = new Cv(
                    userId,
                    originalFileName,
                    filePath,
                    (int) cvFile.length()
            );

            // 5. Sauvegarder dans la base de données
            serviceCv.ajouter(cv);

            System.out.println("✅ CV uploadé avec succès pour l'utilisateur " + userId);
            System.out.println("   📁 Fichier: " + newFileName);
            System.out.println("   📊 Taille: " + cvFile.length() + " bytes");
            System.out.println("   📌 Statut: en_attente");

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la copie du fichier: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur d'upload",
                    "Impossible de sauvegarder le fichier CV. Veuillez réessayer.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion en base: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur base de données",
                    "Impossible d'enregistrer les informations du CV.");
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
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
        String tel = numeroTelField.getText().trim();
        if (tel.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "Le téléphone est obligatoire.");
            numeroTelField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!tel.matches("^[0-9+\\s-]{10,}$")) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Format de téléphone invalide (10 chiffres minimum).");
            numeroTelField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
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

        // Validation Mot de passe
        String password = motDePasseField.getText();
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "Le mot de passe est obligatoire.");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe trop court", "Le mot de passe doit contenir au moins 6 caractères.");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe faible", "Le mot de passe doit contenir au moins une majuscule.");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe faible", "Le mot de passe doit contenir au moins un chiffre.");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation Confirmation
        if (confirmPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ obligatoire", "Veuillez confirmer votre mot de passe.");
            confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!password.equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Mots de passe différents", "Les mots de passe ne correspondent pas.");
            confirmPasswordField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }

        // Validation CV pour Coach et Psy
        if ((selectedRole.equals("coach de vie") || selectedRole.equals("psy")) && cvFile == null) {
            showAlert(Alert.AlertType.ERROR, "CV requis", "Veuillez uploader votre CV (format PDF).");
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

    private String formatTelephone(String telephone) {
        return telephone.replaceAll("\\s+", "").replaceAll("-", "").replaceAll("\\+", "00");
    }

    private Utilisateur getUtilisateurByEmail(String email) throws SQLException {
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    private void openQuizInterface(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/QuizPatient.fxml"));
            Parent root = loader.load();

            QuizPatientController controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz d'évaluation - MENSOS");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le quiz.");
        }
    }

    private void redirectToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - MENSOS");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        redirectToLogin();
    }

    private void resetStyles() {
        nomField.setStyle("");
        prenomField.setStyle("");
        emailField.setStyle("");
        numeroTelField.setStyle("");
        dateNaissanceField.setStyle("");
        motDePasseField.setStyle("");
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