package manip;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import egl.math.Color;
import egl.math.Colorf;
import egl.math.Matrix3;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import gl.RenderMesh;
import gl.RenderObject;
import gl.Renderable;
import mesh.MeshData;

/**
 * @author srm
 *
 * An instance of this class is a single axis manipulator, which responds to user
 * input by changing one aspect of an object's transformation.
 */
public abstract class Manipulator implements Renderable {

  // Axis of manipulator, X, Y, or Z
  protected ManipulatorAxis axis;
  // The RenderObject that this manipulator controls
  protected RenderObject reference;
  // The mesh of the manipulator
  protected RenderMesh mesh;
  // Whether the mouse selects this manipulator
  protected boolean isSelected;

  public Manipulator () {
    MeshData data = new MeshData();
    data.loadOBJ(meshPath());
    this.mesh = new RenderMesh(data);
  }

  public Manipulator (RenderObject referenced) {
    this();
    this.reference = referenced;
  }

  @Override
  public RenderMesh getMesh () {
    return mesh;
  }

  @Override
  public Matrix4 getWorldTransform () {
    return getReferencedTransform().clone().mulBefore(getLocalTransform());
  }

  @Override
  public Matrix3 getWorldTransformIT () {
    return getWorldTransform().getAxes().invert().transpose();
  }

  /**
   * Get the world transform of the referenced object
   * @return the world transform of reference
   */
  protected abstract Matrix4 getReferencedTransform();

  /**
   * Get the object transform of the manipulator so they stay in the correct axis
   * @return local object transform of this manipulator
   */
  private Matrix4 getLocalTransform() {
    Matrix4 mManip = new Matrix4();
    switch (axis) {
      case X:
        Matrix4.createRotationY((float)(Math.PI / 2.0), mManip);
        break;
      case Y:
        Matrix4.createRotationX((float)(-Math.PI / 2.0), mManip);
        break;
      case Z:
        mManip.setIdentity();
        break;
    }
    return mManip;
  }

  /**
   * Modify the appropriate transformation matrix of the referenced object based on mouse input
   * and the type of manipulator being used.
   * @param lastMousePos The previous mouse position, normalized to the range [-1,1] for both the X and Y coordinate.
   * @param curMousePos The new mouse position, normalized to the range [-1,1] for both the X and Y coordinate.
   * @param viewProjection (projection matrix * view matrix) from the current camera.
   */
  public abstract void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection);

  /**
   * Set the RenderObject being referenced
   */
  public void setReferencedObject (RenderObject reference) {
    this.reference = reference;
  }

  /**
   * Where to find the mesh of this manipulator
   */
  protected abstract String meshPath ();

  @Override
  public Colorf getColor () {
    Colorf color = new Colorf();
    switch (axis) {
      case X:
        color.set(Color.Red);
        if (isSelected) {
          color.y = 0.8f;
          color.z = 0.8f;
        }
        break;
      case Y:
        color.set(Color.Lime);
        if (isSelected) {
          color.x = 0.8f;
          color.z = 0.8f;
        }
        break;
      case Z:
        color.set(Color.Blue);
        if (isSelected) {
          color.x = 0.8f;
          color.y = 0.8f;
        }
        break;
    }
    return color;
  }

  /**
   * For shader usage. 0 if a constant color should be used
   */
  @Override
  public float getMode() {
    return 0f;
  }

  @Override
  public void render () {
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ZERO);
    mesh.render();
    glDisable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    mesh.render();
  }

  /**
   * Set whether this manipulator is selected
   */
  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  public static float t(Vector2 mousePos, Matrix4 viewProjection, ManipulatorAxis axis, Matrix4 referencedTransform) {
    Vector3 mousePosNear = new Vector3(mousePos.x, mousePos.y, 1);
    Vector3 mousePosFar = new Vector3(mousePos.x, mousePos.y, -1);
    viewProjection.clone().invert().mulPos(mousePosNear);
    viewProjection.clone().invert().mulPos(mousePosFar);
    Vector3 worldRay = mousePosFar.clone().sub(mousePosNear);
    worldRay.normalize();

    Vector3 manipulatorDir = new Vector3(0);
    Vector3 manipulatorOrigin = new Vector3(0);

    Vector3 centerNear = viewProjection.clone().invert().mulPos(new Vector3(0, 0, 1));
    Vector3 centerFar = viewProjection.clone().invert().mulPos(new Vector3(0, 0, -1));
    Vector3 imageNormal = centerFar.clone().sub(centerNear);
    imageNormal.normalize();

    Vector3 planeNormal = new Vector3(0);

    switch(axis) {
      case X:
        manipulatorDir.x = 1;
        break;
      case Y:
        manipulatorDir.y = 1;
        break;
      case Z:
        manipulatorDir.z = 1;
        break;
    }

    referencedTransform.mulDir(manipulatorDir);
    referencedTransform.mulPos(manipulatorOrigin);

    manipulatorDir.normalize();

    planeNormal.set(manipulatorDir.clone().cross(imageNormal).cross(manipulatorDir));

    if (worldRay.dot(planeNormal) == 0) {
      return Float.NaN;
    }

    float t = manipulatorOrigin.clone().sub(mousePosNear).dot(planeNormal) / worldRay.dot(planeNormal);

    worldRay.mul(t).add(mousePosNear);

    worldRay.sub(manipulatorOrigin);

    t = worldRay.dot(manipulatorDir);

    return t;
}

  public static double angle(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection, ManipulatorAxis axis, Matrix4 referencedTransform) {
    Vector3 lastWorldPosNear = new Vector3(lastMousePos.x, lastMousePos.y, 1);
    Vector3 lastWorldPosFar = new Vector3(lastMousePos.x, lastMousePos.y, -1);
    viewProjection.clone().invert().mulPos(lastWorldPosNear);
    viewProjection.clone().invert().mulPos(lastWorldPosFar);
    Vector3 lastWorldRay = lastWorldPosFar.clone().sub(lastWorldPosNear);
    lastWorldRay.normalize();
    Vector3 curWorldPosNear = new Vector3(curMousePos.x, curMousePos.y, 1);
    Vector3 curWorldPosFar = new Vector3(curMousePos.x, curMousePos.y, -1);
    viewProjection.clone().invert().mulPos(curWorldPosNear);
    viewProjection.clone().invert().mulPos(curWorldPosFar);
    Vector3 curWorldRay = curWorldPosFar.clone().sub(curWorldPosNear);
    curWorldRay.normalize();

    Vector3 manipulatorDir = new Vector3(0);
    Vector3 manipulatorOrigin = new Vector3(0);

    Vector3 planeNormal = new Vector3(0);

    switch(axis) {
      case X:
        manipulatorDir.x = 1;
        break;
      case Y:
        manipulatorDir.y = 1;
        break;
      case Z:
        manipulatorDir.z = 1;
        break;
    }

    referencedTransform.mulDir(manipulatorDir);
    referencedTransform.mulPos(manipulatorOrigin);

    manipulatorDir.normalize();

    planeNormal.set(manipulatorDir);

    if ((lastWorldRay.dot(planeNormal) == 0) || curWorldRay.dot(planeNormal) == 0) {
      return Double.NaN;
    }

    float lastT = manipulatorOrigin.clone().sub(lastWorldPosNear).dot(planeNormal) / lastWorldRay.dot(planeNormal);
    float curT = manipulatorOrigin.clone().sub(curWorldPosNear).dot(planeNormal) / curWorldRay.dot(planeNormal);

    lastWorldRay.mul(lastT).add(lastWorldPosNear);
    curWorldRay.mul(curT).add(curWorldPosNear);

    lastWorldRay.sub(manipulatorOrigin);
    curWorldRay.sub(manipulatorOrigin);

    lastWorldRay.normalize();
    curWorldRay.normalize();

    double cosAngle = lastWorldRay.dot(curWorldRay);
    double angle = Math.acos(cosAngle);

    if (lastWorldRay.clone().cross(curWorldRay).dot(planeNormal) < 0) {
      angle = -1 * angle;
    }

    return angle;
}

//  public static float delta(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection, ManipulatorAxis axis, Matrix4 referencedTransform) {
//    Vector3 lastWorldPosNear = new Vector3(lastMousePos.x, lastMousePos.y, 1);
//    Vector3 lastWorldPosFar = new Vector3(lastMousePos.x, lastMousePos.y, -1);
//    viewProjection.clone().invert().mulPos(lastWorldPosNear);
//    viewProjection.clone().invert().mulPos(lastWorldPosFar);
//    Vector3 lastWorldRay = lastWorldPosFar.clone().sub(lastWorldPosNear);
//    lastWorldRay.normalize();
//    Vector3 curWorldPosNear = new Vector3(curMousePos.x, curMousePos.y, 1);
//    Vector3 curWorldPosFar = new Vector3(curMousePos.x, curMousePos.y, -1);
//    viewProjection.clone().invert().mulPos(curWorldPosNear);
//    viewProjection.clone().invert().mulPos(curWorldPosFar);
//    Vector3 curWorldRay = curWorldPosFar.clone().sub(curWorldPosNear);
//    curWorldRay.normalize();
//
//    Vector3 manipulatorDir = new Vector3(0);
//    Vector3 manipulatorOrigin = new Vector3(0);
//
//    Vector3 centerNear = viewProjection.clone().invert().mulPos(new Vector3(0, 0, 1));
//    Vector3 centerFar = viewProjection.clone().invert().mulPos(new Vector3(0, 0, -1));
//    Vector3 imageNormal = centerFar.clone().sub(centerNear);
//    imageNormal.normalize();
//
//    Vector3 planeNormal = new Vector3(0);
//
//    switch(axis) {
//      case X:
//        manipulatorDir.x = 1;
//        break;
//      case Y:
//        manipulatorDir.y = 1;
//        break;
//      case Z:
//        manipulatorDir.z = 1;
//        break;
//    }
//
//    referencedTransform.mulDir(manipulatorDir);
//    referencedTransform.mulPos(manipulatorOrigin);
//
//    manipulatorDir.normalize();
//
//    planeNormal.set(manipulatorDir.clone().cross(imageNormal).cross(manipulatorDir));
//
//    float lastT = manipulatorOrigin.clone().sub(lastWorldPosNear).dot(planeNormal) / lastWorldRay.dot(planeNormal);
//    float curT = manipulatorOrigin.clone().sub(curWorldPosNear).dot(planeNormal) / curWorldRay.dot(planeNormal);
//
//    lastWorldRay.mul(lastT).add(lastWorldPosNear);
//    curWorldRay.mul(curT).add(curWorldPosNear);
//
//    lastWorldRay.sub(manipulatorOrigin);
//    curWorldRay.sub(manipulatorOrigin);
//
//    lastT = lastWorldRay.dot(manipulatorDir);
//    curT = curWorldRay.dot(manipulatorDir);
//
//    return lastT - curT;
//  }

}
