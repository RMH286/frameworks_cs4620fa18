import math.Vector2;
import math.Vector3;
import meshgen.OBJFace;
import meshgen.OBJMesh;

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

    }

    private static OBJMesh generateCylinder(int n) {
        OBJMesh mesh = new OBJMesh();
        // (2 * Math.PI) / n / 2
        // vertices on the top and bottom are offset so each step is a half angle
        double deltaAngle = Math.PI / n;
        double currAngle = 0

        Vector3 topVertex = Vector3(0, 1, 0);
        Vector3 bottomVertex = Vector3(0, -1, 0);
        //Vector2 topVertexUV = Vector2();
        //Vector2 bottomVertexUV = Vertex2();
        mesh.positions.add(topVertex);
        int topVertexPos = mesh.positions.size() - 1;
        mesh.positions.add(bottomVertex);
        int bottomVertexPos = mesh.positions.size() - 1;
        mesh.normals.add(topVertex);
        int topNormalPos = mesh.normals.size() -1;
        mesh.normals.add(bottomVertex);
        int bottomNormalPos = mesh.normals.size() - 1;

        // start at seam (z = 1)
        Vector3 v1;
        Vector3 v2;
        Vector3 v3;
        Vector3 v1Normal;
        Vector3 v2Normal;
        Vector3 v3Normal;
        int v1Pos;
        int v2Pos;
        int v3Pos;
        int v1NormalPos;
        int v2NormalPos;
        int v3NormalPos;
        v1 = new Vector3(Math.sin(currAngle), 1, Math.cos(currAngle));
        v1Normal = new Vector3(Math.sin(currAngle), 0, Math.cos(currAngle)).normalize();
        currAngle += deltaAngle;
        v2 = new Vector3(Math.sin(currAngle), -1, Math.cos(currAngle));
        v2Normal = new Vector3(Math.sin(currAngle), 0, Math.cos(currAngle)).normalize();
        mesh.positions.add(v1);
        v1Pos = mesh.positions.size() - 1;
        mesh.positions.add(v2);
        v2Pos = mesh.positions.size() - 1;
        mesh.normals.add(v1Normal);
        v1NormalPos = mesh.normals.size() - 1;
        mesh.normals.add(v2Normal);
        v2NormalPos = mesh.normals.size() - 1;


        OBJFace face;

        for (int i = 0; i < n-1; i++) {
            // add next vertex on upper cap (+y)
            currAngle += deltaAngle;
            v3 = new Vector3(Math.sin(currAngle), 1, Math.cos(currAngle));
            v3Normal = new Vector3(Math.sin(currAngle), 0, Math.cos(currAngle)).normalize();
            mesh.positions.add(v3);
            v3Pos = mesh.positions.size() - 1;
            mesh.normals.add(v3Normal);
            v3NormalPos = mesh.normals.size() - 1;

            // form a new face on upper cap (+y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v1Pos;
            face.positions[1] = v3Pos;
            face.positions[2] = topVertexPos;
            // figure out uv coordinates
            face.normals[0] = topNormalPos;
            face.normals[1] = topNormalPos;
            face.normals[3] = topNormalPos;
            mesh.faces.add(face);

            // form a new face on side of cylinder pointing down (-y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v1Pos;
            face.positions[1] = v2Pos;
            face.positions[2] = v3Pos;
            // figure out uv coordinates
            face.normals[0] = v1NormalPos;
            face.normals[1] = v2NormalPos;
            face.normals[3] = v3NormalPos;
            mesh.faces.add(face);

            int v1Pos = v2Pos;
            int v2Pos = v3Pos;
            int v1NormalPos = v2NormalPos;
            int v2NormalPos = v3NormalPos;

            // add next vertex on lower cap (-y)
            currAngle += deltaAngle;
            v3 = new Vector3(Math.sin(currAngle), -1, Math.cos(currAngle));
            v3Normal = new Vector3(Math.sin(currAngle), 0, Math.cos(currAngle)).normalize();
            mesh.positions.add(v3);
            v3Pos = mesh.positions.size() - 1;
            mesh.normals.add(v3Normal);
            v3NormalPos = mesh.normals.size() - 1;

            // form a new face on lower cap (-y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v3Pos;
            face.positions[1] = v1Pos;
            face.positions[2] = bottomVertexPos;
            // figure out uv coordinates
            face.normals[0] = bottomNormalPos;
            face.normals[1] = bottomNormalPos;
            face.normals[3] = bottomNormalPos;
            mesh.faces.add(face);

            // form a new face on side of cylinder pointing up (+y)
            face = new OBJFace(3, true, true);
            face.positions[0] = v3Pos;
            face.positions[1] = v2Pos;
            face.positions[2] = v1Pos;
            // figure out uv coordinates
            face.normals[0] = v3NormalPos;
            face.normals[1] = v2NormalPos;
            face.normals[3] = v1NormalPos;
            mesh.faces.add(face);

            int v1Pos = v2Pos;
            int v2Pos = v3Pos;
            int v1NormalPos = v2NormalPos;
            int v2NormalPos = v3NormalPos;
        }

        // stitch final triangles

        return null;
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
