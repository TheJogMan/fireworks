package celebration;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.TheJogMan.Engine.AbstractGame;
import com.TheJogMan.Engine.GameContainer;
import com.TheJogMan.Engine.Renderer;
import com.TheJogMan.Engine.Util.Vector;
import com.TheJogMan.Engine.gfx.Canvas;

import celebration.Rocket.RocketSize;
import celebration.shows.ImageShow;
import celebration.shows.Line;
import celebration.shows.Text;

public class Main implements AbstractGame
{
	Random random = new Random();
	List<Rocket> rockets;
	List<Rocket> rocketBuffer;
	List<Particle> particles;
	int rocketTime;
	int rocketPeriod = 90;
	int rocketLimit = 20;
	int rocketsLaunched;
	int showTime;
	int showInterval = 1800;
	Show currentShow;
	boolean paused;
	boolean fireRockets;
	boolean inShowSequence;
	boolean useBuffer;
	public Canvas canvas;
	List<Class<? extends Show>> shows;
	
	@Override
	public void init(GameContainer game)
	{
		shows = new ArrayList<Class<? extends Show>>();
		
		shows.add(Line.class);
		shows.add(ImageShow.class);
		shows.add(Text.class);
		
		rocketTime = rocketPeriod / 4;
		rocketsLaunched = 0;
		showTime = showInterval;
		rockets = new ArrayList<Rocket>();
		particles = new ArrayList<Particle>();
		rocketBuffer = new ArrayList<Rocket>();
		useBuffer = false;
		Particle.init();
		paused = false;
		fireRockets = true;
		canvas = game.getRenderer().getBaseCanvas();
	}
	
	@Override
	public void render(GameContainer game, Canvas canvas, Renderer renderer)
	{
		if (!inShowSequence && rocketsLaunched > 1)
		{
			canvas.drawText("Left-Click to launch a rocket from the mouse cursor!", 0, (int)(canvas.getFont().getCharacterHeight() * canvas.getFontScale()));
			if (fireRockets)
			{
				canvas.drawText("Press S to stop fire work launches!", 0, (int)(2 * canvas.getFont().getCharacterHeight() * canvas.getFontScale()));
			}
			else
			{
				canvas.drawText("Press S to resume fire work launches!", 0, (int)(2 * canvas.getFont().getCharacterHeight() * canvas.getFontScale()));
			}
		}
		
		if (paused)
		{
			canvas.drawText("Press space to un-pause", 0, 0);
		}
		else
		{
			canvas.drawText("Press space to pause", 0, 0);
		}
		
		useBuffer = true;
		for (Iterator<Rocket> iterator = rockets.iterator(); iterator.hasNext();)
		{
			iterator.next().draw(canvas);;
		}
		useBuffer = false;
		rockets.addAll(rocketBuffer);
		rocketBuffer.clear();
		for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext();)
		{
			iterator.next().draw(canvas);
		}
	}
	
	public void launch(Vector position, Vector velocity, int duration, RocketSize size, boolean subRocket, Color starColor)
	{
		Rocket rocket = new Rocket(position, velocity, duration, size, subRocket, starColor);
		if (useBuffer)
		{
			rocketBuffer.add(rocket);
		}
		else
		{
			rockets.add(rocket);
		}
		rocketsLaunched++;
	}
	
	public void launch(Vector position, Vector velocity, int duration, RocketSize size, boolean subRocket)
	{
		Rocket rocket = new Rocket(position, velocity, duration, size, subRocket);
		if (useBuffer)
		{
			rocketBuffer.add(rocket);
		}
		else
		{
			rockets.add(rocket);
		}
		rocketsLaunched++;
	}
	
	public void phantomRocket(int x, int y, Canvas canvas)
	{
		Rocket rocket = new Rocket(canvas, false, RocketSize.BIG);
		rocket.position.setX(x);
		rocket.position.setY(y);
		rocket.velocity.rotate(Rocket.random.nextInt(360));
		rockets.add(rocket);
		rocketsLaunched++;
	}
	
	@Override
	public void update(GameContainer game, float deltaTime)
	{
		if (game.getInput().isKeyDown(KeyEvent.VK_SPACE))
		{
			paused = !paused;
		}
		
		if (!paused)
		{
			if (!inShowSequence)
			{
				if (game.getInput().isButtonDown(MouseEvent.BUTTON1) && rocketsLaunched > 1)
				{
					phantomRocket(game.getInput().getMouseX(), game.getRenderer().getBaseCanvas().getHeight() - game.getInput().getMouseY(), game.getRenderer().getBaseCanvas());
				}
				
				if (game.getInput().isKeyDown(KeyEvent.VK_S) && rocketsLaunched > 1)
				{
					fireRockets = !fireRockets;
				}
				
				if (game.getInput().isKeyDown(KeyEvent.VK_P) && rocketsLaunched > 1)
				{
					startShow();
				}
			}
			
			useBuffer = true;
			List<Rocket> newRockets = new ArrayList<Rocket>();
			List<Rocket> currentRockets = new ArrayList<Rocket>();
			currentRockets.addAll(rockets);
			rockets.clear();
			for (Iterator<Rocket> iterator = currentRockets.iterator(); iterator.hasNext();)
			{
				Rocket rocket = iterator.next();
				rocket.update(particles, rockets, game.getRenderer().getBaseCanvas(), this);
				if (!rocket.remove)
				{
					newRockets.add(rocket);
				}
			}
			for (Iterator<Rocket> iterator = rockets.iterator(); iterator.hasNext();)
			{
				newRockets.add(iterator.next());
			}
			rockets = newRockets;
			useBuffer = false;
			rockets.addAll(rocketBuffer);
			rocketBuffer.clear();
			
			List<Particle> newParticles = new ArrayList<Particle>();
			for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext();)
			{
				Particle particle = iterator.next();
				particle.update(game.getRenderer().getBaseCanvas());
				if (!particle.remove)
				{
					newParticles.add(particle);
				}
			}
			particles = newParticles;
			
			if (rocketTime == 0)
			{
				if (rockets.size() < rocketLimit)
				{
					Rocket rocket = new Rocket(game.getRenderer().getBaseCanvas(), false, RocketSize.RANDOM);
					rockets.add(rocket);
					rocketsLaunched++;
					if (rocketsLaunched == 1)
					{
						rocketTime = (int)(rocketPeriod + rocket.duration * 1.5);
					}
					else
					{
						rocketTime = rocketPeriod;
					}
				}
			}
			else if (fireRockets && !inShowSequence)
			{
				rocketTime--;
			}
			
			if (!inShowSequence)
			{
				showTime--;
				if (showTime == 0)
				{
					startShow();
				}
			}
		}
	}
	
	public void startShow()
	{
		int showNumber = random.nextInt(shows.size());
		try
		{
			currentShow = shows.get(showNumber).newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			System.out.println("failed to pick show randomly, defaulting to line");
			currentShow = new Line();
		}
		currentShow.initialize(this);
		inShowSequence = true;
		showTime = showInterval;
		currentShow.start();
	}
	
	public void endShow()
	{
		if (currentShow != null)
		{
			currentShow.stopShow = true;
			currentShow.interrupt();
		}
		inShowSequence = false;
	}
	
	public static void main(String[] args)
	{
		GameContainer game = new GameContainer(new Main(), "Woo!");
		game.start();
	}
}