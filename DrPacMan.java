import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class DrPacMan extends JPanel implements Runnable, KeyListener, MouseListener {

	Image buffer; // double buffering
	Path currentPath; // the maze
	PacMan player; // the player
	Ghost[] ghosts = new Ghost[4]; // the ghosts
	int key = 0; // the current key held down

	ScoreBoard scoreBoard; // must create a new one for each new game

	// Move these timers to the scoreboard, maybe?
	int levelTimeCounter = 1; // overall timer for the current level
	int killPause = -1;		// pause when a ghost is killed 
	int killedGhost;		// ?
	int runawayTimer = -1; // when ghosts are running away
	int readyCounter = 0;
	int diePause = -1;    // pause when pacman dies

	boolean firstDraw = true; // set to false once images are preloaded. ELIMINATE THIS VAR USE state INSTEAD
	boolean threadAlive = true;

	static int WIDTH = 600;
	static int HEIGHT = 772;
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Dr. Pac Man");
		DrPacMan d = new DrPacMan();
		d.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		f.setContentPane(d);
		f.pack();
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		d.init();
	d.requestFocus();
		d.requestFocusInWindow();
		f.setVisible(true);
	}
	/**
	 * Init method.
	 */
	public void init() {	
		
		// Starts a new game - should be in a new method
		currentPath = new Path(1, this);
		player = new PacMan(currentPath.getStartLocation(), 0.21);
		ghosts = currentPath.makeGhosts();
		scoreBoard = new ScoreBoard(1, this);
		readyCounter = 1500;
		// double buffering
		buffer =new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		// Thread & Listeners
		addKeyListener(this);
		addMouseListener(this);
		startThread();
	}

	public void startThread()
	{
		threadAlive = true;
		Thread t = new Thread(this);
		t.start();
	}
	public void stopThread()
	{
		threadAlive = false;
	}
	public void stop()
	{
		stopThread();
	}
	/**
	 * Paint Method.
	 * @param g Graphics Context
	 */
	public void paintComponent(Graphics g) {
		Graphics screengc = g; // double buffering
		g = buffer.getGraphics(); // double buffering

		// preloading of images
		if (firstDraw) 
		{
			boolean done = true;
			done = done && currentPath.draw(g);
			g.setColor(Color.black);
			g.fillRect(0,0,getWidth(),getHeight());
			g.setColor(Color.white);
			g.drawString("Loading Images...",0,30);
			for (int gn=0; gn<ghosts.length; gn++)
			{
				for (int sn=0; sn<ghosts[gn].sprites.length; sn++)
				{
					//System.out.println(gn+"_"+sn);
					done = done && g.drawImage(ghosts[gn].sprites[sn], (gn+1)*40, (sn+1)*40, this);
				}
				for (int sn=0; sn<ghosts[gn].scaredSprite.length; sn++)
					done = done && g.drawImage(ghosts[gn].scaredSprite[sn], (gn+1)*40, (sn+1)*40 + ghosts[gn].sprites.length*40, this);
				for (int sn=0; sn<ghosts[gn].returnSprite.length; sn++)
					done = done && g.drawImage(ghosts[gn].returnSprite[sn], (gn+1)*40, (sn+1)*40 + (ghosts[gn].sprites.length+ghosts[gn].scaredSprite.length)*40, this);
			}

			firstDraw = !done;
			if (firstDraw == false)
			{
				// loading is done. Start a new game.
				scoreBoard.ready();
				levelTimeCounter = 1;
			}
		}
		// draw the screen
		else 
		{
			// background
			g.setColor(Color.black);
			g.fillRect(0,0,getWidth(),getHeight());
			// game elements
			currentPath.draw(g);
			player.draw(g);
			for (int i=0; i<ghosts.length; i++)
				ghosts[i].draw(g);
			scoreBoard.draw(g);
		}

		screengc.drawImage(buffer, 0, 0, null); // double buffering
	}

	/**
	 * Required for double buffering
	 * @param g Graphics
	 */
	public void update(Graphics g) { 
		paint(g); 
	} 

	public int[] wheresPacMan()
	{
		int[] loc = new int[2];
		loc[0] = player.getX();
		loc[1] = player.getY();
		return loc;
	}
	private long lastNanoTime ;
	private final long TIMEDELAY = 1500000; // (in ns)
	/**
	 * ELEMENT 3: You must have a run method. This method is called once when
	 * the thread is started. It handles the animation in an infinite loop.
	 */
	public void run()
	{
		lastNanoTime = System.nanoTime();
		// MAIN GAME LOOP
		while (threadAlive)
		{

			// PACING
			long currentNanoTime = System.nanoTime();
			long timeSinceLastStep = (currentNanoTime - lastNanoTime);
			if (timeSinceLastStep < TIMEDELAY) {
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue; // skip the rest if it's not time to act		
			}
			long numSteps = timeSinceLastStep/TIMEDELAY; // if too much time has passed, do more steps
			lastNanoTime = currentNanoTime + timeSinceLastStep % TIMEDELAY; // keep the residual time

			for (int step=0; step<numSteps; step++)
			{

				if (!firstDraw) // Only play the game if the images are not preloading.
				{
					// TIMER - GHOST DEATH (game pause)
					if (killPause > 0) // waiting for ghost to die
					{
						killPause--;
					}
					else if (killPause == 0) // ghost dies;
					{
						killPause--;
						ghosts[killedGhost].returnToBox();
						scoreBoard.ateGhost(player.getX(), player.getY());
					} 
					// TIMER - PACMAN DEATH (game pause)
					else if (diePause > 0) // waiting for ghost to die
					{
						diePause--;
					}
					else if (diePause == 0) // ghost dies;
					{
						diePause--;
						scoreBoard.death();
						if (!scoreBoard.isGameOver())
						{
							player = new PacMan(currentPath.getStartLocation(), 0.21);
							ghosts = currentPath.makeGhosts();
							levelTimeCounter = 1;
						}
						if (scoreBoard.isStateReady())
							readyCounter = 1500;
					} 

					else // The game is on
					{
						// ANIMATE THE OBJECTS
						player.animateOneStep();
						for(int g=0; g<ghosts.length; g++)
							ghosts[g].animateOneStep();
						scoreBoard.animateOneStep();

						// TIMER - GHOSTS RUNNING AWAY
						if(runawayTimer > 0)
							runawayTimer--;
						if (runawayTimer == 0)
						{
							runawayTimer--;
							for (int i=0; i<ghosts.length; i++)
								if(ghosts[i].isRunningAway())
									ghosts[i].stopRunning();
						}
						// GHOSTS START FLASHING
						else if (runawayTimer < 1000)
						{
							for (int i=0; i<ghosts.length; i++)
								if(ghosts[i].isRunningAway() & !ghosts[i].isFlashing())
									ghosts[i].startFlashing();
						}
						// TIMER - GAME START
						if (scoreBoard.isStateReady())
						{
							readyCounter--;
							if (readyCounter == 0)
								scoreBoard.continueGame();
						}
						// TIMER - MAIN GAME COUNTER
						else if (scoreBoard.isGameOn())
							levelTimeCounter++;

						if (scoreBoard.isGameOn())
						{
							// MOVE STUFF & CHECK COLLISIONS
							movePacMan(); // also checks pill collisions
							moveGhosts();
							ghostCollisions();

							// END OF LEVEL DETECTION
							if (currentPath.numPillsLeft() == 0)
							{
								currentPath = new Path(1, this);
								player = new PacMan(currentPath.getStartLocation(), 0.21);
								ghosts = currentPath.makeGhosts();
								levelTimeCounter = 1;
								scoreBoard.ready();
								readyCounter = 1500;
								//System.out.println("You Win!!!");
							}
						} 
					}
				}
			} // step loop (for processing more than one step)
			repaint();
		} // main game loop
	} // run method

	/**
	 * Move the pac man one step if it's time and check pill collisions
	 */
	public void movePacMan()
	{
		if (player.timeToMove())
		{
			int pillCollide = currentPath.pillCollision(player.getX(),player.getY());
			if (pillCollide == Path.NOPILL)
			{
				// if no pill collision, then pick a direction based on user input

				boolean noMove = true; // stays true until valid move found

				switch(key) // process the key currently held down
				{
				case KeyEvent.VK_UP:
					if (currentPath.onPath(player.getX(),player.getY()-1)) // only if its legal
						noMove = false;
					break;
				case KeyEvent.VK_DOWN:
					if (currentPath.onPath(player.getX(),player.getY()+1)) // only if its legal
						noMove = false;
					break;
				case KeyEvent.VK_LEFT:
					if (currentPath.onPath(player.getX()-1,player.getY()))
						noMove = false;
					break;
				case KeyEvent.VK_RIGHT:
					if (currentPath.onPath(player.getX()+1,player.getY()))
						noMove = false;
					break;
				}

				if (!noMove) // set the direction and make the move if valid direction picked
				{
					player.setDirection(key);
					player.moveOnePixel();
				}

				else // otherwise, just keep going in the current direction (if possible)
				{
					switch(player.getDirection())
					{
					case KeyEvent.VK_UP:
						if (currentPath.onPath(player.getX(),player.getY()-1))
							noMove = false;
						break;
					case KeyEvent.VK_DOWN:
						if (currentPath.onPath(player.getX(),player.getY()+1))
							noMove = false;
						break;
					case KeyEvent.VK_LEFT:
						if (currentPath.onPath(player.getX()-1,player.getY()))
							noMove = false;
						break;
					case KeyEvent.VK_RIGHT:
						if (currentPath.onPath(player.getX()+1,player.getY()))
							noMove = false;
						break;
					}
					// move if you can
					if (!noMove)
						player.moveOnePixel();
				}
				// animate/don't animate, plus screen wrap
				if (noMove)
					player.stopAnimating();
				else
				{
					player.startAnimating();
					// screenwrap
					if (player.getX() == Path.RIGHT_EDGE)
						player.setX(-20);
					else if (player.getX() == Path.LEFT_EDGE)
						player.setX(Path.RIGHT_EDGE);
				}
			}
			else // PILL EATEN: move player back one pixel
			{
				player.moveBackOnePixel();
				if (player.getX() > Path.RIGHT_EDGE) 
					player.setX(Path.LEFT_EDGE);
				else if (player.getX() < Path.LEFT_EDGE)
					player.setX(Path.RIGHT_EDGE);
				// powerpill eaten
				if (pillCollide == Path.POWERPILL)
				{
					scoreBoard.atePowerPill();
					for (int i=0; i<ghosts.length; i++)
						ghosts[i].runaway();
					runawayTimer = currentPath.getRunawayTime();
				}
				else
					scoreBoard.atePill();
			}
		}
	}

	/**
	 * Move the ghosts one step if it's time
	 */
	public void moveGhosts()
	{
		// LEAVING THE BOX
		if (levelTimeCounter > 10000)
		{ 
			if (levelTimeCounter % 1000 == 0) // periodicaly allow ghosts to leave the box
				for (int i=0; i<ghosts.length; i++)
					if (ghosts[i].isStayingTheCourse())
					{
						ghosts[i].leaveBox();
						break;
					}
		}
		else
		{ // start of level leaving the box
			switch (levelTimeCounter)
			{
			case 1000: // start moving after 1 second
				ghosts[0].scatter();
				ghosts[1].stayTheCourse();
				ghosts[2].stayTheCourse();
				ghosts[3].stayTheCourse();
				break;
			case 5000: // leave the box after 4 more seconds
				ghosts[1].leaveBox();
				ghosts[2].leaveBox();
				ghosts[3].leaveBox();
				break;
			}
		}
		// scatter every now and then (doesn't affect ghosts in the box, running away, returning, etc)
		if (levelTimeCounter % 15000 == 0)
			for (int i=0; i<ghosts.length; i++)
				ghosts[i].scatter();

		// MOVING THE GHOSTS
		for (int i=0; i<ghosts.length; i++)
		{
			if (ghosts[i].timeToMove())
			{
				// find all the options for the ghost (note that reversing is a special option)
				int[] options = new int[3];
				int numOptions = 0;
				int reverse = 0;
				if (currentPath.onGhostPath(ghosts[i].getX(),ghosts[i].getY()-1, -1, ghosts[i].returning()))
					if (ghosts[i].getDirection() != KeyEvent.VK_DOWN)
						options[numOptions++] = KeyEvent.VK_UP;
					else
						reverse = KeyEvent.VK_UP;
				if (currentPath.onGhostPath(ghosts[i].getX(),ghosts[i].getY()+1, +1, ghosts[i].returning()))
					if (ghosts[i].getDirection() != KeyEvent.VK_UP)
						options[numOptions++] = KeyEvent.VK_DOWN;
					else
						reverse = KeyEvent.VK_DOWN;
				if (currentPath.onGhostPath(ghosts[i].getX()+1,ghosts[i].getY(), +1, ghosts[i].returning()))
					if (ghosts[i].getDirection() != KeyEvent.VK_LEFT)
						options[numOptions++] = KeyEvent.VK_RIGHT;
					else
						reverse = KeyEvent.VK_RIGHT;
				if (currentPath.onGhostPath(ghosts[i].getX()-1,ghosts[i].getY(), -1, ghosts[i].returning()))
					if (ghosts[i].getDirection() != KeyEvent.VK_RIGHT )
						options[numOptions++] = KeyEvent.VK_LEFT;
					else
						reverse = KeyEvent.VK_LEFT;

				// get current pathElement of the pacman
				PathElement pacmanPE = null;
				if (player.getDirection() == KeyEvent.VK_LEFT || player.getDirection() == KeyEvent.VK_RIGHT)
					pacmanPE = currentPath.getHorizontalPath(player.getX(), player.getY());
				else
					pacmanPE = currentPath.getVerticalPath(player.getX(), player.getY());

				// make the move
				ghosts[i].makeMove(options, numOptions, reverse, player, ghosts, pacmanPE);

				// wrap if necessary
				if (ghosts[i].getX() == Path.RIGHT_EDGE) 
					ghosts[i].setX(-20);
				else if (ghosts[i].getX() == Path.LEFT_EDGE)
					ghosts[i].setX(Path.RIGHT_EDGE);

				// slow/fast path processing
				if (currentPath.onSlowPath(ghosts[i].getX(),ghosts[i].getY()))
					ghosts[i].slow();
				else
					ghosts[i].fast();
			} // end if time to move
		} // end ghost for loop
	}

	/**
	 * Check ghost collisions
	 */
	public void ghostCollisions()
	{
		for (int i=0; i<ghosts.length; i++)
			if (ghosts[i].getDistance(player.getX(), player.getY()) < player.radius)
			{
				if (ghosts[i].isRunningAway()) // kill ghost
				{
					killPause = 100; 
					killedGhost = i;
				}
				else if (!ghosts[i].returning()) // ignore if ghost is returning to box
					diePause = 1000;
			}
	}

	/**
	 * Set the current key
	 * @param e the key event
	 */
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		char key2 = e.getKeyChar();
		switch (key2)
		{
		case 'w':
			key = KeyEvent.VK_UP;
			break;
		case 'a':
			key = KeyEvent.VK_LEFT;
			break;
		case 's':
			key = KeyEvent.VK_DOWN;
			break;
		case 'd':
			key = KeyEvent.VK_RIGHT;
			break;
		}
	}

	/**
	 * unset current key if it's released
	 * @param e the key released
	 */
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == key)
			key = 0;
	}

	// GARBAGE DUMP...
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		// showStatus("("+e.getX()+","+e.getY()+")");
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

