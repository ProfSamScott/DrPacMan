import java.awt.Graphics;
import java.util.LinkedList;


public abstract class PathElement {
	
	int location, start, end, num, flags;
	
	public PathElement(int location, int start, int end, int flags, int num)
	{
		this.location = location;
		if (start<=end)
		{
			this.start = start;
			this.end = end;
		}
		else
		{
			this.start = end;
			this.end = start;
		}
		System.out.println(this.start+" "+this.end);
		this.num = num;
		this.flags = flags;
	}
	
	public boolean pacManAllowed()
	{
		return (flags & (Path.GHOSTS_ONLY + Path.CLOSED)) == 0;
	}
	public boolean ghostAllowed()
	{
		return (flags & Path.CLOSED) == 0;
	}
	public boolean pillsAllowed()
	{
		return (flags & (Path.GHOSTS_ONLY + Path.NO_PILLS)) == 0;
	}
	public boolean slowPath()
	{
		return (flags & Path.SLOW_PATH) != 0;
	}
	public int pathDirection()
	{
		if ((flags & Path.DIR_NEGATIVE) > 0)
			return -1;
		if ((flags & Path.DIR_POSITIVE) > 0)
			return 1;
		return 0;
	}
	public int getStart()
	{
		return start;
	}
	
	public int getEnd()
	{
		return end;
	}
	
	public int getLocation()
	{
		return location;
	}
	public abstract boolean onPath(int x, int y);
	public abstract void draw(Graphics g);
}
