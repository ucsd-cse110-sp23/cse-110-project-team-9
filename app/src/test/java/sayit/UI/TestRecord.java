package sayit.UI;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import sayit.MainUserInterface;

public class TestRecord {

	@Test
	//Assume there is no data.tsv
	public void testRecord() throws InterruptedException {
		MainUserInterface.getInstance().testing();
		JFrame frame = MainUserInterface.getInstance().getFrame();
		JButton recordButton = (JButton) ((JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.PAGE_START)).getComponent(0);
		JScrollPane scrollPane = (JScrollPane) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.LINE_START);
		JPanel scrollBar = (JPanel)scrollPane.getViewport().getComponent(0);
		Icon currIcon = recordButton.getIcon();
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		// mock start recording
		recordButton.getActionListeners()[0].actionPerformed(null);
		assert (!recordButton.getIcon().equals(currIcon));
		currIcon = recordButton.getIcon();
		// Recording nothing for 2 seconds
		Thread.sleep(2000);
		// mock stop recording
		recordButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();

		assert (!recordButton.getIcon().equals(currIcon));
		
		// make sure a button got added
		assert(scrollBar.getComponentCount() == 1);
		assert(scrollBar.getComponent(0) instanceof JButton);
		assertNotNull(((JButton) (scrollBar.getComponent(0))).getActionListeners()[0]);
	}
}
