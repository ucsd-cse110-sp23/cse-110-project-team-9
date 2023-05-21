package sayit.UI;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.jupiter.api.Test;

import sayit.MainUserInterface;


public class TestClearAll {
	@Test
	//assumes TestRecord passes
	public void testClearAll() throws InterruptedException {
		MainUserInterface.getInstance().testing();
		JFrame frame = MainUserInterface.getInstance().getFrame();
		JButton recordButton = (JButton) ((JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.PAGE_START)).getComponent(0);
		JScrollPane scrollPane = (JScrollPane) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.LINE_START);
		JPanel scrollBar = (JPanel)scrollPane.getViewport().getComponent(0);
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		// add 2 dummy questions
		recordButton.getActionListeners()[0].actionPerformed(null);
		Thread.sleep(2000);
		recordButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		recordButton.getActionListeners()[0].actionPerformed(null);
		Thread.sleep(2000);
		recordButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		//uncomment to see 2 questions
		//Thread.sleep(3000);
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		JButton clearAllButton = (JButton) ((JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.PAGE_START)).getComponent(2);
		clearAllButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		//uncomment to see cleared
		//Thread.sleep(3000);
		assert(scrollBar.getComponentCount() == 0);
	}
}
