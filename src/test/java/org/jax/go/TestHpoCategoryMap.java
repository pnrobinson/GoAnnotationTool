package org.jax.go;

import com.github.phenomics.ontolib.formats.hpo.HpoDiseaseAnnotation;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.graph.data.Edge;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.io.obo.hpo.HpoDiseaseAnnotationParser;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.data.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TestHpoCategoryMap {

    private static String hpOboPath="hp.obo";
    private static String hpAnnotationPath="/Users/peterrobinson/Documents/data/phenotype_annotation.tab";
    private static HpoOntology ontology=null;

    private static List<HpoDiseaseAnnotation> annotations=null;


    @BeforeClass
    public static void init() {
        parseOntology(hpOboPath);
        parseAnnotations(hpAnnotationPath);
    }

    private static void parseAnnotations(String path) {
        File inputFile = new File(path);
        annotations = new ArrayList<>();
        try {
            HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(inputFile);
            while (parser.hasNext()) {
                HpoDiseaseAnnotation anno = parser.next();
                annotations.add(anno);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseOntology(String hpoOntologyPath)  {
        TermPrefix pref = new ImmutableTermPrefix("HP");
        TermId inheritId = new ImmutableTermId(pref,"0000005");
        try {
            HpoOboParser hpoOboParser = new HpoOboParser(new File(hpoOntologyPath));
            ontology = hpoOboParser.parse();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1); // TODO do something with the exception
        }
    }


    /** HP:0005091 is Hemiatrophy */
    @Test
    public void test1() {
        HpoCategoryMap categorymap = new HpoCategoryMap();
        // Testing revealed that "HP:0005091" causes a crash because the following function in the
        //class "ImmutableDirectedGraph" gets a null pointer exception -- edgesLists.get(v) is NULL
        // for the vertex of this term
        //@Override
        //    return edgeLists.get(v).getOutEdges().iterator();
        //  }
        TermId hemiatrophyId = ImmutableTermId.constructWithPrefix("HP:0100556");//("HP:0005091"); // blood("HP:0010975");//(
        System.out.println("Checking term \""+hemiatrophyId.getIdWithPrefix() + "\"");
//        Collection<Edge<TermId>> edges = (Collection<Edge<TermId>>) ontology.getGraph().getEdges();
//        for (Edge<TermId> edge : edges) {
//            System.out.println(edge.getSource().getIdWithPrefix() + " -> " + edge.getDest().getIdWithPrefix());
//            if (edge.getSource().equals(hemiatrophyId) || edge.getDest().equals(hemiatrophyId)) {
//                System.out.println( ontology.getTermMap().get(edge.getSource()).getName() +
//                " -> " +
//                ontology.getTermMap().get(edge.getDest()).getName());
//            }
//        }
        categorymap.addAnnotatedTerm(hemiatrophyId,ontology);
    }


    //HP:0005091 is an alt_id for HP:0100556 (Hemiatrophy). Use of alt_ids in the annotations has
    // led to some inconsistencies and errors.
    @Test
    public void testAltId() {
        TermId altHemihypertopy = ImmutableTermId.constructWithPrefix("HP:0005091");
        TermId goodHemihypertrophy = ImmutableTermId.constructWithPrefix("HP:0100556");

        List<TermId> alts=ontology.getTermMap().get(goodHemihypertrophy).getAltTermIds();
        System.out.println("Alt ids of " + goodHemihypertrophy.getIdWithPrefix() +":");
        for (TermId id : alts) {
            System.out.println("\t"+id.getIdWithPrefix());
        }
        System.out.println("Primary id of " + goodHemihypertrophy.getIdWithPrefix() +": " +
        ontology.getTermMap().get(goodHemihypertrophy).getId().getIdWithPrefix());
        alts=ontology.getTermMap().get(altHemihypertopy).getAltTermIds();
        System.out.println("Alt ids of " + altHemihypertopy.getIdWithPrefix() +":");
        for (TermId id : alts) {
            System.out.println("\t"+id.getIdWithPrefix());
        }
        System.out.println("Primary id of " + altHemihypertopy.getIdWithPrefix() +": " +
                ontology.getTermMap().get(altHemihypertopy).getId().getIdWithPrefix());

        // if (ontology.getTermMap().get(tid).)
    }
}
