import java.awt.event.KeyEvent;

import javax.swing.JApplet;


public class Sue extends Ghost {

	public Sue (int[] start, double speed, int ghostNum, int[] scatterPoint, DrPacMan a, int startMode, int[] returnPoint)
	{
		super (start, speed, ghostNum, scatterPoint, a, startMode, returnPoint);
		eyesight = 150;
	}

	// AI here - sue's a lousy chaser. if a horiz or vert choice, takes shorter path.
	// if within eyesight, flees. Doesn't see pacman behind her.
	public void setChaseDirection(int[] options, int n, PacMan p, Ghost[] ghosts, PathElement pe)
	{
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
		int distance = 10000;
		if (withinEyesight(p.getX(),p.getY()))
		{
			if (p.getY() < getY() && down)
			{
				setDirection(KeyEvent.VK_DOWN);
				distance = getY() - p.getY();
			}
			if (p.getY() > getY() && up && (p.getY() - getY()) < distance)
			{
				setDirection(KeyEvent.VK_UP);
				distance = p.getY() - getY();
			}
			if (p.getX() < getX() && right && (getX() - p.getX()) < distance)
			{
				distance = getX() - p.getX();
				setDirection(KeyEvent.VK_RIGHT);
			}
			if (p.getX() > getX() && left && (p.getX() - getX()) < distance)
			{
				distance = p.getX() - getX();
				setDirection(KeyEvent.VK_LEFT);
			}
		}
		else
		{
			if (p.getY() < getY() && up)
			{
				setDirection(KeyEvent.VK_UP);
				distance = getY() - p.getY();
			}
			if (p.getY() > getY() && down && (p.getY() - getY()) < distance)
			{
				setDirection(KeyEvent.VK_DOWN);
				distance = p.getY() - getY();
			}
			if (p.getX() < getX() && left && (getX() - p.getX()) < distance)
			{
				distance = getX() - p.getX();
				setDirection(KeyEvent.VK_LEFT);
			}
			if (p.getX() > getX() && right && (p.getX() - getX()) < distance)
			{
				distance = p.getX() - getX();
				setDirection(KeyEvent.VK_RIGHT);
			}
		}
		// can't see pacman or locator code failed. move randomly.
		if (distance == 10000)
		{
			setDirection(pickDirection(up, left, down, right));
		}
	}
}
