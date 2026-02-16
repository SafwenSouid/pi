package entities;

import java.time.LocalDate;

public class QuizResult {

    private int idResult;
    private int scoreTotal;
    private LocalDate datePassage;
    private String interpretation;
    private int idUser;
    private int idQuiz;

    public QuizResult() {}

    // Constructeur complet
    public QuizResult(int idResult, int scoreTotal, LocalDate datePassage,
                      String interpretation, int idUser, int idQuiz) {
        this.idResult = idResult;
        this.scoreTotal = scoreTotal;
        this.datePassage = datePassage;
        this.interpretation = interpretation;
        this.idUser = idUser;
        this.idQuiz = idQuiz;
    }

    // Constructeur pour ajout (date actuelle par défaut)
    public QuizResult(int scoreTotal, String interpretation, int idUser, int idQuiz) {
        this.scoreTotal = scoreTotal;
        this.interpretation = interpretation;
        this.idUser = idUser;
        this.idQuiz = idQuiz;
        this.datePassage = LocalDate.now();
    }

    // Getters & Setters
    public int getIdResult() { return idResult; }
    public void setIdResult(int idResult) { this.idResult = idResult; }

    public int getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(int scoreTotal) { this.scoreTotal = scoreTotal; }

    public LocalDate getDatePassage() { return datePassage; }
    public void setDatePassage(LocalDate datePassage) { this.datePassage = datePassage; }

    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdQuiz() { return idQuiz; }
    public void setIdQuiz(int idQuiz) { this.idQuiz = idQuiz; }

    @Override
    public String toString() {
        return "QuizResult{" +
                "idResult=" + idResult +
                ", scoreTotal=" + scoreTotal +
                ", datePassage=" + datePassage +
                ", interpretation='" + interpretation + '\'' +
                ", idUser=" + idUser +
                ", idQuiz=" + idQuiz +
                '}';
    }
}
