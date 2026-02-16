package entities;

import java.time.LocalDate;

public class Cv {

    private int idCv;
    private int idUser;
    private String nomFichier;
    private String cheminFichier;
    private int tailleFichier;
    private LocalDate dateUpload;
    private String statut;  // en_attente, valide, refuse
    private String commentaire;

    // Constructeur vide
    public Cv() {}

    // Constructeur complet
    public Cv(int idCv, int idUser, String nomFichier, String cheminFichier,
              int tailleFichier, LocalDate dateUpload, String statut, String commentaire) {
        this.idCv = idCv;
        this.idUser = idUser;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.tailleFichier = tailleFichier;
        this.dateUpload = dateUpload;
        this.statut = statut;
        this.commentaire = commentaire;
    }

    // Constructeur pour ajout (sans ID)
    public Cv(int idUser, String nomFichier, String cheminFichier, int tailleFichier) {
        this.idUser = idUser;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.tailleFichier = tailleFichier;
        this.dateUpload = LocalDate.now();
        this.statut = "en_attente";
        this.commentaire = null;
    }

    // Getters et Setters
    public int getIdCv() { return idCv; }
    public void setIdCv(int idCv) { this.idCv = idCv; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }

    public int getTailleFichier() { return tailleFichier; }
    public void setTailleFichier(int tailleFichier) { this.tailleFichier = tailleFichier; }

    public LocalDate getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDate dateUpload) { this.dateUpload = dateUpload; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    @Override
    public String toString() {
        return "Cv{" +
                "idCv=" + idCv +
                ", idUser=" + idUser +
                ", nomFichier='" + nomFichier + '\'' +
                ", tailleFichier=" + tailleFichier + " bytes" +
                ", dateUpload=" + dateUpload +
                ", statut='" + statut + '\'' +
                '}';
    }
}