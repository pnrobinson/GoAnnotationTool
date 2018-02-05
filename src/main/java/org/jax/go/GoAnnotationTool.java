package org.jax.go;

import com.github.phenomics.ontolib.formats.go.GoGaf21Annotation;
import com.github.phenomics.ontolib.formats.go.GoOntology;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.io.obo.go.GoGeneAnnotationParser;
import com.github.phenomics.ontolib.io.obo.go.GoOboParser;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GoAnnotationTool {
    private static final Logger logger = LogManager.getLogger();
    /** Path to the Gene Ontology file {@code go.obo}. */
    private final String pathToGoOboFile;
    /** Path to the Gene Ontology human annotation file {@code goa_human.gaf}. Note it needs to be g-unzipped after download */
    private final String pathToGoAnnotationFile;

    private GoOntology geneOntology =null;
    ImmutableList<GoGaf21Annotation> goAnnotationList;
    /** Annotation couts for our gene set of interest to GO terms within the biological process subontology. */
    Map<TermId,Integer> biologicalProcessMap;
    /** Annotation couts for our gene set of interest to GO terms within the molecular function subontology. */
    Map<TermId,Integer> molecularFunctionMap;
    /** Annotation couts for our gene set of interest to GO terms within the cellular component subontology. */
    Map<TermId,Integer> cellularComponentMap;






    public GoAnnotationTool(String goObo, String goAnnot) {
        pathToGoOboFile=goObo;
        pathToGoAnnotationFile=goAnnot;
        inputGoData();
        biologicalProcessMap =new HashMap<>();
        molecularFunctionMap =new HashMap<>();
        cellularComponentMap =new HashMap<>();
        Map<String,String> randommap= getRandomGeneMap(100);
        getGoTermsFromTheThreeSubontologies(randommap);
        int numberOfTermsToOutput=3;
        outputTopNTermsInEachSubOntology(numberOfTermsToOutput);
    }

    private void outputTopNTermsInEachSubOntology(int n) {
        System.out.println("###################################");
        System.out.println("## Biological Process Terms: Top n="+n + " ##");
        Map<TermId, Integer> mp = sortMapByTopNValues(this.biologicalProcessMap,n);
        for (TermId tid : mp.keySet() ) {
            String termlabel = geneOntology.getTermMap().get(tid).getName();
            System.out.println(String.format("%s [%s]: %d",termlabel,tid.getIdWithPrefix(),mp.get(tid)));
        }
        System.out.println("## Molecular Function Terms: Top n="+n+ " ##");
        mp = sortMapByTopNValues(this.molecularFunctionMap,n);
        for (TermId tid : mp.keySet() ) {
            String termlabel = geneOntology.getTermMap().get(tid).getName();
            System.out.println(String.format("%s [%s]: %d",termlabel,tid.getIdWithPrefix(),mp.get(tid)));
        }
        System.out.println("## Cellular Component Terms: Top n="+n+ " ##");
        mp = sortMapByTopNValues(this.cellularComponentMap,n);
        for (TermId tid : mp.keySet() ) {
            String termlabel = geneOntology.getTermMap().get(tid).getName();
            System.out.println(String.format("%s [%s]: %d",termlabel,tid.getIdWithPrefix(),mp.get(tid)));
        }
        System.out.println("###################################\n");


    }

    /**
     * This function increments the count of our term of interest in the corresponding count map for the
     * Gene Ontology subontology to which the term belongs.
     * @param tid TermId of the GO term
     * @param aspect One of F, P, C for molecular function, biological process and cellular component
     */
    private void incrementCount(TermId tid, String aspect) {
        Map<TermId,Integer> countmap;
        if (aspect.equals("C"))  {
            countmap= cellularComponentMap;
        } else if (aspect.equals("P")) {
            countmap= biologicalProcessMap;
        } else if (aspect.equals("F")) {
            countmap= molecularFunctionMap;
        } else {
            System.err.println("SHOULD NEVER HAPPEN, ASPECT OTHER THAN C,F,P --SOMETHING IS VERY WRONG");
            return; //TODO just a sanity check, but probably throw an exception in real code
        }
        if (! countmap.containsKey(tid)) {
            countmap.put(tid,0);
        }
        countmap.put(tid,1+countmap.get(tid));
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
                //System.out.println(annot.toString());
                TermId goId=annot.getGoId();
                String aspect = annot.getAspect();
                incrementCount(goId,aspect);
            }
        }

//        for (TermId id : annotationCounts.keySet()) {
//            System.out.println(id.getIdWithPrefix() +": "+ annotationCounts.get(id) + ": " + term2aspect.get(id));
//        }
    }


    /**
     * This gets a random list of genes. TODO -- replace this with the list of genes
     * associated with a set of Diseases that are related to an HPO term.
     * @param n
     * @return
     */
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
            logger.fatal(String.format("Could not ingest GO obo file at %s",pathToGoOboFile));
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


    /** This function receives a map and returns a smaller map that consists of the N entries with the highest
     * values for the key.
     * @param map Map (in our case, the count maps)
     * @param N Number of top entries to return
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V>  sortMapByTopNValues(Map<K, V> map, int N) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        int i=0;
        for (Map.Entry<K, V> entry : list) {
            if (++i>N) break; // just return the top N entries
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }



}
