import org.junit.jupiter.api.Test;
import spinhex.model.AxialPosition;

import static org.junit.jupiter.api.Assertions.*;

public class AxialPositionTest {
    @Test
    public void testAxialPositionAddition() {
        AxialPosition a = new AxialPosition(2,3);
        AxialPosition b = new AxialPosition(1,-1);
        AxialPosition c = new AxialPosition(3,2);
        assertEquals(c,a.add(b));
    }

    @Test
    public void testAxialPositionSubstraction() {
        AxialPosition a = new AxialPosition(2,3);
        AxialPosition b = new AxialPosition(1,-1);
        AxialPosition c = new AxialPosition(1,4);
        assertEquals(c,a.subtract(b));
    }

    @Test
    public void testAxialPositionToString() {
        AxialPosition a = new AxialPosition(2,3);
        assertEquals("(2,3)",a.toString());
    }
}
