import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JApplet;

// http://home.comcast.net/~jpittman2/pacman/pacmandossier.html

public class Ghost extends RailMover {
	public static final int SCATTER = 0;
	public static final int STAYTHECOURSE = 1;
	public static final int CHASE = 2;
	public static final int WAIT = 3;
	public static final int LEAVEBOX = 4;
	public static final int RUNAWAY = 5;
	public static final int RETURN = 6;

	public boolean spotlight = false; // true if you want to see their view

	public boolean flashing = false;
	public boolean flash = false;
	public int flashCounter = 0;

	private int animStepCounter = 0;
	private final int ANIMSTEPS = 40;
	int eyesight = 1000;
	int runawayEyesight = 150;
	int mode = STAYTHECOURSE;
	int imageNum = 0;
	Image[] sprites = new Image[8]; //0,1 = down, 2, 3 = left, 4, 5 = right, 6, 7 = up
	Image[] scaredSprite = new Image[4];
	Image[] returnSprite = new Image[4];
	DrPacMan applet;
	int eyeSight = 50; // default eyesight
	int ghostNum;
	int scatterX, scatterY;
	boolean reverseOK = false;
	double savedSpeed;
	int distanceToPacMan = 1000;
	boolean hotPursuit = false;
	int returnX, returnY;

	double runawaySpeedFactor = 2.0/3.0;
	double returnSpeedFactor = 2.0;
	double slowSpeedFactor = 0.5;

	public Ghost (int[] start, double speed, int ghostNum, int[] scatterPoint, DrPacMan a, int startMode, int[] returnPoint)
	{
		super (start, speed);
		this.savedSpeed = speed;
		this.applet = a;
		this.ghostNum = ghostNum;
		this.scatterX = scatterPoint[0];
		this.scatterY = scatterPoint[1];
		this.mode = startMode;
		this.returnX = returnPoint[0];
		this.returnY = returnPoint[1];
		for (int i=0; i<8; i++)
		{
			//System.out.println("images/g"+ghostNum+"_"+i+".gif");
			//sprites[i] = a.getImage(a.getCodeBase(),"images/g"+ghostNum+"_"+i+".gif");
			sprites[i] = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/g"+ghostNum+"_"+i+".gif"));
		}
		for (int i=0; i<4; i++)
			//scaredSprite[i] = a.getImage(a.getCodeBase(),"images/gscared_"+i+".gif");
			scaredSprite[i] = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/gscared_"+i+".gif"));
		for (int i=0; i<4; i++)
			//returnSprite[i] = a.getImage(a.getCodeBase(),"images/greturn_"+i+".gif");
			returnSprite[i] = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/greturn_"+i+".gif"));
	}

	public void animateOneStep()
	{
		if (++animStepCounter == ANIMSTEPS)
		{
			animStepCounter = 0;
			if (imageNum%2 == 0)
				imageNum++;
			else
				imageNum--;
			if (flashing)
			{
				flashCounter++;
				if (flashCounter%2 == 0)
					if (flash)
						flash = false;
					else 
						flash = true;
			}
		}
	}


	public void chase()
	{
		mode = CHASE;
		System.out.println("Ghost: "+ghostNum+": Chasing");
	}

	public void leaveBox()
	{
		mode = LEAVEBOX;
		System.out.println("Ghost: "+ghostNum+": Leaving Box");
	}

	public void scatter()
	{
		if (mode != RUNAWAY && mode != STAYTHECOURSE && mode != LEAVEBOX && mode != RETURN)
		{
			mode = SCATTER;
			reverseOK = true;
			System.out.println("Ghost: "+ghostNum+": Scattering");
		}
	}

	public void stayTheCourse()
	{
		mode = STAYTHECOURSE;
		System.out.println("Ghost: "+ghostNum+": Staying the course");
	}
	public boolean isStayingTheCourse()
	{
		return mode == STAYTHECOURSE;
	}
	public void stopRunning()
	{
		stopFlashing();
		chase();
	}
	public void stopFlashing()
	{
		flashing = false;
		flash = false;
	}
	public boolean isFlashing()
	{
		return flashing;
	}
	public void runaway()
	{
		stopFlashing();
		if (mode != STAYTHECOURSE && mode != LEAVEBOX && mode != RETURN)
		{
			mode = RUNAWAY;
			reverseOK = true;
			speed = savedSpeed * runawaySpeedFactor;
			System.out.println("Ghost: "+ghostNum+": Running away "+speed);
		}
	}
	public void returnToBox()
	{
		stopFlashing();
		mode = RETURN;
		speed = savedSpeed * returnSpeedFactor;
		System.out.println("Ghost: "+ghostNum+": Returning "+speed);
	}
	public boolean returning()
	{
		return mode == RETURN;
	}
	public boolean isRunningAway()
	{
		return mode == RUNAWAY;
	}
	public void slow()
	{
		if (mode != RETURN)
		{
			speed = savedSpeed * slowSpeedFactor;
			//System.out.println("Ghost: "+ghostNum+": On slow path "+speed);
		}
	}
	public void startFlashing()
	{
		flashing = true;
		flashCounter = 0;
		System.out.println("Ghost: "+ghostNum+": flashing ");
	}
	public void fast()
	{
		double oldSpeed = speed;
		if (savedSpeed > 0)
		{
			if (mode == RUNAWAY)
				speed = savedSpeed * runawaySpeedFactor;
			else if (mode == RETURN)
				speed = savedSpeed * returnSpeedFactor;
			else
				speed = savedSpeed;
		}
		if (speed != oldSpeed)
			System.out.println("Ghost: "+ghostNum+": On Fast Path "+speed);
	}
	public int pickDirection(boolean up, boolean left, boolean down, boolean right)
	{
		if (up)
			return KeyEvent.VK_UP;
		if (left)
			return KeyEvent.VK_LEFT;
		if (down)
			return KeyEvent.VK_DOWN;
		if (right)
			return KeyEvent.VK_RIGHT;
		System.err.println("Ghost: "+ghostNum+" Unable to pick direction.");
		return 0;
	}
	/**
	 * Decides on next move
	 * @param options an array of possible directions
	 * @param n the number of elements in the array
	 */
	public void makeMove(int[] options, int n, int reverse, PacMan p, Ghost[] ghosts, PathElement pe)
	{
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		boolean goStraight = false;
		int distance;

		switch (mode)
		{
		case STAYTHECOURSE: // don't make choices unless you have to
			// see if we can go straight
			goStraight = false;
			for (int i=0; i<n; i++)
				if (options[i] == getDirection())
					goStraight = true;
			// if can't go straight, then pick randomly a new direction
			if (!goStraight)
				setDirection(options[(int)(Math.random()*n)]);
			// make the move
			moveOnePixel();
			break;
		case SCATTER:
			// check the options
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
			if (reverseOK)
			{
				reverseOK = false;
				switch (reverse)
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
			distance = -1;
			if (scatterY < getY() && up)
			{
				setDirection(KeyEvent.VK_UP);
				distance = getY() - scatterY;
			}
			if (scatterY > getY() && down && (scatterY - getY()) > distance)
			{
				setDirection(KeyEvent.VK_DOWN);
				distance = scatterY - getY();
			}
			if (scatterX < getX() && left && (getX() - scatterX) > distance)
			{
				distance = getX() - scatterX;
				setDirection(KeyEvent.VK_LEFT);
			}
			if (scatterX > getX() && right && (scatterX - getX()) > distance)
			{
				distance = scatterX - getX();
				setDirection(KeyEvent.VK_RIGHT);
			}
			if (distance == -1)
				setDirection(pickDirection(up, left, down, right));
			// make the move
			moveOnePixel();
			// are we there yet?
			if (scatterX == getX() && scatterY == getY())
				chase();
			break;
		case RETURN:
			// see if we can go straight
			goStraight = false;
			for (int i=0; i<n; i++)
				if (options[i] == getDirection())
					goStraight = true;
			// check the options
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
			distance = -1;
			if (returnY <= getY() && up)
			{
				setDirection(KeyEvent.VK_UP);
				distance = getY() - returnY;
			}
			if (returnY > getY() && down && (returnY - getY()) > distance)
			{
				setDirection(KeyEvent.VK_DOWN);
				distance = returnY - getY();
			}
			if (returnX < getX() && left && (getX() - returnX) > distance)
			{
				distance = getX() - returnX;
				setDirection(KeyEvent.VK_LEFT);
			}
			if (returnX >= getX() && right && (returnX - getX()) > distance)
			{
				distance = returnX - getX();
				setDirection(KeyEvent.VK_RIGHT);
			}
			// go straight if you can or random
			if (distance == -1 && !goStraight)
				setDirection(pickDirection(up, left, down, right));
			// make the move
			moveOnePixel();
			// are we there yet?
			if (returnX == getX() && returnY == getY())
				stayTheCourse();
			break;
		case RUNAWAY:
			// enable reverse one time only
			if (reverseOK)
			{
				reverseOK = false;
				switch (reverse)
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
			// pick the direction of the shortest distance and go the opposite way!
			distance = 10000;
			if (getDistance(p.getX(),p.getY()) < eyesight)
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
			if (distance == 10000)
				setDirection(options[(int)(Math.random()*n)]);
			// make the move
			moveOnePixel();
			break;
		case CHASE:
			//check for distance & hotpursuit
			hotPursuit = false;
			distanceToPacMan = (int)getDistance(p.getX(), p.getY());
			if (pe != null)
				if (pe.onPath(x, y) && x == p.getX())
				{
					if (y-p.getY() > 0 && direction == KeyEvent.VK_UP && p.getDirection() == KeyEvent.VK_UP)
						hotPursuit = true;
					else if (y-p.getY() < 0 && direction == KeyEvent.VK_DOWN && p.getDirection() == KeyEvent.VK_DOWN)
						hotPursuit = true;
				}
				else if (pe.onPath(x,y) && y == p.getY())
				{
					if (x-p.getX() > 0 && direction == KeyEvent.VK_LEFT && p.getDirection() == KeyEvent.VK_LEFT)

						hotPursuit = true;
					else if (x-p.getX() < 0 && direction == KeyEvent.VK_RIGHT && p.getDirection() == KeyEvent.VK_RIGHT)
						hotPursuit = true;
				}
			//if (hotPursuit) System.out.println(ghostNum+" in Hot Pursuit");
			setChaseDirection(options,n, p, ghosts, pe);
			moveOnePixel();
			break;
		case LEAVEBOX:
			// wait for two options, then go up
			if (n==2)
			{
				setDirection(KeyEvent.VK_UP);
				mode = Ghost.SCATTER;
				reverseOK = false;
			}
			else
				setDirection(options[0]);
			moveOnePixel();
			break;
		}
	}

	public boolean inHotPursuit()
	{
		return hotPursuit;
	}
	public int getDistanceToPacMan()
	{
		return distanceToPacMan;
	}
	public double getDistance(int x, int y)
	{
		return (Math.sqrt(Math.pow(x-getX(),2)+Math.pow(y-getY(),2)));
	}

	public boolean withinEyesight(int x, int y)
	{
		return (Math.sqrt(Math.pow(x-getX(),2)+Math.pow(y-getY(),2)) < eyesight);
	}

	// override and put the AI here
	public void setChaseDirection(int[] options, int n, PacMan p, Ghost[] g, PathElement pe)
	{
		System.err.println("Ghost: "+ghostNum+" Picking Random Direction");
		setDirection(options[(int)(Math.random()*n)]);
	}
	public void setDirection(int dir)
	{
		switch (dir)
		{
		case KeyEvent.VK_DOWN:
			if(imageNum%2 == 0)
				imageNum = 0;
			else 
				imageNum = 1;
			break;
		case KeyEvent.VK_LEFT:
			if(imageNum%2 == 0)
				imageNum = 2;
			else 
				imageNum = 3;
			break;
		case KeyEvent.VK_RIGHT:
			if(imageNum%2 == 0)
				imageNum = 4;
			else 
				imageNum = 5;
			break;
		case KeyEvent.VK_UP:
			if(imageNum%2 == 0)
				imageNum = 6;
			else 
				imageNum = 7;
			break;
		}
		this.direction = dir;
	}

	public void draw(Graphics g)
	{
		if (mode == RUNAWAY)
		{
			if (flash)
				g.drawImage(scaredSprite[imageNum%2+2], (int)x-sprites[imageNum].getWidth(applet)/2,(int)y-sprites[imageNum].getHeight(applet)/2,applet);
			else
				g.drawImage(scaredSprite[imageNum%2], (int)x-sprites[imageNum].getWidth(applet)/2,(int)y-sprites[imageNum].getHeight(applet)/2,applet);
		}
		else if (mode == RETURN)
			g.drawImage(returnSprite[imageNum/2], (int)x-sprites[imageNum].getWidth(applet)/2,(int)y-sprites[imageNum].getHeight(applet)/2,applet);
		else
			g.drawImage(sprites[imageNum], (int)x-sprites[imageNum].getWidth(applet)/2,(int)y-sprites[imageNum].getHeight(applet)/2,applet);
		if (spotlight)
		{
			if (mode == RUNAWAY)
				g.setColor(Color.blue);
			else if (ghostNum == 1)
				g.setColor(Color.red);
			else if (ghostNum == 2)
				g.setColor(Color.pink);
			else if (ghostNum == 3)
				g.setColor(Color.cyan);
			else if (ghostNum == 4)
				g.setColor(Color.orange);
			if (mode == RUNAWAY)
				g.drawOval(x-runawayEyesight, y-runawayEyesight, runawayEyesight*2, runawayEyesight*2);
			else if (mode == CHASE)
				g.drawOval(x-eyesight, y-eyesight, eyesight*2, eyesight*2);
		}
	}
}
