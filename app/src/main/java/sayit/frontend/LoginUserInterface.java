package sayit.frontend;

import static sayit.frontend.FrontEndConstants.CLOSE_WINDOW_TEXT;
import static sayit.frontend.FrontEndConstants.CLOSE_WINDOW_TITLE;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The login user interface for the application.  
 */
public class LoginUserInterface {
	
	//class variables
    private final JFrame frame;
    
    //constants
    private final Dimension buttonDimension = new Dimension(200, 100);
	private static final String createAccountTitle = "Create Account";
	private static final String loginTitle = "Login";
	
	private LoginUserInterface() {
		frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addComponentsToPane(this.frame.getContentPane());
        this.frame.pack();
        this.frame.setVisible(true);

        // Add behavior for closing app, update db.
        // https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        // https://www.codejava.net/java-se/swing/preventing-jframe-window-from-closing
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirmClose = JOptionPane.showConfirmDialog(frame,
                        CLOSE_WINDOW_TEXT, CLOSE_WINDOW_TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                // if user confirms closing the window, exit
                if (confirmClose == JOptionPane.YES_OPTION) {
                    // terminate Java VM and exit
                    System.exit(0);
                }
            }
        });
	}
	private static LoginUserInterface userInterface;
	
    /**
     * <p>
     * Gets or creates a new instance of the <c>LoginUserInterface</c> class. This
     * method is
     * designed so that there can be at most one instance of the
     * <c>LoginUserInterface</c> class
     * at any point.
     * </p>
     *
     * <p>
     * This will also automatically make the user interface visible if it hasn't
     * been initialized.
     * </p>
     *
     * @return The instance of the <c>LoginUserInterface</c> class.
     */
    public static LoginUserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new LoginUserInterface();
        }

        return userInterface;
    }

    /**
     * Adds the specified components to this user interface.
     *
     * @param pane The pane to add the components to.
     */
    private void addComponentsToPane(Container contentPane) {
		JButton createButton = new JButton(createAccountTitle);
		createButton.setPreferredSize(buttonDimension);
        createButton.addActionListener(EventHandlers.onCreateButtonPress());
        contentPane.add(createButton, BorderLayout.WEST);
        JButton loginButton = new JButton(loginTitle);
		loginButton.setPreferredSize(buttonDimension);
        loginButton.addActionListener(EventHandlers.onLoginButtonPress());
        contentPane.add(loginButton, BorderLayout.EAST);
	}
    
    /**
     * Closes the LoginUserInterface
     */
    public void close() {
    	frame.setVisible(false);
    }
}
