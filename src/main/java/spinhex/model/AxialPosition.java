package spinhex.model;

public record AxialPosition(int q, int s) {

    @Override
    public String toString() {
        return String.format("(%d,%d)", q, s);
    }
}
