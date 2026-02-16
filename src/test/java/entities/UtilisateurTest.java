package entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Utilisateur
 */
public class UtilisateurTest {

    @Test
    void testConstructeurComplet() {
        // 🔍 Test du constructeur complet avec ID et actif
        LocalDate naissance = LocalDate.of(1990, 1, 1);
        LocalDate creation = LocalDate.of(2024, 1, 1);

        Utilisateur u = new Utilisateur(1, "DUPONT", "Jean", "jean@test.com",
                "0612345678", naissance, "patient",
                creation, "password123", true);

        assertEquals(1, u.getIdUser(), "L'ID utilisateur devrait être 1");
        assertEquals("DUPONT", u.getNom(), "Le nom devrait être DUPONT");
        assertEquals("Jean", u.getPrenom(), "Le prénom devrait être Jean");
        assertEquals("jean@test.com", u.getEmail(), "L'email devrait être jean@test.com");
        assertEquals("0612345678", u.getNumeroTel(), "Le téléphone devrait être 0612345678");
        assertEquals(naissance, u.getDateNaissance(), "La date de naissance devrait être 1990-01-01");
        assertEquals("patient", u.getRole(), "Le rôle devrait être patient");
        assertEquals(creation, u.getDateCreation(), "La date de création devrait être 2024-01-01");
        assertEquals("password123", u.getMotDePasse(), "Le mot de passe devrait être password123");
        assertTrue(u.isActif(), "L'utilisateur devrait être actif");
    }

    @Test
    void testConstructeurInscription() {
        // 🔍 Test du constructeur pour inscription (sans ID, actif par défaut)
        LocalDate naissance = LocalDate.of(1990, 1, 1);

        Utilisateur u = new Utilisateur("DUPONT", "Jean", "jean@test.com",
                "0612345678", naissance, "patient",
                "password123");

        assertEquals("DUPONT", u.getNom(), "Le nom devrait être DUPONT");
        assertEquals("Jean", u.getPrenom(), "Le prénom devrait être Jean");
        assertEquals("jean@test.com", u.getEmail(), "L'email devrait être jean@test.com");
        assertEquals("0612345678", u.getNumeroTel(), "Le téléphone devrait être 0612345678");
        assertEquals(naissance, u.getDateNaissance(), "La date de naissance devrait être 1990-01-01");
        assertEquals("patient", u.getRole(), "Le rôle devrait être patient");
        assertEquals("password123", u.getMotDePasse(), "Le mot de passe devrait être password123");
        assertEquals(LocalDate.now(), u.getDateCreation(), "La date de création devrait être aujourd'hui");
        assertTrue(u.isActif(), "L'utilisateur devrait être actif par défaut");
    }

    @Test
    void testSetters() {
        // 🔍 Test de tous les setters
        Utilisateur u = new Utilisateur();
        LocalDate naissance = LocalDate.of(1995, 5, 5);
        LocalDate creation = LocalDate.of(2024, 2, 20);

        u.setIdUser(2);
        u.setNom("MARTIN");
        u.setPrenom("Marie");
        u.setEmail("marie@test.com");
        u.setNumeroTel("0987654321");
        u.setDateNaissance(naissance);
        u.setRole("psy");
        u.setDateCreation(creation);
        u.setMotDePasse("newpass");
        u.setActif(false);

        assertEquals(2, u.getIdUser(), "ID devrait être 2");
        assertEquals("MARTIN", u.getNom(), "Nom devrait être MARTIN");
        assertEquals("Marie", u.getPrenom(), "Prénom devrait être Marie");
        assertEquals("marie@test.com", u.getEmail(), "Email devrait être marie@test.com");
        assertEquals("0987654321", u.getNumeroTel(), "Téléphone devrait être 0987654321");
        assertEquals(naissance, u.getDateNaissance(), "Date naissance devrait être 1995-05-05");
        assertEquals("psy", u.getRole(), "Rôle devrait être psy");
        assertEquals(creation, u.getDateCreation(), "Date création devrait être 2024-02-20");
        assertEquals("newpass", u.getMotDePasse(), "Mot de passe devrait être newpass");
        assertFalse(u.isActif(), "Utilisateur devrait être inactif");
    }

    @Test
    void testToString() {
        // 🔍 Test de la méthode toString
        Utilisateur u = new Utilisateur(1, "DUPONT", "Jean", "jean@test.com",
                "0612345678", LocalDate.of(1990,1,1),
                "patient", LocalDate.now(), "pass", true);
        String str = u.toString();

        assertTrue(str.contains("idUser=1"), "toString devrait contenir l'ID");
        assertTrue(str.contains("nom='DUPONT'"), "toString devrait contenir le nom");
        assertTrue(str.contains("prenom='Jean'"), "toString devrait contenir le prénom");
        assertTrue(str.contains("email='jean@test.com'"), "toString devrait contenir l'email");
        assertTrue(str.contains("role='patient'"), "toString devrait contenir le rôle");
        assertTrue(str.contains("actif=true"), "toString devrait contenir le statut actif");
    }
}