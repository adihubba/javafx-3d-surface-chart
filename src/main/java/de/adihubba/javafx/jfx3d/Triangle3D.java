package de.adihubba.javafx.jfx3d;


import javafx.geometry.Point3D;


/**
 * a triangle consisting of three data points. Holds the result of delauney triangulation
 */
public class Triangle3D {

    private final Point3D p0, p1, p2;

    private Triangle3D(Point3D p0, Point3D p1, Point3D p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    public static Triangle3D of(Point3D p1, Point3D p2, Point3D p3) {
        return new Triangle3D(p1, p2, p3);
    }

    public Point3D getP0() {
        return p0;
    }

    public Point3D getP1() {
        return p1;
    }

    public Point3D getP2() {
        return p2;
    }

}
