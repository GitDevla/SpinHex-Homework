package spinhex.ui;

import javafx.scene.layout.Pane;

public class HexGrid extends Pane {
    private static final int HEX_SIZE = 80;
    private double offsetStart;

    public HexGrid() {
        super();
    }

    public void setSize(int size) {
        this.offsetStart = (double) (size - 1) / 4;
    }

    public void addHexTile(HexTile tile, int row, int col) {
        double xOffset = col * HEX_SIZE;
        xOffset -= (offsetStart - (0.5 * row)) * HEX_SIZE;
        var yOffset = row * (HEX_SIZE * 0.75);
        tile.setTranslateX(xOffset);
        tile.setTranslateY(yOffset);
        getChildren().add(tile);
    }
}
