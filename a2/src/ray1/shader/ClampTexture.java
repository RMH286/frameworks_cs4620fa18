package ray1.shader;

import ray1.shader.Texture;
import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;

/**
 * A Texture class that treats UV-coordinates outside the [0.0, 1.0] range as if they
 * were at the nearest image boundary.
 * @author eschweic zz335
 *
 */
public class ClampTexture extends Texture {

	public Colorf getTexColor(Vector2 texCoord) {
		if (image == null) {
			System.err.println("Warning: Texture uninitialized!");
			return new Colorf();
		}
				
		// TODO#A2 Fill in this function.
		// 1) Convert the input texture coordinates to integer pixel coordinates. Adding 0.5
		//    before casting a double to an int gives better nearest-pixel rounding.
		// 2) Clamp the resulting coordinates to the image boundary.
		// 3) Create a Color object based on the pixel coordinate (use Color.fromIntRGB
		//    and the image object from the Texture class), convert it to a Colord, and return it.
		// NOTE: By convention, UV coordinates specify the lower-left corner of the image as the
		//    origin, but the ImageBuffer class specifies the upper-left corner as the origin.
		
		//get image height and width
		int h = image.getHeight();
		int w = image.getWidth();
		double u = (texCoord.get(0));
		double v = (texCoord.get(1));
		//clamp it brah
		u = Math.max(Math.min(u, 1), 0);
		v = Math.max(Math.min(v, 1), 0);
		//convert coords
		int x = (int)((u*w) + .5);
		int y = (int)((v*h) + .5);
		// get texture rbg value (1-y) so that convert from bottom left to top left
		Color convert = Color.fromIntRGB(image.getRGB(x , (1-y))); 
		Colorf texColor = new Colorf((float)convert.r(),(float)convert.g(),(float)convert.b());
		
		return texColor;
	}

}
