package ray1.shader;

import ray1.shader.Texture;
import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;

/**
 * A Texture class that repeats the texture image as necessary for UV-coordinates
 * outside the [0.0, 1.0] range.
 * 
 * @author eschweic zz335
 *
 */
public class RepeatTexture extends Texture {

	public Colorf getTexColor(Vector2 texCoord) {
		if (image == null) {
			System.err.println("Warning: Texture uninitialized!");
			return new Colorf();
		}
				
		// TODO#A2 Fill in this function.
		// 1) Convert the input texture coordinates to integer pixel coordinates. Adding 0.5
		//    before casting a double to an int gives better nearest-pixel rounding.
		// 2) If these coordinates are outside the image boundaries, modify them to read from
		//    the correct pixel on the image to give a repeating-tile effect.
		// 3) Create a Color object based on the pixel coordinate (use Color.fromIntRGB
		//    and the image object from the Texture class), convert it to a Colorf, and return it.
		// NOTE: By convention, UV coordinates specify the lower-left corner of the image as the
		//    origin, but the ImageBuffer class specifies the upper-left corner as the origin.
		
		int h = image.getHeight();
		int w = image.getWidth();
		double u = (texCoord.get(0));
		double v = (texCoord.get(1));
		//System.out.println("u = " + u + "v = " + v);
		//repeat it brah
		
		if(u < -1) {
			int ui = (int)u;
			u = (u-ui)*-1;
		}
		else if(u<0) {
			u = u *-1;
		}
		else if(u>1) {
			int ui = (int)u;
			u = (u-ui);
		}
		
		if(v < -1) {
			int vi = (int)v;
			v = (v-vi)*-1;
		}
		else if(v<0) {
			v = v *-1;
		}
		else if(v>1) {
			int vi = (int)v;
			v = (v-vi);
		}
		//System.out.println("u = " + u + "v = " + v);
		//convert coords
		int x = (int)((u*w)+.5);
		int y = (int)((v*h)+.5);
		//System.out.println("h = " + h + "y = " + y);
		//System.out.println("w = " + w + "x = " + x);
		// get texture rbg value (1-y) so that convert from bottom left to top left
		Color convert = Color.fromIntRGB(image.getRGB(x , (h-y))); 
		System.out.println("r = " + convert.r() + "g = " + convert.g() + "b = " + convert.b());
		Colorf texColor = new Colorf((float)convert.r(),(float)convert.g(),(float)convert.b());
		
		return texColor;
	}

}
