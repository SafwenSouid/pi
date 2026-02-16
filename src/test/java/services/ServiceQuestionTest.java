package services;

import entities.Question;
import entities.Quiz;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ServiceQuestion
 * Teste les opérations CRUD sur les questions
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceQuestionTest {

    private static ServiceQuestion serviceQuestion;
    private static ServiceQuiz serviceQuiz;
    private static Quiz testQuiz;
    private static int testQuizId;
    private static int testQuestionId;

    @BeforeAll
    static void setUp() throws SQLException {
        serviceQuestion = new ServiceQuestion();
        serviceQuiz = new ServiceQuiz();

        testQuiz = new Quiz(
                "Quiz pour questions",
                "Quiz de test",
                "test",
                true
        );
        serviceQuiz.ajouter(testQuiz);

        List<Quiz> quizzes = serviceQuiz.recuperer();
        testQuizId = quizzes.get(quizzes.size() - 1).getIdQuiz();
    }

    @AfterAll
    static void tearDown() throws SQLException {
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
        System.out.println("=== Test 1: Ajouter une question ===");

        Question question = new Question(
                "Contenu de la question de test?",
                5,
                testQuizId
        );

        serviceQuestion.ajouter(question);

        List<Question> questions = serviceQuestion.recuperer();
        assertNotNull(questions);
        assertTrue(questions.size() > 0);

        Question lastQuestion = questions.get(questions.size() - 1);
        testQuestionId = lastQuestion.getIdQuestion();

        assertEquals("Contenu de la question de test?", lastQuestion.getContenu());
        assertEquals(5, lastQuestion.getScore());
        assertEquals(testQuizId, lastQuestion.getIdQuiz());

        System.out.println("✅ Question ajoutée avec ID: " + testQuestionId);
    }

    @Test
    @Order(2)
    void testRecuperer() throws SQLException {
        System.out.println("=== Test 2: Récupérer toutes les questions ===");

        List<Question> questions = serviceQuestion.recuperer();

        assertNotNull(questions);
        assertTrue(questions.size() > 0);

        System.out.println("✅ " + questions.size() + " questions trouvées");
    }

    @Test
    @Order(3)
    void testRecupererParQuiz() throws SQLException {
        System.out.println("=== Test 3: Récupérer questions par quiz ===");

        List<Question> questions = serviceQuestion.recupererParQuiz(testQuizId);

        assertNotNull(questions);
        assertTrue(questions.size() > 0);

        for (Question q : questions) {
            assertEquals(testQuizId, q.getIdQuiz());
        }

        System.out.println("✅ " + questions.size() + " questions trouvées pour le quiz ID " + testQuizId);
    }

    @Test
    @Order(4)
    void testModifier() throws SQLException {
        System.out.println("=== Test 4: Modifier une question ===");

        List<Question> questions = serviceQuestion.recuperer();
        Question question = null;
        for (Question q : questions) {
            if (q.getIdQuestion() == testQuestionId) {
                question = q;
                break;
            }
        }

        assertNotNull(question);

        question.setContenu("Contenu modifié?");
        question.setScore(10);

        serviceQuestion.modifier(question);

        List<Question> updatedQuestions = serviceQuestion.recuperer();
        Question modified = null;
        for (Question q : updatedQuestions) {
            if (q.getIdQuestion() == testQuestionId) {
                modified = q;
                break;
            }
        }

        assertNotNull(modified);
        assertEquals("Contenu modifié?", modified.getContenu());
        assertEquals(10, modified.getScore());

        System.out.println("✅ Question modifiée");
    }

    @Test
    @Order(5)
    void testSupprimer() throws SQLException {
        System.out.println("=== Test 5: Supprimer une question ===");

        List<Question> beforeDelete = serviceQuestion.recuperer();
        int initialCount = beforeDelete.size();

        Question toDelete = null;
        for (Question q : beforeDelete) {
            if (q.getIdQuestion() == testQuestionId) {
                toDelete = q;
                break;
            }
        }

        assertNotNull(toDelete);

        serviceQuestion.supprimer(toDelete);

        List<Question> afterDelete = serviceQuestion.recuperer();

        assertEquals(initialCount - 1, afterDelete.size());

        System.out.println("✅ Question supprimée");
    }
}