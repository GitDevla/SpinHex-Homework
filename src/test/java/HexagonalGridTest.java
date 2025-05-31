import org.junit.jupiter.api.Test;
import spinhex.model.AxialPosition;
import spinhex.model.HexColor;
import spinhex.model.HexagonalGrid;

import static org.junit.jupiter.api.Assertions.*;

public class HexagonalGridTest {

    @Test
    public void testConstructorWithSize() {
        HexagonalGrid grid = new HexagonalGrid(3);

        for (int q = 0; q < 3; q++) {
            for (int s = 0; s < 3; s++) {
                if (grid.isInBounds(q, s)) {
                    assertEquals(0, grid.get(q, s));
                }
            }
        }
    }

    @Test
    public void testConstructorWith2DArray() {
        byte[][] initialBoard = {
                { HexColor.NONE, HexColor.RED, HexColor.BLUE },
                { HexColor.GREEN, HexColor.BLUE, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.NONE }
        };

        HexagonalGrid grid = new HexagonalGrid(initialBoard);

        assertThrows(IllegalArgumentException.class,()-> grid.get(0, 0));
        assertEquals(HexColor.RED, grid.get(0, 1));
        assertEquals(HexColor.BLUE, grid.get(0, 2));
        assertEquals(HexColor.GREEN, grid.get(1, 0));
        assertEquals(HexColor.BLUE, grid.get(1, 1));
        assertEquals(HexColor.RED, grid.get(1, 2));
        assertEquals(HexColor.RED, grid.get(2, 0));
        assertEquals(HexColor.GREEN, grid.get(2, 1));
        assertThrows(IllegalArgumentException.class,()-> grid.get(2, 2));
    }

    @Test
    public void testSetAndGetWithCoordinates() {
        HexagonalGrid grid = new HexagonalGrid(5);

        grid.set(1, 2, HexColor.RED);
        grid.set(2, 3, HexColor.BLUE);
        grid.set(3, 1, HexColor.GREEN);
        assertThrows(IllegalArgumentException.class,()-> grid.set(0, 0, HexColor.GREEN));


        assertEquals(HexColor.RED, grid.get(1, 2));
        assertEquals(HexColor.BLUE, grid.get(2, 3));
        assertEquals(HexColor.GREEN, grid.get(3, 1));
        assertThrows(IllegalArgumentException.class,()-> grid.get(0, 0));
    }

    @Test
    public void testIsInBounds() {
        HexagonalGrid grid = new HexagonalGrid(5);

        assertTrue(grid.isInBounds(new AxialPosition(0, 2)));
        assertTrue(grid.isInBounds(new AxialPosition(2, 2)));
        assertTrue(grid.isInBounds(new AxialPosition(4, 2)));

        assertFalse(grid.isInBounds(new AxialPosition(-1, 0)));
        assertFalse(grid.isInBounds(new AxialPosition(0, -1)));
        assertFalse(grid.isInBounds(new AxialPosition(5, 0)));
    }

    @Test
    public void testClone() {
        HexagonalGrid original = new HexagonalGrid(5);
        original.set(1, 2, HexColor.RED);
        original.set(2, 3, HexColor.BLUE);
        original.set(3, 1, HexColor.GREEN);

        HexagonalGrid cloned = original.clone();

        assertEquals(HexColor.RED, cloned.get(1, 2));
        assertEquals(HexColor.BLUE, cloned.get(2, 3));
        assertEquals(HexColor.GREEN, cloned.get(3, 1));

        original.set(1, 2, HexColor.BLUE);
        assertEquals(HexColor.BLUE, original.get(1, 2));
        assertEquals(HexColor.RED, cloned.get(1, 2));

        cloned.set(2, 3, HexColor.GREEN);
        assertEquals(HexColor.BLUE, original.get(2, 3));
        assertEquals(HexColor.GREEN, cloned.get(2, 3));
    }

    @Test
    public void testEquals() {
        HexagonalGrid grid1 = new HexagonalGrid(5);
        grid1.set(1, 2, HexColor.RED);
        grid1.set(2, 3, HexColor.BLUE);

        HexagonalGrid grid2 = new HexagonalGrid(5);
        grid2.set(1, 2, HexColor.RED);
        grid2.set(2, 3, HexColor.BLUE);

        HexagonalGrid grid3 = new HexagonalGrid(5);
        grid3.set(1, 2, HexColor.RED);
        grid3.set(1, 3, HexColor.BLUE);

        assertEquals(grid1, grid2);
        assertNotEquals(grid1, grid3);
    }

    @Test
    public void testHashCode() {
        HexagonalGrid grid1 = new HexagonalGrid(5);
        grid1.set(1, 2, HexColor.RED);
        grid1.set(2, 3, HexColor.BLUE);

        HexagonalGrid grid2 = new HexagonalGrid(5);
        grid2.set(1, 2, HexColor.RED);
        grid2.set(2, 3, HexColor.BLUE);

        HexagonalGrid grid3 = new HexagonalGrid(5);
        grid3.set(1, 2, HexColor.RED);
        grid3.set(1, 3, HexColor.BLUE);

        assertEquals(grid1.hashCode(), grid2.hashCode());
        assertNotEquals(grid1.hashCode(), grid3.hashCode());
    }
}
