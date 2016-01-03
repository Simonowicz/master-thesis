package pl.edu.eiti.pw.disambiguator;

import pl.edu.eiti.pw.model.Paper;

import java.util.List;

/**
 * Class that provides an interface for service that will recognize number of clusters in the model
 */
public interface NumberOfClustersProvider {

    int findNumberOfClusters(List<Paper> papers);
}
