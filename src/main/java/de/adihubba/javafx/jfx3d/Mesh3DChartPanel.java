package de.adihubba.javafx.jfx3d;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.adihubba.delauney.DelaunayTriangulation;
import de.adihubba.delauney.Point;
import de.adihubba.delauney.Triangle;
import de.adihubba.ObjectUtils;


/**
 * Mesh3DChartPanel
 */
public class Mesh3DChartPanel extends JPanel {

    private JFXPanel                           pnlChart;
    private MeshControlPanel                   controlPanel;

    private Cube                               cube;
    private AxisOrientation                    axisOrientation;
    private MeshView                           meshView;

    private List<Node>                         titles;
    private List<Node>                         labelsX;
    private List<Node>                         labelsY;
    private List<Node>                         labelsZ;

    // variables for mouse interaction
    private double                             mousePosX, mousePosY;
    private double                             mouseOldX, mouseOldY;

    // size of mesh/box and size of image, which will be used as a texture for the mesh. If the image is bigger, than the texture has more details, but the calculation time increase
    private int                                size                = 400;
    private int                                imageSize           = 800;

    // initial rotation
    private final Rotate                       rotateX             = new Rotate(0, Rotate.X_AXIS);
    private final Rotate                       rotateY             = new Rotate(0, Rotate.Y_AXIS);

    // bounds for zooming
    private final double                       MAX_SCALE           = 20.0;
    private final double                       MIN_SCALE           = 0.1;

    // ability to rotate a coordinate, because the delauney algorithm doesn't work with all coordinates. 
    private transient DelauneyModifier         delauneyModifier    = new DefaultDelauneyModifier();
    private DrawMode                           drawMode            = DrawMode.FILL;
    private boolean                            dynamicWallsEnabled = true;
    private transient Function<Double, String> formatterFunction   = this::formatNumber;

    // axis titles
    private String                             axisTitleX;
    private String                             axisTitleY;
    private String                             axisTitleZ;

    public Mesh3DChartPanel() {
        if (Platform.isImplicitExit()) {
            // to avoid errors for multiple FX frames inside one swing application
            Platform.setImplicitExit(false);
        }
    }

    public void showMeshPanel(List<Point3D> points) {
        initComponents();
        Platform.runLater(() -> draw(points, pnlChart));
    }

    public void addMeshControlPanel() {
        controlPanel = new MeshControlPanel(this);
    }

    public void activateDynamicWalls(boolean pDynamicWallsEnabled) {
        this.dynamicWallsEnabled = pDynamicWallsEnabled;
    }

    public void showOrientationCross(boolean visible) {
        if (axisOrientation != null) {
            Platform.runLater(() -> axisOrientation.setVisible(visible));
        }
    }

    public void setLeftWallVisible(boolean visible) {
        if (cube != null && !dynamicWallsEnabled) {
            Platform.runLater(() -> cube.setLeftWallVisible(visible));
        }
    }

    public void setRightWallVisible(boolean visible) {
        if (cube != null && !dynamicWallsEnabled) {
            Platform.runLater(() -> cube.setRightWallVisible(visible));
        }
    }

    public void setTopWallVisible(boolean visible) {
        if (cube != null && !dynamicWallsEnabled) {
            Platform.runLater(() -> cube.setTopWallVisible(visible));
        }
    }

    public void setBottomWallVisible(boolean visible) {
        if (cube != null && !dynamicWallsEnabled) {
            Platform.runLater(() -> cube.setBottomWallVisible(visible));
        }
    }

    public void setFrontWallVisible(boolean visible) {
        if (cube != null && !dynamicWallsEnabled) {
            Platform.runLater(() -> cube.setFrontWallVisible(visible));
        }
    }

    public void setBackWallVisible(boolean visible) {
        if (cube != null && !dynamicWallsEnabled) {
            Platform.runLater(() -> cube.setBackWallVisible(visible));
        }
    }

    public void setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
        if (meshView != null) {
            Platform.runLater(() -> meshView.setDrawMode(drawMode));
        }
    }

    public void setDelauneyModifier(DelauneyModifier delauneyModifier) {
        this.delauneyModifier = delauneyModifier;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public void setAxisTitleX(String axisTitleX) {
        this.axisTitleX = axisTitleX;
    }

    public void setAxisTitleY(String axisTitleY) {
        this.axisTitleY = axisTitleY;
    }

    public void setAxisTitleZ(String axisTitleZ) {
        this.axisTitleZ = axisTitleZ;
    }

    private void updateControlPanel() {
        if (controlPanel != null) {
            SwingUtilities.invokeLater(() -> {
                controlPanel.setDynamicWalls(dynamicWallsEnabled);
                controlPanel.setLeftWallVisible(cube.isLeftWallVisible());
                controlPanel.setRightWallVisible(cube.isRightWallVisible());
                controlPanel.setTopWallVisible(cube.isTopWallVisible());
                controlPanel.setBottomWallVisible(cube.isBottomWallVisible());
                controlPanel.setFrontWallVisible(cube.isFrontWallVisible());
                controlPanel.setBackWallVisible(cube.isBackWallVisible());
                controlPanel.setInterpolateValues(drawMode == DrawMode.FILL);
                controlPanel.setShowOrientationCross(axisOrientation.isVisible());
            });
        }
    }

    private void draw(List<Point3D> dataPoints, JFXPanel chartPanel) {
        MeshCalculationComposite calculationObject = MeshCalculationComposite.of(dataPoints, size);

        // convert input for delauney algorithm
        List<Point> normalizedOldPoints = new ArrayList<Point>(calculationObject.getNormalizedPoints().size());
        for (Point3D point : calculationObject.getNormalizedPoints()) {
            normalizedOldPoints.add(delauneyModifier.convertPoint3d4Delauney(point));
        }

        // convert output of delauney algorithm back to triangle objects
        List<Triangle> triangulation = new DelaunayTriangulation(normalizedOldPoints).getTriangulation();
        for (Triangle triangle : triangulation) {
            if (!triangle.isHalfplane()) {
                calculationObject.addTriangle3D(Triangle3D.of(
                        delauneyModifier.convertPointFromDelauney(triangle.getA()), 
                        delauneyModifier.convertPointFromDelauney(triangle.getB()), 
                        delauneyModifier.convertPointFromDelauney(triangle.getC())));
            }
        }

        // create axis walls
        cube = new Cube(size);
        cube.applyOnAllWalls(w -> w.setMouseTransparent(true));

        // axis-titles
        controlPanel.setLabels(axisTitleX, axisTitleY, axisTitleZ);
        createAxisTitles();
        createAxisLegend(calculationObject);
        cube.getChildren().addAll(titles);
        cube.getChildren().addAll(labelsX);
        cube.getChildren().addAll(labelsY);
        cube.getChildren().addAll(labelsZ);

        // initial cube rotation
        cube.getTransforms().addAll(rotateX, rotateY);

        // add coordinate system for debugging purposes
        axisOrientation = new AxisOrientation(size + 100);
        axisOrientation.setVisible(false);
        axisOrientation.setMouseTransparent(true);
        cube.getChildren().add(axisOrientation);

        TriangleMesh mesh = new TriangleMesh();

        // add points
        for (Point3D point : calculationObject.getNormalizedPoints()) {
            mesh.getPoints().addAll((float) point.getX(), (float) (-1. * point.getY()), (float) point.getZ());
        }

        // add texture coordinates
        for (Point3D point : calculationObject.getNormalizedPoints()) {
            mesh.getTexCoords().addAll((float) (point.getX() / size), (float) (point.getZ() / size));
        }

        //add faces
        List<Point3D> normalizedPoints = calculationObject.getNormalizedPoints();
        for (Triangle3D triangle : calculationObject.getTriangle3DList()) {
            int faceIndex1 = normalizedPoints.indexOf(triangle.getP0());
            int faceIndex2 = normalizedPoints.indexOf(triangle.getP1());
            int faceIndex3 = normalizedPoints.indexOf(triangle.getP2());
            mesh.getFaces().addAll(faceIndex1, faceIndex1, faceIndex2, faceIndex2, faceIndex3, faceIndex3);
        }

        // image/diffuseMap
        MeshImageBuilder imageBuilder = new MeshImageBuilder(imageSize);
        Image diffuseMap = imageBuilder.createImage(calculationObject);

        // material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(diffuseMap);
        material.setSelfIlluminationMap(diffuseMap);

        // mesh view
        meshView = new MeshView(mesh);
        meshView.setTranslateX(-0.5 * size);
        meshView.setTranslateY(0.5 * size);
        meshView.setTranslateZ(-0.5 * size);

        meshView.setMaterial(material);
        meshView.setCullFace(CullFace.NONE);
        meshView.setDrawMode(drawMode);
        meshView.setDepthTest(DepthTest.ENABLE);
        meshView.setOnMouseMoved(me -> {
            PickResult pickResult = me.getPickResult();
            if (pickResult != null && pickResult.getIntersectedNode() == meshView) {
                Point3D clickedPoint = pickResult.getIntersectedPoint();
                double x = calculationObject.getMinX() + (clickedPoint.getX() * (calculationObject.getMaxX() - calculationObject.getMinX()) / size);
                double z = calculationObject.getMinZ() + (clickedPoint.getZ() * (calculationObject.getMaxZ() - calculationObject.getMinZ()) / size);
                double y = calculationObject.getMinY() + (-clickedPoint.getY() * (calculationObject.getMaxY() - calculationObject.getMinY()) / size);
                controlPanel.setSelectedValues(x, y, z);
            }
        });

        cube.getChildren().addAll(meshView);

        // add objects to scene
        StackPane root = new StackPane();
        root.setPickOnBounds(false);
        root.getChildren().add(cube);

        // scene
        Scene scene = new Scene(root, 1600, 900, true, SceneAntialiasing.BALANCED);
        scene.setCamera(new PerspectiveCamera());
        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseDragged(this::mouseDragged);

        makeZoomable(scene, root);
        chartPanel.setScene(scene);
        updateControlPanel();
    }

    private void mouseDragged(MouseEvent me) {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
        rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));

        // shift angle so it is between 0 and 360
        double angleX = rotateX.getAngle() % 360;
        double angleY = rotateY.getAngle() % 360;
        angleX = angleX < 0 ? angleX + 360 : angleX;
        angleY = angleY < 0 ? angleY + 360 : angleY;

        boolean isCubeUpsideDown = 90 <= angleX && angleX < 270;

        if (dynamicWallsEnabled) {

            // set walls visible
            cube.setBottomWallVisible(angleX < 180);
            cube.setTopWallVisible(!cube.isBottomWallVisible());
            cube.setFrontWallVisible((90 <= angleY && angleY < 270) ^ isCubeUpsideDown);
            cube.setRightWallVisible((180 <= angleY) ^ isCubeUpsideDown);
            cube.setBackWallVisible(!cube.isFrontWallVisible());
            cube.setLeftWallVisible(!cube.isRightWallVisible());

            // move/translate labels
            final double labelXTranslateY = (angleX < 180 ? 1 : -1) * 0.5 * size;
            final double labelXTranslateZ = ((cube.isFrontWallVisible()) ? 1 : -1) * 0.5 * size;
            titles.get(0).setTranslateY(labelXTranslateY * 1.1);
            titles.get(0).setTranslateZ(labelXTranslateZ * 1.1);
            for (Node label : labelsX) {
                label.setTranslateY(labelXTranslateY);
                label.setTranslateZ(labelXTranslateZ);
            }

            final double labelYTranslateX = (cube.isBackWallVisible() ^ isCubeUpsideDown ? 1 : -1) * 0.5 * size;
            double labelYTranslateZ = (angleY < 180 ? 1 : -1) * 0.5 * size;
            titles.get(1).setTranslateX(labelYTranslateX);
            titles.get(1).setTranslateZ(labelYTranslateZ);
            labelYTranslateZ = !isCubeUpsideDown ? labelYTranslateZ : labelYTranslateZ + (0 * size / 15.0);
            for (Node label : labelsY) {
                label.setTranslateX(labelYTranslateX);
                label.setTranslateZ(labelYTranslateZ);
            }

            double labelZTranslateX = ((cube.isLeftWallVisible()) ? 1 : -1.) * 0.5 * size;
            final double labelZTranslateY = (angleX < 180 ? 1 : -1) * 0.5 * size;
            titles.get(2).setTranslateX(labelZTranslateX * 1.1);
            titles.get(2).setTranslateY(labelZTranslateY * 1.1);
            labelZTranslateX = cube.isLeftWallVisible() ? labelZTranslateX : labelZTranslateX - (size / 15.0);
            for (Node label : labelsZ) {
                label.setTranslateX(labelZTranslateX);
                label.setTranslateY(labelZTranslateY);
            }

        }

        List<Node> labels = new ArrayList<Node>(33);
        labels.addAll(titles);
        labels.addAll(labelsX);
        labels.addAll(labelsY);
        labels.addAll(labelsZ);

        calculateAndRotatoNodes(labels, 0.0, Math.toRadians(angleX), Math.toRadians(angleY));

        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
    }

    private void mousePressed(MouseEvent mouseEvent) {
        mouseOldX = mouseEvent.getSceneX();
        mouseOldY = mouseEvent.getSceneY();
    }

    private void calculateAndRotatoNodes(List<Node> nodes, double alp, double bet, double gam) {
        double A11 = Math.cos(alp) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alp) + Math.cos(alp) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alp) * Math.sin(bet) - Math.cos(alp) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alp);
        double A22 = Math.cos(alp) * Math.cos(bet) - Math.sin(alp) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alp) * Math.sin(bet) + Math.cos(bet) * Math.sin(alp) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        if (!ObjectUtils.equalsDoublePrecision(d, 0.0)) {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
            for (Node node : nodes) {
                node.setRotationAxis(p);
                node.setRotate(Math.toDegrees(d));
            }
        }
    }

    private void createAxisLegend(MeshCalculationComposite calculationObject) {
        labelsX = new ArrayList<Node>(10);
        labelsY = new ArrayList<Node>(10);
        labelsZ = new ArrayList<Node>(10);

        // for x axis
        for (int i = 0; i < 10; i++) {
            double number = calculationObject.getMinX() + (calculationObject.getSizeX() / 10.0) * i;
            Text text = new Text(formatterFunction.apply(Double.valueOf(number)));

            text.setTranslateX(-0.5 * size + i * (size / 10));
            text.setTranslateY(0.5 * size);
            text.setTranslateZ(-0.5 * size);
            text.setMouseTransparent(true);

            labelsX.add(text);
        }

        // for y axis
        for (int i = 0; i < 10; i++) {
            double number = calculationObject.getMinY() + (calculationObject.getSizeY() / 10.0) * i;
            Text text = new Text(formatterFunction.apply(Double.valueOf(number)));

            text.setTranslateX(0.5 * size);
            text.setTranslateY(0.5 * size - i * (size / 10));
            text.setTranslateZ(0.5 * size);
            text.setMouseTransparent(true);

            labelsY.add(text);
        }

        // for z axis
        for (int i = 0; i < 10; i++) {
            double number = calculationObject.getMinZ() + (calculationObject.getSizeZ() / 10.0) * i;
            Text text = new Text(formatterFunction.apply(Double.valueOf(number)));

            text.setTranslateX(0.5 * size);
            text.setTranslateY(0.5 * size);
            text.setTranslateZ(-0.5 * size + i * (size / 10));
            text.setMouseTransparent(true);

            labelsZ.add(text);
        }

    }

    private void createAxisTitles() {
        titles = new ArrayList<Node>(3);

        Text label = new Text(axisTitleX);
        label.setTranslateX(-0.05 * size);
        label.setTranslateY((0.5 * size) + 15);
        label.setTranslateZ(-0.5 * size - 15);
        label.setMouseTransparent(true);
        titles.add(label);

        label = new Text(axisTitleY);
        label.setTranslateX((0.5 * size) - 30);
        label.setTranslateY(-0.05 * size);
        label.setTranslateZ(0.5 * size - 15);
        label.setMouseTransparent(true);
        titles.add(label);

        label = new Text(axisTitleZ);
        label.setTranslateX(0.5 * size + 15);
        label.setTranslateY(0.5 * size + 15);
        label.setMouseTransparent(true);
        titles.add(label);
    }

    public void setNumberFormatter(Function<Double, String> formatterFunction) {
        this.formatterFunction = formatterFunction;
    }

    private String formatNumber(double number) {
        String result = Double.toString(number);
        int maxLength = 5;
        if (result.length() > maxLength) {
            int pointIndex = result.indexOf(".");

            if (pointIndex > result.length() - 3) {
                result = result.substring(0, pointIndex);
            } else {
                result = result.substring(0, maxLength + 1 < pointIndex ? pointIndex : maxLength + 1);
            }
        }

        while (result.length() > 1 && result.endsWith("0")) {
            result = result.substring(0, result.length() - 1);
        };

        if (result.endsWith(".")) {
            result += "0";
        }

        return result;
    }

    public void makeZoomable(Scene scene4EventFilter, Node control4Scaling) {
        scene4EventFilter.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            double delta = 1.2;
            double scale = control4Scaling.getScaleX();

            if (ObjectUtils.smallerDoublePrecision(event.getDeltaY(), 0)) {
                scale /= delta;
            } else {
                scale *= delta;
            }

            if (scale < MIN_SCALE || scale > MAX_SCALE) {
                scale = scale < MIN_SCALE ? MIN_SCALE : MAX_SCALE;
            }

            control4Scaling.setScaleX(scale);
            control4Scaling.setScaleY(scale);

            event.consume();
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        pnlChart = new JFXPanel();
        pnlChart.setLayout(new BorderLayout());
        add(pnlChart, BorderLayout.CENTER);

        if (controlPanel != null) {
            add(controlPanel, BorderLayout.EAST);
        }
    }

}
