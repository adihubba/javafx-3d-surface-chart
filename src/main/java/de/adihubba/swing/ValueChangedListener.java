package de.adihubba.swing;


/**
 * Listener to handle user input e.g. on text fields
 */
public interface ValueChangedListener {

    public void valueChanged(Object lastValue, Object newValue, Object source);

}
