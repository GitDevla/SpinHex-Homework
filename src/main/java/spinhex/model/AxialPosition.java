package spinhex.model;

public record AxialPosition(int q, int s) {

    @Override
    public String toString() {
        return String.format("(%d,%d)", q, s);
    }

    public AxialPosition add(AxialPosition other) {
        return new AxialPosition(this.q + other.q, this.s + other.s);
    }

    public AxialPosition subtract(AxialPosition other) {
        return new AxialPosition(this.q - other.q, this.s - other.s);
    }

    public boolean isEqual(AxialPosition other) {
        return this.q == other.q && this.s == other.s;
    }
}