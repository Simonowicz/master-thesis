package pl.edu.eiti.pw.disambiguator;

import pl.edu.eiti.pw.disambiguator.model.HiddenMarkovField;
import pl.edu.eiti.pw.disambiguator.model.HiddenMarkovFieldsModelBuilder;
import pl.edu.eiti.pw.disambiguator.model.ModelBuilder;
import pl.edu.eiti.pw.model.Paper;
import pl.edu.eiti.pw.repository.PaperRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Disambiguator implementation
 */
public class DisambiguatorImpl implements Disambiguator {

    private NumberOfClustersProvider numberOfClustersProvider;

    private PaperRepository paperRepository;

    private ModelBuilder modelBuilder;

    private String authorName;

    public DisambiguatorImpl(NumberOfClustersProvider numberOfClustersProvider, PaperRepository paperRepository, ModelBuilder modelBuilder, String authorName) {
        this.numberOfClustersProvider = numberOfClustersProvider;
        this.paperRepository = paperRepository;
        this.modelBuilder = modelBuilder;
        this.authorName = authorName;
    }

    @PostConstruct
    public void postConstruct() {
        disambiguate(authorName);
    }

    @Override
    public Map<String, List<Paper>> disambiguate(String authorName) {
        List<Paper> papers = paperRepository.findPapersByAuthor(authorName);
        int numberOfClusters = numberOfClustersProvider.findNumberOfClusters(papers);
        Map<Integer, List<HiddenMarkovField>> model = modelBuilder.initializeClusters(papers, authorName, numberOfClusters);

        return null;
    }
}
