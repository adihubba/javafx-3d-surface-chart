package de.adihubba.javafx.jfx3d;


import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.adihubba.swing.JBasicFormattedLabel;
import de.adihubba.swing.ListLayout;
import de.adihubba.swing.ValueChangedListener;
import de.adihubba.swing.radiobutton.YesNoRadioButtonHorPanel;
import javafx.scene.shape.DrawMode;


/**
 * Control panel for interaction with the chart
 */
public class MeshControlPanel extends JPanel {

    private final Mesh3DChartPanel   control;

    private YesNoRadioButtonHorPanel showOrientationcross;
    private YesNoRadioButtonHorPanel interpolateValues;
    private YesNoRadioButtonHorPanel dynamicWalls;
    private YesNoRadioButtonHorPanel showLeftWall;
    private YesNoRadioButtonHorPanel showRightWall;
    private YesNoRadioButtonHorPanel showTopWall;
    private YesNoRadioButtonHorPanel showBottomWall;
    private YesNoRadioButtonHorPanel showFrontWall;
    private YesNoRadioButtonHorPanel showBackWall;

    private JBasicFormattedLabel     lblX;
    private JBasicFormattedLabel     lblY;
    private JBasicFormattedLabel     lblZ;
    private JBasicFormattedLabel     txtX;
    private JBasicFormattedLabel     txtY;
    private JBasicFormattedLabel     txtZ;

    public MeshControlPanel(Mesh3DChartPanel control) {
        this.control = control;
        initComponents();
        initListeners();
    }

    public void setLeftWallVisible(boolean visible) {
        showLeftWall.setYesSelected(visible);
    }

    public void setRightWallVisible(boolean visible) {
        showRightWall.setYesSelected(visible);
    }

    public void setTopWallVisible(boolean visible) {
        showTopWall.setYesSelected(visible);
    }

    public void setBottomWallVisible(boolean visible) {
        showBottomWall.setYesSelected(visible);
    }

    public void setFrontWallVisible(boolean visible) {
        showFrontWall.setYesSelected(visible);
    }

    public void setBackWallVisible(boolean visible) {
        showBackWall.setYesSelected(visible);
    }

    public void setInterpolateValues(boolean visible) {
        interpolateValues.setYesSelected(visible);
    }

    public void setShowOrientationCross(boolean visible) {
        showOrientationcross.setYesSelected(visible);
    }

    public void setDynamicWalls(boolean dynamicWallsEnabled) {
        dynamicWalls.setYesSelected(dynamicWallsEnabled);
        updateControls();
    }

    private void updateControls() {
        boolean enable = !dynamicWalls.isYesSelected();
        showLeftWall.setEnabled(enable);
        showRightWall.setEnabled(enable);
        showTopWall.setEnabled(enable);
        showBottomWall.setEnabled(enable);
        showFrontWall.setEnabled(enable);
        showBackWall.setEnabled(enable);
    }

    private void initListeners() {
        ValueChangedListener listener = (lastValue, newValue, source) -> {
            if (newValue != lastValue) {
                control.showOrientationCross(showOrientationcross.isYesSelected());
                control.setDrawMode(interpolateValues.isYesSelected() ? DrawMode.FILL : DrawMode.LINE);
                control.setLeftWallVisible(showLeftWall.isYesSelected());
                control.setRightWallVisible(showRightWall.isYesSelected());
                control.setTopWallVisible(showTopWall.isYesSelected());
                control.setBottomWallVisible(showBottomWall.isYesSelected());
                control.setFrontWallVisible(showFrontWall.isYesSelected());
                control.setBackWallVisible(showBackWall.isYesSelected());
                control.activateDynamicWalls(dynamicWalls.isYesSelected());
                updateControls();
            }
        };

        dynamicWalls.addValueChangedListener(listener);
        showOrientationcross.addValueChangedListener(listener);
        interpolateValues.addValueChangedListener(listener);
        showLeftWall.addValueChangedListener(listener);
        showRightWall.addValueChangedListener(listener);
        showTopWall.addValueChangedListener(listener);
        showBottomWall.addValueChangedListener(listener);
        showFrontWall.addValueChangedListener(listener);
        showBackWall.addValueChangedListener(listener);
    }

    public void setLabels(String x, String y, String z) {
        lblX.setText(x);
        lblY.setText(y);
        lblZ.setText(z);
    }

    public void setSelectedValues(double x, double y, double z) {
        txtX.setText(x + "");
        txtY.setText(y + "");
        txtZ.setText(z + "");
    }

    private void initComponents() {
        this.setLayout(new ListLayout());

        showOrientationcross = new YesNoRadioButtonHorPanel();
        interpolateValues = new YesNoRadioButtonHorPanel();
        dynamicWalls = new YesNoRadioButtonHorPanel();
        showLeftWall = new YesNoRadioButtonHorPanel();
        showRightWall = new YesNoRadioButtonHorPanel();
        showTopWall = new YesNoRadioButtonHorPanel();
        showBottomWall = new YesNoRadioButtonHorPanel();
        showFrontWall = new YesNoRadioButtonHorPanel();
        showBackWall = new YesNoRadioButtonHorPanel();

        lblX = new JBasicFormattedLabel();
        lblY = new JBasicFormattedLabel();
        lblZ = new JBasicFormattedLabel();
        txtX = new JBasicFormattedLabel();
        txtY = new JBasicFormattedLabel();
        txtZ = new JBasicFormattedLabel();

        JPanel panelControl = new JPanel(new ListLayout());
        this.add(panelControl);
        panelControl.add(new LabeledRowComponent(showOrientationcross, "ShowOrientation"));
        panelControl.add(new LabeledRowComponent(interpolateValues, "InterpolateValues"));

        panelControl.add(new LabeledRowComponent(dynamicWalls, "Dynamic walls"));
        panelControl.add(new LabeledRowComponent(new JPanel(), "Show wall"));
        panelControl.add(new LabeledRowComponent(showLeftWall, " - left"));
        panelControl.add(new LabeledRowComponent(showRightWall, " - right"));
        panelControl.add(new LabeledRowComponent(showTopWall, " - top"));
        panelControl.add(new LabeledRowComponent(showBottomWall, " - bottom"));
        panelControl.add(new LabeledRowComponent(showFrontWall, " - front"));
        panelControl.add(new LabeledRowComponent(showBackWall, " - back"));

        JPanel pnlClickedValue = new JPanel(new ListLayout());
        pnlClickedValue.setPreferredSize(new Dimension(210, 70));
        pnlClickedValue.setBorder(BorderFactory.createTitledBorder("Selected point"));
        this.add(pnlClickedValue);
        pnlClickedValue.add(new LabeledRowComponent(txtX, lblX));
        pnlClickedValue.add(new LabeledRowComponent(txtY, lblY));
        pnlClickedValue.add(new LabeledRowComponent(txtZ, lblZ));
    }

    public static class LabeledRowComponent extends JPanel {

        public LabeledRowComponent(JComponent component, JComponent text) {
            this.setLayout(new ListLayout(SwingConstants.HORIZONTAL, true, 15));
            this.add(text);
            this.add(component);
        }

        public LabeledRowComponent(JComponent component, String text) {
            this.setLayout(new ListLayout(SwingConstants.HORIZONTAL, false, 0));
            JBasicFormattedLabel label = new JBasicFormattedLabel(text);
            label.setPreferredSize(new Dimension(120, 20));
            this.add(label);
            this.add(component);
        }
    }
}
