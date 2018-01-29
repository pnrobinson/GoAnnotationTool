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

public class HpoAnnotationTool {
    private static final Logger logger = LogManager.getLogger();
    private HpoOntology ontology=null;

    List<HpoDiseaseAnnotation> annotations=null;

    public HpoAnnotationTool(String hpoOboPath, String HpoAnnotPath) {
        parseOntology(hpoOboPath);
        parseAnnotations(HpoAnnotPath);
        List<TermId> randomterms = getRandomTerms(100);
        calculateCategoryMap(randomterms.get(0),randomterms);
    }




    private void calculateCategoryMap(TermId tid, List<TermId> termlist) {
        HpoCategoryMap categorymap = new HpoCategoryMap();
        //HpoCategory category = categorymap.getCategory(tid);



    }




    private List<TermId> getRandomTerms(int N) {
        int i=0;
        List<TermId> lst = new ArrayList<>();
        for (TermId t : ontology.getTermMap().keySet()) {
            if (++i>N) break;
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
