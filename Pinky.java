import java.awt.event.KeyEvent;

import javax.swing.JApplet;


public class Pinky extends Ghost {

	public Pinky (int[] start, double speed, int ghostNum, int[] scatterPoint, DrPacMan a, int startMode, int[] returnPoint)
	{
		super (start, speed, ghostNum, scatterPoint, a, startMode, returnPoint);
		eyesight = 1000;
	}

	// AI here - pinky's an ambusher. aims a point 150 pixels ahead of pacman 
	// currently in. if faced with both a horiz or vert choice, takes longer path,
	// has pretty good eyesight. see's pacman behind her. Peels off if blinky is in closer hot
	// pursuit - always heads to the inside of the maze in this case.
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
		// target is initially pacman
		int xTarget = p.getX();
		int yTarget = p.getY();

		if (pe != null) // do we have a path for pacman?
			if (!pe.onPath(getX(), getY())) // if you're on pacman's path, just target him
				if (p.getDirection() == KeyEvent.VK_UP) // otherwise target the end of his path
					//yTarget = pe.getStart();
					yTarget -= 150;
				else if (p.getDirection() == KeyEvent.VK_LEFT)
					//xTarget = pe.getStart();
					xTarget -= 150;
				else if (p.getDirection() == KeyEvent.VK_RIGHT)
					//xTarget = pe.getEnd();
					xTarget += 150;
				else if (p.getDirection() == KeyEvent.VK_DOWN)
					//yTarget = pe.getEnd();
					yTarget += 150;

		// if you're in hot pursuit, continue unless someone is closer
		boolean peelOff = false;
		//System.out.println(getDistanceToPacMan());
		if (inHotPursuit())
		{
			for (int i=0; i<1; i++) // only peel off for blinky
				if (ghosts[i].inHotPursuit() && ghosts[i].getDistanceToPacMan() < getDistanceToPacMan())
					peelOff = true;
		}
		// peelOff - someone else is closer
		if (peelOff && n > 1)
		{
			System.out.println("Trying the peeloff");
			int newDirection = 0;
			for (int i=0; i<n; i++)
			{
				System.out.println(options[i]+" "+direction);
				if (options[i] != direction)
				{
					switch (options[i]) // only go into the maze
					{
					case KeyEvent.VK_UP:
						if (y > applet.getHeight()/2)
							newDirection = options[i];
						break;
					case KeyEvent.VK_DOWN:
						if (y < applet.getHeight()/2)
							newDirection = options[i];
						break;
					case KeyEvent.VK_LEFT:
						if (x > applet.getWidth()/2)
							newDirection = options[i];
						break;
					case KeyEvent.VK_RIGHT:
						if (x < applet.getWidth()/2)
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
			int distance = -1;
			if (withinEyesight(p.getX(),p.getY()))
			{
				if (yTarget < getY() && up)
				{
					setDirection(KeyEvent.VK_UP);
					distance = getY() - yTarget;
				}
				if (yTarget > getY() && down && (yTarget - getY()) > distance)
				{
					setDirection(KeyEvent.VK_DOWN);
					distance = yTarget - getY();
				}
				if (xTarget < getX() && left && (getX() - xTarget) > distance)
				{
					distance = getX() - xTarget;
					setDirection(KeyEvent.VK_LEFT);
				}
				if (xTarget > getX() && right && (xTarget - getX()) > distance)
				{
					distance = xTarget - getX();
					setDirection(KeyEvent.VK_RIGHT);
				}
				else // not found check behind you
				{
					if (direction == KeyEvent.VK_UP && xTarget == getX() && yTarget > getY())
					{
						if (left)
							setDirection(KeyEvent.VK_LEFT);
						else if (right)
							setDirection(KeyEvent.VK_RIGHT);
						distance = yTarget - getY();
					}
					else if (direction == KeyEvent.VK_DOWN && xTarget == getX() && yTarget < getY())
					{
						if (left)
							setDirection(KeyEvent.VK_LEFT);
						else if (right)
							setDirection(KeyEvent.VK_RIGHT);;
							distance = getY() - yTarget;
					}
					else if (direction == KeyEvent.VK_LEFT && yTarget == getY() && xTarget > getX())
					{
						if (down)
							setDirection(KeyEvent.VK_DOWN);
						else if (up)
							setDirection(KeyEvent.VK_UP);
						distance = xTarget - getX();
					}
					else if (direction == KeyEvent.VK_RIGHT && yTarget == getY() && xTarget < getX())
					{
						if (down)
							setDirection(KeyEvent.VK_DOWN);
						else if (up)
							setDirection(KeyEvent.VK_UP);
						distance = getX() - xTarget;
					}
				}
			}
			// can't see pacman or locator code failed. move randomly.
			if (distance == -1)
			{
				setDirection(pickDirection(up, left, down, right));
			}
		}
	}
}
