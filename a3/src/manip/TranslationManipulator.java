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

    float lastT = t(lastMousePos, viewProjection, this.axis, this.getReferencedTransform());
    float curT = t(curMousePos, viewProjection, this.axis, this.getReferencedTransform());
    if ((lastT == Float.NaN) || (curT == Float.NaN)) {
      return;
    }
    float delta = curT - lastT;

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
    this.reference.translation.mulBefore(translation);

  }

  @Override
  protected String meshPath () {
    return "data/meshes/Translate.obj";
  }

}
