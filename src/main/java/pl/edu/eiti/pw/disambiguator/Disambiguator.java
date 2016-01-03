package pl.edu.eiti.pw.disambiguator;

import pl.edu.eiti.pw.model.Paper;

import java.util.List;
import java.util.Map;

/**
 * Disambiguator interface
 */
public interface Disambiguator {

    Map<String, List<Paper>> disambiguate(String authorName);
}
