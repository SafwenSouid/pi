package services;

import entities.ConnexionLog;
import entities.Utilisateur;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ServiceConnexionLog
 * Teste la gestion des logs de connexion
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceConnexionLogTest {

    private static ServiceConnexionLog serviceConnexionLog;
    private static ServiceUtilisateur serviceUtilisateur;
    private static Utilisateur testUser;
    private static int testUserId;

    @BeforeAll
    static void setUp() throws SQLException {
        serviceConnexionLog = new ServiceConnexionLog();
        serviceUtilisateur = new ServiceUtilisateur();

        testUser = new Utilisateur(
                "TEST_LOG",
                "Log",
                "log.test@test.com",
                "1234567890",
                LocalDate.of(1990, 1, 1),
                "patient",
                "Test123"
        );
        serviceUtilisateur.ajouter(testUser);

        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equals("log.test@test.com")) {
                testUserId = u.getIdUser();
                break;
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (testUserId > 0) {
            Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
            if (user != null) {
                serviceUtilisateur.supprimer(user);
            }
        }
    }

    @Test
    void testRecuperer() throws SQLException {
        System.out.println("=== Test 1: Récupérer tous les logs ===");

        List<ConnexionLog> logs = serviceConnexionLog.recuperer();

        assertNotNull(logs);
        assertTrue(logs.size() >= 0);

        System.out.println("✅ " + logs.size() + " logs trouvés");
    }

    @Test
    void testRecupererParUtilisateur() throws SQLException {
        System.out.println("=== Test 2: Récupérer logs par utilisateur ===");

        List<ConnexionLog> userLogs = serviceConnexionLog.recupererParUtilisateur(testUserId);

        assertNotNull(userLogs);
        assertTrue(userLogs.size() >= 0);

        for (ConnexionLog log : userLogs) {
            assertEquals(testUserId, log.getUserId());
        }

        System.out.println("✅ " + userLogs.size() + " logs pour l'utilisateur " + testUserId);
    }

    @Test
    void testRecupererParDate() throws SQLException {
        System.out.println("=== Test 3: Récupérer logs par date ===");

        LocalDateTime debut = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);

        List<ConnexionLog> dateLogs = serviceConnexionLog.recupererParDate(debut, fin);

        assertNotNull(dateLogs);

        System.out.println("✅ " + dateLogs.size() + " logs trouvés entre " +
                debut.toLocalDate() + " et " + fin.toLocalDate());
    }

    @Test
    void testRecupererConnexionsSuspectes() throws SQLException {
        System.out.println("=== Test 4: Récupérer connexions suspectes ===");

        List<ConnexionLog> suspectLogs = serviceConnexionLog.recupererConnexionsSuspectes();

        assertNotNull(suspectLogs, "La liste des connexions suspectes ne devrait pas être null");

        System.out.println("✅ " + suspectLogs.size() + " connexions suspectes trouvées");
    }

    @Test
    void testNettoyerAnciensLogs() throws SQLException {
        System.out.println("=== Test 5: Nettoyer les anciens logs ===");

        int deleted = serviceConnexionLog.nettoyerAnciensLogs(30);

        System.out.println("✅ " + deleted + " anciens logs supprimés");
    }

    @Test
    void testCompterConnexionsAujourdhui() throws SQLException {
        System.out.println("=== Test 6: Compter les connexions aujourd'hui ===");

        int count = serviceConnexionLog.compterConnexionsAujourdhui();

        assertTrue(count >= 0);

        System.out.println("✅ " + count + " connexions aujourd'hui");
    }
}