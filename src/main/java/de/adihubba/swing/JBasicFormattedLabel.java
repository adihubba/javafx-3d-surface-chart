package de.adihubba.swing;


import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class JBasicFormattedLabel extends JLabel {

    public JBasicFormattedLabel() {
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    public JBasicFormattedLabel(String text) {
        this();
        setText(text);
    }

}
