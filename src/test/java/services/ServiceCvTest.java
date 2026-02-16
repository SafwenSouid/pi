package services;

import entities.Cv;
import entities.Utilisateur;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ServiceCv
 * Teste toutes les opérations CRUD sur les CV
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceCvTest {

    private static ServiceCv serviceCv;
    private static ServiceUtilisateur serviceUtilisateur;
    private static Utilisateur testUser;
    private static int testUserId;
    private static int testCvId;

    @BeforeAll
    static void setUp() throws SQLException {
        // 🔧 Initialisation avant tous les tests
        // Création des services et d'un utilisateur de test
        serviceCv = new ServiceCv();
        serviceUtilisateur = new ServiceUtilisateur();

        testUser = new Utilisateur(
                "TEST_CV",
                "Cv",
                "cv.test@test.com",
                "1234567890",
                LocalDate.of(1990, 1, 1),
                "coach de vie",
                "Test123"
        );
        serviceUtilisateur.ajouter(testUser);

        // Récupérer l'ID de l'utilisateur créé
        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equals("cv.test@test.com")) {
                testUserId = u.getIdUser();
                break;
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // 🧹 Nettoyage après tous les tests
        if (testUserId > 0) {
            Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
            if (user != null) {
                serviceUtilisateur.supprimer(user);
            }
        }
    }

    @Test
    @Order(1)
    void testAjouter() throws SQLException {
        // ✅ TEST 1: Ajouter un CV
        // Vérifie que l'ajout fonctionne et que les données sont correctes
        System.out.println("=== Test 1: Ajouter un CV ===");

        Cv cv = new Cv(
                testUserId,
                "mon_cv.pdf",
                "uploads/cv/mon_cv.pdf",
                1024
        );

        serviceCv.ajouter(cv);

        List<Cv> cvs = serviceCv.recuperer();
        assertNotNull(cvs, "La liste des CV ne devrait pas être null");
        assertTrue(cvs.size() > 0, "La liste des CV devrait contenir au moins un élément");

        Cv lastCv = cvs.get(cvs.size() - 1);
        testCvId = lastCv.getIdCv();

        assertEquals(testUserId, lastCv.getIdUser(), "L'ID utilisateur devrait correspondre");
        assertEquals("mon_cv.pdf", lastCv.getNomFichier(), "Le nom du fichier devrait être mon_cv.pdf");
        assertEquals("uploads/cv/mon_cv.pdf", lastCv.getCheminFichier(), "Le chemin devrait correspondre");
        assertEquals(1024, lastCv.getTailleFichier(), "La taille devrait être 1024");
        assertEquals("en_attente", lastCv.getStatut(), "Le statut initial devrait être en_attente");
        assertEquals(LocalDate.now(), lastCv.getDateUpload(), "La date d'upload devrait être aujourd'hui");

        System.out.println("✅ CV ajouté avec ID: " + testCvId);
    }

    @Test
    @Order(2)
    void testRecuperer() throws SQLException {
        // ✅ TEST 2: Récupérer tous les CV
        // Vérifie que la méthode recuperer() retourne bien tous les CV
        System.out.println("=== Test 2: Récupérer tous les CV ===");

        List<Cv> cvs = serviceCv.recuperer();

        assertNotNull(cvs, "La liste ne devrait pas être null");
        assertTrue(cvs.size() > 0, "La liste devrait contenir au moins le CV de test");

        System.out.println("✅ " + cvs.size() + " CV trouvés");
    }

    @Test
    @Order(3)
    void testRecupererParUtilisateur() throws SQLException {
        // ✅ TEST 3: Récupérer les CV d'un utilisateur spécifique
        System.out.println("=== Test 3: Récupérer CV par utilisateur ===");

        List<Cv> userCvs = serviceCv.recupererParUtilisateur(testUserId);

        assertNotNull(userCvs, "La liste ne devrait pas être null");
        assertTrue(userCvs.size() > 0, "L'utilisateur devrait avoir au moins un CV");

        for (Cv cv : userCvs) {
            assertEquals(testUserId, cv.getIdUser(), "Tous les CV devraient appartenir à l'utilisateur de test");
        }

        System.out.println("✅ " + userCvs.size() + " CV pour l'utilisateur " + testUserId);
    }

    @Test
    @Order(4)
    void testRecupererEnAttente() throws SQLException {
        // ✅ TEST 4: Récupérer les CV en attente
        System.out.println("=== Test 4: Récupérer CV en attente ===");

        List<Cv> pendingCvs = serviceCv.recupererEnAttente();

        assertNotNull(pendingCvs, "La liste ne devrait pas être null");

        boolean found = false;
        for (Cv cv : pendingCvs) {
            if (cv.getIdCv() == testCvId) {
                found = true;
                assertEquals("en_attente", cv.getStatut(), "Le statut devrait être en_attente");
                break;
            }
        }

        assertTrue(found, "Le CV de test devrait être dans la liste des CV en attente");

        System.out.println("✅ " + pendingCvs.size() + " CV en attente");
    }

    @Test
    @Order(5)
    void testValiderCv() throws SQLException {
        // ✅ TEST 5: Valider un CV
        System.out.println("=== Test 5: Valider un CV ===");

        serviceCv.validerCv(testCvId);

        List<Cv> cvs = serviceCv.recuperer();
        Cv found = null;
        for (Cv cv : cvs) {
            if (cv.getIdCv() == testCvId) {
                found = cv;
                break;
            }
        }

        assertNotNull(found, "Le CV devrait exister");
        assertEquals("valide", found.getStatut(), "Le statut devrait être 'valide' après validation");

        System.out.println("✅ CV validé");
    }

    @Test
    @Order(6)
    void testModifier() throws SQLException {
        // ✅ TEST 6: Modifier un CV
        System.out.println("=== Test 6: Modifier un CV ===");

        List<Cv> cvs = serviceCv.recuperer();
        Cv cv = null;
        for (Cv c : cvs) {
            if (c.getIdCv() == testCvId) {
                cv = c;
                break;
            }
        }

        assertNotNull(cv, "Le CV devrait exister avant modification");

        cv.setNomFichier("cv_modifie.pdf");
        cv.setCheminFichier("uploads/cv/cv_modifie.pdf");
        cv.setTailleFichier(2048);

        serviceCv.modifier(cv);

        List<Cv> updatedCvs = serviceCv.recuperer();
        Cv updated = null;
        for (Cv c : updatedCvs) {
            if (c.getIdCv() == testCvId) {
                updated = c;
                break;
            }
        }

        assertNotNull(updated, "Le CV devrait exister après modification");
        assertEquals("cv_modifie.pdf", updated.getNomFichier(), "Le nom du fichier devrait être modifié");
        assertEquals("uploads/cv/cv_modifie.pdf", updated.getCheminFichier(), "Le chemin devrait être modifié");
        assertEquals(2048, updated.getTailleFichier(), "La taille devrait être modifiée");

        System.out.println("✅ CV modifié");
    }

    @Test
    @Order(7)
    void testRefuserCv() throws SQLException {
        // ✅ TEST 7: Refuser un CV avec commentaire
        System.out.println("=== Test 7: Refuser un CV ===");

        // Créer un nouveau CV pour tester le refus
        Cv cv = new Cv(
                testUserId,
                "a_refuser.pdf",
                "uploads/cv/a_refuser.pdf",
                2048
        );
        serviceCv.ajouter(cv);

        List<Cv> cvs = serviceCv.recuperer();
        Cv lastCv = cvs.get(cvs.size() - 1);
        int newCvId = lastCv.getIdCv();

        String commentaire = "CV incomplet";
        serviceCv.refuserCv(newCvId, commentaire);

        List<Cv> afterRefuse = serviceCv.recuperer();
        Cv refused = null;
        for (Cv c : afterRefuse) {
            if (c.getIdCv() == newCvId) {
                refused = c;
                break;
            }
        }

        assertNotNull(refused, "Le CV refusé devrait exister");
        assertEquals("refuse", refused.getStatut(), "Le statut devrait être 'refuse'");
        assertEquals(commentaire, refused.getCommentaire(), "Le commentaire devrait correspondre");

        System.out.println("✅ CV refusé avec commentaire");

        // Nettoyer
        serviceCv.supprimer(refused);
    }

    @Test
    @Order(8)
    void testSupprimer() throws SQLException {
        // ✅ TEST 8: Supprimer un CV
        System.out.println("=== Test 8: Supprimer un CV ===");

        List<Cv> beforeDelete = serviceCv.recuperer();
        int initialCount = beforeDelete.size();

        Cv toDelete = null;
        for (Cv cv : beforeDelete) {
            if (cv.getIdCv() == testCvId) {
                toDelete = cv;
                break;
            }
        }

        assertNotNull(toDelete, "Le CV à supprimer devrait exister");

        serviceCv.supprimer(toDelete);

        List<Cv> afterDelete = serviceCv.recuperer();

        assertEquals(initialCount - 1, afterDelete.size(),
                "Le nombre de CV devrait diminuer de 1 après suppression");

        System.out.println("✅ CV supprimé");
    }
}