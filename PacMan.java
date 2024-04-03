import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;


public class PacMan extends RailMover {
	int radius = 14;
	int mouthAngle = 60;
	final int maxMouthAngle = 75;
	final int minMouthAngle = 0;
	boolean animating = false;

	private int animStepCounter = 0;
	private final int ANIMSTEPS = 4;

	public PacMan(int[] start, double speed)
	{
		super(start, speed);
	}
	public void stopAnimating()
	{
		animating = false;
	}

	public void startAnimating()
	{
		animating = true;
	}


	private int mouthDirection = -5;
	public void animateOneStep()
	{
		if (++animStepCounter == ANIMSTEPS)
		{
			animStepCounter = 0;
			if (animating)
			{
				mouthAngle += mouthDirection;
				if (mouthAngle == maxMouthAngle | mouthAngle == minMouthAngle)
					mouthDirection *= -1;
			}
		}
	}

	public void draw(Graphics g)
	{
		switch (direction)
		{
		case KeyEvent.VK_RIGHT:
			g.setColor(Color.yellow);
			g.fillArc(x-radius, y-radius, radius*2, radius*2,0+mouthAngle,360-mouthAngle*2);
			//g.setColor(Color.darkGray);
			//g.fillOval((int)(x-radius/2), (int)(y-radius/((3.0/2))), radius/4, radius/4);
			break;
		case KeyEvent.VK_LEFT:
			g.setColor(Color.yellow);
			g.fillArc(x-radius, y-radius, radius*2, radius*2,180+mouthAngle,360-mouthAngle*2);
			//g.setColor(Color.darkGray);
			//g.fillOval((int)(x+radius/2)-radius/4, (int)(y-radius/((3.0/2))), radius/4, radius/4);
			break;
		case KeyEvent.VK_UP:
			g.setColor(Color.yellow);
			g.fillArc(x-radius, y-radius, radius*2, radius*2,90+mouthAngle,360-mouthAngle*2);
			//g.setColor(Color.darkGray);
			//g.fillOval((int)(x+radius/2)-radius/4, (int)(y+radius/((3.0/2)))-radius/4, radius/4, radius/4);
			break;
		case KeyEvent.VK_DOWN:
			g.setColor(Color.yellow);
			g.fillArc(x-radius, y-radius, radius*2, radius*2,270+mouthAngle,360-mouthAngle*2);
			//g.setColor(Color.darkGray);
			//g.fillOval((int)(x+radius/2)-radius/4, (int)(y-radius/((3.0/2))), radius/4, radius/4);
			break;
		}
		//System.out.println("PacMan Location: "+x+","+y);
	}

	public String toString()
	{
		return super.toString()+" ("+x+","+y+")";
	}
}
