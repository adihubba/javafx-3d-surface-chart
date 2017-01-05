package de.adihubba.swing;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import javax.swing.SwingConstants;

import de.adihubba.ObjectUtils;


/**
 * Plain layout putting all components in a column from top to bottom.
 * Using orientation SwingConstants.HORIZONTAL components will be put in a single row from left to right.
 * Similar to BoxLayout, but components will keep their size and position when more space is available. 
 */
public class ListLayout implements LayoutManager2 {

    private Dimension preferredSize = new Dimension();
    private int       gap;
    private boolean   fillHorizontal;
    private boolean   valid         = false;
    private int       orientation;

    /** vertical layout (single column), no horizontal stretching, no gap between components */
    public ListLayout() {
        this(SwingConstants.VERTICAL, false, 0);
    }

    public ListLayout(int yGap) {
        this(SwingConstants.VERTICAL, false, yGap);
    }

    public ListLayout(boolean fillHorizontal) {
        this(SwingConstants.VERTICAL, fillHorizontal, 0);
    }

    public ListLayout(boolean fillHorizontal, int gap) {
        this(SwingConstants.VERTICAL, fillHorizontal, gap);
    }

    /** 
     * @param orientation   see SwingConstants.VERTICAL, SwingConstants.HORIZONTAL
     * @param gap           distance between components in pixel
     */
    public ListLayout(int orientation, int gap) {
        this(orientation, false, gap);
    }

    /** 
     * @param orientation   see SwingConstants.VERTICAL, SwingConstants.HORIZONTAL
     * @param fill          stretch all components
     * @param gap           distance between components in pixel
     */
    public ListLayout(int orientation, boolean fill, int gap) {
        this.orientation = orientation;
        this.fillHorizontal = fill;
        this.gap = gap;
    }

    @Override
    public void layoutContainer(Container parent) {
        if (parent == null) {
            return;
        }

        Insets insets = parent.getInsets();
        int x = insets.left;
        int y = insets.top;
        Dimension parentSize = parent.getSize();
        int innerParentWidth = parentSize.width - insets.left - insets.right;
        int innerParentHeight = parentSize.height - insets.top - insets.bottom;
        int panelWidth = 0;
        int panelHeight = 0;

        Component[] components = parent.getComponents();
        if (!ObjectUtils.arrayHasElements(components)) {
            preferredSize = new Dimension();
            valid = true;
            return;
        }

        for (Component component : components) {
            if (!component.isVisible()) {
                continue;
            }

            Dimension componentDimension = component.getPreferredSize();
            int componentWidth = componentDimension.width;
            int componentHeight = componentDimension.height;

            int width = componentWidth;
            int height = componentHeight;
            if (fillHorizontal) {
                // change component sizes to container size
                if (orientation == SwingConstants.VERTICAL) {
                    width = innerParentWidth;
                }
                if (orientation == SwingConstants.HORIZONTAL) {
                    height = innerParentHeight;
                }
            }
            component.setBounds(x, y, width, height);

            if (orientation == SwingConstants.VERTICAL) {
                panelWidth = Math.max(panelWidth, componentWidth);
                y += componentHeight + gap;
            }
            if (orientation == SwingConstants.HORIZONTAL) {
                panelHeight = Math.max(panelHeight, componentHeight);
                x += componentWidth + gap;
            }
        }

        if (orientation == SwingConstants.VERTICAL) {
            if (y >= gap) {
                y -= gap;
            }

            preferredSize = new Dimension(panelWidth + insets.left + insets.right, y + insets.bottom);
        }
        if (orientation == SwingConstants.HORIZONTAL) {
            if (x >= gap) {
                x -= gap;
            }

            preferredSize = new Dimension(x + insets.right, panelHeight + insets.top + insets.bottom);
        }

        valid = true;
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        if (!valid) {
            layoutContainer(target);
        }
        return new Dimension(Integer.MAX_VALUE, preferredSize.height);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        if (!valid) {
            layoutContainer(parent);
        }
        return preferredSize;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if (!valid) {
            layoutContainer(parent);
        }
        return preferredSize;
    }

    @Override
    public void invalidateLayout(Container target) {
        valid = false;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

}
