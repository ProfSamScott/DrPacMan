import java.awt.Color;
import java.awt.Graphics;


public class PowerPill extends Pill implements Runnable {

	int visible = 1;

	public PowerPill(int x, int y)
	{
		super(x,y);
		(new Thread(this)).start();
	}
	public void run()
	{
		while (true)
		{
			visible *= -1;
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void draw(Graphics g)
	{
		if(visible == 1)
		{
			g.setColor(Color.white);
			g.fillOval(x-5,y-5,11,11);
		}
	}
}
