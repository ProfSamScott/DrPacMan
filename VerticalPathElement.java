import java.awt.Color;
import java.awt.Graphics;


public class VerticalPathElement extends PathElement {

	public VerticalPathElement(int location, int start, int end, int flags, int num)
	{
		super (location, start, end, flags, num);
	}

	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.drawLine(location, start, location, end);
		g.drawString("v"+num, location+1, (start+end)/2);
	}
	
	public String toString()
	{
		return "v"+num+":"+location+","+start+","+end;
	}
	public boolean onPath(int x, int y) {
		return (x == location & ((y <= end & y >= start) | (y >= end & y <= start)));
	}
}
