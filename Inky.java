import java.awt.event.KeyEvent;

import javax.swing.JApplet;


public class Inky extends Ghost {

	public Inky (int[] start, double speed, int ghostNum, int[] scatterPoint, DrPacMan a, int startMode, int[] returnPoint)
	{
		super (start, speed, ghostNum, scatterPoint, a, startMode, returnPoint);
		eyesight = 1000;
	}

	// AI here - inky's a bad ambusher. Heads for the end of the tunnel pacman is heading down.
	// Low eyesight and takes shorter path. 
	// Can't see pacman behind him. Peels off if blinky or pinky is in closer hot
	// pursuit - always heads to the outside of the maze in this case.
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
		int xTarget = p.getX();
		int yTarget = p.getY();
		if (pe != null) // do we have a path for pacman?
			if (!pe.onPath(getX(), getY())) // if you're on pacman's path, just target him
				if (p.getDirection() == KeyEvent.VK_UP) // otherwise target the end of his path
					yTarget = pe.getStart();
				else if (p.getDirection() == KeyEvent.VK_LEFT)
					xTarget = pe.getStart();
				else if (p.getDirection() == KeyEvent.VK_RIGHT)
					xTarget = pe.getEnd();
				else if (p.getDirection() == KeyEvent.VK_DOWN)
					yTarget = pe.getEnd();
		// if you're in hot pursuit, continue unless someone is closer
		boolean peelOff = false;
		//System.out.println(getDistanceToPacMan());
		if (inHotPursuit())
		{
			for (int i=0; i<2; i++) // peel off for blinky and pinky
				if (ghosts[i].inHotPursuit() && ghosts[i].getDistanceToPacMan() < getDistanceToPacMan())
					peelOff = true;
		}
		// peelOff - someone else is closer
		if (peelOff && n > 1)
		{
			System.out.println("Trying the peeloff");
			int newDirection = 0;
			for (int i=0; i<1; i++) // only peeloff for blinky
			{
				System.out.println(options[i]+" "+direction);
				if (options[i] != direction)
				{
					switch (options[i]) // only go out of the maze
					{
					case KeyEvent.VK_UP:
						if (y < applet.getHeight()/2)
							newDirection = options[i];
						break;
					case KeyEvent.VK_DOWN:
						if (y > applet.getHeight()/2)
							newDirection = options[i];
						break;
					case KeyEvent.VK_LEFT:
						if (x < applet.getWidth()/2)
							newDirection = options[i];
						break;
					case KeyEvent.VK_RIGHT:
						if (x > applet.getWidth()/2)
							newDirection = options[i];
						break;
					}
				}
			}
			if (newDirection != 0)
				setDirection(newDirection);
		}
		else
		{
			// pick the direction of the greatest distance
			int distance = 10000;
			if (withinEyesight(p.getX(),p.getY()))
			{
				if (yTarget < getY() && up)
				{
					setDirection(KeyEvent.VK_UP);
					distance = getY() - yTarget;
				}
				if (yTarget > getY() && down && (yTarget - getY()) < distance)
				{
					setDirection(KeyEvent.VK_DOWN);
					distance = yTarget - getY();
				}
				if (xTarget < getX() && left && (getX() - xTarget) < distance)
				{
					distance = getX() - xTarget;
					setDirection(KeyEvent.VK_LEFT);
				}
				if (xTarget > getX() && right && (xTarget - getX()) < distance)
				{
					distance = xTarget - getX();
					setDirection(KeyEvent.VK_RIGHT);
				}
			}
			// not within eyesight or not found
			if (distance == 10000)
			{
				setDirection(pickDirection(up, left, down, right));
			}
		}
	}
}
