package sayit.frontend.components;

import javax.swing.*;

import static sayit.frontend.FrontEndConstants.PROMPT_HISTORY_BTN_DIMENSIONS;

/**
 * Button class for questions on sidebar
 */
public class SidebarButton extends JButton {
    private final long id;

    /**
     * Creates a <c>QuestionButton</c> object with the displayName.
     *
     * @param displayName The text to be displayed on the button.
     * @param id          The ID of the corresponding QuestionAnswerEntry in the
     *                    database
     */
    public SidebarButton(String displayName, long id) {
        super(displayName);
        this.setPreferredSize(PROMPT_HISTORY_BTN_DIMENSIONS);
        this.id = id;
    }

    /**
     * Public getter method for the ID
     *
     * @return The ID.
     */
    public long getId() {
        return this.id;
    }
}