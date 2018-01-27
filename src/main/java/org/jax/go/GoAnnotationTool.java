package org.jax.go;

import com.github.phenomics.ontolib.formats.go.GoGaf21Annotation;
import com.github.phenomics.ontolib.formats.go.GoOntology;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.io.obo.go.GoGeneAnnotationParser;
import com.github.phenomics.ontolib.io.obo.go.GoOboParser;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GoAnnotationTool {
    private static final Logger logger = LogManager.getLogger();
    /** Path to the Gene Ontology file {@code go.obo}. */
    private final String pathToGoOboFile;
    /** Path to the Gene Ontology human annotation file {@code goa_human.gaf}. Note it needs to be g-unzipped after download */
    private final String pathToGoAnnotationFile;

    private GoOntology geneOntology =null;
    ImmutableList<GoGaf21Annotation> goAnnotationList;

    public static void main(String [] args) {
        System.out.println("Go Annotation Tool");
        if (args.length!=2) {
            System.err.println("Usage: java -jar GoAnnotationTool.jar go.obo ");
        }
        GoAnnotationTool tool=new GoAnnotationTool(args[0],args[1]);
    }


    public GoAnnotationTool(String goObo, String goAnnot) {
        pathToGoOboFile=goObo;
        pathToGoAnnotationFile=goAnnot;
        inputGoData();
        Map<String,String> randommap= getRandomGeneMap(100);
        getGoTermsFromTheThreeSubontologies(randommap);
    }

    /**
     * map is a map with key=ID and value=genesymbol
     * @param map
     */
    public void getGoTermsFromTheThreeSubontologies(Map<String,String> map) {
        Map<TermId, Integer> annotationCounts=new HashMap<>();
        Map<TermId, String> term2aspect=new HashMap<>();
        for (GoGaf21Annotation annot : this.goAnnotationList) {
            if (map.containsKey(annot.getDbObjectId())) {
                System.out.println(annot.toString());
                TermId goId=annot.getGoId();
                String aspect = annot.getAspect();
                term2aspect.put(goId,aspect);
                if (! annotationCounts.containsKey(goId)) {
                    annotationCounts.put(goId,0);
                }
                annotationCounts.put(goId,1+annotationCounts.get(goId));
            }
        }

        for (TermId id : annotationCounts.keySet()) {
            System.out.println(id.getIdWithPrefix() +": "+ annotationCounts.get(id) + ": " + term2aspect.get(id));
        }
    }












    private Map<String,String> getRandomGeneMap(int n) {
        Map<String,String> genemap = new HashMap<>();
        int i=0;
        for (GoGaf21Annotation s : this.goAnnotationList) {
            String id=s.getDbObjectId();
            String genesymbol=s.getDbObjectName();
            //System.out.println(id + ": "+ genesymbol);
            genemap.put(id,genesymbol);
            if (++i>n) break;
        }
        return genemap;
    }





    /**
     * Input data from the two GO files into {@link #geneOntology}
     */

    private void inputGoData() {
        final GoOboParser parser = new GoOboParser(new File(pathToGoOboFile));
        try {
            geneOntology = parser.parse();
        } catch (IOException e) {
            logger.fatal("Could not ingest GO obo file");
            e.printStackTrace();
            System.exit(1);
        }
        // Now get the annotations!
        ImmutableList.Builder<GoGaf21Annotation> builder = new ImmutableList.Builder();
        try {
            GoGeneAnnotationParser annotparser = new GoGeneAnnotationParser(new File(pathToGoAnnotationFile));
            while (annotparser.hasNext()) {
                GoGaf21Annotation anno = annotparser.next();
                builder.add(anno);
            }
        } catch (IOException e) {
            logger.fatal(String.format("I/O problem reading from GO annotation file %s.",
                    pathToGoAnnotationFile));
            e.printStackTrace();
            System.exit(1);
        } catch (TermAnnotationParserException e) {
            logger.fatal(String.format("Parse problem reading from GO annotation file %s\n\t%s.",
                    pathToGoAnnotationFile));
            e.printStackTrace();
            System.exit(1);
        }
        goAnnotationList = builder.build();
        logger.trace("We found a total of " + goAnnotationList.size() + " annotations to GO terms for human genes");
    }



}
