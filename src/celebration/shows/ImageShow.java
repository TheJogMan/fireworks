package celebration.shows;

import java.awt.Color;
import java.util.Random;

import com.TheJogMan.Engine.Util.Vector;
import com.TheJogMan.Engine.gfx.Image;
import com.TheJogMan.Engine.gfx.ImageData;

import celebration.Main;
import celebration.Rocket.RocketSize;
import celebration.Show;

public class ImageShow extends Show
{
	static Random random = new Random();
	static String[] images = {"test.png", "logo.png", "smile.png"};
	
	static int pixelSize = 8;
	static int height;
	
	Image image;
	int y;
	
	@Override
	public int step(Main main, int stepNumber)
	{
		double origin = (double)main.canvas.getWidth() / 2.0 - ((double)(image.getWidth() * pixelSize) / 2.0);
		for (int x = 0; x < image.getWidth(); x++)
		{
			Color color = new Color(image.getPixel(x, y));
			if (!(color.getBlue() == 0 && color.getGreen() == 0 && color.getRed() == 0))
			{
				main.launch(new Vector(origin + x * pixelSize, 0), new Vector(0, 2), height / 2 - (y * pixelSize) / 2, RocketSize.TINY, false, color);
			}
		}
		y++;
		if (y == image.getHeight())
		{
			return -1;
		}
		return pixelSize / 2;
	}
	
	public Image getImage()
	{
		return new Image(new ImageData("/images/" + images[random.nextInt(images.length)]));
	}
	
	@Override
	public void init(Main main)
	{
		image = getImage();
		height = main.canvas.getHeight() / 2 - image.getHeight() / 2;
		y = 0;
	}
}