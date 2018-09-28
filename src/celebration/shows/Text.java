package celebration.shows;

import java.awt.Color;

import com.TheJogMan.Engine.gfx.Canvas;
import com.TheJogMan.Engine.gfx.Image;

public class Text extends ImageShow
{
	static double fontScale = 1;
	
	String[] messages = {"Hello World!", "Woo!"};
	
	@Override
	public Image getImage()
	{
		String message = messages[random.nextInt(messages.length)];
		int width = (int)(getMain().canvas.getFont().getStringWidth(message) * fontScale);
		int height = (int)(getMain().canvas.getFont().getCharacterHeight() * fontScale);
		Canvas canvas = new Canvas(width, height);
		canvas.setFontScale(fontScale);
		canvas.drawText(message, 0, 0, Color.WHITE);
		return new Image(canvas);
	}
}