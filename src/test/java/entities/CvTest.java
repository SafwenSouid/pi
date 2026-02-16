package entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Cv
 * Vérifie les constructeurs, getters, setters et toString
 */
public class CvTest {

    @Test
    void testConstructeurComplet() {
        // 🔍 Test du constructeur avec tous les paramètres
        // Arrange - Préparer les données de test
        LocalDate date = LocalDate.of(2024, 1, 15);

        // Act - Exécuter le constructeur
        Cv cv = new Cv(1, 100, "cv.pdf", "uploads/cv.pdf", 1024, date, "en_attente", null);

        // Assert - Vérifier que toutes les valeurs sont correctes
        assertEquals(1, cv.getIdCv(), "L'ID du CV devrait être 1");
        assertEquals(100, cv.getIdUser(), "L'ID utilisateur devrait être 100");
        assertEquals("cv.pdf", cv.getNomFichier(), "Le nom du fichier devrait être cv.pdf");
        assertEquals("uploads/cv.pdf", cv.getCheminFichier(), "Le chemin devrait être uploads/cv.pdf");
        assertEquals(1024, cv.getTailleFichier(), "La taille devrait être 1024 bytes");
        assertEquals(date, cv.getDateUpload(), "La date d'upload devrait être 2024-01-15");
        assertEquals("en_attente", cv.getStatut(), "Le statut devrait être en_attente");
        assertNull(cv.getCommentaire(), "Le commentaire devrait être null");
    }

    @Test
    void testConstructeurAjout() {
        // 🔍 Test du constructeur simplifié pour ajout
        // Arrange - Pas de données préalables

        // Act - Créer un CV avec le constructeur simplifié
        Cv cv = new Cv(100, "cv.pdf", "uploads/cv.pdf", 2048);

        // Assert - Vérifier les valeurs par défaut
        assertEquals(100, cv.getIdUser(), "L'ID utilisateur devrait être 100");
        assertEquals("cv.pdf", cv.getNomFichier(), "Le nom du fichier devrait être cv.pdf");
        assertEquals("uploads/cv.pdf", cv.getCheminFichier(), "Le chemin devrait être uploads/cv.pdf");
        assertEquals(2048, cv.getTailleFichier(), "La taille devrait être 2048 bytes");
        assertEquals(LocalDate.now(), cv.getDateUpload(), "La date d'upload devrait être aujourd'hui");
        assertEquals("en_attente", cv.getStatut(), "Le statut par défaut devrait être en_attente");
        assertNull(cv.getCommentaire(), "Le commentaire par défaut devrait être null");
    }

    @Test
    void testSetters() {
        // 🔍 Test des setters et getters
        // Arrange - Créer un objet vide
        Cv cv = new Cv();
        LocalDate date = LocalDate.of(2024, 2, 20);

        // Act - Utiliser tous les setters
        cv.setIdCv(5);
        cv.setIdUser(200);
        cv.setNomFichier("nouveau.pdf");
        cv.setCheminFichier("uploads/nouveau.pdf");
        cv.setTailleFichier(3072);
        cv.setDateUpload(date);
        cv.setStatut("valide");
        cv.setCommentaire("CV validé");

        // Assert - Vérifier que tous les getters retournent les bonnes valeurs
        assertEquals(5, cv.getIdCv(), "ID devrait être 5 après setter");
        assertEquals(200, cv.getIdUser(), "ID utilisateur devrait être 200");
        assertEquals("nouveau.pdf", cv.getNomFichier(), "Nom fichier devrait être nouveau.pdf");
        assertEquals("uploads/nouveau.pdf", cv.getCheminFichier(), "Chemin devrait être uploads/nouveau.pdf");
        assertEquals(3072, cv.getTailleFichier(), "Taille devrait être 3072");
        assertEquals(date, cv.getDateUpload(), "Date devrait être 2024-02-20");
        assertEquals("valide", cv.getStatut(), "Statut devrait être valide");
        assertEquals("CV validé", cv.getCommentaire(), "Commentaire devrait être 'CV validé'");
    }

    @Test
    void testToString() {
        // 🔍 Test de la méthode toString
        // Arrange - Créer un CV avec des valeurs connues
        Cv cv = new Cv(1, 100, "cv.pdf", "uploads/cv.pdf", 1024,
                LocalDate.of(2024, 1, 15), "en_attente", null);

        // Act - Appeler toString
        String str = cv.toString();

        // Assert - Vérifier que la chaîne contient toutes les informations
        assertTrue(str.contains("idCv=1"), "toString devrait contenir l'ID");
        assertTrue(str.contains("idUser=100"), "toString devrait contenir l'ID utilisateur");
        assertTrue(str.contains("nomFichier='cv.pdf'"), "toString devrait contenir le nom du fichier");
        assertTrue(str.contains("tailleFichier=1024"), "toString devrait contenir la taille");
        assertTrue(str.contains("statut='en_attente'"), "toString devrait contenir le statut");
    }
}