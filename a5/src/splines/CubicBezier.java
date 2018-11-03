package splines;

import java.util.ArrayList;

import egl.math.Vector2;
import egl.math.Vector3;
/*
 * Cubic Bezier class for the splines assignment
 */

public class CubicBezier {

  //This Bezier's control points
  public Vector2 p0, p1, p2, p3;

  //Control parameter for curve smoothness
  float epsilon;

  //The points on the curve represented by this Bezier
  private ArrayList<Vector2> curvePoints;

  //The normals associated with curvePoints
  private ArrayList<Vector2> curveNormals;

  //The tangent vectors of this bezier
  private ArrayList<Vector2> curveTangents;

  //for recursion
  private int times;

  /**
   *
   * Cubic Bezier Constructor
   *
   * Given 2-D BSpline Control Points correctly set self.{p0, p1, p2, p3},
   * self.uVals, self.curvePoints, and self.curveNormals
   *
   * @param bs0 First Bezier Spline Control Point
   * @param bs1 Second Bezier Spline Control Point
   * @param bs2 Third Bezier Spline Control Point
   * @param bs3 Fourth Bezier Spline Control Point
   * @param eps Maximum angle between line segments
   */
  public CubicBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, float eps) {
    curvePoints = new ArrayList<Vector2>();
    curveTangents = new ArrayList<Vector2>();
    curveNormals = new ArrayList<Vector2>();
    epsilon = eps;

    this.p0 = new Vector2(p0);
    this.p1 = new Vector2(p1);
    this.p2 = new Vector2(p2);
    this.p3 = new Vector2(p3);

    //for recursion
    times = 0;
    ArrayList<Vector2> cP = new ArrayList<Vector2>();
    cP.add(p0);
    cP.add(p1);
    cP.add(p2);
    cP.add(p3);
    tessellate(cP);
  }

  /**
   * Approximate a Bezier segment with a number of vertices, according to an appropriate
   * smoothness criterion for how many are needed.  The points on the curve are written into the
   * array self.curvePoints, the tangents into self.curveTangents, and the normals into self.curveNormals.
   * The final point, p3, is not included, because cubic Beziers will be "strung together".
   */
  private void tessellate(ArrayList<Vector2> cP) {
    // TODO A5
    Vector2 p00 = cP.get(0);
    Vector2 p01 = cP.get(1);
    Vector2 p02 = cP.get(2);
    Vector2 p03 = cP.get(3);
    //find angle to determine to recurse or not
    Vector2 a10 = p01.clone().sub(p00).normalize();
    Vector2 a11 = p02.clone().sub(p01).normalize();
    Vector2 a20 = a11;
    Vector2 a21 = p03.clone().sub(p02).normalize();
    float a1 = a10.angle(a11);
    float a2 = a20.angle(a21);
    //check to see if you should recurse
    if (((a1<=this.epsilon) && (a2<=this.epsilon)) || times == 10) {
      //baseCase
      //add control points to curve points
      this.curvePoints.add(p00);
      this.curvePoints.add(p01);
      this.curvePoints.add(p02);
      //find tangents at points
      Vector2 t00 = p01.clone().sub(p00).normalize();
      Vector2 t01 = p02.clone().sub(p00).normalize();
      Vector2 t02 = p03.clone().sub(p01).normalize();
      //add tangents to list
      this.curveTangents.add(t00);
      this.curveTangents.add(t01);
      this.curveTangents.add(t02);
      //find normals at points
      Vector2 n00 = normal(t00);
      Vector2 n01 = normal(t01);
      Vector2 n02 = normal(t02);
      //add normals to list
      this.curveNormals.add(n00);
      this.curveNormals.add(n01);
      this.curveNormals.add(n02);
      times = 0;
    } else {
      times++;
      //recursive case
      //find mid points
      Vector2 p10 = midpoint(p00,p01);
      Vector2 p11 = midpoint(p01,p02);
      Vector2 p12 = midpoint(p02,p03);
      Vector2 p20 = midpoint(p10,p11);
      Vector2 p21 = midpoint(p11,p12);
      Vector2 p30 = midpoint(p20,p21);

      ArrayList<Vector2> cPL = new ArrayList<Vector2>();
      ArrayList<Vector2> cPR = new ArrayList<Vector2>();
      cPL.add(p00);
      cPL.add(p10);
      cPL.add(p20);
      cPL.add(p30);
      cPR.add(p30);
      cPR.add(p21);
      cPR.add(p12);
      cPR.add(p03);

      tessellate(cPL);
      tessellate(cPR);
    }

  }

  private Vector2 midpoint(Vector2 a, Vector2 b) {
    return new Vector2((a.x+b.x)/2,(a.y + b.y)/2);
  }

  private Vector2 normal(Vector2 t) {
    Vector3 unitZ = new Vector3(0,0,1);
    Vector3 tan = new Vector3(t.x,t.y,0);
    Vector3 n = tan.clone().cross(unitZ);
    return new Vector2(n.x,n.y);
  }

  /**
   * @return The points on this cubic bezier
   */
  public ArrayList<Vector2> getPoints() {
    ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    for(Vector2 p : curvePoints) returnList.add(p.clone());
    return returnList;
  }

  /**
   * @return The tangents on this cubic bezier
   */
  public ArrayList<Vector2> getTangents() {
    ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    for(Vector2 p : curveTangents) returnList.add(p.clone());
    return returnList;
  }

  /**
   * @return The normals on this cubic bezier
   */
  public ArrayList<Vector2> getNormals() {
    ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    for(Vector2 p : curveNormals) returnList.add(p.clone());
    return returnList;
  }

  /**
   * @return The references to points on this cubic bezier
   */
  public ArrayList<Vector2> getPointReferences() {
    ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    for(Vector2 p : curvePoints) returnList.add(p);
    return returnList;
  }

  /**
   * @return The references to tangents on this cubic bezier
   */
  public ArrayList<Vector2> getTangentReferences() {
    ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    for(Vector2 p : curveTangents) returnList.add(p);
    return returnList;
  }

  /**
   * @return The references to normals on this cubic bezier
   */
  public ArrayList<Vector2> getNormalReferences() {
    ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    for(Vector2 p : curveNormals) returnList.add(p);
    return returnList;
  }

}
