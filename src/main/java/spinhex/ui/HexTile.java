package spinhex.ui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import spinhex.model.HexColor;

public class HexTile extends StackPane {

    public HexTile(int size, ReadOnlyIntegerProperty representing) {
        super();
        setMinSize(size, size);
        setMaxSize(size, size);
        getStyleClass().add("hex-tile");
        var inside_circle = new Circle(size / 2.7);
        inside_circle.fillProperty().bind(createHexBindingColor(representing));
        var inside_text = new Text();
        inside_text.textProperty().bind(createHexBindingString(representing));
        inside_text.setBoundsType(TextBoundsType.VISUAL);
        getChildren().addAll(inside_circle, inside_text);
    }

    public HexTile(int size, int color) {
        this(size, new ReadOnlyIntegerWrapper(color));
    }

    private ObjectBinding<Paint> createHexBindingColor(ReadOnlyIntegerProperty hexColorProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected Paint computeValue() {
                return assignHexColorToPaint((byte) hexColorProperty.get());
            }
        };
    }

    private Paint assignHexColorToPaint(Byte hexColor) {
        return switch (hexColor) {
            case HexColor.NONE -> Color.TRANSPARENT;
            case HexColor.RED -> Color.RED;
            case HexColor.BLUE -> Color.BLUE;
            case HexColor.GREEN -> Color.GREEN;
            default -> throw new IllegalStateException("Unexpected value: " + hexColor);
        };
    }

    private String assignHexColorToString(Byte hexColor) {
        return switch (hexColor) {
            case HexColor.NONE -> "";
            case HexColor.RED -> "P";
            case HexColor.BLUE -> "K";
            case HexColor.GREEN -> "Z";
            default -> throw new IllegalStateException("Unexpected value: " + hexColor);
        };
    }

    private ObjectBinding<String> createHexBindingString(ReadOnlyIntegerProperty hexColorProperty) {
        return new ObjectBinding<String>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected String computeValue() {
                return assignHexColorToString((byte) hexColorProperty.get());
            }
        };
    }
}
