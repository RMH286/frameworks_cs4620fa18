package ray1.shader;

import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.RayTracer;
import ray1.Scene;

public abstract class ReflectionShader extends Shader {

  /** BEDF used by this shader. */
  protected BRDF brdf = null;

  /** Coefficient for mirror reflection. */
  protected final Colorf mirrorCoefficient = new Colorf();
  public void setMirrorCoefficient(Colorf mirrorCoefficient) { this.mirrorCoefficient.set(mirrorCoefficient); }
  public Colorf getMirrorCoefficient() {return new Colorf(mirrorCoefficient);}

  public ReflectionShader() {
    super();
  }

  /**
   * Evaluate the intensity for a given intersection using the Microfacet shading model.
   *
   * @param outRadiance The color returned towards the source of the incoming ray.
   * @param scene The scene in which the surface exists.
   * @param ray The ray which intersected the surface.
   * @param record The intersection record of where the ray intersected the surface.
   * @param depth The recursion depth.
   */
  @Override
  public void shade(Colorf outRadiance, Scene scene, Ray ray, IntersectionRecord record, int depth) {
    // TODO#A2: Fill in this function.
    // 1) Loop through each light in the scene.
    // 2) If the intersection point is shadowed, skip the calculation for the light.
    //    See Shader.java for a useful shadowing function.
    // 3) Compute the incoming direction by subtracting
    //    the intersection point from the light's position.
    // 4) Compute the color of the point using the shading model.
    //    EvalBRDF method of brdf object should be called to evaluate BRDF value at the shaded surface point.
    // 5) Add the computed color value to the output.
    // 6) If mirrorCoefficient is not zero vector, add recursive mirror reflection
    // 6a) Compute the mirror reflection ray direction by reflecting the direction vector of "ray" about surface normal
    // 6b) Construct mirror reflection ray starting from the intersection point (record.location) and pointing along
    //     direction computed in 6a) (Hint: remember to call makeOffsetRay to avoid self-intersecting)
    // 6c) call RayTracer.shadeRay() with the mirror reflection ray and (depth+1)
    // 6d) add returned color value in 6c) to output

    outRadiance.set(0, 0, 0);

    Vector3d origin = new Vector3d();
    Vector3d direction = new Vector3d();
    double t = 0;
    Ray shadowRay = new Ray();

    Colorf brdfValue =  new Colorf();

    Vector2 tex;

    Vector3 scale = new Vector3();

    Vector3d zero = new Vector3d().setZero();

    Vector3d reflection = new Vector3d();

    Ray reflectionRay = new Ray();

    Colorf reflectionColor = new Colorf();

    for (Light l : scene.getLights()) {
      origin.set(record.location);
      direction.set(new Vector3d(l.position).sub(record.location));
      t = direction.len();
      shadowRay.set(origin, direction.normalize());
      shadowRay.makeOffsetSegment(t);
      if (scene.getAnyIntersection(shadowRay)) {
        continue;
      }

      if (record.texCoords != null) {
        tex = new Vector2(record.texCoords);
      } else {
        tex = null;
      }

      brdf.evalBRDF(direction.normalize(), ray.direction.clone().negate().normalize(), record.normal.normalize(), tex, brdfValue);

      scale = l.intensity.clone().mul((float)Math.max(record.normal.normalize().dot(direction.normalize()), 0)).div((float)Math.pow(t,  2));

      outRadiance.add(brdfValue.mul(scale));
    }

    if (!this.getMirrorCoefficient().equals(zero)) {
      reflectionColor.setZero();
      reflection.set(record.normal.clone().mul(record.normal.dot(ray.direction.clone().negate()))).mul(2).sub(ray.direction.clone().negate());
      reflectionRay.set(record.location, reflection.normalize());
      reflectionRay.makeOffsetRay();
      RayTracer.shadeRay(reflectionColor, scene, reflectionRay, depth+1);
      reflectionColor.mul(this.getMirrorCoefficient());
      outRadiance.add(reflectionColor);
    }
  }

}