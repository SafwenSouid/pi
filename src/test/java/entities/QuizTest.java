package entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Quiz
 */
public class QuizTest {

    @Test
    void testConstructeurComplet() {
        // 🔍 Test du constructeur avec ID
        Quiz q = new Quiz(1, "Quiz de test", "Description", "psychologique", true);

        assertEquals(1, q.getIdQuiz(), "L'ID du quiz devrait être 1");
        assertEquals("Quiz de test", q.getTitre(), "Le titre devrait être 'Quiz de test'");
        assertEquals("Description", q.getDescription(), "La description devrait être 'Description'");
        assertEquals("psychologique", q.getType(), "Le type devrait être 'psychologique'");
        assertTrue(q.isActif(), "Le quiz devrait être actif");
    }

    @Test
    void testConstructeurSansId() {
        // 🔍 Test du constructeur sans ID
        Quiz q = new Quiz("Quiz de test", "Description", "psychologique", false);

        assertEquals("Quiz de test", q.getTitre(), "Le titre devrait être 'Quiz de test'");
        assertEquals("Description", q.getDescription(), "La description devrait être 'Description'");
        assertEquals("psychologique", q.getType(), "Le type devrait être 'psychologique'");
        assertFalse(q.isActif(), "Le quiz devrait être inactif");
    }

    @Test
    void testSetters() {
        // 🔍 Test des setters
        Quiz q = new Quiz();

        q.setIdQuiz(2);
        q.setTitre("Nouveau titre");
        q.setDescription("Nouvelle description");
        q.setType("test");
        q.setActif(true);

        assertEquals(2, q.getIdQuiz(), "ID devrait être 2");
        assertEquals("Nouveau titre", q.getTitre(), "Titre devrait être 'Nouveau titre'");
        assertEquals("Nouvelle description", q.getDescription(), "Description devrait être 'Nouvelle description'");
        assertEquals("test", q.getType(), "Type devrait être 'test'");
        assertTrue(q.isActif(), "Quiz devrait être actif");
    }

    @Test
    void testToString() {
        // 🔍 Test de toString
        Quiz q = new Quiz(1, "Quiz", "Desc", "type", true);
        String str = q.toString();

        assertTrue(str.contains("idQuiz=1"), "toString devrait contenir l'ID");
        assertTrue(str.contains("titre='Quiz'"), "toString devrait contenir le titre");
        assertTrue(str.contains("description='Desc'"), "toString devrait contenir la description");
        assertTrue(str.contains("type='type'"), "toString devrait contenir le type");
        assertTrue(str.contains("actif=true"), "toString devrait contenir le statut actif");
    }
}