package manip;

import egl.math.*;
import gl.RenderObject;

public class TranslationManipulator extends Manipulator {

  public TranslationManipulator (ManipulatorAxis axis) {
    super();
    this.axis = axis;
  }

  public TranslationManipulator (RenderObject reference, ManipulatorAxis axis) {
    super(reference);
    this.axis = axis;
  }

  @Override
  protected Matrix4 getReferencedTransform () {
    if (this.reference == null) {
      throw new RuntimeException ("Manipulator has no controlled object!");
    }
    return new Matrix4().set(reference.translation);
  }

  @Override
  public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
    // TODO#A3: Modify this.reference.translation given the mouse input.
    // Use this.axis to determine the axis of the transformation.
    // Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
    //   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
    //   corner of the screen, and (1, 1) is the top right corner of the screen.
    // TODO(ryan): calculate proper delta
    // TODO: fix projection of mouse pos into world coordinates
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
    Vector3 curWorldRay = lastWorldPosFar.clone().sub(lastWorldPosNear);
    curWorldRay.normalize();

    Vector3 manipulatorDir = new Vector3(0);
    Vector3 manipulatorOrigin = new Vector3(0);

    Vector3 centerNear = viewProjection.clone().invert().mulPos(new Vector3(0, 0, 1));
    Vector3 centerFar = viewProjection.clone().invert().mulPos(new Vector3(0, 0, -1));
    Vector3 imageNormal = centerFar.clone().sub(centerNear);
    imageNormal.normalize();

    Vector3 planeNormal = new Vector3(0);

    switch(this.axis) {
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

    this.getReferencedTransform().mulDir(manipulatorDir);
    this.getReferencedTransform().mulPos(manipulatorOrigin);

    manipulatorDir.normalize();

    planeNormal.set(manipulatorDir.clone().cross(imageNormal).cross(manipulatorDir));

    float lastT = manipulatorOrigin.clone().sub(lastWorldPosNear).dot(planeNormal) / lastWorldRay.dot(planeNormal);
    float curT = manipulatorOrigin.clone().sub(curWorldPosNear).dot(planeNormal) / curWorldRay.dot(planeNormal);

    lastWorldRay.mul(lastT); //.add(lastWorldPosNear);
    curWorldRay.mul(curT); //.add(curWorldPosNear);

    lastWorldRay.sub(manipulatorOrigin);
    curWorldRay.sub(manipulatorOrigin);

    lastT = lastWorldRay.dot(manipulatorDir);
    curT = curWorldRay.dot(manipulatorDir);

    float delta = 10*(curT - lastT);
    Vector3 v = new Vector3(0);
    switch(this.axis) {
      case X:
        v.x = delta;
        break;
      case Y:
        v.y = delta;
        break;
      case Z:
        v.z = delta;
        break;
    }
    Matrix4 translation = Matrix4.createTranslation(v);
    System.out.println(this.reference.translation.mulBefore(translation));

  }

  @Override
  protected String meshPath () {
    return "data/meshes/Translate.obj";
  }

}
