package services;

import entities.QuizResult;
import entities.Utilisateur;
import entities.Quiz;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ServiceQuizResult
 * Teste les opérations CRUD sur les résultats de quiz
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceQuizResultTest {

    private static ServiceQuizResult serviceQuizResult;
    private static ServiceUtilisateur serviceUtilisateur;
    private static ServiceQuiz serviceQuiz;
    private static Utilisateur testUser;
    private static Quiz testQuiz;
    private static int testUserId;
    private static int testQuizId;
    private static int testResultId;

    @BeforeAll
    static void setUp() throws SQLException {
        serviceQuizResult = new ServiceQuizResult();
        serviceUtilisateur = new ServiceUtilisateur();
        serviceQuiz = new ServiceQuiz();

        testUser = new Utilisateur(
                "TEST_RESULT",
                "Result",
                "result.test@test.com",
                "1234567890",
                LocalDate.of(1990, 1, 1),
                "patient",
                "Test123"
        );
        serviceUtilisateur.ajouter(testUser);

        for (Utilisateur u : serviceUtilisateur.recuperer()) {
            if (u.getEmail().equals("result.test@test.com")) {
                testUserId = u.getIdUser();
                break;
            }
        }

        testQuiz = new Quiz(
                "Quiz pour résultats",
                "Description",
                "test",
                true
        );
        serviceQuiz.ajouter(testQuiz);

        List<Quiz> quizzes = serviceQuiz.recuperer();
        testQuizId = quizzes.get(quizzes.size() - 1).getIdQuiz();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (testUserId > 0) {
            Utilisateur user = serviceUtilisateur.recupererParId(testUserId);
            if (user != null) {
                serviceUtilisateur.supprimer(user);
            }
        }

        List<Quiz> quizzes = serviceQuiz.recuperer();
        if (!quizzes.isEmpty()) {
            for (Quiz q : quizzes) {
                if (q.getIdQuiz() == testQuizId) {
                    serviceQuiz.supprimer(q);
                    break;
                }
            }
        }
    }

    @Test
    @Order(1)
    void testAjouter() throws SQLException {
        System.out.println("=== Test 1: Ajouter un résultat de quiz ===");

        QuizResult result = new QuizResult(
                35,
                "Stress modéré détecté",
                testUserId,
                testQuizId
        );

        serviceQuizResult.ajouter(result);

        List<QuizResult> results = serviceQuizResult.recuperer();
        assertNotNull(results);
        assertTrue(results.size() > 0);

        QuizResult lastResult = results.get(results.size() - 1);
        testResultId = lastResult.getIdResult();

        assertEquals(35, lastResult.getScoreTotal());
        assertEquals("Stress modéré détecté", lastResult.getInterpretation());
        assertEquals(testUserId, lastResult.getIdUser());
        assertEquals(testQuizId, lastResult.getIdQuiz());
        assertEquals(LocalDate.now(), lastResult.getDatePassage());

        System.out.println("✅ Résultat ajouté avec ID: " + testResultId);
    }

    @Test
    @Order(2)
    void testRecuperer() throws SQLException {
        System.out.println("=== Test 2: Récupérer tous les résultats ===");

        List<QuizResult> results = serviceQuizResult.recuperer();

        assertNotNull(results);
        assertTrue(results.size() > 0);

        System.out.println("✅ " + results.size() + " résultats trouvés");
    }

    @Test
    @Order(3)
    void testRecupererParUtilisateur() throws SQLException {
        System.out.println("=== Test 3: Récupérer résultats par utilisateur ===");

        List<QuizResult> userResults = serviceQuizResult.recupererParUtilisateur(testUserId);

        assertNotNull(userResults);
        assertTrue(userResults.size() > 0);

        for (QuizResult r : userResults) {
            assertEquals(testUserId, r.getIdUser());
        }

        System.out.println("✅ " + userResults.size() + " résultats pour l'utilisateur " + testUserId);
    }

    @Test
    @Order(4)
    void testModifier() throws SQLException {
        System.out.println("=== Test 4: Modifier un résultat ===");

        List<QuizResult> results = serviceQuizResult.recuperer();
        QuizResult resultToModify = null;
        for (QuizResult r : results) {
            if (r.getIdResult() == testResultId) {
                resultToModify = r;
                break;
            }
        }

        assertNotNull(resultToModify);

        resultToModify.setScoreTotal(42);
        resultToModify.setInterpretation("Interprétation modifiée");

        serviceQuizResult.modifier(resultToModify);

        List<QuizResult> afterModify = serviceQuizResult.recuperer();
        QuizResult modified = null;
        for (QuizResult r : afterModify) {
            if (r.getIdResult() == testResultId) {
                modified = r;
                break;
            }
        }

        assertNotNull(modified);
        assertEquals(42, modified.getScoreTotal());
        assertEquals("Interprétation modifiée", modified.getInterpretation());

        System.out.println("✅ Résultat modifié");
    }

    @Test
    @Order(5)
    void testSupprimer() throws SQLException {
        System.out.println("=== Test 5: Supprimer un résultat ===");

        List<QuizResult> beforeDelete = serviceQuizResult.recuperer();
        int initialCount = beforeDelete.size();

        QuizResult toDelete = null;
        for (QuizResult r : beforeDelete) {
            if (r.getIdResult() == testResultId) {
                toDelete = r;
                break;
            }
        }

        assertNotNull(toDelete);

        serviceQuizResult.supprimer(toDelete);

        List<QuizResult> afterDelete = serviceQuizResult.recuperer();

        assertEquals(initialCount - 1, afterDelete.size());

        System.out.println("✅ Résultat supprimé");
    }
}