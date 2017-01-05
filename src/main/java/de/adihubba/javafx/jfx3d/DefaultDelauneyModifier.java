package de.adihubba.javafx.jfx3d;


import javafx.geometry.Point3D;
import de.adihubba.delauney.Point;


/**
 * Default DelauneyModifier which just converts the points without modification of the axis
 */
public class DefaultDelauneyModifier implements DelauneyModifier {

    @Override
    public Point convertPoint3d4Delauney(Point3D point) {
        return new Point(point.getX(), point.getY(), point.getZ());
    }

    @Override
    public Point3D convertPointFromDelauney(Point coord) {
        return new Point3D(coord.getX(), coord.getY(), coord.getZ());
    }

}
