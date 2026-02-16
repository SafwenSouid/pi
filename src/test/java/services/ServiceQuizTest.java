package services;

import entities.Quiz;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ServiceQuiz
 * Teste les opérations CRUD sur les quiz
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceQuizTest {

    private static ServiceQuiz serviceQuiz;
    private static Quiz testQuiz;
    private static int testQuizId;

    @BeforeAll
    static void setUp() {
        serviceQuiz = new ServiceQuiz();

        testQuiz = new Quiz(
                "Test Quiz",
                "Description du quiz de test",
                "psychologique",
                true
        );
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (testQuizId > 0) {
            List<Quiz> quizzes = serviceQuiz.recuperer();
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
        System.out.println("=== Test 1: Ajouter un quiz ===");

        serviceQuiz.ajouter(testQuiz);

        List<Quiz> quizzes = serviceQuiz.recuperer();
        assertNotNull(quizzes);
        assertTrue(quizzes.size() > 0);

        Quiz lastQuiz = quizzes.get(quizzes.size() - 1);
        testQuizId = lastQuiz.getIdQuiz();

        assertEquals("Test Quiz", lastQuiz.getTitre());
        assertEquals("Description du quiz de test", lastQuiz.getDescription());
        assertEquals("psychologique", lastQuiz.getType());
        assertTrue(lastQuiz.isActif());

        System.out.println("✅ Quiz ajouté avec ID: " + testQuizId);
    }

    @Test
    @Order(2)
    void testRecuperer() throws SQLException {
        System.out.println("=== Test 2: Récupérer tous les quiz ===");

        List<Quiz> quizzes = serviceQuiz.recuperer();

        assertNotNull(quizzes);
        assertTrue(quizzes.size() > 0);

        System.out.println("✅ " + quizzes.size() + " quiz trouvés");
    }

    @Test
    @Order(3)
    void testModifier() throws SQLException {
        System.out.println("=== Test 3: Modifier un quiz ===");

        List<Quiz> quizzes = serviceQuiz.recuperer();
        Quiz quiz = null;
        for (Quiz q : quizzes) {
            if (q.getIdQuiz() == testQuizId) {
                quiz = q;
                break;
            }
        }

        assertNotNull(quiz);

        quiz.setTitre("Test Quiz Modifié");
        quiz.setDescription("Description modifiée");
        quiz.setType("test");
        quiz.setActif(false);

        serviceQuiz.modifier(quiz);

        List<Quiz> updatedQuizzes = serviceQuiz.recuperer();
        Quiz updated = null;
        for (Quiz q : updatedQuizzes) {
            if (q.getIdQuiz() == testQuizId) {
                updated = q;
                break;
            }
        }

        assertNotNull(updated);
        assertEquals("Test Quiz Modifié", updated.getTitre());
        assertEquals("Description modifiée", updated.getDescription());
        assertEquals("test", updated.getType());
        assertFalse(updated.isActif());

        System.out.println("✅ Quiz modifié");
    }

    @Test
    @Order(4)
    void testSupprimer() throws SQLException {
        System.out.println("=== Test 4: Supprimer un quiz ===");

        List<Quiz> beforeDelete = serviceQuiz.recuperer();
        int initialCount = beforeDelete.size();

        Quiz toDelete = null;
        for (Quiz q : beforeDelete) {
            if (q.getIdQuiz() == testQuizId) {
                toDelete = q;
                break;
            }
        }

        assertNotNull(toDelete);

        serviceQuiz.supprimer(toDelete);

        List<Quiz> afterDelete = serviceQuiz.recuperer();

        assertEquals(initialCount - 1, afterDelete.size());

        System.out.println("✅ Quiz supprimé");
    }
}