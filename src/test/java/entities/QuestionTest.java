package entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Question
 * Vérifie les constructeurs et les accesseurs
 */
public class QuestionTest {

    @Test
    void testConstructeurComplet() {
        // 🔍 Test du constructeur avec ID
        Question q = new Question(1, "Contenu de la question?", 5, 10);

        assertEquals(1, q.getIdQuestion(), "L'ID de la question devrait être 1");
        assertEquals("Contenu de la question?", q.getContenu(), "Le contenu devrait être 'Contenu de la question?'");
        assertEquals(5, q.getScore(), "Le score devrait être 5");
        assertEquals(10, q.getIdQuiz(), "L'ID du quiz devrait être 10");
    }

    @Test
    void testConstructeurSansId() {
        // 🔍 Test du constructeur sans ID (pour ajout)
        Question q = new Question("Contenu de la question?", 5, 10);

        assertEquals("Contenu de la question?", q.getContenu(), "Le contenu devrait être 'Contenu de la question?'");
        assertEquals(5, q.getScore(), "Le score devrait être 5");
        assertEquals(10, q.getIdQuiz(), "L'ID du quiz devrait être 10");
    }

    @Test
    void testSetters() {
        // 🔍 Test des setters
        Question q = new Question();

        q.setIdQuestion(2);
        q.setContenu("Nouvelle question?");
        q.setScore(10);
        q.setIdQuiz(20);

        assertEquals(2, q.getIdQuestion(), "ID devrait être 2");
        assertEquals("Nouvelle question?", q.getContenu(), "Contenu devrait être 'Nouvelle question?'");
        assertEquals(10, q.getScore(), "Score devrait être 10");
        assertEquals(20, q.getIdQuiz(), "ID quiz devrait être 20");
    }

    @Test
    void testToString() {
        // 🔍 Test de la méthode toString
        Question q = new Question(1, "Question?", 5, 10);
        String str = q.toString();

        assertTrue(str.contains("idQuestion=1"), "toString devrait contenir l'ID");
        assertTrue(str.contains("contenu='Question?'"), "toString devrait contenir le contenu");
        assertTrue(str.contains("score=5"), "toString devrait contenir le score");
        assertTrue(str.contains("idQuiz=10"), "toString devrait contenir l'ID du quiz");
    }
}