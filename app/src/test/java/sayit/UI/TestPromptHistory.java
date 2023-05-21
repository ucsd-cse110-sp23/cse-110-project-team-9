package sayit.UI;

import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import sayit.MainUserInterface;

public class TestPromptHistory {
	@Test
	//assumes TestRecord passes
	public void testPromptyHistory() throws InterruptedException {
		MainUserInterface.getInstance().testing();
		JFrame frame = MainUserInterface.getInstance().getFrame();
		JButton recordButton = (JButton) ((JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.PAGE_START)).getComponent(0);
		JScrollPane scrollPane = (JScrollPane) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.LINE_START);
		JPanel scrollBar = (JPanel) scrollPane.getViewport().getComponent(0);
		JPanel content = (JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.CENTER);
		JTextArea answer = (JTextArea) (((JScrollPane) content.getComponent(1)).getViewport()).getComponent(0);
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		// add 2 dummy questions
		recordButton.getActionListeners()[0].actionPerformed(null);
		Thread.sleep(2000);
		recordButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		String firstAnswer = answer.getText();
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		recordButton.getActionListeners()[0].actionPerformed(null);
		Thread.sleep(2000);
		recordButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		String secondAnswer = answer.getText();
		
		//uncomment to see 2 questions
		Thread.sleep(3000);
		
		//Very small chance where chatGPT produces the same answer this will fail
		assertNotEquals(firstAnswer, secondAnswer);
		
		// click on the first question
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		((JButton) (scrollBar.getComponent(0))).getActionListeners()[0].actionPerformed(null);
		assertEquals(answer.getText(), firstAnswer);
		MainUserInterface.getInstance().await();
		
		//uncomment to see 1st question's response
		//Thread.sleep(3000);
		
		//click on the second question
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		((JButton) (scrollBar.getComponent(1))).getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		assertEquals(answer.getText(), secondAnswer);
		
		//uncomment to see 2nd question's response
		//Thread.sleep(3000);
	}
}
