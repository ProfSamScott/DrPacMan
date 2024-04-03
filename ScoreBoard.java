import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.StringTokenizer;
import javax.swing.JApplet;
//import netscape.javascript.JSObject;
//import netscape.javascript.JSException;

public class ScoreBoard {

	public static final int CONTINUEGAME = 0;
	public static final int GAMEOVER = 1;
	public static final int PLAYEROVER = 2;
	public static final int READY = 3;
	public static final int LOADING = 4;

	private int[] score = new int[2];
	private int highScore = 0, ghostScore = 200;
	private final int ANIMSTEPS = 10;
	private int animStepCounter = 0;
	private final int DISPLAYSIZE = 4;
	private int[] displayCounter = new int[DISPLAYSIZE];
	private int[][] displayLoc = new int[DISPLAYSIZE][2];
	private int[] displayScore = new int[DISPLAYSIZE];
	private int nextIndex = 0;
	private int[] lives = new int [2];
	private int currentPlayer;
	private int numPlayers;
	private int state;
	private DrPacMan applet;

	public ScoreBoard (int numPlayers, DrPacMan a)
	{
		score[0]=score[1]=0;
		lives[0]=lives[1]=2;
		currentPlayer=1;
		this.numPlayers = numPlayers;
		loading();
		this.applet = a;
		loadHighScore();
	}

	public void loadHighScore()
	{
		highScore = 0;
//		String cookietext = "";
//		try {
//			String linesep =System.getProperty ("line.separator");
//			String cookie =(String)JSObject.getWindow (applet).eval ("document.cookie");
//			StringTokenizer st = new StringTokenizer(cookie, ";", false);
//			// peel apart the cookies
//			while (st.hasMoreTokens()) {
//				String nextToken = st.nextToken().trim();
//				cookietext = cookietext + nextToken + linesep;
//				if (nextToken.startsWith("pacmanhighscore"))
//					highScore = Integer.parseInt(nextToken.substring(16,nextToken.length()));
//			}
//		}
//		catch (JSException ex) {
//			cookietext = cookietext + "This browser may not support Java to Javascript communication";
//		}
//		catch (Exception ex)
//		{
//			cookietext = ex.toString();
//		}
//		System.err.println(cookietext);
//		//applet.showStatus(cookietext);
	}

	public void death()
	{
		lives[currentPlayer-1]--;

		// is game over?
		boolean gameOver = true;
		for (int i=0; i<numPlayers; i++)
			if (lives[i] > -1)
			{
				System.out.println(i+" = "+lives[i]);
				gameOver = false;
			}

		// is game over?
		if (gameOver)
			gameOver();

		// is player done?
		else if (lives[currentPlayer-1] < 0)
			playerOver();

		// get ready to go again
		else 
			ready();

		// switch player
		if (numPlayers == 2)
			if (currentPlayer == 1)
				currentPlayer = 2;
			else 
				currentPlayer = 1;


	}
	public void atePill()
	{
		score[currentPlayer-1] += 10;
	}
	public void ateGhost(int x, int y)
	{
		displayCounter[nextIndex] = 40;
		displayScore[nextIndex] = ghostScore;
		displayLoc[nextIndex][0]=x-10;
		displayLoc[nextIndex][1]=y-14;
		if (++nextIndex == DISPLAYSIZE)
			nextIndex = 0;
		score[currentPlayer-1] += ghostScore;
		ghostScore *= 2;

	}
	public void atePowerPill()
	{
		score[currentPlayer-1] += 50;
		ghostScore = 200;
	}
	public String paddedScore(int player)
	{
		String pad = "";
		for (int i=10000000; i>1; i/=10)
			if (score[player-1] <i)
				pad += " ";
		return pad + score[player-1];
	}
	public String paddedHighScore()
	{
		int pad = 0;
		for (long i=10000000000L; i>1; i/=10)
			if (highScore <i)
				pad += 1;
		pad = pad/2;
		String spaces = "";
		for (int i = 0; i<pad; i++)
			spaces += " ";
		return spaces + highScore;
	}
	public void animateOneStep()
	{
		if (++animStepCounter == ANIMSTEPS)
		{
			animStepCounter = 0;
			for (int i=0; i<DISPLAYSIZE; i++)
				if (displayCounter[i] > 0)
				{
					displayCounter[i]--;
					displayLoc[i][1]--;
				}
		}
	}
	public void draw (Graphics g)
	{
		g.setColor(Color.white);
		g.setFont(new Font("MonoSpaced",Font.BOLD,20));
		g.drawString("PLAYER 1",10,20);
		g.drawString(paddedScore(1), 10, 40);
		g.drawString("HIGH SCORE",240,20);
		g.drawString(paddedHighScore(), 240, 40);
		g.drawString("PLAYER 2", 500,20);
		g.drawString(paddedScore(2), 500, 40);
		for (int i=0; i<DISPLAYSIZE; i++)
		{
			if (displayCounter[i] > 0)
			{
				g.setFont(new Font("MonoSpaced",Font.BOLD,16));
				g.drawString(displayScore[i]+"",displayLoc[i][0],displayLoc[i][1]);
			}
		}
		g.setColor(Color.yellow);
		int radius = 10;
		for (int i=0; i<lives[currentPlayer-1]; i++)
		{
			g.fillArc(10+i*radius*2, 710, radius*2, radius*2, 45, 270);
		}
		if (isGameOver())
		{
			g.setColor(Color.white);
			g.setFont(new Font("MonoSpaced",Font.BOLD,40));
			g.drawString("GAME OVER",193,433);
		} else if (isStateReady())
		{
			g.setColor(Color.white);
			g.setFont(new Font("MonoSpaced",Font.BOLD,40));
			g.drawString("READY!",235,433);
		}
	}
	public void setHighScore(int newScore)
	{
		if (newScore > highScore)
		{
			highScore = newScore;
//			try {
//					JSObject.getWindow (applet).eval ("document.cookie ='pacmanhighscore " + highScore + ";expires=31-Dec-2599 23:59:59 GMT ';");
//			}
//			catch (Exception ex) {
//				System.err.println(ex.getMessage());
//			}
		}
	}
	public void gameOver()
	{
		state = GAMEOVER;
		setHighScore(score[currentPlayer-1]);
	}
	public void playerOver()
	{
		state = PLAYEROVER;
		setHighScore(score[currentPlayer-1]);
	}
	public void ready()
	{
		state = READY;
	}
	public void loading()
	{
		state = LOADING;
	}
	public void continueGame()
	{
		state = CONTINUEGAME;
	}
	public boolean isGameOver()
	{
		return state == GAMEOVER;
	}
	public boolean isPlayerOver()
	{
		return state == PLAYEROVER;
	}
	public boolean isStateReady()
	{
		return state == READY;
	}
	public boolean isLoading()
	{
		return state == LOADING;
	}
	public boolean isGameOn()
	{
		return state == CONTINUEGAME;
	}
}
