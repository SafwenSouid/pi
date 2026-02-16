package controllers;

import entities.Utilisateur;
import services.ServiceUtilisateur;
import services.ServiceConnexionLog;
import entities.ConnexionLog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.io.File;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    // ✅ AJOUTER CES DEUX LIGNES POUR LE LOGO
    @FXML private ImageView leftLogoImageView;
    @FXML private ImageView rightLogoImageView;

    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private ServiceConnexionLog serviceConnexionLog = new ServiceConnexionLog();
    private SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        // ✅ AJOUTER CETTE LIGNE POUR CHARGER LE LOGO
        loadLogo();

        emailField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                emailField.setStyle("-fx-border-color: #7BC6A4; -fx-border-width: 2;");
            }
        });

        motDePasseField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.isEmpty()) {
                motDePasseField.setStyle("-fx-border-color: #7BC6A4; -fx-border-width: 2;");
            }
        });
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

    @FXML
    private void handleLogin() {
        resetStyles();

        String email = emailField.getText().trim();
        String password = motDePasseField.getText();

        if (!validateEmail(email) || !validatePassword(password)) {
            return;
        }

        try {
            Utilisateur user = authenticate(email, password);

            if (user != null) {
                if (!user.isActif()) {
                    showStatus("Ce compte a été désactivé par l'administrateur. Veuillez contacter le support.", "error");
                    enregistrerLog(user.getIdUser(), user.getEmail(), user.getPrenom() + " " + user.getNom(),
                            "Tentative sur compte désactivé", getClientIp());
                    motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
                    return;
                }

                sessionManager.setCurrentUser(user);
                enregistrerLog(user.getIdUser(), user.getEmail(), user.getPrenom() + " " + user.getNom(),
                        "Connexion", getClientIp());

                showStatus("Connexion réussie ! Redirection...", "success");
                redirectBasedOnRole(user);

            } else {
                showStatus("Email ou mot de passe incorrect", "error");
                motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");

                Utilisateur userExists = getUtilisateurByEmail(email);
                if (userExists != null) {
                    enregistrerLog(userExists.getIdUser(), email,
                            userExists.getPrenom() + " " + userExists.getNom(),
                            "Échec connexion", getClientIp());
                }
            }

        } catch (SQLException e) {
            showStatus("Erreur de connexion à la base de données", "error");
            e.printStackTrace();
        }
    }

    private void enregistrerLog(int userId, String email, String userName, String status, String ip) {
        try {
            ConnexionLog log = new ConnexionLog(userId, email, userName, status, ip);
            serviceConnexionLog.ajouter(log);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du log: " + e.getMessage());
        }
    }

    private Utilisateur getUtilisateurByEmail(String email) throws SQLException {
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    private Utilisateur authenticate(String email, String password) throws SQLException {
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equals(email) && u.getMotDePasse().equals(password)) {
                return u;
            }
        }
        return null;
    }

    private void redirectBasedOnRole(Utilisateur user) {
        try {
            String fxmlFile;
            String title;

            switch (user.getRole().toLowerCase()) {
                case "patient":
                    fxmlFile = "/fxml/PatientDashboard.fxml";
                    title = "Mon espace - MENSOS";
                    break;
                case "psy":
                    fxmlFile = "/fxml/PsyDashboard.fxml";
                    title = "Tableau de bord - Psychiatre";
                    break;
                case "coach de vie":
                    fxmlFile = "/fxml/CoachDashboard.fxml";
                    title = "Tableau de bord - Coach";
                    break;
                case "admin":
                    fxmlFile = "/fxml/AdminDashboard.fxml";
                    title = "Administration - MENSOS";
                    break;
                default:
                    showStatus("Rôle non reconnu: " + user.getRole(), "error");
                    return;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            showStatus("Erreur de redirection: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription - MENSOS");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            showStatus("L'email est obligatoire", "error");
            emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showStatus("Format d'email invalide", "error");
            emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (email.length() > 150) {
            showStatus("L'email ne peut pas dépasser 150 caractères", "error");
            emailField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            showStatus("Le mot de passe est obligatoire", "error");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (password.length() < 6) {
            showStatus("Le mot de passe doit contenir au moins 6 caractères", "error");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        if (password.length() > 255) {
            showStatus("Le mot de passe est trop long", "error");
            motDePasseField.setStyle("-fx-border-color: #E74C3C; -fx-border-width: 2;");
            return false;
        }
        return true;
    }

    private String getClientIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private void resetStyles() {
        emailField.setStyle("");
        motDePasseField.setStyle("");
    }

    private void showStatus(String message, String type) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        if (type.equals("error")) {
            statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-background-color: #FEF5F5; -fx-padding: 10; -fx-background-radius: 8; -fx-font-weight: 600; -fx-border-color: #E74C3C; -fx-border-radius: 8; -fx-border-width: 1;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-background-color: #F0F9F0; -fx-padding: 10; -fx-background-radius: 8; -fx-font-weight: 600; -fx-border-color: #27AE60; -fx-border-radius: 8; -fx-border-width: 1;");
        }
    }
}