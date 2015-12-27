package pl.edu.eiti.pw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing paper
 */
public class Paper {
    @BasicDBObjectKey("_id")
    private Long index;
    private String title;
    private List<String> authors = new ArrayList<>();
    private String publicationYear;
    private String publicationVenue;
    private List<Long> references = new ArrayList<>();
    private String abstractOfPaper;

    private boolean complete = false;

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public void addReference(Long reference) {
        this.references.add(reference);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublicationVenue() {
        return publicationVenue;
    }

    public void setPublicationVenue(String publicationVenue) {
        this.publicationVenue = publicationVenue;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public List<Long> getReferences() {
        return references;
    }

    public void setReferences(List<Long> references) {
        this.references = references;
    }

    public String getAbstractOfPaper() {
        return abstractOfPaper;
    }

    public void setAbstractOfPaper(String abstractOfPaper) {
        this.abstractOfPaper = abstractOfPaper;
    }

    public void clear() {
        index = null;
        title = null;
        authors.clear();
        publicationYear = null;
        publicationVenue = null;
        references.clear();
        abstractOfPaper = null;
        complete = false;
    }
}
