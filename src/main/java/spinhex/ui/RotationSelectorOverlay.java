package spinhex.ui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class RotationSelectorOverlay extends StackPane {
    private EventHandler<? super MouseEvent> onClockwiseClickHandler;
    private EventHandler<? super MouseEvent> onCounterClockwiseClickHandler;

    public RotationSelectorOverlay(double xPos, double yPos, int size) {
        super();
        getStyleClass().add("rotation-overlay");
        setMinSize(size, size);
        setMaxSize(size, size);
        setTranslateX(xPos);
        setTranslateY(yPos);

        var clockwise = createRotationSelectorCircle("⟳");
        clockwise.setTranslateX(15);
        clockwise.setOnMouseClicked(event -> {
            if (onClockwiseClickHandler != null) {
                onClockwiseClickHandler.handle(event);
            }
        });
        var counterClockwise = createRotationSelectorCircle("⟲");
        counterClockwise.setOnMouseClicked(event -> {
            if (onCounterClockwiseClickHandler != null) {
                onCounterClockwiseClickHandler.handle(event);
            }
        });
        counterClockwise.setTranslateX(-15);
        getChildren().addAll(clockwise, counterClockwise);
    }

    private StackPane createRotationSelectorCircle(String Text) {
        var stackPane = new StackPane();
        var circle = new Circle(10);
        var text = new Text(Text);
        text.setBoundsType(TextBoundsType.VISUAL);
        stackPane.getChildren().addAll(circle, text);
        stackPane.setMinSize(30, 30);
        stackPane.setMaxSize(30, 30);
        return stackPane;
    }

    public void setOnClockwiseClick(EventHandler<? super MouseEvent> action) {
        this.onClockwiseClickHandler = action;
    }

    public void setOnCounterClockwiseClick(EventHandler<? super MouseEvent> action) {
        this.onCounterClockwiseClickHandler = action;
    }
}
