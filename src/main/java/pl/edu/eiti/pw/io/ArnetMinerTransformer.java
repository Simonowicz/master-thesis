package pl.edu.eiti.pw.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.eiti.pw.model.Paper;
import pl.edu.eiti.pw.repository.PaperRepository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Transformer implementation for Arnet Miner input file
 */
public class ArnetMinerTransformer implements InputTransformer {

    private static final Logger logger = LoggerFactory.getLogger(ArnetMinerTransformer.class);
    private final String filePath;
    private final Integer documentLimit;
    private final PaperRepository paperRepository;

    public ArnetMinerTransformer(String filePath, Integer documentLimit, PaperRepository paperRepository) {
        this.filePath = filePath;
        this.documentLimit = documentLimit;
        this.paperRepository = paperRepository;
    }

    @PostConstruct
    public void postConstruct() {
        try {
            readInputAndSave();
        } catch (Exception e) {
            logger.error("Encountered exception when reading input: ", e);
        }
    }

    @Override
    public void readInputAndSave() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        int documentCount = 0;

        String line = bufferedReader.readLine();
        Paper paper = new Paper();
        while (line != null && (documentLimit == null || documentCount < documentLimit)) {
            parseLineAndAddToPaper(line, paper);
            if (paper.isComplete()) {
                paperRepository.insert(paper);
                documentCount++;
                paper.clear();
            }
            line = bufferedReader.readLine();
        }
    }

    private void parseLineAndAddToPaper(String line, Paper paper) {
        String identifier = line.substring(0, 2);
        switch (identifier) {
            case "#*" : paper.setTitle(line.substring(2)); break;
            case "#@" : parseAndAddAuthors(line.substring(2), paper); break;
            case "#t" : paper.setPublicationYear(line.substring(2)); break;
            case "#c" : paper.setPublicationVenue(line.substring(2)); break;
            case "#i" : paper.setIndex(Long.parseLong(line.replace("#index", ""))); break;
            case "#%" : parseAndAddReference(line.substring(2), paper); break;
            case "#!" : paper.setAbstractOfPaper(line.substring(2)); break;
            default: paper.setComplete(true);
        }
    }

    private void parseAndAddAuthors(String line, Paper paper) {
        String [] authorsArray = line.split(",");
        for (String author : authorsArray) {
            paper.addAuthor(author);
        }
    }

    private void parseAndAddReference(String line, Paper paper) {
        try {
            Long reference = Long.parseLong(line);
            paper.addReference(reference);
        } catch (NumberFormatException e) {
            logger.debug("Could not add reference to paper: " + paper.getIndex());
        }
    }
}
