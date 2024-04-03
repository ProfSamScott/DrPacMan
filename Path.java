import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JApplet;


public class Path {
	public static final int GHOSTS_ONLY = 1;
	public static final int DIR_POSITIVE = 2;
	public static final int DIR_NEGATIVE = 4;
	public static final int CLOSED = 8;
	public static final int NO_PILLS = 16;
	public static final int SLOW_PATH = 32;

	public static final int LEFT_EDGE = -30;
	public static final int RIGHT_EDGE = 630;
	
	LinkedList<PathElement> pathElements = new LinkedList<PathElement>();

	private boolean visible = false;
	private DrPacMan applet;
	private int level;
	private Image mazeImage;
	private int numVPaths = 0, numHPaths = 0;
	private LinkedList<Pill> pills;

	private static int[] yAdjust = 
	{
		-10, // level 1
	};
	private static int[][] verticals = 
	{// format: location, start, end
		// level 1
		// *** bottom box area
		{39, 682, 556, 0, 			//v0
			144, 682, 556, 0,
			207, 682, 618, 0,
			269, 618, 556, 0,
			332, 618, 556, 0,
			394, 682, 618, 0,
			456, 682, 556, 0,
			561, 682, 556, 0,
			// *** long side verticals
			81, 556, 160, 0,		//v8
			518,556, 160, 0,
			// *** top box area
			39, 160, 98, 0,		//v10
			144, 98, 241, 0,
			206, 98, 160, 0,
			394, 98, 160, 0,
			456, 98, 241, 0,
			561, 160, 98, 0,
			269, 241, 160, 0,
			332, 241, 160, 0,
			// *** middle box area
			206, 241, 431, 0,		//v18
			393, 241, 431, 0,
			270, 431, 493, 0,
			333, 431, 493, 0,
			395, 493, 556, 0,
			206, 493, 556, 0,
			456, 362, 431, 0,
			144, 362, 431, 0,
			// ghost box
			251, 352, 386, GHOSTS_ONLY,
			352, 352, 386, GHOSTS_ONLY,
			301, 352, 304, GHOSTS_ONLY+DIR_NEGATIVE,
		} 
	};

	private static int[][] horizontals = 
	{// format: location, start, end
		// level 1
		// *** bottom box area
		{556, 39, 561, 0, 			//h0
			618, 207, 269, 0, 
			618, 394, 332, 0,
			682, 39, 561, 0, 
			// top box area
			98, 39, 144, 0,		//h4
			98, 206, 394, 0,
			98, 561, 456, 0,
			160, 39, 561, 0,
			241, 144, 269, 0,
			241, 456, 332, 0,
			// middle box area
			304, 81, 518, 0,		//h10
			431, 206, 393, 0,
			493, 81, 270, 0,
			493, 518, 333, 0,
			362, 206, 144, 0,
			362, 393, 456, 0,
			431, 144, 81, 0,
			431, 518, 456, 0,
			// exits
			431, -30, 81, NO_PILLS+SLOW_PATH,		// h16
			431, 630, 518, NO_PILLS+SLOW_PATH,
			244, 81, -30, NO_PILLS+SLOW_PATH,
			244, 518, 630, NO_PILLS+SLOW_PATH,
			// ghost box
			352, 251, 352, GHOSTS_ONLY,
			386, 251, 352, GHOSTS_ONLY,
		} 
	};	

	private static int[][] verticalPowerPills =
	{
		{0,5,		// vertical id, then pill number (numbered from top)
			7,5,
			10,2,
			15,2}
	};

	private static int[][] startLocations =
	{
		{300,556+yAdjust[0]} // level 1
	};

	private static int[][][] ghostStartLocations =
	{
		{{301, 304+yAdjust[0]}, {271, 352+yAdjust[0]}, {332, 352+yAdjust[0]}, {301, 386+yAdjust[0]} } // level 1
	};

	private static int[][][] ghostScatterPoints = 
	{
		{{39, 98+yAdjust[0]}, {561, 98+yAdjust[0]}, {39, 682+yAdjust[0]} , {561, 682+yAdjust[0]}}   // level 1
	};

	public static int[][] boxReturnPoint = 
	{
		{301,352+yAdjust[0]}	// level 1
	};
	
	public static int[] runawayTimes =
	{
		7500, // level 1
	};
	public Path(int level, DrPacMan applet)
	{
		if (level < 1 || level > verticals.length)
			System.err.println("Path: Bad level number "+level);
		this.level = level-1;
		this.applet = applet;
		//mazeImage = applet.getImage(applet.getCodeBase(),"images/Maze1.jpg");
		mazeImage = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/Maze1.jpg"));
		loadPathElementList();
		makePills();
	}

	public boolean onPath(int x, int y) // for pacman only!
	{
		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement pe = it.next();
			if (pe.pacManAllowed() && pe.onPath(x,y))
				return true; 
		}
		return false;
	}

	
	public int numPillsLeft()
	{
		return pills.size();
	}
	public PathElement getHorizontalPath(int x, int y) // which horizontal path is pacman on?
	{
		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement pe = it.next();
			if (pe.getClass().getName().equals("HorizontalPathElement") && pe.onPath(x,y))
				return pe; 
		}
		System.err.println("getHorizontalPath: Returning null.");
		return null;
	}

	public PathElement getVerticalPath(int x, int y) // which horizontal path is pacman on?
	{
		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement pe = it.next();
			if (pe.getClass().getName().equals("VerticalPathElement") && pe.onPath(x,y))
				return pe; 
		}
		System.err.println("getVerticalPath: Returning null.");
		return null;
	}

	public boolean onPath(int x, int y, PathElement pe)
	{
		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement newpe = it.next();
			if ((newpe != pe) && newpe.onPath(x,y))
				return true; 
		}
		return false;
	}

	public boolean onSlowPath(int x, int y) // slow paths for ghosts
	{
		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement pe = it.next();
			if (pe.slowPath() && pe.onPath(x,y))
				return true; 
		}
		return false;
	}
	public boolean onGhostPath(int x, int y, int dir, boolean reverseAllowed)
	{
		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement pe = it.next();
			if (pe.ghostAllowed() && (reverseAllowed || pe.pathDirection() == dir || pe.pathDirection() == 0) && pe.onPath(x,y))
				return true; 
		}
		return false;
	}
	public int getLevel()
	{
		return level;
	}

	public int[] getStartLocation()
	{
		return startLocations[level];
	}

	private void loadPathElementList()
	{
		for (int i=0; i<verticals[level].length; i+=4)
			pathElements.add(new VerticalPathElement(verticals[level][i],verticals[level][i+1]+yAdjust[level],verticals[level][i+2]+yAdjust[level],verticals[level][i+3],numVPaths++));
		for (int i=0; i<horizontals[level].length; i+=4)
			pathElements.add(new HorizontalPathElement(horizontals[level][i]+yAdjust[level],horizontals[level][i+1],horizontals[level][i+2],horizontals[level][i+3],numHPaths++));
	}

	public final int pillSpacing = 18;

	public int getRunawayTime()
	{
		return runawayTimes[level];
	}
	private boolean pillNearby(Pill pill)
	{
		for(Iterator<Pill> it = pills.iterator(); it.hasNext(); )
		{
			Pill pill2 = it.next();
			if (pill != pill2)
				if (pill.getDistance(pill2.getX(),pill2.getY()) < 4)
					return true;
		}

		return false;

	}

	public Ghost[] makeGhosts()
	{
		Ghost[] ghosts = new Ghost[4];
		ghosts[0] = new Blinky(ghostStartLocations[level][0], 0.2, 1, ghostScatterPoints[level][0], applet, Ghost.WAIT, boxReturnPoint[level]);
		ghosts[1] = new Pinky(ghostStartLocations[level][1], 0.19, 2, ghostScatterPoints[level][1], applet, Ghost.WAIT, boxReturnPoint[level]);
		ghosts[2] = new Inky(ghostStartLocations[level][2], 0.19, 3, ghostScatterPoints[level][2], applet, Ghost.WAIT, boxReturnPoint[level]);
		ghosts[3] = new Sue(ghostStartLocations[level][3], 0.19, 4, ghostScatterPoints[level][3], applet, Ghost.WAIT, boxReturnPoint[level]);
		return ghosts;
	}
	
/*	public Ghost newGhost(int level, int ghost)
	{
		switch (ghost)
		{
		case 0:
			return new Blinky(ghostStartLocations[level-1][3], 0.2, 1, ghostScatterPoints[level-1][0], applet, Ghost.STAYTHECOURSE, boxReturnPoint[level]);
		case 1:
			return new Pinky(ghostStartLocations[level-1][1], 0.19, 2, ghostScatterPoints[level-1][1], applet, Ghost.STAYTHECOURSE, boxReturnPoint[level]);
		case 2:
			return new Inky(ghostStartLocations[level-1][2], 0.18, 3, ghostScatterPoints[level-1][2], applet, Ghost.STAYTHECOURSE, boxReturnPoint[level]);
		case 3:
			return new Sue(ghostStartLocations[level-1][3], 0.17, 4, ghostScatterPoints[level-1][3], applet, Ghost.STAYTHECOURSE, boxReturnPoint[level]);
		}
		return null;
	}*/
	private void makePills()
	{
		pills = new LinkedList<Pill>();

		for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
		{
			PathElement pe = it.next();
			//System.out.println(pe);
			if (pe.pillsAllowed())
				if (pe.getClass().getName().equals("HorizontalPathElement"))
				{
					int start = pe.getStart();
					//System.out.println(start);
					for (int end = start+1; end<=pe.getEnd(); end++) // look for intersections
					{
						if (onPath(end,pe.getLocation(),pe) || end == pe.getEnd()) // is this an intersection point?
						{
							//System.out.println(end);
							int numPills =(end - start)/pillSpacing;
							double pillInc = (end - start)/(double)numPills;
							for (double x=start; (int)x<=end; x+=pillInc)
							{
								Pill newPill = new Pill((int)x, pe.getLocation());
								if (!pillNearby(newPill))
									pills.add(newPill);
							}
							start = end;
						}
					}
				}
				else
				{
					int start = pe.getStart();
					for (int end = start+1; end<=pe.getEnd(); end++) // look for intersections
					{
						if (onPath(pe.getLocation(),end,pe) || end == pe.getEnd()) // is this an intersection point?
						{
							//System.out.println(end);
							int numPills =(end - start)/pillSpacing;
							double pillInc = (end - start)/(double)numPills;
							for (double y=start; (int)y<=end; y+=pillInc)
							{
								Pill newPill = new Pill(pe.getLocation(),(int)y);
								if (!pillNearby(newPill))
									pills.add(newPill);
							}
							start = end;
						}
					}
				}
		}
		for (int i=0; i<verticalPowerPills[level].length/2; i++)
		{
			LinkedList<Pill> newPills = new LinkedList<Pill>();
			int x=verticals[level][verticalPowerPills[level][i*2]*4];
			int start=verticals[level][verticalPowerPills[level][i*2]*4+1];
			int end=verticals[level][verticalPowerPills[level][i*2]*4+2];
			int num = verticalPowerPills[level][i*2+1];
			int y = 0;
			int counter = 0;
			for(Iterator<Pill> it = pills.iterator(); it.hasNext(); )
			{
				Pill next = it.next();
				if (next.getX() == x && (next.getY() >= start && next.getY() <= end || next.getY() <= start && next.getY() >= end))
				{
					counter++;
					if (counter == num)
					{
						y = next.getY();
					}
					else
						newPills.add(next);
				}
				else
					newPills.add(next);
			}
			pills = newPills;
			pills.add(new PowerPill(x,y));
		}
	}

	public static final int NOPILL = 0;
	public static final int PILL = 1;
	public static final int POWERPILL = 2;
	public int pillCollision(int x, int y)
	{
		int result = NOPILL;
		LinkedList<Pill> newPillList = new LinkedList<Pill>();
		for(Iterator<Pill> it = pills.iterator(); it.hasNext(); )
		{
			Pill next = it.next();
			if (next.getDistance(x,y)<5)
			{
				if (next.getClass().getName().equals("Pill"))
					result = PILL;
				else
				{
					result = POWERPILL;
				}
			}
			else
				newPillList.add(next);
		}
		pills = newPillList;
		return result;
	}

	public boolean draw(Graphics g)
	{
		boolean result = g.drawImage(mazeImage,0,0+yAdjust[level],applet);
		if (visible)
			for(Iterator<PathElement> it = pathElements.iterator(); it.hasNext(); )
			{
				it.next().draw(g);
			}
		for (Iterator<Pill> it= pills.iterator(); it.hasNext();)
			it.next().draw(g);
		return result;
	}
}
