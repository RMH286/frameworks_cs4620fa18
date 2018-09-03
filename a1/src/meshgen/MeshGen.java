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
