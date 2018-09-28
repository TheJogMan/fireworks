package celebration;

import java.awt.Color;
import java.util.Random;

import com.TheJogMan.Engine.Util.Vector;
import com.TheJogMan.Engine.gfx.Canvas;

public class Particle
{
	static Random random = new Random();
	static Vector down = new Vector(0, -1);
	static double gravityFactor = .01;
	static Vector gravity;
	
	Vector position;
	Vector velocity;
	boolean remove;
	Color color;
	int duration;
	int age;
	int velocityTime;
	
	public Particle(Vector position, Vector vector, Color color, int duration, int velocityTime)
	{
		this.position = position;
		this.velocity = vector;
		this.duration = duration;
		this.velocityTime = velocityTime;
		age = 0;
		remove = false;
		this.color = color;
	}
	
	public Particle(Vector position, Vector vector, boolean randomColor, int duration, int velocityTime)
	{
		this.position = position;
		this.velocity = vector;
		this.duration = duration;
		this.velocityTime = velocityTime;
		age = 0;
		remove = false;
		if (randomColor)
		{
			color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
		}
		else
		{
			color = Color.GRAY;
		}
	}
	
	public static void init()
	{
		gravity = down.clone();
		gravity.multiply(gravityFactor);
	}
	
	public void update(Canvas canvas)
	{
		position.add(velocity);
		age++;
		
		if (age > velocityTime)
		{
			double angle = velocity.getAngle(down);
			if (velocity.getX() > 0)
			{
				angle = -angle;
			}
			angle *= gravityFactor;
			velocity.rotate(angle);
			
			velocity.add(gravity);
		}
		
		Vector nextPosition = position.clone();
		nextPosition.add(velocity);
		if (nextPosition.getX() < 0 || nextPosition.getX() > canvas.getWidth())
		{
			velocity.setX(-velocity.getX());
		}
		
		if (position.getY() < 0 || age >= duration)
		{
			remove = true;
		}
	}
	
	public void draw(Canvas canvas)
	{
		canvas.setPixel((int)position.getX(), (int)((double)canvas.getHeight() - position.getY()), color);
	}
}