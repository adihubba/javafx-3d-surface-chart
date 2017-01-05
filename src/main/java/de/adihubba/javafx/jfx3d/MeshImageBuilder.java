package de.adihubba.javafx.jfx3d;


import java.util.Optional;
import java.util.stream.IntStream;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


/**
 * MeshImageBuilder creates an colored image which will be used as diffuse map
 */
public class MeshImageBuilder {

    private final Color transparentColor = new Color(1.0, 1.0, 1.0, 0.0);
    private final int   imageSize;

    public MeshImageBuilder(int imageSize) {
        this.imageSize = imageSize;
    }

    public Image createImage(MeshCalculationComposite calculationObject) {
        WritableImage img = new WritableImage(imageSize, imageSize);
        PixelWriter pw = img.getPixelWriter();
        final double factor = (double) calculationObject.getSize() / (double) imageSize;

        IntStream.range(0, imageSize).forEach(x -> IntStream.range(0, imageSize).parallel().forEach(y -> pw.setColor(x, y, calculateColor(0, x, y, factor, calculationObject))));

        // save image for testing purposes
        //        try {
        //            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(System.getProperty("user.home") + "\\Desktop\\Vola3DImage.png"));
        //        } catch (IOException ex) {
        //        }

        return img;
    }

    private Color calculateColor(int recursionDepth, int x, int y, double factor, MeshCalculationComposite calculationObject) {
        if (recursionDepth >= 2) {
            return transparentColor;
        }
        final Point2D point2Calculate = new Point2D(x * factor, y * factor);

        Optional<Triangle3D> triangleMatch = calculationObject.getTriangle3DList().stream().filter(t -> MeshImageBuilder.isPointInTriangle(point2Calculate, t.getP0(), t.getP1(), t.getP2())).findFirst();
        if (triangleMatch.isPresent()) {
            Triangle3D triangle = triangleMatch.get();
            double calculatedYValue = MeshImageBuilder.calculateYValueInTriangle(point2Calculate, triangle.getP0(), triangle.getP1(), triangle.getP2());
            return getColor(calculatedYValue, calculationObject);
        }

        // check neighboor pixel
        Color result = null;
        if (x == 0) {
            result = calculateColor(recursionDepth + 1, x + 1, y, factor, calculationObject);
        } else if (y == 0) {
            result = calculateColor(recursionDepth + 1, x, y + 1, factor, calculationObject);
        } else {
            result = calculateColor(recursionDepth + 1, x, y + 1, factor, calculationObject);
            if (result != transparentColor) {
                return result;
            }
            result = calculateColor(recursionDepth + 1, x + 1, y, factor, calculationObject);
            if (result != transparentColor) {
                return result;
            }
            result = calculateColor(recursionDepth + 1, x - 1, y, factor, calculationObject);
            if (result != transparentColor) {
                return result;
            }
            result = calculateColor(recursionDepth + 1, x, y - 1, factor, calculationObject);
            if (result != transparentColor) {
                return result;
            }
        }

        return result != null ? result : transparentColor;
    }

    /**
     * returns an color, where the h value in HSB is between 0 and 230 degree in depentend of the value parameter.
     */
    private Color getColor(double value, MeshCalculationComposite calculationObject) {
        java.awt.Color hsb = java.awt.Color.getHSBColor((float) (value / calculationObject.getSize() * (23.0 / 36.0)), 1.0f, 1.0f);
        return Color.rgb(hsb.getRed(), hsb.getGreen(), hsb.getBlue());
    }

    /**
     * Calculates the z value of an given 3d triangle and a 2d point, where x and y coordinates are in this triangle
     * http://math.stackexchange.com/a/851752
     */
    public static double calculateZValueInTriangle(Point2D p, Point3D p0, Point3D p1, Point3D p2) {
        double divider = ((p1.getX() - p0.getX()) * (p2.getY() - p0.getY())) - ((p2.getX() - p0.getX()) * (p1.getY() - p0.getY()));
        double z = p0.getZ() + (((((p1.getX() - p0.getX()) * (p2.getZ() - p0.getZ())) - ((p2.getX() - p0.getX()) * (p1.getZ() - p0.getZ()))) / divider) * (p.getY() - p0.getY()))
                - (((((p1.getY() - p0.getY()) * (p2.getZ() - p0.getZ())) - ((p2.getY() - p0.getY()) * (p1.getZ() - p0.getZ()))) / divider) * (p.getX() - p0.getX()));
        return z;
    }

    /**
     * Calculates the y value of an given 3d triangle and a 2d point, where x and z coordinates are in this triangle
     */
    public static double calculateYValueInTriangle(Point2D p, Point3D p0, Point3D p1, Point3D p2) {
        double divider = ((p1.getZ() - p0.getZ()) * (p2.getX() - p0.getX())) - ((p1.getX() - p0.getX()) * (p2.getZ() - p0.getZ()));
        double y = p0.getY() + (((((p1.getY() - p0.getY()) * (p2.getX() - p0.getX())) - ((p1.getX() - p0.getX()) * (p2.getY() - p0.getY()))) / divider) * (p.getY() - p0.getZ()))
                - (((((p1.getY() - p0.getY()) * (p2.getZ() - p0.getZ())) - ((p2.getY() - p0.getY()) * (p1.getZ() - p0.getZ()))) / divider) * (p.getX() - p0.getX()));
        return y;
    }

    /**
     * method returns true, if point p is in the triangle. Method uses barycentric coordinate system.
     * https://stackoverflow.com/questions/2049582/how-to-determine-a-point-in-a-triangle
     */
    public static boolean isPointInTriangle(Point2D p, Point3D p0, Point3D p1, Point3D p2) {
        double area = 0.5 * (-p1.getZ() * p2.getX() + p0.getZ() * (-p1.getX() + p2.getX()) + p0.getX() * (p1.getZ() - p2.getZ()) + p1.getX() * p2.getZ());
        int sign = area < 0.0 ? -1 : 1;
        double s = (p0.getZ() * p2.getX() - p0.getX() * p2.getZ() + (p2.getZ() - p0.getZ()) * p.getX() + (p0.getX() - p2.getX()) * p.getY()) * sign;
        double t = (p0.getX() * p1.getZ() - p0.getZ() * p1.getX() + (p0.getZ() - p1.getZ()) * p.getX() + (p1.getX() - p0.getX()) * p.getY()) * sign;

        return s > 0.0 && t > 0.0 && (s + t) < 2.0 * area * sign;
    }

}
