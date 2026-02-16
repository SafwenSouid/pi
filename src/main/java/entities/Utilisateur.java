package entities;

import java.time.LocalDate;

public class Utilisateur {

    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String numeroTel;
    private LocalDate dateNaissance;
    private String role;
    private LocalDate dateCreation;
    private String motDePasse;
    private boolean actif;  // ✅ AJOUT DU CHAMP ACTIF

    public Utilisateur() {}

    // Constructeur complet avec ID et actif
    public Utilisateur(int idUser, String nom, String prenom, String email,
                       String numeroTel, LocalDate dateNaissance, String role,
                       LocalDate dateCreation, String motDePasse, boolean actif) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numeroTel = numeroTel;
        this.dateNaissance = dateNaissance;
        this.role = role;
        this.dateCreation = dateCreation;
        this.motDePasse = motDePasse;
        this.actif = actif;  // ✅ AJOUTÉ
    }

    // Constructeur pour inscription (SANS ID) - actif = true par défaut
    public Utilisateur(String nom, String prenom, String email,
                       String numeroTel, LocalDate dateNaissance,
                       String role, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numeroTel = numeroTel;
        this.dateNaissance = dateNaissance;
        this.role = role;
        this.motDePasse = motDePasse;
        this.dateCreation = LocalDate.now();
        this.actif = true;  // ✅ AJOUTÉ - compte actif par défaut
    }

    // Getters & Setters
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumeroTel() { return numeroTel; }
    public void setNumeroTel(String numeroTel) { this.numeroTel = numeroTel; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    // ✅ GETTER & SETTER POUR ACTIF
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUser=" + idUser +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", numeroTel='" + numeroTel + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", role='" + role + '\'' +
                ", actif=" + actif +  // ✅ AJOUTÉ
                ", dateCreation=" + dateCreation +
                '}';
    }
}