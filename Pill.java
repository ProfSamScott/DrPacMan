import java.awt.Color;
import java.awt.Graphics;


public class Pill {
	int x,y;
	
	public Pill(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public double getDistance(int x2, int y2) //manhattan distance
	{
		return Math.abs(x2-x)+Math.abs(y2-y);
	}
	public void draw(Graphics g)
	{
		g.setColor(Color.white);
		g.fillOval(x-2,y-2,5,5);
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
}
