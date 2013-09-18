package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jsesh.mdc.MDCSyntaxError;
import jsesh.mdcDisplayer.draw.MDCDrawingFacade;

public class MdCPanel extends JPanel {

	private static final long serialVersionUID = 1874922666308276520L;

	private BufferedImage image;

	private JTextField textField;

	private ImagePanel imagePanel;

	public MdCPanel() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		textField = new JTextField();
		add(textField, BorderLayout.PAGE_START);
		imagePanel = new ImagePanel();
		imagePanel.setBackground(Color.GRAY);
		add(imagePanel, BorderLayout.CENTER);
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (arg0.getKeyChar() == 10) {

					MDCDrawingFacade facade = new MDCDrawingFacade();
					facade.setCadratHeight(120);
					try {
						image = facade.createImage(textField.getText());
						imagePanel.repaint();
					} catch (MDCSyntaxError e) {
						JOptionPane.showMessageDialog(null, "MDCSyntaxError");
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
	}

	private class ImagePanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image, getWidth()/2 - image.getWidth()/2, getHeight()/2 - image.getHeight()/2, null);
			}
		}
	}
}
