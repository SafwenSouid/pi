package entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité QuizResult
 */
public class QuizResultTest {

    @Test
    void testConstructeurComplet() {
        // 🔍 Test du constructeur complet avec ID
        LocalDate date = LocalDate.of(2024, 1, 15);
        QuizResult r = new QuizResult(1, 35, date, "Stress modéré", 100, 5);

        assertEquals(1, r.getIdResult(), "L'ID du résultat devrait être 1");
        assertEquals(35, r.getScoreTotal(), "Le score total devrait être 35");
        assertEquals(date, r.getDatePassage(), "La date devrait être 2024-01-15");
        assertEquals("Stress modéré", r.getInterpretation(), "L'interprétation devrait être 'Stress modéré'");
        assertEquals(100, r.getIdUser(), "L'ID utilisateur devrait être 100");
        assertEquals(5, r.getIdQuiz(), "L'ID quiz devrait être 5");
    }

    @Test
    void testConstructeurAjout() {
        // 🔍 Test du constructeur pour ajout (sans ID, date auto)
        QuizResult r = new QuizResult(42, "Stress élevé", 100, 5);

        assertEquals(42, r.getScoreTotal(), "Le score total devrait être 42");
        assertEquals("Stress élevé", r.getInterpretation(), "L'interprétation devrait être 'Stress élevé'");
        assertEquals(100, r.getIdUser(), "L'ID utilisateur devrait être 100");
        assertEquals(5, r.getIdQuiz(), "L'ID quiz devrait être 5");
        assertEquals(LocalDate.now(), r.getDatePassage(), "La date devrait être aujourd'hui");
    }

    @Test
    void testSetters() {
        // 🔍 Test des setters
        QuizResult r = new QuizResult();
        LocalDate date = LocalDate.of(2024, 2, 20);

        r.setIdResult(2);
        r.setScoreTotal(50);
        r.setDatePassage(date);
        r.setInterpretation("État stable");
        r.setIdUser(200);
        r.setIdQuiz(10);

        assertEquals(2, r.getIdResult(), "ID résultat devrait être 2");
        assertEquals(50, r.getScoreTotal(), "Score devrait être 50");
        assertEquals(date, r.getDatePassage(), "Date devrait être 2024-02-20");
        assertEquals("État stable", r.getInterpretation(), "Interprétation devrait être 'État stable'");
        assertEquals(200, r.getIdUser(), "ID utilisateur devrait être 200");
        assertEquals(10, r.getIdQuiz(), "ID quiz devrait être 10");
    }

    @Test
    void testToString() {
        // 🔍 Test de toString
        QuizResult r = new QuizResult(1, 35, LocalDate.now(), "Test", 100, 5);
        String str = r.toString();

        assertTrue(str.contains("idResult=1"), "toString devrait contenir l'ID résultat");
        assertTrue(str.contains("scoreTotal=35"), "toString devrait contenir le score");
        assertTrue(str.contains("interpretation='Test'"), "toString devrait contenir l'interprétation");
        assertTrue(str.contains("idUser=100"), "toString devrait contenir l'ID utilisateur");
        assertTrue(str.contains("idQuiz=5"), "toString devrait contenir l'ID quiz");
    }
}