package services;

import entities.Utilisateur;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ServiceUtilisateur
 * Teste toutes les opérations CRUD sur les utilisateurs
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceUtilisateurTest {

    private static ServiceUtilisateur serviceUtilisateur;
    private static Utilisateur testUser;
    private static int testUserId;

    @BeforeAll
    static void setUp() {
        serviceUtilisateur = new ServiceUtilisateur();

        testUser = new Utilisateur(
                "TEST",
                "Test",
                "test.unit@test.com",
                "1234567890",
                LocalDate.of(1990, 1, 1),
                "patient",
                "Test123"
        );
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (testUserId > 0) {
            try {
                Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
                if (user != null) {
                    serviceUtilisateur.supprimer(user);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors du nettoyage: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testAjouter() throws SQLException {
        System.out.println("=== Test 1: Ajouter un utilisateur ===");

        serviceUtilisateur.ajouter(testUser);

        Utilisateur found = null;
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equals("test.unit@test.com")) {
                found = u;
                testUserId = u.getIdUser();
                break;
            }
        }

        assertNotNull(found, "L'utilisateur devrait être trouvé après ajout");
        assertEquals("TEST", found.getNom());
        assertEquals("Test", found.getPrenom());
        assertEquals("test.unit@test.com", found.getEmail());
        assertEquals("patient", found.getRole());
        assertTrue(found.isActif(), "L'utilisateur devrait être actif par défaut");

        System.out.println("✅ Utilisateur ajouté avec ID: " + testUserId);
    }

    @Test
    @Order(2)
    void testRecuperer() throws SQLException {
        System.out.println("=== Test 2: Récupérer tous les utilisateurs ===");

        List<Utilisateur> users = serviceUtilisateur.recuperer();

        assertNotNull(users, "La liste ne devrait pas être null");
        assertTrue(users.size() > 0, "La liste devrait contenir au moins un utilisateur");

        System.out.println("✅ " + users.size() + " utilisateurs trouvés");
    }

    @Test
    @Order(3)
    void testRecupererParId() throws SQLException {
        System.out.println("=== Test 3: Récupérer utilisateur par ID ===");

        assertTrue(testUserId > 0, "L'ID de test devrait être valide");

        Utilisateur user = serviceUtilisateur.recupererParId(testUserId);

        assertNotNull(user, "L'utilisateur devrait être trouvé par son ID");
        assertEquals("test.unit@test.com", user.getEmail());
        assertEquals("TEST", user.getNom());

        System.out.println("✅ Utilisateur trouvé: " + user.getPrenom() + " " + user.getNom());
    }

    @Test
    @Order(4)
    void testModifier() throws SQLException {
        System.out.println("=== Test 4: Modifier un utilisateur ===");

        Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
        assertNotNull(user, "L'utilisateur devrait exister avant modification");

        user.setNom("TEST_UPDATED");
        user.setPrenom("Updated");
        user.setNumeroTel("9876543210");
        user.setRole("psy");

        serviceUtilisateur.modifier(user);

        Utilisateur updated = serviceUtilisateur.recupererParId(testUserId);

        assertEquals("TEST_UPDATED", updated.getNom(), "Le nom devrait être modifié");
        assertEquals("Updated", updated.getPrenom(), "Le prénom devrait être modifié");
        assertEquals("9876543210", updated.getNumeroTel(), "Le téléphone devrait être modifié");
        assertEquals("psy", updated.getRole(), "Le rôle devrait être modifié");

        System.out.println("✅ Utilisateur modifié: " + updated.getPrenom() + " " + updated.getNom());
    }

    @Test
    @Order(5)
    void testRecupererParRole() throws SQLException {
        System.out.println("=== Test 5: Récupérer par rôle ===");

        List<Utilisateur> patients = serviceUtilisateur.recupererParRole("patient");
        List<Utilisateur> psychiatres = serviceUtilisateur.recupererParRole("psy");

        assertNotNull(patients, "La liste des patients ne devrait pas être null");
        assertNotNull(psychiatres, "La liste des psychiatres ne devrait pas être null");

        System.out.println("✅ Patients: " + patients.size());
        System.out.println("✅ Psychiatres: " + psychiatres.size());
    }

    @Test
    @Order(6)
    void testRecupererParStatut() throws SQLException {
        System.out.println("=== Test 6: Récupérer par statut actif/inactif ===");

        List<Utilisateur> actifs = serviceUtilisateur.recupererParStatut(true);
        List<Utilisateur> inactifs = serviceUtilisateur.recupererParStatut(false);

        assertNotNull(actifs, "La liste des actifs ne devrait pas être null");
        assertNotNull(inactifs, "La liste des inactifs ne devrait pas être null");

        System.out.println("✅ Actifs: " + actifs.size());
        System.out.println("✅ Inactifs: " + inactifs.size());
    }

    @Test
    @Order(7)
    void testChangerStatut() throws SQLException {
        System.out.println("=== Test 7: Changer statut actif/inactif ===");

        Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
        assertNotNull(user);

        // Désactiver
        serviceUtilisateur.changerStatut(testUserId, false);

        Utilisateur inactive = serviceUtilisateur.recupererParId(testUserId);
        assertFalse(inactive.isActif(), "L'utilisateur devrait être inactif");

        // Réactiver
        serviceUtilisateur.changerStatut(testUserId, true);

        Utilisateur active = serviceUtilisateur.recupererParId(testUserId);
        assertTrue(active.isActif(), "L'utilisateur devrait être actif");

        System.out.println("✅ Statut changé avec succès");
    }

    @Test
    @Order(8)
    void testRechercher() throws SQLException {
        System.out.println("=== Test 8: Rechercher utilisateurs ===");

        List<Utilisateur> results = serviceUtilisateur.rechercher("test");

        assertNotNull(results, "Les résultats de recherche ne devraient pas être null");
        assertTrue(results.size() > 0, "Au moins un résultat devrait être trouvé pour 'test'");

        System.out.println("✅ " + results.size() + " résultats trouvés pour 'test'");
    }

    @Test
    @Order(9)
    void testCompter() throws SQLException {
        System.out.println("=== Test 9: Compter les utilisateurs ===");

        int total = serviceUtilisateur.compterUtilisateurs();
        int patients = serviceUtilisateur.compterParRole("patient");
        int actifs = serviceUtilisateur.compterParStatut(true);

        assertTrue(total > 0, "Le total devrait être > 0");
        assertTrue(patients >= 0, "Le nombre de patients devrait être >= 0");
        assertTrue(actifs >= 0, "Le nombre d'actifs devrait être >= 0");

        System.out.println("✅ Total: " + total);
        System.out.println("✅ Patients: " + patients);
        System.out.println("✅ Actifs: " + actifs);
    }

    @Test
    @Order(10)
    void testSupprimer() throws SQLException {
        System.out.println("=== Test 10: Supprimer un utilisateur ===");

        Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
        assertNotNull(user, "L'utilisateur devrait exister avant suppression");

        serviceUtilisateur.supprimer(user);

        Utilisateur deleted = serviceUtilisateur.recupererParId(testUserId);
        assertNull(deleted, "L'utilisateur ne devrait plus exister après suppression");

        System.out.println("✅ Utilisateur supprimé avec succès");
    }
}