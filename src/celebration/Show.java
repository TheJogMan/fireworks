package celebration;

public abstract class Show extends Thread
{
	public abstract int step(Main main, int stepNumber);
	public abstract void init(Main main);
	
	Main main;
	boolean stopShow;
	
	public void initialize(Main main)
	{
		this.main = main;
		stopShow = false;
		init(main);
	}
	
	public Main getMain()
	{
		return main;
	}
	
	public void run()
	{
		int stepNum = 0;
		int delay = step(main, stepNum);
		while (delay >= 0)
		{
			if (delay != 0)
			{
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
					
				}
			}
			if (stopShow)
			{
				return;
			}
			delay = step(main, stepNum);
			stepNum++;
		}
		main.endShow();
	}
}