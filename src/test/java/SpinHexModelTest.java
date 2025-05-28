import org.junit.jupiter.api.Test;
import spinhex.model.TwoPhaseActionState.TwoPhaseAction;
import spinhex.model.AxialPosition;
import spinhex.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpinHexModelTest {
    public static HexColor[][] smallBoardStart = new HexColor[][] {
            { HexColor.NONE, HexColor.RED, HexColor.RED },
            { HexColor.RED, HexColor.GREEN, HexColor.RED },
            { HexColor.BLUE, HexColor.RED, HexColor.NONE }
    };
    public static HexColor[][] smallBoardTarget = new HexColor[][] {
            { HexColor.NONE, HexColor.BLUE, HexColor.RED },
            { HexColor.RED, HexColor.GREEN, HexColor.RED },
            { HexColor.RED, HexColor.RED, HexColor.NONE }
    };

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
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(0, 0)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(1, 1)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(0, 0)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(4, 4)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(2, 0)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(-1, 0)));
        assertFalse(board.isLegalToMoveFrom(new AxialPosition(5, 5)));
    }

    @Test
    public void testIsLegalMove() {
        var board = new SpinHexModel();
        assertTrue(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE)));
        assertTrue(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(3, 2), Rotation.COUNTERCLOCKWISE)));

        assertFalse(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(0, 0), Rotation.CLOCKWISE)));
        assertFalse(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(4, 4), Rotation.COUNTERCLOCKWISE)));
        assertFalse(board.isLegalMove(new TwoPhaseAction<>(new AxialPosition(2, 2), null)));
    }

    @Test
    public void testInvalidRotation() {
        var board = new SpinHexModel();
        assertThrows(IllegalArgumentException.class,
                () -> board.makeMove(new TwoPhaseAction<>(new AxialPosition(-1, -1), Rotation.CLOCKWISE)));
    }

    @Test
    public void testRotateClockwise() {
        var board = new SpinHexModel();
        var centerStartingHex = board.getHex(new AxialPosition(2, 2));
        var baseNeighbors = board.getNeighbors(new AxialPosition(2, 2));
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE));
        var centerRotatedHex = board.getHex(new AxialPosition(2, 2));
        var rotatedNeighbors = board.getNeighbors(new AxialPosition(2, 2));

        assertTrue(rotatedNeighbors.containsAll(baseNeighbors));
        assertEquals(centerStartingHex, centerRotatedHex);
    }

    @Test
    public void testRotateCounterClockwise() {
        var board = new SpinHexModel();
        var centerStartingHex = board.getHex(new AxialPosition(2, 2));
        var baseNeighbors = board.getNeighbors(new AxialPosition(2, 2));
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.COUNTERCLOCKWISE));
        var centerRotatedHex = board.getHex(new AxialPosition(2, 2));
        var rotatedNeighbors = board.getNeighbors(new AxialPosition(2, 2));

        assertTrue(rotatedNeighbors.containsAll(baseNeighbors));
        assertEquals(centerStartingHex, centerRotatedHex);
    }

    @Test
    public void testRotate() {
        var base = new SpinHexModel();
        var copy = base.clone();
        base.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE));
        assertNotEquals(base, copy);
        base.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.COUNTERCLOCKWISE));
        assertEquals(base, copy);
    }

    @Test
    public void testRotate2() {
        var base = new SpinHexModel();
        var copy = base.clone();
        for (int i = 0; i < 5; i++) {
            base.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE));
            assertNotEquals(base, copy);
        }
        base.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE));
        assertEquals(base, copy);
    }

    @Test
    public void testIsSolved() {
        var board = new SpinHexModel(smallBoardStart, smallBoardTarget);
        assertFalse(board.isSolved());
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.CLOCKWISE));
        assertFalse(board.isSolved());
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.CLOCKWISE));
        assertTrue(board.isSolved());
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.CLOCKWISE));
        assertFalse(board.isSolved());
    }

    @Test
    public void testGetLegalMoves() {
        var board = new SpinHexModel();
        var legalMoves = board.getLegalMoves();
        var expectedLegalPositions = new AxialPosition[] {
                new AxialPosition(1, 2),
                new AxialPosition(1, 3),
                new AxialPosition(2, 1),
                new AxialPosition(2, 2),
                new AxialPosition(2, 3),
                new AxialPosition(3, 1),
                new AxialPosition(3, 2),
        };
        for (var position : expectedLegalPositions) {
            assertTrue(legalMoves.contains(new TwoPhaseAction<>(position, Rotation.CLOCKWISE)));
            assertTrue(legalMoves.contains(new TwoPhaseAction<>(position, Rotation.COUNTERCLOCKWISE)));
        }
    }

    @Test
    public void testGetHexProperty() {
        var board = new SpinHexModel(smallBoardStart, smallBoardTarget);
        var property = board.getHexProperty(1, 0);
        assertEquals(property.get(), HexColor.RED);
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.CLOCKWISE));
        assertEquals(property.get(), HexColor.BLUE);
        assertThrows(IllegalArgumentException.class, () -> board.getHexProperty(-1, 0));
    }

    @Test
    public void testHashCode() {
        var board1 = new SpinHexModel(smallBoardStart, smallBoardTarget);
        var board2 = new SpinHexModel(smallBoardStart, smallBoardTarget);
        assertEquals(board1.hashCode(), board2.hashCode());

        board1.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.CLOCKWISE));
        assertNotEquals(board1.hashCode(), board2.hashCode());
    }

    @Test
    public void testHashCode2() {
        var fullRedBoard = new HexColor[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fullRedBoard[i][j] = HexColor.RED;
            }
        }
        var board = new SpinHexModel(fullRedBoard, smallBoardTarget);
        var copy = board.clone();
        assertEquals(board.hashCode(), copy.hashCode());
        board.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.CLOCKWISE));
        assertEquals(board.hashCode(), copy.hashCode());
    }
}
