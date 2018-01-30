package org.jax.go;

import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a category of HPO terms that we would like to display or treat as a group. Roughly, it
 * corresponds to the major organ abnormality categories, but it allows subcategoires to be added, for instance,
 * gastrointestinal can have the subcategory liver
 */
public class HpoCategory {
  private static final Logger logger = LogManager.getLogger();

  private final TermId tid;

  private final String label;
  /** List of the HPO terms from the disease we want to display that are children of this category. Note that we
   * try to put the TermsIds in the {@link #subcatlist} if possible, and then they do not appear here.
   */
  private List<TermId> annotatedTerms;

  private List<HpoCategory> subcatlist=new ArrayList<>();


  private HpoCategory(TermId id, String labl) {
    tid=id;
    label=labl;
  }

  private void setSubcateogyrList(List<HpoCategory> sublist) {
    this.subcatlist=sublist;
  }

  public TermId getTid() {
    return tid;
  }

  public String getLabel() {
    return label;
  }


  public void addAnnotatedTerm(TermId tid,Ontology ontology){

  }

  public static class Builder {

    private final TermId tid;
    private String label;
    private ImmutableList.Builder<HpoCategory> builder=new ImmutableList.Builder<>();



    public Builder(TermId id, String labl) {
      this.tid=id;
      this.label=labl;
    }

    public Builder subcategory(TermId id , String label) {
      HpoCategory subcat=new HpoCategory(id,label);
      builder.add(subcat);
      return this;
    }



    public HpoCategory build() {
      HpoCategory cat = new HpoCategory(this.tid,this.label);
      cat.setSubcateogyrList(builder.build());
      return cat;

    }



  }


}
