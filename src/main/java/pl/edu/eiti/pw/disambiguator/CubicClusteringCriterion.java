package pl.edu.eiti.pw.disambiguator;

import pl.edu.eiti.pw.model.Paper;

import java.util.List;

/**
 * Cubic Clustering Criterion implementation of algorithm determining the number of clusters
 */
public class CubicClusteringCriterion implements NumberOfClustersProvider {

    @Override
    public int findNumberOfClusters(List<Paper> papers) {
        return 2;
    }
}
