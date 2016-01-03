package pl.edu.eiti.pw.disambiguator.model;

import pl.edu.eiti.pw.model.Paper;

import java.util.*;

/**
 * Hidden markov field implementation for disambiguation of authors problem
 */
public class HiddenMarkovField {
    private Paper paper;
    private Map<Relation, List<HiddenMarkovField>> relations = new HashMap<>();

    public HiddenMarkovField() {
        for (Relation relation : Relation.values()) {
            relations.put(relation, new ArrayList<>());
        }
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Map<Relation, List<HiddenMarkovField>> getRelations() {
        return relations;
    }

    public void setRelations(Map<Relation, List<HiddenMarkovField>> relations) {
        this.relations = relations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiddenMarkovField that = (HiddenMarkovField) o;

        return paper != null ? paper.equals(that.paper) : that.paper == null;

    }

    @Override
    public int hashCode() {
        return paper != null ? paper.hashCode() : 0;
    }
}
