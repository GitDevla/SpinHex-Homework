import org.junit.jupiter.api.Test;
import spinhex.model.TwoPhaseActionState.TwoPhaseAction;

import spinhex.model.*;

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

    @Test
    public void testGetHex() {
        var board = new SpinHexModel();
        assertEquals(HexColor.NONE, board.getHex(new AxialPosition(0, 0)));
        assertEquals(HexColor.RED, board.getHex(new AxialPosition(0, 2)));
        assertEquals(HexColor.BLUE, board.getHex(new AxialPosition(2, 0)));
        assertEquals(HexColor.GREEN, board.getHex(new AxialPosition(4, 1)));

        assertThrows(IllegalArgumentException.class, () -> board.getHex(new AxialPosition(-1, 0)));
        assertThrows(IllegalArgumentException.class, () -> board.getHex(new AxialPosition(5, 5)));
    }

    @Test
    public void testGetNeighbors() {
        var board = new SpinHexModel();
        var neighbors = board.getNeighbors(new AxialPosition(1, 1));
        assertEquals(6, neighbors.size());
        assertTrue(neighbors.contains(HexColor.RED));
        assertTrue(neighbors.contains(HexColor.BLUE));
        assertTrue(neighbors.contains(HexColor.NONE));

        neighbors = board.getNeighbors(new AxialPosition(0, 2));
        assertEquals(6, neighbors.size());
        assertTrue(neighbors.contains(HexColor.RED));
        assertTrue(neighbors.contains(HexColor.NONE));
    }

    @Test
    public void testIsLegalToMoveFrom() {
        var board = new SpinHexModel();
        assertTrue(board.isLegalToMoveFrom(new AxialPosition(2, 2)));

        assertFalse(board.isLegalToMoveFrom(new AxialPosition(0, 2)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(1, 1)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(0, 0)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(4, 4)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(2, 0)));
    }


    @Test
    public void isLegalMove(){
        var board = new SpinHexModel();
        assertTrue(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE)));
        assertTrue(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(3, 2), Rotation.COUNTERCLOCKWISE)));


        assertFalse(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(0, 0), Rotation.CLOCKWISE)));
        assertFalse(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(4, 4), Rotation.COUNTERCLOCKWISE)));
        assertFalse(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(3, 3), null)));
    }
}
