package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {

  /** The center of the sphere. */
  protected final Vector3 center = new Vector3();
  public void setCenter(Vector3 center) { this.center.set(center); }

  /** The radius of the sphere. */
  protected float radius = 1.0f;
  public void setRadius(float radius) { this.radius = radius; }

  protected final double M_2PI = 2 * Math.PI;

  public Sphere() { }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param ray the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.
    double b = rayIn.direction.dot(rayIn.origin.clone().sub(this.center));
    double four_ac = (rayIn.direction.dot(rayIn.direction) *
        rayIn.origin.clone().sub(this.center).dot(rayIn.origin.clone().sub(this.center)) -
        Math.pow(this.radius, 2));
    double two_a = rayIn.direction.dot(rayIn.direction);
    double root = Math.pow(b, 2) - four_ac;

    if (root < 0) {
      return false;
    }
    double t_minus = (- b - Math.sqrt(root)) / two_a;
    double t_plus = (- b + Math.sqrt(root)) / two_a;
    double t = t_minus;
    if (t_minus < rayIn.start || rayIn.end < t_minus) {
      t = t_plus;
      if (t_plus < rayIn.start || rayIn.end < t_plus) {
        return false;
      }
    }

    Vector3d location = rayIn.origin.clone().add(rayIn.direction.clone().mul(t));
    Vector3d normal = location.clone().sub(this.center);

    double phi = Math.acos(normal.y/this.radius);
    //double theta = Math.atan(normal.x / normal.z);
    double theta = Math.atan2(normal.x, normal.z);
    if (theta < 0.0) {
        theta += Math.PI*2;
    }
    theta += Math.PI;
    if(theta > Math.PI*2) {
    	theta -= Math.PI*2;
    }
    
    double u = theta / (2 * Math.PI);
    double v = 1 - phi / Math.PI;
    Vector2d uv = new Vector2d(u, v);
    
    if(Double.isNaN(phi)) {
    	System.out.println("here");
    }
    
    outRecord.location.set(location);
    outRecord.normal.set(normal.normalize());
    outRecord.texCoords.set(uv);
    outRecord.surface = this;
    outRecord.t = t;

    return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
      return "sphere " + center + " " + radius + " " + shader + " end";
  }

}