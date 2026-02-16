package entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité ConnexionLog
 * Vérifie les constructeurs, getters, setters et formatage de date
 */
public class ConnexionLogTest {

    @Test
    void testConstructeurComplet() {
        // 🔍 Test du constructeur avec tous les paramètres
        // Arrange
        LocalDateTime now = LocalDateTime.of(2024, 1, 15, 14, 30, 0);

        // Act
        ConnexionLog log = new ConnexionLog(1, 100, "test@test.com", "Jean Dupont",
                now, "192.168.1.1", "Connexion");

        // Assert
        assertEquals(1, log.getId(), "L'ID du log devrait être 1");
        assertEquals(100, log.getUserId(), "L'ID utilisateur devrait être 100");
        assertEquals("test@test.com", log.getUserEmail(), "L'email devrait être test@test.com");
        assertEquals("Jean Dupont", log.getUserName(), "Le nom devrait être Jean Dupont");
        assertEquals(now, log.getDate(), "La date devrait être 2024-01-15 14:30");
        assertEquals("192.168.1.1", log.getIpAddress(), "L'IP devrait être 192.168.1.1");
        assertEquals("Connexion", log.getStatus(), "Le statut devrait être Connexion");
    }

    @Test
    void testConstructeurAjout() {
        // 🔍 Test du constructeur simplifié pour ajout
        // Act
        ConnexionLog log = new ConnexionLog(100, "test@test.com", "Jean Dupont",
                "Connexion", "192.168.1.1");

        // Assert
        assertEquals(100, log.getUserId(), "L'ID utilisateur devrait être 100");
        assertEquals("test@test.com", log.getUserEmail(), "L'email devrait être test@test.com");
        assertEquals("Jean Dupont", log.getUserName(), "Le nom devrait être Jean Dupont");
        assertEquals("Connexion", log.getStatus(), "Le statut devrait être Connexion");
        assertEquals("192.168.1.1", log.getIpAddress(), "L'IP devrait être 192.168.1.1");
        assertNotNull(log.getDate(), "La date ne devrait pas être null");
    }

    @Test
    void testDateFormatted() {
        // 🔍 Test du formatage de date
        // Arrange
        LocalDateTime now = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
        ConnexionLog log = new ConnexionLog(1, 100, "test@test.com", "Jean",
                now, "192.168.1.1", "Connexion");

        // Act
        String formatted = log.getDateFormatted();

        // Assert
        assertEquals("15/01/2024 14:30:00", formatted,
                "La date formatée devrait être 15/01/2024 14:30:00");
    }

    @Test
    void testSetters() {
        // 🔍 Test des setters et getters
        // Arrange
        ConnexionLog log = new ConnexionLog();
        LocalDateTime now = LocalDateTime.of(2024, 2, 20, 10, 15, 30);

        // Act
        log.setId(5);
        log.setUserId(200);
        log.setUserEmail("new@test.com");
        log.setUserName("Marie Curie");
        log.setDate(now);
        log.setIpAddress("10.0.0.1");
        log.setStatus("Échec");

        // Assert
        assertEquals(5, log.getId(), "ID devrait être 5");
        assertEquals(200, log.getUserId(), "User ID devrait être 200");
        assertEquals("new@test.com", log.getUserEmail(), "Email devrait être new@test.com");
        assertEquals("Marie Curie", log.getUserName(), "Nom devrait être Marie Curie");
        assertEquals(now, log.getDate(), "Date devrait être 2024-02-20 10:15:30");
        assertEquals("10.0.0.1", log.getIpAddress(), "IP devrait être 10.0.0.1");
        assertEquals("Échec", log.getStatus(), "Statut devrait être Échec");
    }
}