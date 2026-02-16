package entities;

public class Question {

    private int idQuestion;
    private String contenu;
    private int score;
    private int idQuiz;

    public Question() {}

    public Question(int idQuestion, String contenu, int score, int idQuiz) {
        this.idQuestion = idQuestion;
        this.contenu = contenu;
        this.score = score;
        this.idQuiz = idQuiz;
    }

    public Question(String contenu, int score, int idQuiz) {
        this.contenu = contenu;
        this.score = score;
        this.idQuiz = idQuiz;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    @Override
    public String toString() {
        return "Question{" +
                "idQuestion=" + idQuestion +
                ", contenu='" + contenu + '\'' +
                ", score=" + score +
                ", idQuiz=" + idQuiz +
                '}';
    }
}
