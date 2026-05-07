import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class GUIQuizTest {

     @Test
    void testCorrectAnswer() {
        GUIQuiz quiz = new GUIQuiz();
        quiz.checkAnswer("True", "True");
        assertEquals(1, quiz.getScore());  // because user got it right
}

    @Test
    void testIncorrectAnswer() {
        GUIQuiz quiz = new GUIQuiz();
        quiz.checkAnswer("False", "True");
        assertEquals(0, quiz.getScore());  // because user got it wrong
    }

    @Test
    void testEmptyAnswer() {
        GUIQuiz quiz = new GUIQuiz();
        quiz.checkAnswer("", "True"); // User gave an empty answer
        assertEquals(0, quiz.getScore()); // Score should stay 0
    }
    
}
