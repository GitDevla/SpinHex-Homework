package spinhex.ui.component;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import spinhex.model.HexColor;

public class HexTile extends StackPane {

    private static final float CIRCLE_RATIO = 0.5f;
    private final ObjectProperty<Paint> colorProperty;
    private final StringProperty textProperty;
    private final int q;
    private final int s;

    public HexTile(int size, int color, int q, int s) {
        super();
        this.q = q;
        this.s = s;
        setMinSize(size, size);
        setPrefSize(size, size);
        setMaxSize(size, size);
        getStyleClass().add("hex-tile");
        var inside_circle = new Circle(size / (2 + CIRCLE_RATIO));
        colorProperty = inside_circle.fillProperty();
        colorProperty.set(assignHexColorToPaint((byte) color));

        var inside_text = new Text();
        textProperty = inside_text.textProperty();
        textProperty.set(assignHexColorToString((byte) color));
        inside_text.setBoundsType(TextBoundsType.VISUAL);
        getChildren().addAll(inside_circle, inside_text);
    }

    public int getQ() {
        return q;
    }

    public int getS() {
        return s;
    }

    public void bind(ReadOnlyIntegerProperty representing) {
        colorProperty.bind(createHexBindingColor(representing));
        textProperty.bind(createHexBindingString(representing));
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
