package manip;

import egl.math.*;
import gl.RenderObject;

public class RotationManipulator extends Manipulator {

  protected String meshPath = "Rotate.obj";

  public RotationManipulator(ManipulatorAxis axis) {
    super();
    this.axis = axis;
  }

  public RotationManipulator(RenderObject reference, ManipulatorAxis axis) {
    super(reference);
    this.axis = axis;
  }

  //assume X, Y, Z on stack in that order
  @Override
  protected Matrix4 getReferencedTransform() {
    Matrix4 m = new Matrix4();
    switch (this.axis) {
      case X:
        m.set(reference.rotationX).mulAfter(reference.translation);
        break;
      case Y:
        m.set(reference.rotationY)
          .mulAfter(reference.rotationX)
          .mulAfter(reference.translation);
        break;
      case Z:
        m.set(reference.rotationZ)
          .mulAfter(reference.rotationY)
          .mulAfter(reference.rotationX)
          .mulAfter(reference.translation);
        break;
    }
    return m;
  }

  @Override
  public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
    // TODO#A3: Modify this.reference.rotationX, this.reference.rotationY, or this.reference.rotationZ
    //   given the mouse input.
    // Use this.axis to determine the axis of the transformation.
    // Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
    //   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
    //   corner of the screen, and (1, 1) is the top right corner of the screen.

    double angle = angle(lastMousePos, curMousePos, viewProjection, this.axis, this.getReferencedTransform());
    float delta;
    if (angle == Double.NaN) {
      return;
    } else {
      delta = (float) angle;
    }
    Vector3 v = new Vector3(1, 1, 1);

    switch(this.axis) {
      case X:
        v.x = delta;
        Matrix4 rotx = Matrix4.createRotationX(delta);
        this.reference.translation.clone().invert().mulBefore(rotx);
        this.reference.rotationX.mulBefore(rotx);
        break;
      case Y:
        v.y = delta;
        Matrix4 roty = Matrix4.createRotationY(delta);
        Matrix4 med = this.reference.translation.clone().mulBefore(this.reference.rotationX);
        med.invert().mulBefore(roty);
        this.reference.rotationY.mulBefore(roty);
        break;
      case Z:
        v.z = delta;
        Matrix4 rotz = Matrix4.createRotationZ(delta);
        Matrix4 med2 = this.reference.translation.clone()
            .mulBefore(this.reference.rotationX)
            .mulBefore(this.reference.rotationY);
        med2.invert().mulBefore(rotz);
        this.reference.rotationZ.mulBefore(rotz);
        break;
    }

  }

  @Override
  protected String meshPath () {
    return "data/meshes/Rotate.obj";
  }
}
