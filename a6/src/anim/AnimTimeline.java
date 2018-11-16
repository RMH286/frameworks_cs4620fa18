package anim;

import java.util.TreeSet;

import common.SceneObject;
import egl.math.Matrix3;
import egl.math.Matrix4;
import egl.math.Quat;
import egl.math.Vector3;

/**
 * A timeline for a particular object in the scene.  The timeline holds
 * a sequence of keyframes and a reference to the object that they
 * pertain to.  Via linear interpolation between keyframes, the timeline
 * can compute the object's transformation at any point in time.
 * 
 * @author Cristian
 */
public class AnimTimeline {

  /**
   * A sorted set of keyframes.  Invariant: there is at least one keyframe.
   */
  public final TreeSet<AnimKeyframe> frames = new TreeSet<>(AnimKeyframe.COMPARATOR);

  /**
   * The object that this timeline animates
   */
  public final SceneObject object;

  /**
   * Create a new timeline for an object.  The new timeline initially has the object
   * stationary, with the same transformation it currently has at all times.  This is
   * achieve by createing a timeline with a single keyframe at time zero.
   * @param o Object
   */
  public AnimTimeline(SceneObject o) {
    object = o;

    // Create A Default Keyframe
    AnimKeyframe f = new AnimKeyframe(0);
    f.transformation.set(o.transformation);
    frames.add(f);
  }

  /**
   * Add A keyframe to the timeline.
   * @param frame Frame number
   * @param t Transformation
   */
  public void addKeyFrame(int frame, Matrix4 t) {
    // TODO#A6: Add an AnimKeyframe to frames and set its transformation
    AnimKeyframe f = new AnimKeyframe(frame);
    f.transformation.set(t);
    this.frames.add(f);
  }

  /**
   * Remove a keyframe from the timeline.  If the timeline is empty,
   * maintain the invariant by adding a single keyframe with the given
   * transformation.
   * @param frame Frame number
   * @param t Transformation
   */
  public void removeKeyFrame(int frame, Matrix4 t) {
    // TODO#A6: Delete a frame, you might want to use Treeset.remove
    // If there is no frame after deletion, add back this frame.
    AnimKeyframe f = new AnimKeyframe(frame);
    f.transformation.set(t);
    this.frames.remove(f);
    if (this.frames.size() == 0) {
      this.frames.add(f);
    }
  }

  /**
   * Takes a rotation matrix and decomposes into Euler angles. 
   * Returns a Vector3 containing the X, Y, and Z degrees in radians.
   * Formulas from http://nghiaho.com/?page_id=846
   */
  public static Vector3 eulerDecomp(Matrix3 mat) {
    double theta_x = Math.atan2(mat.get(2, 1), mat.get(2, 2));
    double theta_y = Math.atan2(-mat.get(2, 0), Math.sqrt(Math.pow(mat.get(2, 1), 2) + Math.pow(mat.get(2, 2), 2)));
    double theta_z = Math.atan2(mat.get(1, 0), mat.get(0, 0));

    return new Vector3((float)theta_x, (float)theta_y, (float)theta_z);
  }

  /**
   * Update the transformation for the object connected to this timeline to the current frame
   * @curFrame Current frame number
   * @rotation Rotation interpolation mode: 
   * 0 - Euler angles, 
   * 1 - Linear interpolation of quaternions,
   * 2 - Spherical linear interpolation of quaternions.
   */
  public void updateTransformation(int curFrame, int rotation) {
    //TODO#A6: You need to get pair of surrounding frames,
    // calculate interpolation ratio,
    // calculate Translation, Scale and Rotation Interpolation,
    // and combine them.
    // Argument curFrame is current frame number
    // Argument rotation is rotation interpolation mode
    // 0 - Euler angles, 
    // 1 - Linear interpolation of quaternions,
    // 2 - Spherical linear interpolation of quaternions.

    AnimKeyframe f = new AnimKeyframe(curFrame);
    AnimKeyframe prev = this.frames.lower(f);
    AnimKeyframe next = this.frames.higher(f);

    if (this.frames.contains(f)) {
      if (prev == null) {
        this.object.transformation.set(this.frames.first().transformation);
      } else {
        this.object.transformation.set(this.frames.higher(prev).transformation);
      }
      return;
    }

    if (next == null) {
      this.object.transformation.set(prev.transformation);
      return;
    }
    if (prev == null) {
      this.object.transformation.set(next.transformation);
      return;
    }

    float t = ((float)(f.frame - prev.frame)) / ((float)(next.frame - prev.frame));

    Vector3 prevTranslation = prev.transformation.getTrans();
    Vector3 nextTranslation = next.transformation.getTrans();
    Matrix3 prevRS = prev.transformation.getAxes();
    Matrix3 nextRS = next.transformation.getAxes();
    Matrix3 prevRotation = new Matrix3();
    Matrix3 nextRotation = new Matrix3();
    Matrix3 prevScale = new Matrix3();
    Matrix3 nextScale = new Matrix3();
    prevRS.polar_decomp(prevRotation, prevScale);
    nextRS.polar_decomp(nextRotation, nextScale);

    Vector3 interpolatedTranslation = prevTranslation.clone().add(nextTranslation.clone().sub(prevTranslation).mul(t));
    Matrix3 interpolatedScale = new Matrix3().interpolate(prevScale, nextScale, t);
    Matrix3 interpolatedRotation = new Matrix3();

    Quat prevQuat;
    Quat nextQuat;
    Quat interpolatedQuat;

    switch (rotation) {
      case 0:
    	Vector3 firstAng = eulerDecomp(prevRotation);
    	Vector3 nextAng = eulerDecomp(nextRotation);
    	float interpXAng = (t-1)*firstAng.x + (t)*nextAng.x;
    	float interpYAng = (t-1)*firstAng.y + (t)*nextAng.y;
    	float interpZAng = (t-1)*firstAng.z + (t)*nextAng.z;
    	Matrix3 eXR = Matrix3.createRotationX(interpXAng);
    	Matrix3 eYR = Matrix3.createRotationY(interpYAng);
    	Matrix3 eZR = Matrix3.createRotationZ(interpZAng);
    	interpolatedRotation = eZR.mulBefore(eYR.mulBefore(eXR));
        break;
      case 1:
        prevQuat = new Quat(prevRotation);
        nextQuat = new Quat(nextRotation);
        interpolatedQuat = prevQuat.clone().scale(1 - t).add(nextQuat.clone().scale(t));
        interpolatedQuat.toRotationMatrix(interpolatedRotation);
        break;
      case 2:
        prevQuat = new Quat(prevRotation);
        nextQuat = new Quat(nextRotation);
        interpolatedQuat = Quat.slerp(prevQuat, nextQuat, t);
        interpolatedQuat.toRotationMatrix(interpolatedRotation);
        break;
      default:
        break;
    }

    Matrix4 translation = Matrix4.createTranslation(interpolatedTranslation);
    Matrix4 rot = new Matrix4(interpolatedRotation);
    Matrix4 scale = new Matrix4(interpolatedScale);
    Matrix4 transformation = translation.clone().mulBefore(rot).mulBefore(scale);

    object.transformation.set(transformation);
  }

}
