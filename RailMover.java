import java.awt.event.KeyEvent;

import javax.swing.JApplet;


public class RailMover {

	protected int x;
	protected int y;
	protected int direction = KeyEvent.VK_LEFT;
	protected double speed;
	double timeSinceLastMove = 0;

	public RailMover (int[] start, double speed)
	{
		x = start[0];
		y = start[1];
		this.speed = speed;
	}
	
	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	public boolean timeToMove() {
		timeSinceLastMove += speed;
		if (timeSinceLastMove >= 1)
		{
			timeSinceLastMove -= 1;
			return true;
		}
		return false;
	}

	public void moveOnePixel() {
		switch (direction)
		{
		case KeyEvent.VK_DOWN:
			y += 1;
			break;
		case KeyEvent.VK_LEFT:
			x -= 1;
			break;
		case KeyEvent.VK_RIGHT:
			x += 1;
			break;
		case KeyEvent.VK_UP:
			y -= 1;
			break;
		}
	}
	
	public void moveBackOnePixel() {
		if (direction == KeyEvent.VK_UP)
			y=y+1;
		else if (direction == KeyEvent.VK_DOWN)
			y=y-1;
		else if (direction == KeyEvent.VK_LEFT)
			x=x+1;
		else if (direction == KeyEvent.VK_RIGHT)
			x=x-1;
	}

}
