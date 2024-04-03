import java.awt.event.KeyEvent;

import javax.swing.JApplet;


public class Blinky extends Ghost {

	public Blinky (int[] start, double speed, int ghostNum, int[] scatterPoint, DrPacMan a, int startMode, int[] returnPoint)
	{
		super (start, speed, ghostNum, scatterPoint, a, startMode, returnPoint);
		eyesight = 1000;
	}

	// AI here - blinky's a chaser. if a horiz or vert choice, takes longer path,
	// has infinite eyesight. sees pacman behind him and veers off if that happens.
	public void setChaseDirection(int[] options, int n, PacMan p, Ghost[] ghosts, PathElement pe)
	{
		int[] pacMan = applet.wheresPacMan();
		// check the options
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		for (int i=0; i<n; i++)
		{
			switch (options[i])
			{
			case KeyEvent.VK_UP:
				up = true;
				break;
			case KeyEvent.VK_DOWN:
				down = true;
				break;
			case KeyEvent.VK_LEFT:
				left = true;
				break;
			case KeyEvent.VK_RIGHT:
				right = true;
				break;
			}
		}
		// pick the direction of the greatest distance
		int distance = -1;
		if (withinEyesight(p.getX(),p.getY()))
		{
			if (p.getY() < getY() && up)
			{
				setDirection(KeyEvent.VK_UP);
				distance = getY() - p.getY();
			}
			if (p.getY() > getY() && down && (p.getY() - getY()) > distance)
			{
				setDirection(KeyEvent.VK_DOWN);
				distance = p.getY() - getY();
			}
			if (p.getX() < getX() && left && (getX() - p.getX()) > distance)
			{
				distance = getX() - p.getX();
				setDirection(KeyEvent.VK_LEFT);
			}
			if (p.getX() > getX() && right && (p.getX() - getX()) > distance)
			{
				distance = p.getX() - getX();
				setDirection(KeyEvent.VK_RIGHT);
			}
			if (distance == -1) // not found check behind you
			{
				if (direction == KeyEvent.VK_UP && p.getX() == getX() && p.getY() > getY())
				{
					if (right)
						setDirection(KeyEvent.VK_RIGHT);
					else if (left)
						setDirection(KeyEvent.VK_LEFT);
					distance = p.getY() - getY();
				}
				else if (direction == KeyEvent.VK_DOWN && p.getX() == getX() && p.getY() < getY())
				{
					if (right)
						setDirection(KeyEvent.VK_RIGHT);
					else if (left)
						setDirection(KeyEvent.VK_LEFT);
					distance = getY() - p.getY();
				}
				else if (direction == KeyEvent.VK_LEFT && p.getY() == getY() && p.getX() > getX())
				{
					if (up)
						setDirection(KeyEvent.VK_UP);
					else if (down)
						setDirection(KeyEvent.VK_DOWN);
					distance = p.getX() - getX();
				}
				else if (direction == KeyEvent.VK_RIGHT && p.getY() == getY() && p.getX() < getX())
				{
					if (up)
						setDirection(KeyEvent.VK_UP);
					else if (down)
						setDirection(KeyEvent.VK_DOWN);
					distance = getX() - p.getX();
				}
			}
		}
		// not within eyesight or not found.
		if (distance == -1)
		{
			setDirection(pickDirection(up, left, down, right));
		}
	}
}
