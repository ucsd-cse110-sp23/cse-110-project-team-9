package sayit.UI;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.junit.jupiter.api.Test;

import sayit.MainUserInterface;

public class TestInitialComponents {

	@Test
	public void testInitalComponents() {
		// Test Frame is there
		JFrame frame = MainUserInterface.getInstance().getFrame();
		assertNotNull(frame);
		Container pane = frame.getContentPane();
		assertNotNull(pane);
		BorderLayout layout = (BorderLayout) pane.getLayout();
		assertNotNull(layout);
		// Test toolBar is there
		JPanel toolBar = (JPanel) layout.getLayoutComponent(BorderLayout.PAGE_START);
		assertNotNull(toolBar);
		JButton recordButton = (JButton) toolBar.getComponent(0);
		assertNotNull(recordButton);
		assertNotNull(recordButton.getIcon());
		assert (recordButton.getActionListeners().length != 0);
		JButton deleteButton = (JButton) toolBar.getComponent(1);
		assertNotNull(deleteButton);
		assertNotNull(deleteButton.getIcon());
		// Uncomment when delete button's functionality gets added
		// assert(deleteButton.getActionListeners().length != 0);
		JButton clearAllButton = (JButton) toolBar.getComponent(2);
		assertNotNull(clearAllButton);
		assertEquals(clearAllButton.getText(), "Clear All");
		assert (clearAllButton.getActionListeners().length != 0);

		// Test the Prompt History on the left can be scrolled
		Component scrollPane = layout.getLayoutComponent(BorderLayout.LINE_START);
		assert (scrollPane instanceof JScrollPane);

		// Test the Question and Answer text fields are scrollable
		JPanel content = (JPanel) layout.getLayoutComponent(BorderLayout.CENTER);
		assertNotNull(content);
		Component qScrollPane = content.getComponent(0);
		assert (qScrollPane instanceof JScrollPane);
		Component aScrollPane = content.getComponent(1);
		assert (aScrollPane instanceof JScrollPane);
	}
}