package spinhex.ui;

import java.util.List;

import javafx.scene.layout.Pane;
import spinhex.model.HexagonalGrid;

public class HexGrid extends Pane {
    private int HEX_SIZE = 80;
    private static final double PADDING = 4;
    private double offsetStart;

    public HexGrid() {
        super();
    }

    public void setSize(int size) {
        final var pxSize = (HEX_SIZE) * size + PADDING*(size-1);
        setPrefSize(pxSize, pxSize/1.25);
        setMaxSize(pxSize, pxSize/1.25);
        setMinSize(pxSize, pxSize/1.25);
        this.offsetStart = (double) (size - 1) / 4;
    }

    public void setHexSize(int size) {
        this.HEX_SIZE = size;
    }

    public void addHexTile(HexTile tile) {
        final int row = tile.getQ();
        final int col = tile.getS();
        final double paddedSize = HEX_SIZE + PADDING;
        final double xPos = col * paddedSize;
        final double yPos = row * (paddedSize * 0.75);
        final double xOffset = (offsetStart - (0.5 * row)) * paddedSize;

        tile.setTranslateX(xPos - xOffset);
        tile.setTranslateY(yPos);
        getChildren().add(tile);
    }

    public void populateFromGrid(HexagonalGrid model) {
        setSize(model.getSize());
        for (var row = 0; row < model.getSize(); row++) {
            for (var col = 0; col < model.getSize(); col++) {
                byte modelHex;
                try {
                    modelHex = model.get(row, col);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                HexTile hexTile = new HexTile(HEX_SIZE, modelHex, row, col);
                addHexTile(hexTile);
            }
        }
    }

    public List<HexTile> getHexTiles() {
        return getChildren().stream()
                .filter(HexTile.class::isInstance)
                .map(x -> (HexTile) x)
                .toList();
    }
}
