package celebration.shows;

import com.TheJogMan.Engine.Util.Vector;

import celebration.Main;
import celebration.Rocket.RocketSize;
import celebration.Show;

public class Line extends Show
{
	static int amount = 10;
	int current;
	
	@Override
	public void init(Main main)
	{
		current = 0;
	}
	
	@Override
	public int step(Main main, int stepNum)
	{
		current++;
		if (current < amount)
		{
			double origin = (double)main.canvas.getWidth() / 4.0;
			double area = (double)main.canvas.getWidth() / 2.0;
			double position = origin + (area / (double)amount * (double)current);
			main.launch(new Vector(position, 1), new Vector(0, 2), 180, RocketSize.SMALL, false);
		}
		else if (current == amount)
		{
			return 3500;
		}
		else if (current > amount)
		{
			return -1;
		}
		return 500;
	}
}