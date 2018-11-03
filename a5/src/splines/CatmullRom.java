/**
 * @author Jimmy, Andrew 
 */

package splines;
import java.util.ArrayList;

import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector4;

public class CatmullRom extends SplineCurve {

  public CatmullRom(ArrayList<Vector2> controlPoints, boolean isClosed,
      float epsilon) throws IllegalArgumentException {
    super(controlPoints, isClosed, epsilon);
  }

  @Override
  public CubicBezier toBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3,
      float eps) {
    //TODO A5

    Matrix4 mBez = new Matrix4(-1, 3, -3, 1,
                                3, -6, 3, 0,
                                -3, 3, 0, 0,
                                1, 0, 0, 0);
    Matrix4 mCR = new Matrix4(-0.5f, 1.5f, -1.5f, 0.5f,
                               1.0f, -2.5f, 2.0f, -0.5f,
                               -0.5f, 0.0f, 0.5f, 0.0f,
                               0.0f, 1.0f, 0.0f, 0.0f);
    Matrix4 m = mBez.clone().invert().mulBefore(mCR);

    Vector4 v1 = new Vector4(p0.x, p1.x, p2.x, p3.x);
    Vector4 v2 = new Vector4(p0.y, p1.y, p2.y, p3.y);

    Vector4 v3 = m.mul(v1);
    Vector4 v4 = m.mul(v2);

    Vector2 q0 = new Vector2(v3.x, v4.x);
    Vector2 q1 = new Vector2(v3.y, v4.y);
    Vector2 q2 = new Vector2(v3.z, v4.z);
    Vector2 q3 = new Vector2(v3.w, v4.w);
    return new CubicBezier(q0, q1, q2, q3, eps);
  }

}
