package de.adihubba.javafx.jfx3d;


import de.adihubba.ObjectUtils;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


public class Wall extends Pane {

    private Rectangle wall;

    public Wall(double size) {
        this(size, 0.0, Color.BLACK);
    }

    public Wall(double size, Color gridColor) {
        this(size, 0.0, gridColor);
    }

    public Wall(double size, double opacity) {
        this(size, opacity, Color.BLACK);
    }

    public Wall(double size, double opacity, Color gridColor) {

        // wall
        // first the wall, then the lines => overlapping of lines over walls
        this.wall = new Rectangle(size, size);

        // just show lines without wall backgroundcolor
        this.wall.setOpacity(opacity);
        getChildren().add(this.wall);

        // grid
        double zTranslate = 0;
        double lineWidth = 1.0;

        Line line = null;
        for (int y = 0; ObjectUtils.smallerOrEqualsDoublePrecision(y, size); y += size / 10) {
            line = new Line(0, 0, size, 0);
            line.setStroke(gridColor);
            line.setFill(gridColor);
            line.setTranslateY(y);
            line.setTranslateZ(zTranslate);
            line.setStrokeWidth(lineWidth);

            getChildren().addAll(line);
        }

        for (int x = 0; ObjectUtils.smallerOrEqualsDoublePrecision(x, size); x += size / 10) {
            line = new Line(0, 0, 0, size);
            line.setStroke(gridColor);
            line.setFill(gridColor);
            line.setTranslateX(x);
            line.setTranslateZ(zTranslate);
            line.setStrokeWidth(lineWidth);

            getChildren().addAll(line);
        }
    }

    public void setFill(Paint paint) {
        this.wall.setFill(paint);
    }

}
