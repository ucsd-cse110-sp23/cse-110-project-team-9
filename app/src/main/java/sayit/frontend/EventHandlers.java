package sayit.frontend;

import sayit.common.qa.QuestionAnswerEntry;
import sayit.frontend.helpers.ImageHelper;
import sayit.frontend.helpers.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static sayit.frontend.FrontEndConstants.*;

/**
 * A class that contains static methods to handle events in the UI.
 */
public final class EventHandlers {

	// constants
	private static final String emailHeader = "Email: ";
	private static final String passwordHeader = "Passworrd ";
	private static final String createAccountTitle = "Create Account";
	private static final String loginTitle = "Login";

	/**
	 * Handles the event when the user presses the button from the sidebar (the
	 * question/answer history button).
	 *
	 * @param ui     The <c>MainUserInterface</c> object.
	 * @param qa     The <c>QuestionAnswerEntry</c> object.
	 * @param button The <c>QuestionButton</c> object.
	 * @return An <c>ActionListener</c> object.
	 */
	public static ActionListener onQaButtonPress(MainUserInterface ui, QuestionAnswerEntry qa, QuestionButton button) {
		return e -> {
			ui.displayEntry(qa);
			// track which button was last selected for deletion
			ui.setSelectedButton(button);
		};
	}

	/**
	 * Handles the event when the user presses the create button.
	 * 
	 * @return An <c>ActionListener</c> object.
	 */
	public static ActionListener onCreateButtonPress() {
		return e -> {
			JPanel myPanel = new JPanel();
			myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
			JPanel topPanel = new JPanel();
			topPanel.add(new JLabel(emailHeader));
			JTextField emailField = new JTextField(10);
			topPanel.add(emailField);
			myPanel.add(topPanel);

			JPanel bottomPanel = new JPanel();
			bottomPanel.add(new JLabel(passwordHeader));
			JTextField passwordField = new JTextField(10);
			bottomPanel.add(passwordField);
			myPanel.add(bottomPanel);

			int result = JOptionPane.showConfirmDialog(null, myPanel, createAccountTitle, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				// TODO: ADD to database if user presses ok
				System.out.println("Created Account!");
				System.out.println("Email entered: " + emailField.getText());
				System.out.println("Password enterd: " + passwordField.getText());
				LoginUserInterface.getInstance().close(); //close the login UI
				MainUserInterface.getInstance(); //start the main UI
			} 
		};
	}

	/**
	 * Handles the event when the user presses the login button.
	 * 
	 * @return An <c>ActionListener</c> object.
	 */
	public static ActionListener onLoginButtonPress() {
		return e -> {
			JPanel myPanel = new JPanel();
			myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
			JPanel topPanel = new JPanel();
			topPanel.add(new JLabel(emailHeader));
			JTextField emailField = new JTextField(10);
			topPanel.add(emailField);
			myPanel.add(topPanel);

			JPanel bottomPanel = new JPanel();
			bottomPanel.add(new JLabel(passwordHeader));
			JTextField passwordField = new JTextField(10);
			bottomPanel.add(passwordField);
			myPanel.add(bottomPanel);

			int result = JOptionPane.showConfirmDialog(null, myPanel, loginTitle, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				// TODO: Verify credentials to determine successful login or not
				System.out.println("Logged in!");
				System.out.println("Email entered: " + emailField.getText());
				System.out.println("Password enterd: " + passwordField.getText());
				LoginUserInterface.getInstance().close(); //close the login UI
				MainUserInterface.getInstance(); //start the main UI
			} 
		};
	}
}
