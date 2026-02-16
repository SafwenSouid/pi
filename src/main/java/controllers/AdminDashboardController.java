package controllers;

import entities.Utilisateur;
import entities.ConnexionLog;
import entities.Cv;
import services.ServiceUtilisateur;
import services.ServiceConnexionLog;
import services.ServiceCv;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.geometry.Pos;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML private Label adminNameLabel;
    @FXML private Label adminRoleLabel;
    @FXML private Label adminEmailLabel;
    @FXML private Label welcomeDateLabel;
    @FXML private Button logoutButton;

    // Statistiques
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label inactiveUsersLabel;
    @FXML private Label patientsCountLabel;
    @FXML private Label psyCountLabel;
    @FXML private Label coachCountLabel;
    @FXML private Label pendingCvCountLabel;

    // Tables
    @FXML private TableView<Utilisateur> usersTable;
    @FXML private TableColumn<Utilisateur, Integer> idCol;
    @FXML private TableColumn<Utilisateur, String> nomCol;
    @FXML private TableColumn<Utilisateur, String> prenomCol;
    @FXML private TableColumn<Utilisateur, String> emailCol;
    @FXML private TableColumn<Utilisateur, String> roleCol;
    @FXML private TableColumn<Utilisateur, String> statusCol;
    @FXML private TableColumn<Utilisateur, String> dateCol;
    @FXML private TableColumn<Utilisateur, Void> actionsCol;

    @FXML private TableView<ConnexionLog> logsTable;
    @FXML private TableColumn<ConnexionLog, Integer> logIdCol;
    @FXML private TableColumn<ConnexionLog, String> logUserCol;
    @FXML private TableColumn<ConnexionLog, String> logEmailCol;
    @FXML private TableColumn<ConnexionLog, String> logDateCol;
    @FXML private TableColumn<ConnexionLog, String> logIpCol;
    @FXML private TableColumn<ConnexionLog, String> logStatusCol;

    // Filtres
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TextField searchField;
    @FXML private DatePicker dateFilterPicker;

    // CV en attente
    @FXML private TableView<Cv> pendingCvTable;
    @FXML private TableColumn<Cv, Integer> cvIdCol;
    @FXML private TableColumn<Cv, String> cvUserCol;
    @FXML private TableColumn<Cv, String> cvNomCol;
    @FXML private TableColumn<Cv, String> cvDateCol;
    @FXML private TableColumn<Cv, String> cvTailleCol;
    @FXML private TableColumn<Cv, Void> cvActionsCol;

    private Utilisateur currentUser;
    private SessionManager sessionManager = SessionManager.getInstance();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private ServiceConnexionLog serviceConnexionLog = new ServiceConnexionLog();
    private ServiceCv serviceCv = new ServiceCv();

    private ObservableList<Utilisateur> usersList = FXCollections.observableArrayList();
    private ObservableList<ConnexionLog> logsList = FXCollections.observableArrayList();
    private ObservableList<Cv> pendingCvList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
        }

        setupTables();
        setupFilters();
        loadUsers();
        loadLogs();
        loadPendingCv();
        loadStatistics();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm");
        welcomeDateLabel.setText(now.format(formatter));
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        loadUserData();
        loadUsers();
        loadStatistics();
    }

    private void loadUserData() {
        if (currentUser != null) {
            adminNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
            adminEmailLabel.setText(currentUser.getEmail());
            adminRoleLabel.setText("Administrateur");
        }
    }

    private void setupTables() {
        // Table Utilisateurs
        idCol.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        dateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateCreation();
            return new javafx.beans.property.SimpleStringProperty(
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });

        statusCol.setCellValueFactory(cellData -> {
            boolean actif = cellData.getValue().isActif();
            String status = actif ? "Actif" : "Inactif";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        statusCol.setCellFactory(col -> new TableCell<Utilisateur, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Actif")) {
                        setStyle("-fx-text-fill: #27AE60; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: 600;");
                    }
                }
            }
        });

        actionsCol.setCellFactory(col -> new TableCell<Utilisateur, Void>() {
            private final Button activateBtn = new Button("Activer");
            private final Button deactivateBtn = new Button("Désactiver");
            private final Button editBtn = new Button("✏️ Modifier");
            private final Button deleteBtn = new Button("🗑️ Supprimer");
            private final HBox pane = new HBox(5);

            {
                activateBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
                deactivateBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");

                activateBtn.setOnAction(e -> activerUtilisateur(getTableView().getItems().get(getIndex())));
                deactivateBtn.setOnAction(e -> desactiverUtilisateur(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e -> modifierUtilisateur(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> supprimerUtilisateur(getTableView().getItems().get(getIndex())));

                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(5);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    if (user.isActif()) {
                        pane.getChildren().add(deactivateBtn);
                    } else {
                        pane.getChildren().add(activateBtn);
                    }
                    pane.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(pane);
                }
            }
        });

        usersTable.setItems(usersList);

        // Table Logs
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        logUserCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        logEmailCol.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        logDateCol.setCellValueFactory(new PropertyValueFactory<>("dateFormatted"));
        logIpCol.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        logStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        logsTable.setItems(logsList);

        // Table CV
        cvIdCol.setCellValueFactory(new PropertyValueFactory<>("idCv"));
        cvUserCol.setCellValueFactory(cellData -> {
            try {
                Utilisateur user = serviceUtilisateur.recupererParId(cellData.getValue().getIdUser());
                String name = user.getPrenom() + " " + user.getNom();
                return new javafx.beans.property.SimpleStringProperty(name);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Inconnu");
            }
        });
        cvNomCol.setCellValueFactory(new PropertyValueFactory<>("nomFichier"));
        cvDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateUpload();
            return new javafx.beans.property.SimpleStringProperty(
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });
        cvTailleCol.setCellValueFactory(cellData -> {
            int taille = cellData.getValue().getTailleFichier();
            String tailleFormatted = taille < 1024 ? taille + " o" :
                    (taille < 1048576 ? (taille / 1024) + " Ko" : (taille / 1048576) + " Mo");
            return new javafx.beans.property.SimpleStringProperty(tailleFormatted);
        });

        cvActionsCol.setCellFactory(col -> new TableCell<Cv, Void>() {
            private final Button viewBtn = new Button("👁️ Voir");
            private final Button validateBtn = new Button("✅ Valider");
            private final Button rejectBtn = new Button("❌ Refuser");
            private final HBox pane = new HBox(5);

            {
                viewBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
                validateBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> voirCV(getTableView().getItems().get(getIndex())));
                validateBtn.setOnAction(e -> validerCV(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e -> refuserCV(getTableView().getItems().get(getIndex())));

                pane.setAlignment(Pos.CENTER);
                pane.setSpacing(5);
                pane.getChildren().addAll(viewBtn, validateBtn, rejectBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        pendingCvTable.setItems(pendingCvList);
    }

    private void setupFilters() {
        roleFilterCombo.getItems().addAll("Tous", "Patient", "Psy", "Coach de vie", "Admin");
        roleFilterCombo.setValue("Tous");
        roleFilterCombo.setOnAction(e -> filterUsers());

        statusFilterCombo.getItems().addAll("Tous", "Actif", "Inactif");
        statusFilterCombo.setValue("Tous");
        statusFilterCombo.setOnAction(e -> filterUsers());

        searchField.textProperty().addListener((obs, old, newValue) -> filterUsers());
    }

    private void loadUsers() {
        try {
            usersList.clear();
            usersList.addAll(serviceUtilisateur.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les utilisateurs.");
        }
    }

    private void loadLogs() {
        try {
            logsList.clear();
            logsList.addAll(serviceConnexionLog.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPendingCv() {
        try {
            pendingCvList.clear();
            pendingCvList.addAll(serviceCv.recupererEnAttente());
            pendingCvCountLabel.setText(pendingCvList.size() + " CV en attente de validation");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStatistics() {
        try {
            List<Utilisateur> allUsers = serviceUtilisateur.recuperer();
            totalUsersLabel.setText(String.valueOf(allUsers.size()));

            long active = allUsers.stream().filter(Utilisateur::isActif).count();
            long inactive = allUsers.size() - active;
            activeUsersLabel.setText(String.valueOf(active));
            inactiveUsersLabel.setText(String.valueOf(inactive));

            long patients = allUsers.stream().filter(u -> u.getRole().equals("patient")).count();
            long psy = allUsers.stream().filter(u -> u.getRole().equals("psy")).count();
            long coach = allUsers.stream().filter(u -> u.getRole().equals("coach de vie")).count();

            patientsCountLabel.setText(String.valueOf(patients));
            psyCountLabel.setText(String.valueOf(psy));
            coachCountLabel.setText(String.valueOf(coach));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String roleFilter = roleFilterCombo.getValue();
        String statusFilter = statusFilterCombo.getValue();

        try {
            List<Utilisateur> filtered = serviceUtilisateur.recuperer().stream()
                    .filter(user -> {
                        if (searchText.isEmpty()) return true;
                        return user.getNom().toLowerCase().contains(searchText) ||
                                user.getPrenom().toLowerCase().contains(searchText) ||
                                user.getEmail().toLowerCase().contains(searchText);
                    })
                    .filter(user -> {
                        if (roleFilter.equals("Tous")) return true;
                        String role = user.getRole();
                        if (roleFilter.equals("Patient")) return role.equals("patient");
                        if (roleFilter.equals("Psy")) return role.equals("psy");
                        if (roleFilter.equals("Coach de vie")) return role.equals("coach de vie");
                        if (roleFilter.equals("Admin")) return role.equals("admin");
                        return true;
                    })
                    .filter(user -> {
                        if (statusFilter.equals("Tous")) return true;
                        if (statusFilter.equals("Actif")) return user.isActif();
                        return !user.isActif();
                    })
                    .collect(Collectors.toList());

            usersList.setAll(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void activerUtilisateur(Utilisateur user) {
        try {
            user.setActif(true);
            serviceUtilisateur.modifier(user);
            loadUsers();
            loadStatistics();
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Le compte de " + user.getPrenom() + " " + user.getNom() + " a été activé.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'activer le compte.");
        }
    }

    @FXML
    private void desactiverUtilisateur(Utilisateur user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Désactiver le compte");
        confirm.setContentText("Voulez-vous vraiment désactiver le compte de " +
                user.getPrenom() + " " + user.getNom() + " ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                user.setActif(false);
                serviceUtilisateur.modifier(user);
                loadUsers();
                loadStatistics();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le compte a été désactivé.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de désactiver le compte.");
            }
        }
    }

    @FXML
    private void modifierUtilisateur(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminEditUser.fxml"));
            Parent root = loader.load();
            AdminEditUserController controller = loader.getController();
            controller.setUser(user);
            controller.setAdminController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier l'utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadUsers();
            loadStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerUtilisateur(Utilisateur user) {
        if (user.getIdUser() == currentUser.getIdUser()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le compte");
        confirm.setContentText("Voulez-vous vraiment supprimer définitivement le compte de " +
                user.getPrenom() + " " + user.getNom() + " ?\nCette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceUtilisateur.supprimer(user);
                loadUsers();
                loadStatistics();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le compte a été supprimé.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le compte.");
            }
        }
    }

    @FXML
    private void ajouterUtilisateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminAddUser.fxml"));
            Parent root = loader.load();
            AdminAddUserController controller = loader.getController();
            controller.setAdminController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadUsers();
            loadStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshData() {
        loadUsers();
        loadLogs();
        loadPendingCv();
        loadStatistics();
        showAlert(Alert.AlertType.INFORMATION, "Rafraîchissement", "Les données ont été mises à jour.");
    }

    @FXML
    private void exportLogs() {
        showAlert(Alert.AlertType.INFORMATION, "Export", "Les logs ont été exportés avec succès.");
    }

    private void voirCV(Cv cv) {
        try {
            File file = new File(cv.getCheminFichier());
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier CV est introuvable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le CV.");
        }
    }

    private void validerCV(Cv cv) {
        try {
            serviceCv.validerCv(cv.getIdCv());
            loadPendingCv();
            Utilisateur user = serviceUtilisateur.recupererParId(cv.getIdUser());
            showAlert(Alert.AlertType.INFORMATION, "CV validé",
                    "Le CV de " + user.getPrenom() + " " + user.getNom() + " a été validé.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de valider le CV.");
        }
    }

    private void refuserCV(Cv cv) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Refuser le CV");
        dialog.setHeaderText("Motif du refus");
        dialog.setContentText("Veuillez indiquer la raison du refus :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(commentaire -> {
            try {
                serviceCv.refuserCv(cv.getIdCv(), commentaire);
                loadPendingCv();
                showAlert(Alert.AlertType.INFORMATION, "CV refusé", "Le CV a été refusé.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de refuser le CV.");
            }
        });
    }

    /**
     * ✅ MÉTHODE DE DÉCONNEXION - AJOUTÉE
     */
    @FXML
    private void handleLogout() {
        try {
            // Enregistrer le log de déconnexion
            if (currentUser != null) {
                try {
                    ConnexionLog log = new ConnexionLog(
                            currentUser.getIdUser(),
                            currentUser.getEmail(),
                            currentUser.getPrenom() + " " + currentUser.getNom(),
                            "Déconnexion",
                            getClientIp()
                    );
                    serviceConnexionLog.ajouter(log);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'enregistrement du log: " + e.getMessage());
                }
            }

            // Vider la session
            sessionManager.clearSession();

            // Rediriger vers la page de connexion
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - MENSOS");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
        }
    }

    /**
     * ✅ MÉTHODE POUR OBTENIR L'IP - AJOUTÉE
     */
    private String getClientIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}