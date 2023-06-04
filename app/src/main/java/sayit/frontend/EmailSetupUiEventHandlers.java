package sayit.frontend;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

/**
 * This class contains the event handlers for the email setup UI.
 */
public final class EmailSetupUiEventHandlers {
    /**
     * Defines behavior when the close button is pressed.
     *
     * @param ui The <c>EmailSetUpUserInterface</c> object
     * @return a An <c>WindowAdapter</c> object. object
     */
    public static WindowAdapter onClosePress(EmailSetupUserInterface ui) {
        return new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ui.close();
            }
        };
    }

    /**
     * Handles the event when the user presses the Save button on the bottom
     *
     * @param ui The <c>EmailSetUpUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onSavePress(EmailSetupUserInterface ui) {
        return e -> ui.save();
    }

    /**
     * Handles the event when the user presses the Cancel button on the bottom
     *
     * @param ui The <c>EmailSetUpUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onCancelPress(EmailSetupUserInterface ui) {
        return e -> ui.close();
    }
}
