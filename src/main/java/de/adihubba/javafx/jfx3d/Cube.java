package de.adihubba.javafx.jfx3d;


import java.util.function.Consumer;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;


/**
 * A cube consisting of six walls
 */
public class Cube extends Group {

    private final Wall front;
    private final Wall back;
    private final Wall right;
    private final Wall left;
    private final Wall top;
    private final Wall bottom;

    public Cube(int size) {
        // back face
        back = new Wall(size);
        back.setTranslateX(-0.5 * size);
        back.setTranslateY(-0.5 * size);
        back.setTranslateZ(0.5 * size);

        // bottom face
        bottom = new Wall(size);
        bottom.setTranslateX(-0.5 * size);
        bottom.setTranslateY(0);
        bottom.setRotationAxis(Rotate.X_AXIS);
        bottom.setRotate(90);

        // left face
        left = new Wall(size);
        left.setTranslateX(-1 * size);
        left.setTranslateY(-0.5 * size);
        left.setRotationAxis(Rotate.Y_AXIS);
        left.setRotate(90);

        // right face
        right = new Wall(size);
        right.setTranslateX(0);
        right.setTranslateY(-0.5 * size);
        right.setRotationAxis(Rotate.Y_AXIS);
        right.setRotate(90);
        right.setVisible(false);

        // top face
        top = new Wall(size);
        top.setTranslateX(-0.5 * size);
        top.setTranslateY(-1 * size);
        top.setRotationAxis(Rotate.X_AXIS);
        top.setRotate(90);
        top.setVisible(false);

        // front face
        front = new Wall(size);
        front.setTranslateX(-0.5 * size);
        front.setTranslateY(-0.5 * size);
        front.setTranslateZ(-0.5 * size);
        front.setVisible(false);

        this.getChildren().addAll(front, back, right, left, top, bottom);
    }

    public void setFrontWallVisible(boolean visible) {
        front.setVisible(visible);
    }

    public void setBackWallVisible(boolean visible) {
        back.setVisible(visible);
    }

    public void setRightWallVisible(boolean visible) {
        right.setVisible(visible);
    }

    public void setLeftWallVisible(boolean visible) {
        left.setVisible(visible);
    }

    public void setTopWallVisible(boolean visible) {
        top.setVisible(visible);
    }

    public void setBottomWallVisible(boolean visible) {
        bottom.setVisible(visible);
    }

    public boolean isFrontWallVisible() {
        return front.isVisible();
    }

    public boolean isBackWallVisible() {
        return back.isVisible();
    }

    public boolean isRightWallVisible() {
        return right.isVisible();
    }

    public boolean isLeftWallVisible() {
        return left.isVisible();
    }

    public boolean isTopWallVisible() {
        return top.isVisible();
    }

    public boolean isBottomWallVisible() {
        return bottom.isVisible();
    }

    public void applyOnAllWalls(Consumer<Wall> consumer) {
        if (consumer != null) {
            consumer.accept(front);
            consumer.accept(back);
            consumer.accept(right);
            consumer.accept(left);
            consumer.accept(top);
            consumer.accept(bottom);
        }
    }

}
