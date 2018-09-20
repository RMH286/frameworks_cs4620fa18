package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;
import ray1.shader.Shader;
import ray1.OBJFace;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
   /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;

  /** The mesh that contains this triangle */
  Mesh owner;

  /** The face that contains this triangle */
  OBJFace face = null;

  double a, b, c, d, e, f;
  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    Vector3 v0 = owner.getMesh().getPosition(face,0);
    Vector3 v1 = owner.getMesh().getPosition(face,1);
    Vector3 v2 = owner.getMesh().getPosition(face,2);
    
    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;

    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;

    this.setShader(shader);
  }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.

    double g = rayIn.direction.x;
    double h = rayIn.direction.y;
    double i = rayIn.direction.z;

    Vector3 v0 = this.owner.getMesh().getPosition(face, 0);
    double j = v0.x - rayIn.origin.x;
    double k = v0.y - rayIn.origin.y;
    double l = v0.z - rayIn.origin.z;

    double m = a*(e*i - h*f) + b*(g*f - d*i) + c*(d*h - e*g);
    double t = -(f*(a*k - j*b) + h*(j*c - a*l) + d*(b*l - k*c)) / m;
    double gamma = (i*(a*k - j*b) + h*(j*c - a*l) + g*(b*l - k*c)) / m;

    if (gamma < 0 || gamma > 1) {
      return false;
    }

    double beta = (j*(e*i - h*f) + k*(g*f - d*i) + l*(d*h - e*g)) / m;

    if (beta < 0 || beta > (1-gamma)) {
      return false;
    }

    // USE BARYCENTRIC COORDINATES TO FILL IN OUTRECORD

    return false;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}