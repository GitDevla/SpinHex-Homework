import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spinhex.Score.Score;
import spinhex.model.TwoPhaseActionState.TwoPhaseAction;
import spinhex.model.AxialPosition;
import spinhex.model.*;
import spinhex.Score.ScoreManager;
import java.nio.file.*;

import static java.nio.file.Files.deleteIfExists;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {
    @Test
    public void scoreTest() {
        var score = new Score("a", 10);
        assertEquals(10, score.getScore());
        assertEquals("a", score.getUsername());
    }

    @BeforeEach
    void setUp() {
        try {
            deleteIfExists(Path.of("testScore.json"));
        } catch (java.io.IOException e) {
            System.err.println("Failed to delete testScore.json: " + e.getMessage());
        }
    }

    @Test
    public void scoreManagerWriteTest() {
        var scoreManager = new ScoreManager(Path.of("testScore.json"));
        var score1 = new Score("user1", 100);
        var score2 = new Score("user2", 200);

        try {
            var scores = scoreManager.add(score1);
            assertEquals(1, scores.size());
            assertEquals(score1, scores.getFirst());

            scores = scoreManager.add(score2);
            assertEquals(2, scores.size());
            assertEquals(score2, scores.get(1));
        } catch (java.io.IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }

    @Test
    public void scoreManagerReadTest() {
        var scoreManager = new ScoreManager(Path.of("testScore.json"));
        var score1 = new Score("user1", 100);
        var score2 = new Score("user2", 200);

        try {
            scoreManager.add(score1);
            scoreManager.add(score2);
            var scores = scoreManager.getAll();
            assertEquals(2, scores.size());
            assertTrue(scores.contains(score1));
            assertTrue(scores.contains(score2));
        } catch (java.io.IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }
}
