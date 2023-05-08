package sayit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

public class Interface {
	private static final String appName = "SayIt Assistant";
	private static final String trashCanFileName = "./Pictures/TrashCan.jpg";
	private static final String recordButtonFileName = "./Pictures/RecordButton.jpg";
	private static final String stopButtonFileName = "./Pictures/StopButton.jpg";
	
	private static JButton recordButton;
	private static JButton stopButton;

	// initialize the interface
	public static void init() {
		JFrame frame = new JFrame(appName);
		addComponentsToPane(frame.getContentPane());
		frame.pack();
		frame.setVisible(true);
	}

	// adds components to the interface
	public static void addComponentsToPane(Container pane) {
		JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		recordButton = new RoundButton(recordButtonFileName, 50);
		toolBar.add(recordButton);
		stopButton = new RoundButton(stopButtonFileName, 40);
		toolBar.add(stopButton);
		JButton button = new RoundButton(trashCanFileName, 40);
		toolBar.add(button);
		//TODO: ADD ACTION LISTENER TO THIS BUTTON
		button = new JButton("Clear All");
		toolBar.add(button);
		//TODO: ADD ACTION LISTENER TO THIS BUTTON
		pane.add(toolBar, BorderLayout.PAGE_START);

		//JTextArea scrollBar = new JTextArea("Questions"); //TEST SCROLL BAR
		JPanel scrollBar = new JPanel(new GridLayout(0, 1)); //USE THIS FOR APP
		for(int i = 0; i < 6; i++) {
			JButton test = new JButton(""+i);
			test.setPreferredSize(new Dimension(180, 100));
			scrollBar.add(test);
		}
		//TODO: ADD ALL QUESTIONS TO THIE PANEL
		JScrollPane scrollPane = new JScrollPane(scrollBar);
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(200, 500));
		pane.add(scrollPane, BorderLayout.LINE_START);

		JTextField content = new JTextField("CONTENT");
		content.setPreferredSize(new Dimension(500, 500));
		pane.add(content, BorderLayout.CENTER);
	}

	// For debugging
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();
			}
		});
	}
}

class RoundButton extends JButton {

	public RoundButton(String fileName, int size) {
		Image img;
		try {
			img = ImageIO.read(new File(fileName));
			super.setIcon(new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH)));

		} catch (Exception e) {
			System.out.println("ERROR");
			e.getStackTrace();
		}

		setBackground(Color.lightGray);
		setFocusable(false);
		Dimension size1 = getPreferredSize();
		size1.width = size1.height = 50;
		setPreferredSize(size1);
		setContentAreaFilled(false);
	}

	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			g.setColor(Color.gray);
		} else {
			g.setColor(getBackground());
		}
		g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

		super.paintComponent(g);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(Color.darkGray);
		g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
	}

	Shape shape;

	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
		}
		return shape.contains(x, y);
	}
}
