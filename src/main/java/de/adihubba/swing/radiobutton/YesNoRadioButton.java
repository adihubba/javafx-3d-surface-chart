package de.adihubba.swing.radiobutton;


/**
 * YesNoRadioButton
 * Created: 27.11.2009
 * @author <A HREF="mailto:stefan.baer@x-map.de">Stefan Baer </A>
 * @version $Revision: 1.2 $ $Date: 2012/10/09 12:22:10 $ $Author: schloemer $
 */
public interface YesNoRadioButton {

    void setYesSelected(boolean yes);

    /**
     * getter for Yes
     * @return true if yes is selected
     */
    boolean isYesSelected();

    /**
     * getter for No
     * @return true if no is selected
     */
    boolean isNoSelected();

    void setValue(Boolean value);

    Boolean getValue();

}
