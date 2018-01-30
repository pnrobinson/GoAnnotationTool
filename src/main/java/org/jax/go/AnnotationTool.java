package org.jax.go;

public class AnnotationTool {

    public static void main(String [] args) {
        System.out.println("Go Annotation Tool");
        if (args.length<2) {
            HpoAnnotationTool hat = new HpoAnnotationTool();

            return;
        }
        if (args.length!=2) {
            System.err.println("Usage: java -jar GoAnnotationTool.jar go.obo goa_human.gaf");
            System.err.println("\tYou entered " + args.length + " arguments but 2 are required");
            System.exit(1);
        }
        GoAnnotationTool tool=new GoAnnotationTool(args[0],args[1]);
    }
}
