package de.adihubba.javafx.jfx3d;


import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;


/**
 * Composite objects which holds data points and the result of delauney transformation
 */
public class MeshCalculationComposite {

    private final List<Point3D>    dataPoints;
    private final List<Point3D>    normalizedPoints;
    private final List<Triangle3D> triangle3DList;

    private double                 minX  = Double.MAX_VALUE;
    private double                 maxX  = Double.MIN_VALUE;
    private double                 minY  = Double.MAX_VALUE;
    private double                 maxY  = Double.MIN_VALUE;
    private double                 minZ  = Double.MAX_VALUE;
    private double                 maxZ  = Double.MIN_VALUE;

    private final int              size;
    private double                 sizeX = 0.0;
    private double                 sizeY = 0.0;
    private double                 sizeZ = 0.0;

    private MeshCalculationComposite(List<Point3D> dataPoints, int size) {
        this.dataPoints = dataPoints;
        this.size = size;

        for (Point3D point3d : dataPoints) {
            minX = Math.min(minX, point3d.getX());
            maxX = Math.max(maxX, point3d.getX());
            minY = Math.min(minY, point3d.getY());
            maxY = Math.max(maxY, point3d.getY());
            minZ = Math.min(minZ, point3d.getZ());
            maxZ = Math.max(maxZ, point3d.getZ());
        }
        sizeX = maxX - minX;
        sizeY = maxY - minY;
        sizeZ = maxZ - minZ;

        // normalize points according to coordinate system size
        normalizedPoints = new ArrayList<Point3D>(dataPoints.size());
        for (Point3D point : dataPoints) {
            double x = (point.getX() - minX) / sizeX * size;
            double y = (point.getY() - minY) / sizeY * size;
            double z = (point.getZ() - minZ) / sizeZ * size;
            normalizedPoints.add(new Point3D(x, y, z));
        }

        triangle3DList = new ArrayList<Triangle3D>(dataPoints.size() / 2);
    }

    public static MeshCalculationComposite of(List<Point3D> dataPoints, int size) {
        return new MeshCalculationComposite(dataPoints, size);
    }

    public List<Point3D> getDataPoints() {
        return dataPoints;
    }

    public List<Point3D> getNormalizedPoints() {
        return normalizedPoints;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public int getSize() {
        return size;
    }

    public double getSizeX() {
        return sizeX;
    }

    public double getSizeY() {
        return sizeY;
    }

    public double getSizeZ() {
        return sizeZ;
    }

    public List<Triangle3D> getTriangle3DList() {
        return triangle3DList;
    }

    public void addTriangle3D(Triangle3D triangle) {
        this.triangle3DList.add(triangle);
    }
}
