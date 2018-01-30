package org.jax.go;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.graph.algo.BreadthFirstSearch;
import com.github.phenomics.ontolib.graph.data.DirectedGraph;
import com.github.phenomics.ontolib.graph.data.Edge;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.github.phenomics.ontolib.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * Model of the upper-level HPO classes. This allows a canonical view of HPO Annotation per disease
 * according to categories such as Heart, Eye, Brain etc.
 * <p>
 * <p>
 * The main function takes a list of HPO terms and returns a compatible list but sorted according to the
 * categories
 * </p>
 *
 * @author <a href="mailto:peter.robinson@bjax.org">Peter Robinson</a>
 */
public class HpoCategoryMap {
    private static final Logger logger = LogManager.getLogger();
    /**
     * Key: the HPO TermId, value: HpoCategory for this term. For instance, the HPO TermId HP:0001626
     * (Abnormality of the cardiovascular system) would corresspond to the category Cardiovascular and
     * several subcategories.
     */
    ImmutableMap<TermId, HpoCategory> categorymap;

    private static final TermId ABNORMAL_CELLULAR_ID = ImmutableTermId.constructWithPrefix("HP:0025354");
    private static final TermId NEOPLASM_ID = ImmutableTermId.constructWithPrefix("HP:0002664");
    private static final TermId BLOOD_ID = ImmutableTermId.constructWithPrefix("HP:0001871");


    HpoCategoryMap() {
        initializeMap();
    }


    private void initializeMap() {
        ImmutableMap.Builder<TermId, HpoCategory> mapbuilder = new ImmutableMap.Builder<>();
        // Abn cellular phenotype
        HpoCategory abnCellCategory = new HpoCategory.Builder(ABNORMAL_CELLULAR_ID, "Cellular phenotype").build();
        mapbuilder.put(ABNORMAL_CELLULAR_ID, abnCellCategory);
        // Blood
        HpoCategory abnBlood = new HpoCategory.Builder(BLOOD_ID, "Blood and blood-forming tissues").build();
        mapbuilder.put(abnBlood.getTid(), abnBlood);
        // Connective tissue
        TermId abnConnectiveTissueTid = ImmutableTermId.constructWithPrefix("HP:0003549");
        HpoCategory abnConnTiss = new HpoCategory.Builder(abnConnectiveTissueTid,
                "Connective tissue").build();
        mapbuilder.put(abnConnTiss.getTid(), abnConnTiss);
        // head or neck
        TermId headNeckId = ImmutableTermId.constructWithPrefix("HP:0000152");
        HpoCategory headNeckCat = new HpoCategory.Builder(headNeckId, "Head and neck").
                subcategory(ImmutableTermId.constructWithPrefix("HP:0000234"), "Head").
                subcategory(ImmutableTermId.constructWithPrefix("HP:0000464"), "Neck").build();
        mapbuilder.put(headNeckCat.getTid(), headNeckCat);
        // limbs
        TermId limbId = ImmutableTermId.constructWithPrefix("HP:0040064");
        HpoCategory limbCat = new HpoCategory.Builder(limbId, "Limbs").build();
        mapbuilder.put(limbCat.getTid(), limbCat);
        // metabolism
        TermId metabolismId = ImmutableTermId.constructWithPrefix("HP:0001939");
        HpoCategory metabolismCat = new HpoCategory.Builder(metabolismId, "Metabolism/Laboratory abnormality").build();
        mapbuilder.put(metabolismId, metabolismCat);
        //prenatal
        TermId prenatalId = ImmutableTermId.constructWithPrefix("HP:0001197");
        HpoCategory prenatalCat = new HpoCategory.Builder(prenatalId, "Prenatal and Birth").build();
        mapbuilder.put(prenatalId, prenatalCat);
        //breast
        TermId breastId = ImmutableTermId.constructWithPrefix("HP:0000769");
        HpoCategory breastCat = new HpoCategory.Builder(breastId, "Breast").build();
        mapbuilder.put(breastId, breastCat);
        //cardiovascular
        TermId cardiovascularId = ImmutableTermId.constructWithPrefix("HP:0001626");
        HpoCategory cardiovascularCat = new HpoCategory.Builder(cardiovascularId, "Cardiovascular").
                subcategory(ImmutableTermId.constructWithPrefix("HP:0002597"), "Vascular").
                subcategory(ImmutableTermId.constructWithPrefix("HP:0010948"), "fetal cardiovascular").
                build(); // ToDo extend!!!
        mapbuilder.put(cardiovascularId, cardiovascularCat);
        //digestive
        TermId digestiveId = ImmutableTermId.constructWithPrefix("HP:0025031");
        HpoCategory digestiveCat = new HpoCategory.Builder(digestiveId, "Digestive System").build();
        mapbuilder.put(digestiveId, digestiveCat);
        // ear
        TermId earId = ImmutableTermId.constructWithPrefix("HP:0000598");
        HpoCategory earCat = new HpoCategory.Builder(earId, "Ear").build();
        mapbuilder.put(earId, earCat);
        //endocrine
        TermId endocrineId = ImmutableTermId.constructWithPrefix("HP:0000818");
        HpoCategory endocrineCat = new HpoCategory.Builder(endocrineId, "Endocrine").build();
        mapbuilder.put(endocrineId, endocrineCat);
        // eye
        TermId eyeId = ImmutableTermId.constructWithPrefix("HP:0000478");
        HpoCategory eyeCat = new HpoCategory.Builder(eyeId, "Eye").build();
        mapbuilder.put(eyeId, eyeCat);
        //genitourinary
        TermId guId = ImmutableTermId.constructWithPrefix("HP:0000119");
        HpoCategory guCat = new HpoCategory.Builder(guId, "Genitourinary system").
                subcategory(ImmutableTermId.constructWithPrefix("HP:0000078"), "Genital system").
                subcategory(ImmutableTermId.constructWithPrefix("HP:0000079"), "Urinary system").
                build();
        mapbuilder.put(guId, guCat);
        // Immune
        TermId immuneId = ImmutableTermId.constructWithPrefix("HP:0002715");
        HpoCategory immuneCat = new HpoCategory.Builder(immuneId, "Immunology").build();
        mapbuilder.put(immuneId, immuneCat);
        //integument
        TermId integumentId = ImmutableTermId.constructWithPrefix("HP:0001574");
        HpoCategory integumentCat = new HpoCategory.Builder(integumentId, "Skin, Hair, and Nails").build();
        mapbuilder.put(integumentId, integumentCat);
        //muscle
        TermId muscleId = ImmutableTermId.constructWithPrefix("HP:0003011");
        HpoCategory muscleCat = new HpoCategory.Builder(muscleId, "Musculature").build();
        mapbuilder.put(muscleId, muscleCat);
        //Nervous system
        TermId nervousId = ImmutableTermId.constructWithPrefix("HP:0000707");
        HpoCategory nervousCat = new HpoCategory.Builder(nervousId, "Nervous System").build();
        mapbuilder.put(nervousId, nervousCat);
        //respiratory system
        TermId resporatoryId = ImmutableTermId.constructWithPrefix("HP:0002086");
        HpoCategory respiratoryCat = new HpoCategory.Builder(resporatoryId, "Repiratory System").build();
        mapbuilder.put(resporatoryId, respiratoryCat);
        // skeletal
        TermId skeletalId = ImmutableTermId.constructWithPrefix("HP:0000924");
        HpoCategory skeletalCat = new HpoCategory.Builder(skeletalId, "Skeletal system").build();
        mapbuilder.put(skeletalId, skeletalCat);
        //thoracic cavity
        TermId thoracicId = ImmutableTermId.constructWithPrefix("HP:0045027");
        HpoCategory thoracicCat = new HpoCategory.Builder(thoracicId, "Thoracic cavity").build();
        mapbuilder.put(thoracicId, thoracicCat);
        //voice
        TermId voiceId = ImmutableTermId.constructWithPrefix("HP:0001608");
        HpoCategory voiceCat = new HpoCategory.Builder(voiceId, "Voice").build();
        mapbuilder.put(voiceId, voiceCat);
        //consistutiotnal symtpom
        TermId constitutionalId = ImmutableTermId.constructWithPrefix("HP:0025142");
        HpoCategory constitutionalCat = new HpoCategory.Builder(constitutionalId, "Constitutional Symptom").build();
        mapbuilder.put(constitutionalId, constitutionalCat);
        // growth
        TermId growthId = ImmutableTermId.constructWithPrefix("HP:0001507");
        HpoCategory growthCat = new HpoCategory.Builder(growthId, "Growth").build();
        mapbuilder.put(growthId, growthCat);
        // neoplasm

        HpoCategory neoplasmCat = new HpoCategory.Builder(NEOPLASM_ID, "Neoplasm").build();
        mapbuilder.put(NEOPLASM_ID, neoplasmCat);
        // Finally, build the map!
        categorymap = mapbuilder.build();

    }


//    public Map<TermId, List<TermId>> sortTerms(HpoOntology ontology, List<TermId> termlist) {
//        Map<TermId, List<TermId>> mp = new HashMap<>();
//        TermId phenotypicAbnormalty = ImmutableTermId.constructWithPrefix("HP:0000118");
//        for (TermId tid : termlist) {
//            Set<TermId> ancestors = new HashSet<>();
//            for (TermId candidate : categorymap.keySet()) {
//                if (existsPath(ontology, candidate, tid)) {
//                    ancestors.add(tid);
//                }
//            }
//            if (ancestors.isEmpty()) {
//                if (!mp.containsKey(phenotypicAbnormalty)) {
//                    mp.put(phenotypicAbnormalty, new ArrayList<>());
//                }
//                List<TermId> lst = mp.get(phenotypicAbnormalty);
//                lst.add(tid);
//            } else if (ancestors.size() == 1) {
//                TermId anc = ancestors.iterator().next();
//                if (!mp.containsKey(anc)) {
//                    mp.put(anc, new ArrayList<>());
//                }
//                List<TermId> lst = mp.get(anc);
//                lst.add(tid);
//            } else {
//                TermId primaryTid = getPrioritizedTid(ancestors);
//                if (!mp.containsKey(primaryTid)) {
//                    mp.put(primaryTid, new ArrayList<>());
//                }
//                List<TermId> lst = mp.get(primaryTid);
//                lst.add(tid);
//            }
//        }
//        return mp;
//    }

    public void addAnnotatedTerm(TermId tid, HpoOntology ontology) {
        logger.trace("AddAnnotated Term for tid="+tid.getIdWithPrefix() + ", " +
        ontology.getTermMap().get(tid).getName());

        try {
            HpoCategory cat = getCategory(tid, ontology);
            if (cat==null) {
                logger.error("Could not get category for " + ontology.getTermMap().get(tid).getName());
                return;
            } else {
                logger.trace("Found category "+cat.getLabel());
            }
            cat.addAnnotatedTerm(tid, ontology);
        } catch (Exception e) {
            logger.error(String.format("Exception trying to find category for %s",ontology.getTermMap().get(tid).getName()));
            e.printStackTrace();
            Collection<Edge<TermId>> coll2 = (Collection<Edge<TermId>>) ontology.getGraph().getEdges();
            for (Edge<TermId> edge : coll2) {
                if (edge.getDest().equals(tid) || edge.getSource().equals(tid)) {
                    System.out.println("Edge " + ontology.getTermMap().get(edge.getSource()).getName() +
                    " -> " + ontology.getTermMap().get(edge.getDest()).getName());
                }
            }
        }
    }



    /** Find all of the direct parents of childTermId (do not include "grandchildren" and other descendents).
     * @param ontology The ontology to which parentTermId belongs
     * @param childTermId The term whose parents were are seeking
     * @return A set of all parent terms of childTermId
     */
    public Set<TermId> getParentTerms(HpoOntology ontology, TermId childTermId) {
        ImmutableSet.Builder<TermId> anccset = new ImmutableSet.Builder<>();
        logger.trace("getParentTerms childTermOd="+childTermId.getIdWithPrefix() + ", " +
                ontology.getTermMap().get(childTermId).getName());
        Iterator it =  ontology.getGraph().outEdgeIterator(childTermId);
        while (it.hasNext()) {
            Edge<TermId> edge = (Edge<TermId>) it.next();
            TermId destId=edge.getDest();
            anccset.add(destId);
        }
        return anccset.build();
    }

    public Set<TermId> getAncestorCategories(HpoOntology ontology, TermId childTermId) {
        ImmutableSet.Builder<TermId> anccset = new ImmutableSet.Builder<>();
        Stack<TermId> stack = new Stack<>();
        stack.push(childTermId);
        while (! stack.empty()) {
            TermId tid = stack.pop();
            Set<TermId> parents = getParentTerms(ontology,tid);
            for (TermId p : parents) {
                if (this.categorymap.containsKey(p)) {
                    anccset.add(p);
                } else {
                    stack.add(p);
                }
            }
        }
        return anccset.build();
    }





    public static boolean existsPath2(Ontology ontology, final TermId sourceID, TermId destID){
        // special case -- a term cannot have a path to itself in an ontology (DAG)
        if (sourceID.equals(destID)) return false;
        final DirectedGraph<TermId, ImmutableEdge<TermId>> graph=ontology.getGraph();
        List<TermId> visited = new ArrayList<>();
        BreadthFirstSearch<TermId, ImmutableEdge<TermId>> bfs = new BreadthFirstSearch<>();
        bfs.startFromForward(graph, sourceID, (g, termId) -> {
            visited.add(termId);
            System.out.println("visited "+termId.getIdWithPrefix());
            return true;
        });
        return visited.contains(destID);
    }




    /** Identify the category that best matches the term id (usually just fine the category that represents a parent term. */
    public HpoCategory getCategory(TermId tid, HpoOntology ontology) throws Exception {
        logger.trace(String.format("GetCategory for %s[%s]",ontology.getTermMap().get(tid).getName(),tid.getIdWithPrefix()));
        List<HpoCategory> activeCategoryList = new ArrayList<>();
        if (tid==null) {
            logger.error("Warning, tid was null...");
            return null;
        }
//        for (TermId categoryId : categorymap.keySet()) {
//            if (existsPath2(ontology, tid, categoryId)) {
//               //
//                HpoCategory parentCategory = categorymap.get(categoryId);
//                // see if there is a match with any of the children of this category
//                activeCategoryList.add(parentCategory);
//                logger.trace(String.format("We found category %s for term %s",
//                        parentCategory.getLabel(),
//                        ontology.getTermMap().get(tid).getName()));
//            }
////            else {
////                System.out.println(String.format("Could not identify parentage from %s to %s",
//                        ontology.getTermMap().get(tid).getName(),
//                        ontology.getTermMap().get(categoryId).getName()));
////            }
//        }
//        if (activeCategoryList.isEmpty()) {
//            logger.error(String.format("Could not identify category for %s [%s]",
//                    ontology.getTermMap().get(tid).getName(),
//                    tid.getIdWithPrefix()));
//            System.exit(1);
//        }
        Set<TermId> ancs = getAncestorCategories(ontology,tid);
        return getPrioritizedCategory(ancs);
    }


    private HpoCategory getPrioritizedCategory(Set<TermId> catlist) {
        for (TermId hpocat : catlist) {
            if (hpocat.equals(NEOPLASM_ID))
                return categorymap.get(hpocat);
        }
        // if we get here there was no prioritized category
        for (TermId c : catlist) {
            return  categorymap.get(c);
        }
        return null;
    }


    /**
     * Return neoplasm with higher priority than the other categories. ToDO Anything else?
     *
     * @return the primariy termId if we have more than one.
     */
    private TermId getPrioritizedTid(Set<TermId> ancestors) {
        for (TermId id : ancestors) {
            if (id.equals(NEOPLASM_ID)) {
                return NEOPLASM_ID; // we always want this
            }
        }
        return ancestors.iterator().next();
    }


}
