import org.junit.jupiter.api.Test;
import spinhex.model.AxialPosition;
import spinhex.model.SpinHexModel;

import static org.junit.jupiter.api.Assertions.*;

public class SpinHexModelTest {
    @Test
    public void testBoundsDetection() {
        var board = new SpinHexModel();
        assertTrue(board.isInBounds(new AxialPosition(0, 0)));
        assertTrue(board.isInBounds(new AxialPosition(1, 3)));
        assertTrue(board.isInBounds(new AxialPosition(4, 4)));

        assertFalse(board.isInBounds(new AxialPosition(-1, 0)));
        assertFalse(board.isInBounds(new AxialPosition(0, -1)));
        assertFalse(board.isInBounds(new AxialPosition(5, 0)));
        assertFalse(board.isInBounds(new AxialPosition(0, 5)));
    }
}
