package spinhex.ui.component;

import java.util.List;

import javafx.scene.layout.Pane;
import spinhex.model.HexagonalGrid;

public class HexGrid extends Pane {
    private static final double HEXAGON_VERTICAL_SCALE_FACTOR = 0.8;
    private static final double HEXAGON_OFFSET_PER_ROW = 0.5;

    private int hexSize = 80;
    private double hexMargin = 4;
    private double offsetStart;

    public HexGrid() {
        super();
    }

    public void setSize(int size) {
        final var pxSize = (hexSize) * size + hexMargin * (size - 1);
        setPrefSize(pxSize, pxSize * HEXAGON_VERTICAL_SCALE_FACTOR);
        setMaxSize(pxSize, pxSize * HEXAGON_VERTICAL_SCALE_FACTOR);
        setMinSize(pxSize, pxSize * HEXAGON_VERTICAL_SCALE_FACTOR);
        this.offsetStart = (double) (size - 1) / 4;
    }

    public int getHexSize() {
        return hexSize;
    }

    public void setHexSize(int size) {
        this.hexSize = size;
    }

    public double getHexMargin() {
        return hexMargin;
    }

    public void setHexMargin(double padding) {
        this.hexMargin = padding;
    }

    public void addHexTile(HexTile tile) {
        final int row = tile.getQ();
        final int col = tile.getS();
        final double totalHexSize = hexSize + hexMargin;
        final double xPos = col * totalHexSize;
        final double yPos = row * (totalHexSize * 0.75);
        final double xOffset = (offsetStart - (HEXAGON_OFFSET_PER_ROW * row)) * totalHexSize;

        tile.setTranslateX(xPos - xOffset);
        tile.setTranslateY(yPos);
        getChildren().add(tile);
    }

    public void populateFromGrid(HexagonalGrid model) {
        setSize(model.getSize());
        for (var row = 0; row < model.getSize(); row++) {
            for (var col = 0; col < model.getSize(); col++) {
                if (!model.isInBounds(row, col))
                    continue;
                byte modelHex = model.get(row, col);
                HexTile hexTile = new HexTile(hexSize, modelHex, row, col);
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
