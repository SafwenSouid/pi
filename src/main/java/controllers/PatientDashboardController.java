package controllers;

import entities.Utilisateur;
import entities.QuizResult;
import services.ServiceQuizResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientDashboardController {

    @FXML private Label patientNameLabel;
    @FXML private Label patientAgeLabel;
    @FXML private Label patientEmailLabel;
    @FXML private Label patientPhoneLabel;
    @FXML private Label firstConsultationLabel;
    @FXML private Label lastConsultationLabel;
    @FXML private Label nextAppointmentLabel;
    @FXML private Label totalReportsLabel;
    @FXML private Label moodLabel;
    @FXML private Label moodEvolutionLabel;
    @FXML private Label stressLabel;
    @FXML private Label stressEvolutionLabel;
    @FXML private Label anxietyLabel;
    @FXML private Label anxietyEvolutionLabel;
    @FXML private Label iaRecommendationLabel;
    @FXML private VBox reportsContainer;
    @FXML private Button backButton;
    @FXML private Button newReportButton;
    @FXML private Button downloadAllButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeDateLabel;

    // ✅ ImageView pour la photo de profil
    @FXML private ImageView profileImageView;
    @FXML private Label avatarInitials;

    // ✅ Bouton modifier le profil
    @FXML private Button editProfileButton;

    private Utilisateur currentUser;
    private SessionManager sessionManager = SessionManager.getInstance();
    private ServiceQuizResult serviceQuizResult = new ServiceQuizResult();
    private List<QuizResult> quizResults;

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        loadUserData();
        loadQuizHistory();
        loadProfileImage(); // ✅ AJOUTÉ
    }

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur de la session
        currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
            loadQuizHistory();
            loadProfileImage(); // ✅ AJOUTÉ
        }

        // Cacher le bouton retour (pas besoin pour le patient)
        if (backButton != null) {
            backButton.setVisible(false);
            backButton.setManaged(false);
        }

        // Date du jour
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        if (welcomeDateLabel != null) {
            welcomeDateLabel.setText(today.format(formatter));
        }
    }

    private void loadUserData() {
        if (currentUser != null) {
            patientNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
            patientEmailLabel.setText(currentUser.getEmail());
            patientPhoneLabel.setText(currentUser.getNumeroTel() != null ? currentUser.getNumeroTel() : "Non renseigné");

            // Calculer l'âge
            if (currentUser.getDateNaissance() != null) {
                int age = LocalDate.now().getYear() - currentUser.getDateNaissance().getYear();
                patientAgeLabel.setText(age + " ans");
            } else {
                patientAgeLabel.setText("Non renseigné");
            }

            // Initiales pour l'avatar
            if (avatarInitials != null) {
                String initials = currentUser.getPrenom().substring(0, 1).toUpperCase() +
                        currentUser.getNom().substring(0, 1).toUpperCase();
                avatarInitials.setText(initials);
            }
        }
    }

    // ✅ MÉTHODE POUR CHARGER LA PHOTO DE PROFIL (AMÉLIORÉE)
    private void loadProfileImage() {
        try {
            if (currentUser == null) return;

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

            if (imageFile != null && imageFile.exists() && profileImageView != null) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
                profileImageView.setVisible(true);
                if (avatarInitials != null) {
                    avatarInitials.setVisible(false);
                }
            } else {
                if (profileImageView != null) {
                    profileImageView.setVisible(false);
                }
                if (avatarInitials != null) {
                    avatarInitials.setVisible(true);
                    // Mettre à jour les initiales
                    String initials = currentUser.getPrenom().substring(0, 1).toUpperCase() +
                            currentUser.getNom().substring(0, 1).toUpperCase();
                    avatarInitials.setText(initials);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (profileImageView != null) {
                profileImageView.setVisible(false);
            }
            if (avatarInitials != null) {
                avatarInitials.setVisible(true);
            }
        }
    }

    // ✅ MÉTHODE POUR RAFRAÎCHIR LE DASHBOARD QUAND ON REVIENT
    @FXML
    public void onDashboardShown() {
        // Recharger l'utilisateur depuis la session
        currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
            loadProfileImage(); // ✅ RECHARGER LA PHOTO
            loadQuizHistory();
        }
    }

    private void loadQuizHistory() {
        try {
            if (currentUser == null) return;

            quizResults = serviceQuizResult.recupererParUtilisateur(currentUser.getIdUser());

            // Mettre à jour les statistiques
            totalReportsLabel.setText(String.valueOf(quizResults.size()));

            if (!quizResults.isEmpty()) {
                // Premier et dernier rapport
                QuizResult firstResult = quizResults.get(0);
                QuizResult lastResult = quizResults.get(quizResults.size() - 1);

                firstConsultationLabel.setText(firstResult.getDatePassage().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                lastConsultationLabel.setText(lastResult.getDatePassage().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

                // Métriques IA (simulées à partir des scores)
                int firstScore = firstResult.getScoreTotal();
                int lastScore = lastResult.getScoreTotal();
                int maxScore = 50;

                // Humeur = inverse du stress (simulation)
                int firstMood = 100 - (firstScore * 100 / maxScore);
                int lastMood = 100 - (lastScore * 100 / maxScore);

                moodLabel.setText(lastMood + "%");
                int moodEvolution = lastMood - firstMood;
                moodEvolutionLabel.setText((moodEvolution > 0 ? "+" : "") + moodEvolution + "%");
                moodEvolutionLabel.setStyle(moodEvolution > 0 ?
                        "-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-weight: 600;" :
                        "-fx-text-fill: #E74C3C; -fx-font-size: 14px; -fx-font-weight: 600;");

                // Stress = score normalisé
                int firstStress = firstScore * 100 / maxScore;
                int lastStress = lastScore * 100 / maxScore;

                stressLabel.setText(lastStress + "%");
                int stressEvolution = lastStress - firstStress;
                stressEvolutionLabel.setText((stressEvolution > 0 ? "+" : "") + stressEvolution + "%");
                stressEvolutionLabel.setStyle(stressEvolution < 0 ?
                        "-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-weight: 600;" :
                        "-fx-text-fill: #E74C3C; -fx-font-size: 14px; -fx-font-weight: 600;");

                // Anxiété = similaire au stress
                anxietyLabel.setText(lastStress + "%");
                anxietyEvolutionLabel.setText((stressEvolution > 0 ? "+" : "") + stressEvolution + "%");
                anxietyEvolutionLabel.setStyle(stressEvolution < 0 ?
                        "-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-weight: 600;" :
                        "-fx-text-fill: #E74C3C; -fx-font-size: 14px; -fx-font-weight: 600;");

                // Prochain rendez-vous (simulé)
                nextAppointmentLabel.setText(lastResult.getDatePassage().plusWeeks(2).format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

                // Recommandation IA
                generateIARecommendation(lastScore, maxScore);

                // Charger les rapports
                loadReports();
            } else {
                // Aucun quiz passé
                firstConsultationLabel.setText("Aucun");
                lastConsultationLabel.setText("Aucun");
                nextAppointmentLabel.setText("Non planifié");
                moodLabel.setText("0%");
                stressLabel.setText("0%");
                anxietyLabel.setText("0%");
                iaRecommendationLabel.setText("Vous n'avez pas encore passé de quiz. Commencez dès maintenant pour évaluer votre santé mentale.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateIARecommendation(int score, int maxScore) {
        int percentage = score * 100 / maxScore;
        String recommendation;

        if (percentage < 30) {
            recommendation = "État mental stable. Vous gérez bien votre stress et vos émotions. Continuez vos activités habituelles et pratiquez la relaxation.";
        } else if (percentage < 50) {
            recommendation = "Stress modéré. Nous vous recommandons des exercices de respiration quotidienne et une activité physique régulière.";
        } else if (percentage < 70) {
            recommendation = "Stress élevé. Il est conseillé de consulter un psychologue et de pratiquer la méditation guidée.";
        } else {
            recommendation = "Stress sévère. Une consultation urgente avec un professionnel est fortement recommandée. N'hésitez pas à demander de l'aide.";
        }

        iaRecommendationLabel.setText(recommendation);
    }

    private void loadReports() {
        if (reportsContainer != null && quizResults != null && !quizResults.isEmpty()) {
            reportsContainer.getChildren().clear();

            for (int i = quizResults.size() - 1; i >= 0; i--) {
                QuizResult result = quizResults.get(i);
                int reportNumber = quizResults.size() - i;

                String type = i == 0 ? "Première consultation" : "Suivi";
                String date = result.getDatePassage().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                String diagnosis = generateDiagnosis(result.getScoreTotal());
                String notes = generateNotes(result.getScoreTotal(), result.getInterpretation());
                String recommendations = result.getInterpretation();

                int score = result.getScoreTotal();
                int maxScore = 50;
                int stressPercent = score * 100 / maxScore;
                int moodPercent = 100 - stressPercent;
                int anxietyPercent = stressPercent;

                reportsContainer.getChildren().add(createReportCard(
                        String.valueOf(reportNumber), type, date,
                        diagnosis, notes, recommendations,
                        moodPercent + "%", stressPercent + "%", anxietyPercent + "%"
                ));
            }
        }
    }

    private String generateDiagnosis(int score) {
        if (score <= 15) return "État stable";
        if (score <= 25) return "Stress léger";
        if (score <= 35) return "Stress modéré";
        if (score <= 45) return "Stress élevé";
        return "Stress sévère";
    }

    private String generateNotes(int score, String interpretation) {
        if (score <= 15) {
            return "Patient montre un bon équilibre émotionnel. Aucun signe de détresse psychologique majeur.";
        } else if (score <= 25) {
            return "Quelques signes de stress léger. Patient semble gérer ses activités quotidiennes.";
        } else if (score <= 35) {
            return "Stress modéré détecté. Patient rapporte des difficultés de sommeil et de concentration.";
        } else if (score <= 45) {
            return "Stress élevé. Patient présente des symptômes d'anxiété et des tensions musculaires.";
        } else {
            return "Stress sévère. Patient nécessite une prise en charge rapide et un suivi rapproché.";
        }
    }

    private VBox createReportCard(String number, String type, String date,
                                  String diagnosis, String notes, String recommendations,
                                  String mood, String stress, String anxiety) {

        VBox card = new VBox(20);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E5E9F0; -fx-border-radius: 16; " +
                "-fx-background-radius: 16; -fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 5, 0, 0, 0);");

        card.setOnMouseEntered(e ->
                card.setStyle("-fx-background-color: white; -fx-border-color: #7BC6A4; -fx-border-radius: 16; " +
                        "-fx-background-radius: 16; -fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(123,198,164,0.1), 10, 0, 0, 0);")
        );

        card.setOnMouseExited(e ->
                card.setStyle("-fx-background-color: white; -fx-border-color: #E5E9F0; -fx-border-radius: 16; " +
                        "-fx-background-radius: 16; -fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 5, 0, 0, 0);")
        );

        // ========== EN-TÊTE ==========
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Rapport #" + number);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #1F3C88;");

        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-background-color: #E8F0FE; -fx-text-fill: #1F3C88; " +
                "-fx-padding: 4 12; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: 600;");

        titleRow.getChildren().addAll(titleLabel, typeLabel);

        HBox dateRow = new HBox(8);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        Label calendarIcon = new Label("📅");
        calendarIcon.setStyle("-fx-font-size: 14px;");
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");
        dateRow.getChildren().addAll(calendarIcon, dateLabel);

        titleBox.getChildren().addAll(titleRow, dateRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button downloadBtn = new Button("📥");
        downloadBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-cursor: hand;");
        downloadBtn.setOnAction(e -> handleDownloadReport(number));

        header.getChildren().addAll(titleBox, spacer, downloadBtn);

        // ========== CONTENU ==========
        VBox content = new VBox(15);
        VBox diagnosticBox = new VBox(5);
        Label diagTitle = new Label("Diagnostic :");
        diagTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #4A4A4A;");
        Label diagText = new Label(diagnosis);
        diagText.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A4A4A; -fx-opacity: 0.8;");
        diagText.setWrapText(true);
        diagnosticBox.getChildren().addAll(diagTitle, diagText);

        VBox notesBox = new VBox(5);
        Label notesTitle = new Label("Notes :");
        notesTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #4A4A4A;");
        Label notesText = new Label(notes);
        notesText.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A4A4A; -fx-opacity: 0.8;");
        notesText.setWrapText(true);
        notesBox.getChildren().addAll(notesTitle, notesText);

        VBox recoBox = new VBox(5);
        Label recoTitle = new Label("Recommandations :");
        recoTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #4A4A4A;");
        Label recoText = new Label(recommendations);
        recoText.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A4A4A; -fx-opacity: 0.8;");
        recoText.setWrapText(true);
        recoBox.getChildren().addAll(recoTitle, recoText);

        content.getChildren().addAll(diagnosticBox, notesBox, recoBox);

        // ========== MÉTRIQUES ==========
        HBox metrics = new HBox(30);
        metrics.setAlignment(Pos.CENTER);
        metrics.setStyle("-fx-padding: 15 0 0 0; -fx-border-color: #E8F0FE; -fx-border-width: 1 0 0 0;");

        VBox moodMetric = new VBox(5);
        moodMetric.setAlignment(Pos.CENTER);
        Label moodLabelTitle = new Label("Humeur");
        moodLabelTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        Label moodValue = new Label(mood);
        moodValue.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #7BC6A4;");
        moodMetric.getChildren().addAll(moodLabelTitle, moodValue);

        VBox stressMetric = new VBox(5);
        stressMetric.setAlignment(Pos.CENTER);
        Label stressLabelTitle = new Label("Stress");
        stressLabelTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        Label stressValue = new Label(stress);
        stressValue.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #FFB703;");
        stressMetric.getChildren().addAll(stressLabelTitle, stressValue);

        VBox anxietyMetric = new VBox(5);
        anxietyMetric.setAlignment(Pos.CENTER);
        Label anxietyLabelTitle = new Label("Anxiété");
        anxietyLabelTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        Label anxietyValue = new Label(anxiety);
        anxietyValue.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #1F3C88;");
        anxietyMetric.getChildren().addAll(anxietyLabelTitle, anxietyValue);

        metrics.getChildren().addAll(moodMetric, stressMetric, anxietyMetric);

        card.getChildren().addAll(header, content, metrics);
        return card;
    }

    @FXML
    private void handleNewReport() {
        showAlert(Alert.AlertType.INFORMATION, "Information",
                "Seuls les psychiatres et coachs peuvent créer des rapports.");
    }

    @FXML
    private void handleDownloadAll() {
        showAlert(Alert.AlertType.INFORMATION, "Téléchargement",
                "Tous vos rapports seront téléchargés au format PDF.");
    }

    private void handleDownloadReport(String reportNumber) {
        showAlert(Alert.AlertType.INFORMATION, "Téléchargement",
                "Téléchargement du rapport #" + reportNumber + " au format PDF.");
    }

    @FXML
    private void handleLogout() {
        try {
            sessionManager.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - MENSOS");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientEditProfile.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) editProfileButton.getScene().getWindow();

            // ✅ Garder la scène actuelle pour y revenir
            Scene currentScene = stage.getScene();

            stage.setScene(new Scene(root));
            stage.setTitle("Modifier mon profil - MENSOS");

            // ✅ Quand on ferme la fenêtre d'édition, revenir au dashboard
            stage.setOnHidden(e -> {
                stage.setScene(currentScene);
                stage.setTitle("Mon espace patient - MENSOS");
                onDashboardShown(); // Rafraîchir les données
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la page de modification de profil.");
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