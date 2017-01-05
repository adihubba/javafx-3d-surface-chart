package de.adihubba.javafx.jfx3d;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


/**
 * Help for orientation during development 
 */
public class AxisOrientation extends Group {

    public AxisOrientation(int size) {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        final Box xAxis = new Box(size + 100, 2, 2);
        final Box yAxis = new Box(2, size + 100, 2);
        final Box zAxis = new Box(2, 2, size + 100);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        this.getChildren().addAll(xAxis, yAxis, zAxis);
    }

}
