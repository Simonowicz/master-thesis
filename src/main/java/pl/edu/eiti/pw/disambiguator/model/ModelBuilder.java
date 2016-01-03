package pl.edu.eiti.pw.disambiguator.model;

import pl.edu.eiti.pw.model.Paper;

import java.util.List;
import java.util.Map;

/**
 * Model builder interface
 */
public interface ModelBuilder {
    Map<Integer, List<HiddenMarkovField>> initializeClusters(List<Paper> papers, String nameDisambiguated, int numberOfClusters);
}
