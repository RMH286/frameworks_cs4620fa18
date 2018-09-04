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
                        return;
                    }
                }
            } else if (geometry.equals("sphere")) {
                OBJMesh mesh = generateSphere(n,m);
                if (mesh.isValid(true)) {
                    try {
                        mesh.writeOBJ(out_obj);
                    } catch (IOException e) {
                        System.out.println("Unable to write .obj file: " + e.getMessage());
                        return;
                    }
                }
            }
        } else if (mode.equals("-i")) {
            OBJMesh mesh = null;
            try {
                mesh = smooth(in_obj);
            } catch (IOException e) {
                System.out.println("Unable to read .obj file: " + e.getMessage());
                return;
            }
            if (mesh.isValid(true)) {
                try {
                    mesh.writeOBJ(out_obj);
                } catch (IOException e) {
                    System.out.println("Unable to write .obj file: " + e.getMessage());
                }
            }
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

    //convert spherical to cartesian corrdinates
    private static Vector3 sphereToCart(Vector3 sph){
        float r = sph.get(0);
        float theta = sph.get(1);
        float phi = sph.get(2);
        return new Vector3((float)((Math.sin(theta)*r)*(Math.sin(phi)*r)),(float)(Math.cos(phi)*r),(float)((Math.sin(phi)*r)*(Math.cos(theta)*r)));
    }

    private static Vector2 sphereToUV(Vector3 sph){
        float r = sph.get(0);
        float theta = sph.get(1);
        float phi = sph.get(2);
        float normalizedTheta = (float)((theta/(2*Math.PI)) + .5);
        float u = 0;
        if (normalizedTheta > 1){
            u = normalizedTheta - 1;
        }
        else{
            u = normalizedTheta;
        }
        float v = (float)(1-(phi/Math.PI));

        System.out.println("(" + u + " , " + v +")");

        return new Vector2(u,v);
    }


    private static OBJMesh generateSphere(int n, int m) {
        OBJMesh mesh = new OBJMesh();

        //phi delta
        double dp = Math.PI/m;
        //theta delta
        double dt = (2*Math.PI)/n;
        //start at meridian and south most lattitude
        //phi start
        double cp = Math.PI*((m-1.0)/m);
        //theta start
        double ct = 0.0;

        mesh.positions.add(sphereToCart(new Vector3(1,0,0)));
        mesh.positions.add(sphereToCart(new Vector3(1,0,(float)Math.PI)));
        int topVertex = mesh.positions.size()-2;
        int bottomVertex = mesh.positions.size()-1;

        //mesh.uvs.add(sphereToUV(new Vector3(1,(float)ct,(float)0)));
        //mesh.uvs.add(new Vector2((float).5,(float)0));

        //int topUV = mesh.uvs.size()-2;
        //int botUV = mesh.uvs.size()-1;

        //keep track of the last row of position vectors so they can be reused.
        int[] lastP = new int[n];
        int[] lastUV = new int[n];

        //botom and top ring
        int[] bottomring = new int[n];
        int[] topring = new int[n];

        int[] bUV = new int[n];
        int[] tUV = new int[n];

        //create southmost latt of vertecies
        for(int i = 0; i < n; i++){
            //posistion
            Vector3 posVec = new Vector3(1,(float)ct,(float)cp);
            mesh.positions.add(sphereToCart(posVec));
            lastP[i] = mesh.positions.size()-1;
            bottomring[i] = mesh.positions.size()-1;

            mesh.uvs.add(sphereToUV(posVec));
            lastUV[i] = mesh.uvs.size()-1;
            bUV[i] = mesh.uvs.size()-1;

            ct += dt;
        }

        //create faces
        //from the southmost latt to north most latt with two triangles per square
        for(int i = 0; i < (m-2); i++){
            //update cordinates
            ct = 0.0;
            cp -= dp;
            int first = lastP[0];
            int firstUv = lastUV[0];
            mesh.positions.add(sphereToCart(new Vector3(1,(float)(ct),(float)cp)));
            mesh.uvs.add(sphereToUV(new Vector3(1,(float)(ct),(float)cp)));
            for(int j = 0; j < n; j++){
                //update vector positions
                int v1;
                int v2;
                int v3;
                int v4;
                v2 = lastP[j];
                v1 = mesh.positions.size()-1;

                int uv1;
                int uv2;
                int uv3;
                int uv4;
                uv2 = lastUV[j];
                uv1 = mesh.uvs.size()-1;

                if(j+1 != n){
                    mesh.positions.add(sphereToCart(new Vector3(1,(float)(ct + dt),(float)cp)));
                    v3 = mesh.positions.size()-1;
                    v4 = lastP[j+1];

                    mesh.uvs.add(sphereToUV(new Vector3(1,(float)(ct + dt),(float)cp)));
                    uv3 = mesh.uvs.size()-1;
                    uv4 = lastUV[j+1];

                }
                else{
                    v3 = lastP[0];
                    v4 = first;

                    uv3 = lastUV[0];
                    uv4 = firstUv;
                }

                //update lastP
                lastP[j] = v1;
                //fill top ring
                topring[j] = v1;

                //update lastUV
                lastUV[j] = uv1;
                //fill tUV
                tUV[j] = uv1;

                //create face


                OBJFace face1 = new OBJFace(3, true, false);
                face1.positions[0] = v1;
                face1.positions[1] = v2;
                face1.positions[2] = v4;

                face1.uvs[0] = uv1;
                face1.uvs[1] = uv2;
                face1.uvs[2] = uv4;

                OBJFace face2 = new OBJFace(3, true, false);
                face2.positions[0] = v1;
                face2.positions[1] = v4;
                face2.positions[2] = v3;

                face2.uvs[0] = uv1;
                face2.uvs[1] = uv4;
                face2.uvs[2] = uv3;

                mesh.faces.add(face1);
                mesh.faces.add(face2);

                ct += dt;

            }
        }

        //create top and bottom disk
        for(int i = 0; i < n; i++){
            //ct = 0.0;

            int v1t;
            int v2t;
            int v1b;
            int v2b;

            int uv1t;
            int uv2t;
            int uv1b;
            int uv2b; 

            if(i+1 == n){
                v2t = topring[0];
                v2b = bottomring[0]; 

                uv2t = tUV[0];
                uv2b = bUV[0];
            }
            else{
                v2t = topring[i+1];
                v2b = bottomring[i+1];

                uv2t = tUV[i+1];
                uv2b = bUV[i+1];
            }
            v1t = topring[i];
            v1b = bottomring[i];

            uv1t = tUV[i];
            uv1b = bUV[i];

            OBJFace face1 = new OBJFace(3, true, false);
            face1.positions[0] = v1t;
            face1.positions[1] = v2t;
            face1.positions[2] = topVertex;

            face1.uvs[0] = uv1t;
            face1.uvs[1] = uv2t;
            // mesh.uvs.add(sphereToUV(new Vector3(1,(float)ct,(float)0)));
            // face1.uvs[2] = mesh.uvs.size()-1;
            Vector2 topUV = mesh.uvs.get(uv1t).clone();
            topUV.add(mesh.uvs.get(uv2t));
            topUV.div(2f);
            topUV.y = 1;
            mesh.uvs.add(topUV);
            face1.uvs[2] = mesh.uvs.size() - 1;


            OBJFace face2 = new OBJFace(3, true, false);
            face2.positions[0] = v2b;
            face2.positions[1] = v1b;
            face2.positions[2] = bottomVertex;

            face2.uvs[0] = uv2b;
            face2.uvs[1] = uv1b;
            // mesh.uvs.add(sphereToUV(new Vector3(1,(float)ct,(float)1)));
            // face2.uvs[2] = mesh.uvs.size()-1;
            Vector2 botUV = mesh.uvs.get(uv1t).clone();
            botUV.add(mesh.uvs.get(uv2t));
            botUV.div(2f);
            botUV.y = 0;
            mesh.uvs.add(botUV);
            face2.uvs[2] = mesh.uvs.size() - 1;
            
            mesh.faces.add(face1);
            mesh.faces.add(face2);

            //ct += dt;
        }



        return mesh;
    }

    private static OBJMesh smooth(String inOBJ) throws IOException, OBJMesh.OBJFileFormatException {
        OBJMesh mesh = new OBJMesh();
        mesh.parseOBJ(inOBJ);

        mesh.normals.clear();
        for (int i = 0; i < mesh.positions.size(); i++) {
            mesh.normals.add(new Vector3(0, 0, 0));
        }

        for (OBJFace face : mesh.faces) {
            if (face.normals == null) {
                face.normals = new int[face.positions.length];
            }
            Vector3 v1 = mesh.positions.get(face.positions[0]).clone();
            Vector3 v2 = mesh.positions.get(face.positions[1]).clone();
            Vector3 v3 = mesh.positions.get(face.positions[2]).clone();
            Vector3 deltaV1 = v2.sub(v1);
            Vector3 deltaV2 = v3.sub(v1);
            Vector3 faceNormal = (deltaV1.cross(deltaV2)).normalize();
            for (int i = 0; i < face.positions.length; i++) {
                face.normals[i] = face.positions[i];
                mesh.normals.set(face.normals[i], mesh.normals.get(face.normals[i]).add(faceNormal));
            }
        }

        for (Vector3 vertex : mesh.normals) {
            vertex.normalize();
        }

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
