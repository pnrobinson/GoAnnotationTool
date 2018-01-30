package org.jax.go;

import com.github.phenomics.ontolib.formats.hpo.HpoDiseaseAnnotation;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.io.obo.hpo.HpoDiseaseAnnotationParser;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HpoAnnotationTool {
    private static final Logger logger = LogManager.getLogger();
    private HpoOntology ontology=null;

    List<HpoDiseaseAnnotation> annotations=null;


    String hpOboPath="hp.obo";
    String hpAnnotationPath="/Users/peterrobinson/Documents/data/phenotype_annotation.tab";


    public HpoAnnotationTool() {
        parseOntology(this.hpOboPath);
        parseAnnotations(this.hpAnnotationPath);
        List<TermId> randomterms = getRandomTerms(100);
        calculateCategoryMap(randomterms);
    }



    public HpoAnnotationTool(String hpoOboPath, String HpoAnnotPath) {
        parseOntology(hpoOboPath);
        parseAnnotations(HpoAnnotPath);
        List<TermId> randomterms = getRandomTerms(10);
        calculateCategoryMap(randomterms);
    }




    private void calculateCategoryMap(List<TermId> termlist) {
        HpoCategoryMap categorymap = new HpoCategoryMap();
        for (TermId tid : termlist) {
            categorymap.addAnnotatedTerm(tid,ontology);
        }

        //HpoCategory category = categorymap.getCategory(tid);



    }




    private List<TermId> getRandomTerms(int N) {
        N=Math.min(N,ontology.getTermMap().size());
        List<TermId > allterms=new ArrayList<>(ontology.getTermMap().keySet());
        List<TermId> lst = new ArrayList<>();
        Random rand=new Random();
        for (int i=0;i<N;i++) {
            int r = rand.nextInt(allterms.size());
            TermId t = allterms.get(r);
            lst.add(t);
        }
        return lst;
    }



    private void parseAnnotations(String path) {
        File inputFile = new File(path);
        annotations = new ArrayList<>();
        try {
            HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(inputFile);
            while (parser.hasNext()) {
                HpoDiseaseAnnotation anno = parser.next();
                TermId tid = anno.getHpoId();
                TermId primaryId=ontology.getTermMap().get(tid).getId();
                if (! tid.equals(primaryId)) {
                    //create a new HpoDiseaseAnnotation object with correct primary id
                }

                annotations.add(anno);
            }
        } catch (IOException e) {
            logger.error("Could not read from file at \"" + path + "\"");
            logger.error(e, e);
        } catch (TermAnnotationParserException e) {
            logger.error("Could not parse file at " + path);
            logger.error(e, e);
        }
    }

    /**
     * Parse the HP ontology file and place the data in {@link #ontology} a

     */
    public void parseOntology(String hpoOntologyPath)  {
        TermPrefix pref = new ImmutableTermPrefix("HP");
        TermId inheritId = new ImmutableTermId(pref,"0000005");
        try {
            HpoOboParser hpoOboParser = new HpoOboParser(new File(hpoOntologyPath));
            this.ontology = hpoOboParser.parse();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1); // TODO do something with the exception
        }
    }
}
