package celebration;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import com.TheJogMan.Engine.Audio.SoundClip;
import com.TheJogMan.Engine.Util.Vector;
import com.TheJogMan.Engine.gfx.Canvas;

public class Rocket
{
	public enum RocketSize
	{
		SMALL, BIG, RANDOM, TINY;
	}
	
	static Random random = new Random();
	static Vector down = new Vector(0, -1);
	static int exhaustCone = 45;
	static int exhaustDuration = 30;
	static int starDuration = 6000;
	static int starCount = 40;
	static int starVelocityTime = 45;
	static int minStarSpeed = 1;
	static int maxStarSpeed = 2;
	static int rocketLaunchCone = 15;
	
	public Vector position;
	public Vector velocity;
	public int duration;
	int age;
	int randomResult;
	boolean remove;
	SoundClip boom;
	Color starColor;
	RocketSize size;
	
	public Rocket(Vector position, Vector velocity, int duration, RocketSize size, boolean subRocket, Color starColor)
	{
		this.position = position;
		this.velocity = velocity;
		this.duration = duration;
		this.starColor = starColor;
		this.size = size;
		age = 0;
		remove = false;
		boom = new SoundClip("/sound/boom.wav");
		SoundClip launch = new SoundClip("/sound/launch.wav");
		if (size.compareTo(RocketSize.BIG) == 0 || (size.compareTo(RocketSize.RANDOM) == 0 && random.nextInt(100) == 5))
		{
			boom.setVolume(-15F);
			launch.setVolume(-15F);
			randomResult = 5;
		}
		else
		{
			boom.setVolume(-30F);
			launch.setVolume(-30F);
			randomResult = 0;
		}
		
		if (!subRocket)
		{
			launch.play();
		}
	}
	
	public Rocket(Vector position, Vector velocity, int duration, RocketSize size, boolean subRocket)
	{
		this.position = position;
		this.velocity = velocity;
		this.duration = duration;
		this.size = size;
		age = 0;
		remove = false;
		boom = new SoundClip("/sound/boom.wav");
		SoundClip launch = new SoundClip("/sound/launch.wav");
		if (size.compareTo(RocketSize.BIG) == 0 || (size.compareTo(RocketSize.RANDOM) == 0 && random.nextInt(100) == 5))
		{
			boom.setVolume(-15F);
			launch.setVolume(-15F);
			randomResult = 5;
		}
		else
		{
			boom.setVolume(-30F);
			launch.setVolume(-30F);
			randomResult = 0;
		}
		
		if (!subRocket)
		{
			launch.play();
		}
	}
	
	public Rocket(Canvas canvas, boolean subRocket, RocketSize size)
	{
		boom = new SoundClip("/sound/boom.wav");
		SoundClip launch = new SoundClip("/sound/launch.wav");
		launch.setVolume(-30F);
		boom.setVolume(-30F);
		remove = false;
		this.size = size;
		int height = random.nextInt(canvas.getHeight() / 4) + (canvas.getHeight() / 4 * 3) - (canvas.getHeight() / 3);
		duration = height / 2;
		age = 0;
		position = new Vector(random.nextInt(canvas.getWidth() / 2) + (canvas.getWidth() / 4), 0);
		
		velocity = new Vector(0, 2);
		velocity.rotate(random.nextInt(rocketLaunchCone * 2) - rocketLaunchCone);
		
		randomResult = 0;
		if (size.compareTo(RocketSize.BIG) == 0 || (size.compareTo(RocketSize.RANDOM) == 0 && random.nextInt(100) == 5))
		{
			setBig();
			launch.setVolume(-15F);
		}
		
		if (!subRocket)
		{
			launch.play();
		}
	}
	
	public void setBig()
	{
		if (randomResult != 5)
		{
			randomResult = 5;
			velocity.divide(2);
			duration *= 2;
			boom.setVolume(-15F);
		}
	}
	
	public void setSmall()
	{
		if (randomResult == 5)
		{
			randomResult = 0;
			velocity.multiply(2);
			duration /= 2;
			boom.setVolume(-30F);
		}
	}
	
	public void update(List<Particle> particles, List<Rocket> rockets, Canvas canvas, Main main)
	{
		Vector particleVector = velocity.clone();
		particleVector.multiply(-.5);
		particleVector.rotate(random.nextInt(exhaustCone * 2) - exhaustCone);
		particles.add(new Particle(position.clone(), particleVector, false, exhaustDuration, 0));
		
		position.add(velocity);
		Vector nextPosition = position.clone();
		nextPosition.add(velocity);
		if (nextPosition.getY() > canvas.getHeight())
		{
			explode(particles, rockets, canvas, main);
			return;
		}
		if (nextPosition.getX() < 0 || nextPosition.getX() > canvas.getWidth())
		{
			velocity.setX(-velocity.getX());
		}
		
		age++;
		if (age >= duration)
		{
			explode(particles, rockets, canvas, main);
		}
	}
	
	public void explode(List<Particle> particles, List<Rocket> rockets, Canvas canvas, Main main)
	{
		boom.play();
		remove = true;
		int amount = starCount;
		if (randomResult == 5)
		{
			amount /= 4;
		}
		Vector direction = down.clone();
		for (int index = 0; index < amount; index++)
		{
			Vector vector = down.clone();
			double speed = random.nextDouble() * (random.nextInt(maxStarSpeed) + minStarSpeed);
			vector.multiply(speed);
			vector.rotate(random.nextInt(360));
			if (randomResult == 5)
			{
				Rocket rocket = new Rocket(canvas, true, RocketSize.RANDOM);
				rocket.position = position.clone();
				rocket.velocity = direction.clone();
				rocket.duration /= 2;
				rockets.add(rocket);
				main.rocketsLaunched++;
			}
			else
			{
				if (size.compareTo(RocketSize.TINY) == 0)
				{
					Vector velocity = direction.clone();
					velocity.multiply(.1);
					if (starColor != null)
					{
						particles.add(new Particle(position.clone(), velocity, starColor, starDuration, starVelocityTime));
					}
					else
					{
						particles.add(new Particle(position.clone(), velocity, true, starDuration, starVelocityTime));
					}
				}
				else
				{
					if (starColor != null)
					{
						particles.add(new Particle(position.clone(), vector, starColor, starDuration, starVelocityTime));
					}
					else
					{
						particles.add(new Particle(position.clone(), vector, true, starDuration, starVelocityTime));
					}
				}
			}
			direction.rotate(360 / amount);
		}
	}
	
	public void draw(Canvas canvas)
	{
		if (randomResult == 5)
		{
			Color color = Color.WHITE;
			if (starColor != null)
			{
				color = starColor;
			}
			canvas.drawRect((int)position.getX() - 1, canvas.getHeight() - (int)position.getY() - 1, 3, 3, color);
		}
		else
		{
			canvas.setPixel((int)position.getX(), canvas.getHeight() - (int)position.getY(), Color.WHITE);
		}
	}
}