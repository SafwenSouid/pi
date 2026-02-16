package entities;

public class Quiz {

    private int idQuiz;
    private String titre;
    private String description;
    private String type;
    private boolean actif;

    public Quiz() {}

    public Quiz(int idQuiz, String titre, String description, String type, boolean actif) {
        this.idQuiz = idQuiz;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.actif = actif;
    }

    public Quiz(String titre, String description, String type, boolean actif) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.actif = actif;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "idQuiz=" + idQuiz +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", actif=" + actif +
                '}';
    }
}
