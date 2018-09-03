package meshgen;

import math.Vector2;
import math.Vector3;
import meshgen.OBJFace;
import meshgen.OBJMesh;

import java.io.IOException;

public class MeshGen {
    private static String mode = null;
    private static String geometry = null;
    private static int n = 32;
    private static int m = 16;
    private static String in_obj = null;
    private static String out_obj = null;

    public static void main (String[] args) {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            return;
        }

        try {
            parseArgs(args);
        } catch (Exception e) {
            printUsage();
            System.out.println("Invalid command line arguments\n");
        }

        if (mode.equals("-g")) {
            if (geometry.equals("cylinder")) {
                OBJMesh mesh = generateCylinder(n);
                if (mesh.isValid(true)) {
                    try {
                        mesh.writeOBJ(out_obj);
                    } catch (IOException e) {
                        System.out.println("Unable to write .obj file: " + e.getMessage());
                    }
                }
            } else if (geometry.equals("sphere")) {

            }
        } else if (mode.equals("-i")) {

        }

        return;

    }

    private static OBJMesh generateCylinder(int n) {
        OBJMesh mesh = new OBJMesh();
        double deltaAngle = 2 * Math.PI / n;
        double currAngle = Math.PI;
        double sideTextureAngle = currAngle - Math.PI;
        float sideTextureVal = (float) (sideTextureAngle / (2 * Math.PI));
        double capTextureAngleTop = (currAngle - Math.PI / 2);
        double capTextureAngleBottom = (currAngle + Math.PI / 2);

        Vector2 topCapTextureOffset = new Vector2(0.75f, 0.75f);
        Vector2 bottomCapTextureOffset = new Vector2(0.25f, 0.75f);

        Vector3 topVertex = new Vector3(0f, 1f, 0f);
        Vector3 bottomVertex = new Vector3(0f, -1f, 0f);
        mesh.positions.add(topVertex);
        int topVertexPos = mesh.positions.size() - 1;
        mesh.positions.add(bottomVertex);
        int bottomVertexPos = mesh.positions.size() - 1;
        mesh.normals.add(topVertex);
        int topNormalPos = mesh.normals.size() -1;
        mesh.normals.add(bottomVertex);
        int bottomNormalPos = mesh.normals.size() - 1;
        mesh.uvs.add(topCapTextureOffset);
        int topCapTexturePos = mesh.uvs.size() -1;
        mesh.uvs.add(bottomCapTextureOffset);
        int bottomCapTexturePos = mesh.uvs.size() - 1;

        // start at seam (z = 1)
        Vector3 v1;
        Vector3 v2;
        Vector3 v3;
        Vector3 v1Normal;
        Vector3 v2Normal;
        Vector3 v3Normal;
        Vector2 v1SideTexture;
        Vector2 v2SideTexture;
        Vector2 v3SideTexture;
        Vector2 v1CapTexture;
        Vector2 v2CapTexture;
        Vector2 v3CapTexture;
        int v1Pos;
        int v2Pos;
        int v3Pos = 0;
        int v1NormalPos;
        int v2NormalPos;
        int v3NormalPos = 0;
        int v1SideTexturePos;
        int v2SideTexturePos;
        int v3SideTexturePos = 0;
        int v1CapTexturePos;
        int v2CapTexturePos;
        int v3CapTexturePos = 0;
        v1 = new Vector3((float) -Math.sin(currAngle), 1f, (float) -Math.cos(currAngle));
        v1Normal = new Vector3((float) -Math.sin(currAngle), 0f, (float) -Math.cos(currAngle)).normalize();
        v1SideTexture = new Vector2(sideTextureVal, 0.5f);
        v1CapTexture = new Vector2((float) Math.cos(capTextureAngleTop), (float) Math.sin(capTextureAngleTop)).normalize().div(4f).add(topCapTextureOffset);
        //currAngle += deltaAngle;
        //sideTextureAngle = (currAngle + Math.PI) % (2*Math.PI);
        //sideTextureVal = (float) (sideTextureAngle / (2 * Math.PI));
        //capTextureAngle = (currAngle + Math.PI / 4) % 2*Math.PI;
        v2 = new Vector3((float) -Math.sin(currAngle), -1f, (float) -Math.cos(currAngle));
        v2SideTexture = new Vector2(sideTextureVal, 0f);
        v2CapTexture = new Vector2((float) Math.cos(capTextureAngleBottom), (float) Math.sin(capTextureAngleBottom)).normalize().div(4f).add(bottomCapTextureOffset);
        mesh.positions.add(v1);
        v1Pos = mesh.positions.size() - 1;
        mesh.positions.add(v2);
        v2Pos = mesh.positions.size() - 1;
        mesh.normals.add(v1Normal);
        v1NormalPos = mesh.normals.size() - 1;
        v2NormalPos = v1NormalPos;
        mesh.uvs.add(v1SideTexture);
        v1SideTexturePos = mesh.uvs.size() - 1;
        mesh.uvs.add(v2SideTexture);
        v2SideTexturePos = mesh.uvs.size() - 1;
        mesh.uvs.add(v1CapTexture);
        v1CapTexturePos = mesh.uvs.size() - 1;
        mesh.uvs.add(v2CapTexture);
        v2CapTexturePos = mesh.uvs.size() - 1;

        int start1Pos = v1Pos;
        int start2Pos = v2Pos;
        int start1NormalPos = v1NormalPos;
        int start2NormalPos = v2NormalPos;
        int start1SideTexturePos = v1SideTexturePos;
        int start2SideTexturePos = v2SideTexturePos;
        int start1CapTexturePos = v1CapTexturePos;
        int start2CapTexturePos = v2CapTexturePos;

        OBJFace face;

        for (int i = 0; i < n-1; i++) {
            // add next vertex on upper cap (+y)
            currAngle += deltaAngle;
            sideTextureAngle = (currAngle + Math.PI) % (2*Math.PI);
            sideTextureVal = (float) (sideTextureAngle / (2 * Math.PI));
            capTextureAngleTop = (currAngle - Math.PI / 2);
            capTextureAngleBottom = (currAngle + Math.PI / 2);
            v3 = new Vector3((float) -Math.sin(currAngle), 1f, (float) -Math.cos(currAngle));
            v3Normal = new Vector3((float) -Math.sin(currAngle), 0f, (float) -Math.cos(currAngle)).normalize();
            v3SideTexture = new Vector2(sideTextureVal, 0.5f);
            v3CapTexture = new Vector2((float) Math.cos(capTextureAngleTop), (float) Math.sin(capTextureAngleTop)).normalize().div(4f).add(topCapTextureOffset);
            mesh.positions.add(v3);
            v3Pos = mesh.positions.size() - 1;
            mesh.normals.add(v3Normal);
            v3NormalPos = mesh.normals.size() - 1;
            mesh.uvs.add(v3SideTexture);
            v3SideTexturePos = mesh.uvs.size() - 1;
            mesh.uvs.add(v3CapTexture);
            v3CapTexturePos = mesh.uvs.size() - 1;

            // form a new face on upper cap (+y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v1Pos;
            face.positions[1] = v3Pos;
            face.positions[2] = topVertexPos;
            face.normals[0] = topNormalPos;
            face.normals[1] = topNormalPos;
            face.normals[2] = topNormalPos;
            face.uvs[0] = v1CapTexturePos;
            face.uvs[1] = v3CapTexturePos;
            face.uvs[2] = topCapTexturePos;
            mesh.faces.add(face);

            // form a new face on side of cylinder pointing down (-y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v1Pos;
            face.positions[1] = v2Pos;
            face.positions[2] = v3Pos;
            face.normals[0] = v1NormalPos;
            face.normals[1] = v2NormalPos;
            face.normals[2] = v3NormalPos;
            face.uvs[0] = v1SideTexturePos;
            face.uvs[1] = v2SideTexturePos;
            face.uvs[2] = v3SideTexturePos;
            mesh.faces.add(face);

            v1Pos = v2Pos;
            v2Pos = v3Pos;
            v1NormalPos = v2NormalPos;
            v2NormalPos = v3NormalPos;
            v1SideTexturePos = v2SideTexturePos;
            v2SideTexturePos = v3SideTexturePos;
            v1CapTexturePos = v2CapTexturePos;
            v2CapTexturePos = v3CapTexturePos;

            // add next vertex on lower cap (-y)
            //currAngle += deltaAngle;
            //sideTextureAngle = (currAngle + Math.PI) % (2*Math.PI);
            //sideTextureVal = (float) (sideTextureAngle / (2 * Math.PI));
            //capTextureAngle = (currAngle + Math.PI / 4) % (2*Math.PI);
            v3 = new Vector3((float) -Math.sin(currAngle), -1f, (float) -Math.cos(currAngle));
            //v3Normal = new Vector3((float) Math.sin(currAngle), 0f, (float) Math.cos(currAngle)).normalize();
            v3SideTexture = new Vector2(sideTextureVal, 0f);
            v3CapTexture = new Vector2((float) Math.cos(capTextureAngleBottom), (float) Math.sin(capTextureAngleBottom)).normalize().div(4f).add(bottomCapTextureOffset);
            mesh.positions.add(v3);
            v3Pos = mesh.positions.size() - 1;
            //mesh.normals.add(v3Normal);
            //v3NormalPos = mesh.normals.size() - 1;
            v3NormalPos = v2NormalPos;
            mesh.uvs.add(v3SideTexture);
            v3SideTexturePos = mesh.uvs.size() - 1;
            mesh.uvs.add(v3CapTexture);
            v3CapTexturePos = mesh.uvs.size() - 1;

            // form a new face on lower cap (-y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v3Pos;
            face.positions[1] = v1Pos;
            face.positions[2] = bottomVertexPos;
            face.normals[0] = bottomNormalPos;
            face.normals[1] = bottomNormalPos;
            face.normals[2] = bottomNormalPos;
            face.uvs[0] = v3CapTexturePos;
            face.uvs[1] = v1CapTexturePos;
            face.uvs[2] = bottomCapTexturePos;
            mesh.faces.add(face);

            // form a new face on side of cylinder pointing up (+y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v3Pos;
            face.positions[1] = v2Pos;
            face.positions[2] = v1Pos;
            face.normals[0] = v3NormalPos;
            face.normals[1] = v2NormalPos;
            face.normals[2] = v1NormalPos;
            face.uvs[0] = v3SideTexturePos;
            face.uvs[1] = v2SideTexturePos;
            face.uvs[2] = v1SideTexturePos;
            mesh.faces.add(face);

            v1Pos = v2Pos;
            v2Pos = v3Pos;
            v1NormalPos = v2NormalPos;
            v2NormalPos = v3NormalPos;
            v1SideTexturePos = v2SideTexturePos;
            v2SideTexturePos = v3SideTexturePos;
            v1CapTexturePos = v2CapTexturePos;
            v2CapTexturePos = v3CapTexturePos;
        }

        // stitch final triangles

        Vector2 final1SideTexture = new Vector2(1, 0.5f);
        mesh.uvs.add(final1SideTexture);
        int final1SideTexturePos = mesh.uvs.size() - 1;
        Vector2 final2SideTexture = new Vector2(1, 0f);
        mesh.uvs.add(final2SideTexture);
        int final2SideTexturePos = mesh.uvs.size() - 1;

        // form a new face on upper cap (+y)
        face = new OBJFace(3, true, true);
        face.positions[0] = v1Pos;
        face.positions[1] = start1Pos;
        face.positions[2] = topVertexPos;
        face.normals[0] = topNormalPos;
        face.normals[1] = topNormalPos;
        face.normals[2] = topNormalPos;
        face.uvs[0] = v1CapTexturePos;
        face.uvs[1] = start1CapTexturePos;
        face.uvs[2] = topCapTexturePos;
        mesh.faces.add(face);

        face = new OBJFace(3, true, true);
        face.positions[0] = v1Pos;
        face.positions[1] = v2Pos;
        face.positions[2] = start1Pos;
        face.normals[0] = v1NormalPos;
        face.normals[1] = v2NormalPos;
        face.normals[2] = start1NormalPos;
        face.uvs[0] = v1SideTexturePos;
        face.uvs[1] = v2SideTexturePos;
        face.uvs[2] = final1SideTexturePos;
        mesh.faces.add(face);

        face = new OBJFace(3, true, true);
        face.positions[0] = start2Pos;
        face.positions[1] = v2Pos;
        face.positions[2] = bottomVertexPos;
        face.normals[0] = bottomNormalPos;
        face.normals[1] = bottomNormalPos;
        face.normals[2] = bottomNormalPos;
        face.uvs[0] = start2CapTexturePos;
        face.uvs[1] = v2CapTexturePos;
        face.uvs[2] = bottomCapTexturePos;
        mesh.faces.add(face);

        face = new OBJFace(3, true, true);
        face.positions[0] = start2Pos;
        face.positions[1] = start1Pos;
        face.positions[2] = v2Pos;
        face.normals[0] = start2NormalPos;
        face.normals[1] = start1NormalPos;
        face.normals[2] = v2NormalPos;
        face.uvs[0] = final2SideTexturePos;
        face.uvs[1] = final1SideTexturePos;
        face.uvs[2] = v2SideTexturePos;
        mesh.faces.add(face);

        return mesh;
    }

    private static void printUsage() {
        StringBuilder string = new StringBuilder();
        string.append("Usage:  ");
        string.append("java MeshGen -g <sphere|cylinder> [-n <divisionsU>] [-m <divisionsV>] -o <outfile.obj>\n");
        string.append("\t\t(to generate an object)\n");
        string.append("\tjava MeshGen -i <infile.obj> -o <outfile.obj>\n");
        string.append("\t\t(to provide an input mesh)\n");
        System.out.println(string.toString());
    }

    private static void parseArgs(String[] args) throws Exception {
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-g")) {
                mode = args[i];
                if (args[i+1].equals("cylinder")) {
                    geometry = args[i+1];
                } else if (args[i+1].equals("sphere")) {
                    geometry = args[i+1];
                }
            } else if (args[i].equals("-i")) {
                mode = args[i];
                in_obj = args[i+1];
            } else if (args[i].equals("-o")) {
                out_obj = args[i+1];
            } else if (args[i].equals("-n")) {
                n = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-m")) {
                m = Integer.parseInt(args[i+1]);
            }

            i += 2;
        }
        if (in_obj != null && out_obj != null) {
            return;
        } else if (out_obj != null && geometry != null) {
            return;
        }
        throw new Exception("Invalid command line arguments");
    }
}
