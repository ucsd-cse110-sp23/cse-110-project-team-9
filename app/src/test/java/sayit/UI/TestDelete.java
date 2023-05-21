package sayit.UI;

import java.awt.AWTException;
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

public class TestDelete {
	@Test
	//assumes TestRecord passes
	public void testDelete() throws InterruptedException, AWTException {
		MainUserInterface.getInstance().testing();
		JFrame frame = MainUserInterface.getInstance().getFrame();
		JButton recordButton = (JButton) ((JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.PAGE_START)).getComponent(0);
		JButton deleteButton = (JButton) ((JPanel) ((BorderLayout) frame.getContentPane().getLayout())
				.getLayoutComponent(BorderLayout.PAGE_START)).getComponent(1);
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
		
		//Very small chance where chatGPT produces the same answer this will fail
		assertNotEquals(firstAnswer, secondAnswer);
		
		assert(scrollBar.getComponentCount() == 2);
		
		//uncomment to see 2 questions
		//Thread.sleep(3000);
		
		// click on the first question
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		((JButton) (scrollBar.getComponent(0))).getActionListeners()[0].actionPerformed(null);
		assertEquals(answer.getText(), firstAnswer);
		MainUserInterface.getInstance().await();
		
		//uncomment to see click
		//Thread.sleep(3000);
		
		//mimic delete
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		deleteButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		
		assert(scrollBar.getComponentCount() == 1);
		
		//uncomment to see only the second question
		//Thread.sleep(3000);
		
		// click on the only question that is left(the second question)
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		((JButton) (scrollBar.getComponent(0))).getActionListeners()[0].actionPerformed(null);
		assertEquals(answer.getText(), secondAnswer);
		MainUserInterface.getInstance().await();
		
		//mimic delete
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		deleteButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
		
		assert(scrollBar.getComponentCount() == 0);
		
		//uncomment to see no questions left
		//Thread.sleep(3000);
		
		//make sure delete doesn't give an error when theres nothing deleted
		
		//mimic delete
		MainUserInterface.getInstance().addLatch(new CountDownLatch(1));
		deleteButton.getActionListeners()[0].actionPerformed(null);
		MainUserInterface.getInstance().await();
	}
}
