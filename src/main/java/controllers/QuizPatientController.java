package controllers;

import entities.Question;
import entities.Quiz;
import entities.QuizResult;
import services.ServiceQuestion;
import services.ServiceQuiz;
import services.ServiceQuizResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.*;

public class QuizPatientController {

    @FXML private Label quizTitleLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Label questionContentLabel;
    @FXML private VBox answersContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label scoreLabel; // ✅ AJOUTÉ

    private int userId;
    private Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Map<Integer, Integer> userAnswers = new HashMap<>();
    private ToggleGroup toggleGroup;

    private ServiceQuestion serviceQuestion = new ServiceQuestion();
    private ServiceQuiz serviceQuiz = new ServiceQuiz();
    private ServiceQuizResult serviceQuizResult = new ServiceQuizResult();

    @FXML
    public void initialize() {
        toggleGroup = new ToggleGroup();
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        if (previousButton != null) {
            previousButton.setDisable(true);
        }

        loadQuizForPatient();
    }

    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID utilisateur invalide");
        }
        this.userId = userId;
    }

    private void loadQuizForPatient() {
        try {
            List<Quiz> allQuizzes = serviceQuiz.recuperer();

            if (allQuizzes == null || allQuizzes.isEmpty()) {
                showError("Aucun quiz disponible dans la base de données");
                redirectToLogin();
                return;
            }

            currentQuiz = allQuizzes.stream()
                    .filter(q -> q != null && q.getType() != null
                            && q.getType().toLowerCase().contains("psycho")
                            && q.isActif())
                    .findFirst()
                    .orElse(null);

            if (currentQuiz == null) {
                showError("Aucun quiz d'évaluation actif disponible");
                redirectToLogin();
                return;
            }

            quizTitleLabel.setText(currentQuiz.getTitre() != null ? currentQuiz.getTitre() : "Quiz sans titre");

            questions = serviceQuestion.recupererParQuiz(currentQuiz.getIdQuiz());

            if (questions == null || questions.isEmpty()) {
                showError("Ce quiz ne contient aucune question");
                redirectToLogin();
                return;
            }

            questions.removeIf(q -> q == null || q.getContenu() == null || q.getContenu().trim().isEmpty());

            if (questions.isEmpty()) {
                showError("Toutes les questions sont invalides");
                redirectToLogin();
                return;
            }

            displayQuestion(currentQuestionIndex);
            updateTotalScore(); // ✅ AJOUTÉ

        } catch (SQLException e) {
            showError("Erreur de chargement du quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ METHODE POUR METTRE À JOUR LE SCORE TOTAL
    private void updateTotalScore() {
        if (scoreLabel != null) {
            int total = userAnswers.values().stream().mapToInt(Integer::intValue).sum();
            int max = questions != null ? questions.size() * 5 : 50;
            scoreLabel.setText(total + "/" + max);
        }
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }

        Question q = questions.get(index);
        questionNumberLabel.setText("Question " + (index + 1) + "/" + questions.size());
        questionContentLabel.setText(q.getContenu());

        double progress = (double) (index + 1) / questions.size();
        progressBar.setProgress(progress);

        answersContainer.getChildren().clear();
        toggleGroup = new ToggleGroup();

        // ✅ OPTIONS AVEC NUMÉROS 1-5 (COMME DANS L'IMAGE)
        String[] answerLabels = {
                "1 - Pas du tout d'accord",
                "2 - Plutôt pas d'accord",
                "3 - Neutre",
                "4 - Plutôt d'accord",
                "5 - Tout à fait d'accord"
        };
        int[] scores = {1, 2, 3, 4, 5};

        for (int i = 0; i < answerLabels.length; i++) {
            RadioButton rb = new RadioButton(answerLabels[i]);
            rb.setToggleGroup(toggleGroup);
            rb.setUserData(scores[i]);
            rb.setWrapText(true);

            // ✅ STYLE AMÉLIORÉ POUR LES RADIOBUTTONS
            rb.setStyle("-fx-font-size: 16px; -fx-padding: 12 0 12 20; " +
                    "-fx-background-radius: 12; -fx-cursor: hand; " +
                    "-fx-text-fill: #2C3E50;");

            // ✅ METTRE LE NUMÉRO EN GRAS
            rb.setStyle(rb.getStyle() + "-fx-font-weight: 600;");

            // ✅ EFFET HOVER
            rb.setOnMouseEntered(e -> {
                if (!rb.isSelected()) {
                    rb.setStyle("-fx-background-color: #F8FDFA; -fx-font-size: 16px; -fx-padding: 12 0 12 20; " +
                            "-fx-background-radius: 12; -fx-cursor: hand; -fx-text-fill: #1F3C88; " +
                            "-fx-font-weight: 600;");
                }
            });

            rb.setOnMouseExited(e -> {
                if (!rb.isSelected()) {
                    rb.setStyle("-fx-font-size: 16px; -fx-padding: 12 0 12 20; " +
                            "-fx-background-radius: 12; -fx-cursor: hand; -fx-text-fill: #2C3E50; " +
                            "-fx-font-weight: 600;");
                }
            });

            // ✅ STYLE QUAND SÉLECTIONNÉ
            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    rb.setStyle("-fx-background-color: #E8F0FE; -fx-border-color: #1F3C88; " +
                            "-fx-border-width: 1.5; -fx-border-radius: 12; " +
                            "-fx-font-size: 16px; -fx-padding: 12 0 12 20; " +
                            "-fx-background-radius: 12; -fx-cursor: hand; " +
                            "-fx-text-fill: #1F3C88; -fx-font-weight: 700;");
                } else {
                    rb.setStyle("-fx-font-size: 16px; -fx-padding: 12 0 12 20; " +
                            "-fx-background-radius: 12; -fx-cursor: hand; " +
                            "-fx-text-fill: #2C3E50; -fx-font-weight: 600;");
                }
            });

            // ✅ AJOUTER UN SÉPARATEUR ENTRE LES OPTIONS
            if (i < answerLabels.length - 1) {
                Separator separator = new Separator();
                separator.setStyle("-fx-background-color: #E8F0FE; -fx-opacity: 0.5; -fx-pref-height: 1;");
                answersContainer.getChildren().add(separator);
            }

            answersContainer.getChildren().add(rb);
        }

        // ✅ RESTAURER LA RÉPONSE PRÉCÉDENTE
        if (userAnswers.containsKey(q.getIdQuestion())) {
            int previousScore = userAnswers.get(q.getIdQuestion());
            for (javafx.scene.Node node : answersContainer.getChildren()) {
                if (node instanceof RadioButton) {
                    RadioButton rb = (RadioButton) node;
                    if ((int) rb.getUserData() == previousScore) {
                        rb.setSelected(true);
                        break;
                    }
                }
            }
        }

        updateNavigationButtons();
    }

    @FXML
    private void handlePrevious() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
            updateTotalScore(); // ✅ AJOUTÉ
        }
    }

    @FXML
    private void handleNext() {
        if (currentQuestionIndex < questions.size() - 1) {
            if (!validateCurrentAnswer()) {
                showStatus("Veuillez sélectionner une réponse", "error");
                return;
            }
            saveCurrentAnswer();
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
            updateTotalScore(); // ✅ AJOUTÉ
        }
    }

    private boolean validateCurrentAnswer() {
        return toggleGroup.getSelectedToggle() != null;
    }

    private void saveCurrentAnswer() {
        if (toggleGroup.getSelectedToggle() != null) {
            Question q = questions.get(currentQuestionIndex);
            int score = (int) toggleGroup.getSelectedToggle().getUserData();
            userAnswers.put(q.getIdQuestion(), score);
            updateTotalScore(); // ✅ AJOUTÉ
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateCurrentAnswer()) {
            showStatus("Veuillez répondre à la question avant de terminer", "error");
            return;
        }
        saveCurrentAnswer();

        if (userAnswers.size() < questions.size()) {
            showStatus("Veuillez répondre à toutes les questions", "error");

            for (Question q : questions) {
                if (!userAnswers.containsKey(q.getIdQuestion())) {
                    currentQuestionIndex = questions.indexOf(q);
                    displayQuestion(currentQuestionIndex);
                    return;
                }
            }
        }

        int totalScore = userAnswers.values().stream().mapToInt(Integer::intValue).sum();
        int maxPossibleScore = questions.size() * 5;
        double percentage = (totalScore * 100.0) / maxPossibleScore;

        String interpretation = generateInterpretation(percentage);

        try {
            if (userId <= 0) {
                showError("ID utilisateur invalide");
                return;
            }

            QuizResult result = new QuizResult(
                    totalScore,
                    interpretation,
                    userId,
                    currentQuiz.getIdQuiz()
            );

            serviceQuizResult.ajouter(result);

            showSuccess("Quiz terminé ! Votre score: " + totalScore + "/" + maxPossibleScore +
                    "\n\n" + interpretation);
            redirectToLogin();

        } catch (SQLException e) {
            showError("Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateInterpretation(double percentage) {
        if (percentage < 30) {
            return "✅ État mental stable. Vous gérez bien votre stress et vos émotions.";
        } else if (percentage < 50) {
            return "⚠️ Stress modéré. Un suivi léger pourrait être bénéfique.";
        } else if (percentage < 70) {
            return "📋 Stress élevé. Il est recommandé de consulter un psychologue.";
        } else {
            return "🆘 Stress sévère. Une consultation urgente est fortement recommandée.";
        }
    }

    private void updateNavigationButtons() {
        if (previousButton != null) {
            previousButton.setDisable(currentQuestionIndex == 0);
        }

        if (nextButton != null) {
            boolean isLastQuestion = currentQuestionIndex == questions.size() - 1;
            nextButton.setDisable(isLastQuestion);
            nextButton.setVisible(!isLastQuestion);
            nextButton.setManaged(!isLastQuestion);
        }

        if (submitButton != null) {
            boolean isLastQuestion = currentQuestionIndex == questions.size() - 1;
            submitButton.setVisible(isLastQuestion);
            submitButton.setManaged(isLastQuestion);
            submitButton.setDisable(false);
        }
    }

    private void showStatus(String message, String type) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);

            if (type.equals("error")) {
                statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-background-color: #FEF5F5; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #E74C3C; -fx-border-radius: 8; -fx-font-weight: 600;");
            } else {
                statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-background-color: #F0F9F0; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #27AE60; -fx-border-radius: 8; -fx-font-weight: 600;");
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz terminé");
        alert.setHeaderText("Félicitations !");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - MENSOS");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}