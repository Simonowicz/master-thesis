package pl.edu.eiti.pw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.eiti.pw.model.Paper;
import pl.edu.eiti.pw.repository.PaperRepository;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Integration test of paper repository
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MasterThesisApplication.class)
public class PaperRepositoryIT {

    @Autowired
    private PaperRepository paperRepository;

    @Test
    public void paperRepositoryReadsData() {
        List<Paper> relevantPapers = paperRepository.findPapersByAuthor("Dittrich");
        assertTrue(relevantPapers.size() > 0);
    }
}
