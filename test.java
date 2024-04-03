import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;


public class test extends JApplet implements KeyListener, MouseListener {

	public void init() {
		addKeyListener(this);
		addMouseListener(this);
		System.out.println("init");
	}
	int x;
	public void paint(Graphics g) {
		g.drawString("hi "+x, 50, 50);
	}

	public void keyPressed(KeyEvent arg0) {
		System.out.println("p");		
System.out.println(arg0.getKeyCode());
		x=arg0.getKeyCode();
		repaint();
	}

	public void keyReleased(KeyEvent arg0) {
System.out.println("r");		
	}

	public void keyTyped(KeyEvent arg0) {
		System.out.println("t");		
		
	}

	public void mouseClicked(MouseEvent arg0) {
x = 1000;
repaint();
System.out.println("fdsa");
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
