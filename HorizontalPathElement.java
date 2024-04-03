import java.awt.Color;
import java.awt.Graphics;


public class HorizontalPathElement extends PathElement {

	public HorizontalPathElement(int location, int start, int end, int flags, int num)
	{
		super (location, start, end, flags, num);
	}

	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.drawLine(start, location, end, location);
		g.drawString("h"+num, (start+end)/2, location-1);
	}
	public String toString()
	{
		return "h"+num+":"+location+","+start+","+end;
	}
	public boolean onPath(int x, int y) {
		return (y == location & ((x <= end & x >= start) | (x >= end & x <= start)));
	}

}
