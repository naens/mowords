package test;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class MdCDisplayer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 647);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((dim.width - frame.getSize().width) / 2, (dim.height - frame.getSize().height) / 2);

		MdCPanel p = new MdCPanel();
		frame.add(p);
		frame.setVisible(true);

	}

}
