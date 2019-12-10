import java.util.List;
import java.util.ArrayList;

public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
        if (k < 2 || k > trainData.size()) return 0.0;
        double acc = 0.0;
        for (int i = 0; i < k; i++) {
            List<Instance> training_set = new ArrayList<Instance>();
            List<Instance> testing_set = new ArrayList<Instance>();
            for (int j = 0; j < trainData.size(); j++) {
                if (j / (trainData.size() / k) == i)
                    testing_set.add(trainData.get(j));
                else
                    training_set.add(trainData.get(j));
            }
            clf.train(training_set, v);
            int num_hit = 0;
            for (int j = 0; j < testing_set.size(); j++)
                if (clf.classify(testing_set.get(j).words).label 
                        == testing_set.get(j).label) num_hit++;
            acc += (double)num_hit / testing_set.size() / k;
        }
        return acc;
    }
}
