package pl.edu.eiti.pw.disambiguator.model;

import pl.edu.eiti.pw.model.Paper;

import java.util.*;

/**
 * Class that generates initial HMF model
 */
public class HiddenMarkovFieldsModelBuilder implements ModelBuilder {

    @Override
    public Map<Integer, List<HiddenMarkovField>> initializeClusters(List<Paper> papers, String nameDisambiguated, int numberOfClusters) {
        Map<Integer, List<HiddenMarkovField>> model = new HashMap<>();
        List<HiddenMarkovField> markovFields = transformPapersIntoHiddenMarkovFields(papers, nameDisambiguated);
        populateModelWithClustersFromRelatedFields(model, markovFields);
        while (numberOfClusters != model.size()) {
            trimModel(model, numberOfClusters);
        }
        return model;
    }

    private List<HiddenMarkovField> transformPapersIntoHiddenMarkovFields(List<Paper> papers, String nameDisambiguated) {
        List<HiddenMarkovField> markovFields = new ArrayList<>();

        for (Paper paper : papers) {
            HiddenMarkovField markovField = addOrFindMarkovFieldForPaper(markovFields, paper);
            findAndAddRelations(markovField, markovFields, papers, nameDisambiguated);
            for (Map.Entry<Relation, List<HiddenMarkovField>> mapEntry : markovField.getRelations().entrySet()) {
                mapEntry.getValue().stream().filter(relatedField -> !markovFields.contains(relatedField)).forEach(markovFields::add);
            }
        }

        return markovFields;
    }

    private void populateModelWithClustersFromRelatedFields(Map<Integer, List<HiddenMarkovField>> model, List<HiddenMarkovField> markovFields) {
        int i = 1;
        while (!markovFields.isEmpty()) {
            List<HiddenMarkovField> relatedFields = new ArrayList<>();
            findRelatedFields(markovFields.get(0), relatedFields);
            model.put(i++, relatedFields);
            markovFields.removeAll(relatedFields);
        }
    }

    private void trimModel(Map<Integer, List<HiddenMarkovField>> model, int numberOfClusters) {
        if (model.size() > numberOfClusters) {
            assignSmallestClusterToOtherRandomCluster(model);
        } else {
            createSeparateClusterFromRandomField(model);
        }
    }

    private void assignSmallestClusterToOtherRandomCluster(Map<Integer, List<HiddenMarkovField>> model) {
        Random random = new Random();
        int smallestClusterIndex = findSmallestClusterIndex(model);
        int randomClusterIndex;
        do {
            randomClusterIndex = random.nextInt(model.size());
        } while (randomClusterIndex == smallestClusterIndex);

        Iterator<Map.Entry<Integer, List<HiddenMarkovField>>> smallestCluster = model.entrySet().iterator();
        for (int i = 0; i < smallestClusterIndex; i++) {
            smallestCluster.next();
        }
        Iterator<Map.Entry<Integer, List<HiddenMarkovField>>> randomCluster = model.entrySet().iterator();
        for (int i = 0; i < randomClusterIndex; i++) {
            randomCluster.next();
        }
        randomCluster.next().getValue().addAll(smallestCluster.next().getValue());
        smallestCluster.remove();
    }

    private int findSmallestClusterIndex(Map<Integer, List<HiddenMarkovField>> model) {
        int smallestClusterCount = Integer.MAX_VALUE;
        int smallestClusterIndex = 0;
        int i = 0;
        for (Map.Entry<Integer, List<HiddenMarkovField>> entry : model.entrySet()) {
            if (smallestClusterCount == 1) {
                break;
            }
            if (smallestClusterCount > entry.getValue().size()) {
                smallestClusterCount = entry.getValue().size();
                smallestClusterIndex = i;
            }
            i++;
        }

        return smallestClusterIndex;
    }

    private void createSeparateClusterFromRandomField(Map<Integer, List<HiddenMarkovField>> model) {
        Random random = new Random();
        Map.Entry<Integer, List<HiddenMarkovField>> clusterEntry;
        do {
            int clusterIndex = random.nextInt(model.size());
            Iterator<Map.Entry<Integer, List<HiddenMarkovField>>> iterator = model.entrySet().iterator();
            for (int i = 0; i < clusterIndex; i++) {
                iterator.next();
            }
            clusterEntry = iterator.next();
        } while (clusterEntry.getValue().size() <= 1);
        int fieldIndex = random.nextInt(clusterEntry.getValue().size());
        model.put(model.size() + 1, Collections.singletonList(clusterEntry.getValue().get(fieldIndex)));
        clusterEntry.getValue().remove(fieldIndex);
    }

    private void findRelatedFields(HiddenMarkovField field, List<HiddenMarkovField> relatedFields) {
        if (!relatedFields.contains(field)) {
            relatedFields.add(field);
            for (Map.Entry<Relation, List<HiddenMarkovField>> mapEntry : field.getRelations().entrySet()) {
                for (HiddenMarkovField relatedField : mapEntry.getValue()) {
                    findRelatedFields(relatedField, relatedFields);
                }
            }
        }
    }

    private HiddenMarkovField addOrFindMarkovFieldForPaper(List<HiddenMarkovField> markovFields, Paper paper) {
        HiddenMarkovField markovField = new HiddenMarkovField();
        markovField.setPaper(paper);
        if (!markovFields.contains(markovField)) {
            markovFields.add(markovField);
        } else {
            markovField = findPreviouslyCreatedField(markovField, markovFields);
        }
        return markovField;
    }


    private HiddenMarkovField findPreviouslyCreatedField(HiddenMarkovField field, List<HiddenMarkovField> fields) {
        return fields.stream().filter(mmf -> mmf.equals(field)).findFirst().get();
    }

    private void findAndAddRelations(HiddenMarkovField field, List<HiddenMarkovField> markovFields, List<Paper> papers, String nameDisambiguated) {
        papers.stream().filter(paper -> !paper.equals(field.getPaper())).forEach(paper -> {
            if (coAuthorshipPresent(field.getPaper(), paper, nameDisambiguated)) {
                createOrFindMarkovFieldForPaperAndSetAsRelatedToField(paper, field, Relation.CO_AUTHOR, markovFields);
            }
            if (coPubVenuePresent(field.getPaper(), paper)) {
                createOrFindMarkovFieldForPaperAndSetAsRelatedToField(paper, field, Relation.CO_PUB_VENUE, markovFields);
            }
            if (citationPresent(field.getPaper(), paper)) {
                createOrFindMarkovFieldForPaperAndSetAsRelatedToField(paper, field, Relation.CITATION, markovFields);
            }
            if (userConstraintPresent(field.getPaper(), paper)) {
                createOrFindMarkovFieldForPaperAndSetAsRelatedToField(paper, field, Relation.USER_CONSTAINT, markovFields);
            }
            if (enhancedCoAuthorshipPresent(field.getPaper(), paper, nameDisambiguated)) {
                createOrFindMarkovFieldForPaperAndSetAsRelatedToField(paper, field, Relation.ENHANCED_CO_AUTHOR, markovFields);
            }
        });
    }

    private boolean coAuthorshipPresent(Paper fieldPaper, Paper paper, String nameDisambiguated) {
        for (String author : fieldPaper.getAuthors()) {
            if (!author.equals(nameDisambiguated)) {
                if (paper.getAuthors().contains(author)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean coPubVenuePresent(Paper fieldPaper, Paper paper) {
        return fieldPaper.getPublicationVenue().equals(paper.getPublicationVenue());
    }

    private boolean citationPresent(Paper fieldPaper, Paper paper) {
        return cites(fieldPaper, paper) || cites(paper, fieldPaper);
    }

    private boolean cites(Paper paper, Paper citedPaper) {
        return paper.getReferences().contains(citedPaper.getIndex());
    }

    private boolean userConstraintPresent(Paper fieldPaper, Paper paper) {
        //@TODO: implement
        return false;
    }

    private boolean enhancedCoAuthorshipPresent(Paper fieldPaper, Paper paper, String nameDisambiguated) {
        //@TODO: implement
        return false;
    }

    private void createOrFindMarkovFieldForPaperAndSetAsRelatedToField(Paper paper, HiddenMarkovField field, Relation relation, List<HiddenMarkovField> markovFields) {
        HiddenMarkovField relatedMarkovField = addOrFindMarkovFieldForPaper(markovFields, paper);
        field.getRelations().get(relation).add(relatedMarkovField);
    }
}
