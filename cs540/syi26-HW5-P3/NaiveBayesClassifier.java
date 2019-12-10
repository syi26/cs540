import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import static java.util.stream.Collectors.toList;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {
    private Map<String,Integer> num_pos_words;
    private Map<String,Integer> num_neg_words;
    private Map<Label,Double> conditional_table;
    private Map<Label, Integer> ret_map;
    private List<String> distinct_dictionary;
    private double pos_prior;
    private double neg_prior;
    private int num_vocabulary;
    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        conditional_table = null;
        Map<Label, Integer> num_doc = getDocumentsCountPerLabel(trainData);
        Map<Label, Integer> num_word = getWordsCountPerLabel(trainData);
        num_vocabulary = v;
        List<String> dictionary = new ArrayList<String>();
        num_pos_words = new HashMap<String,Integer>();
        num_neg_words = new HashMap<String,Integer>();
        for (int i = 0; i < trainData.size(); i++) {
            String temp_str;
            if (trainData.get(i).label == Label.POSITIVE) {
                for (int j = 0; j < trainData.get(i).words.size(); j++) {
                    temp_str = trainData.get(i).words.get(j); 
                    dictionary.add(temp_str);
                    num_pos_words.put(temp_str, num_pos_words.getOrDefault(temp_str, 0) + 1);
                }
            } else if (trainData.get(i).label == Label.NEGATIVE) {
                for(int j = 0; j < trainData.get(i).words.size(); j++) {
                    temp_str = trainData.get(i).words.get(j);
                    dictionary.add(temp_str);
                    num_neg_words.put(temp_str, num_neg_words.getOrDefault(temp_str, 0) + 1);
                }
            }
        }
        distinct_dictionary = dictionary.stream().distinct().collect(toList());
        pos_prior = trainData.size() == 0 ? 0.0 :
            (double) num_doc.getOrDefault(Label.POSITIVE, 0) / trainData.size();
        neg_prior = trainData.size() == 0 ? 0.0 : 
            (double) num_doc.getOrDefault(Label.NEGATIVE, 0) / trainData.size();
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
        ret_map = new HashMap<>();
        int num_pos = 0;
        int num_neg = 0;
        for (int i = 0; i < trainData.size(); i++) {
            if (trainData.get(i).label == Label.POSITIVE)
                num_pos += trainData.get(i).words.size();
            else 
                num_neg += trainData.get(i).words.size();
        }
        ret_map.put(Label.POSITIVE, num_pos);
        ret_map.put(Label.NEGATIVE, num_neg);
        return ret_map;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        ret_map = new HashMap<>();
        for (int i = 0; i < trainData.size(); i++) {
            if (trainData.get(i).label == Label.POSITIVE)
                ret_map.put(Label.POSITIVE, ret_map.getOrDefault(Label.POSITIVE, 0) + 1);
            else
                ret_map.put(Label.NEGATIVE, ret_map.getOrDefault(Label.NEGATIVE, 0) + 1);
        }
        return ret_map;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        return label == Label.POSITIVE ? pos_prior : neg_prior;
    }
    
    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        Map<String, Integer> r_map = label == Label.POSITIVE ? num_pos_words : num_neg_words;
        double numerator = r_map.getOrDefault(word, 0) + 1;
        double denominator = 0.0;
        if (conditional_table != null && conditional_table.containsKey(label))
            denominator = conditional_table.get(label);
        else {
            for (String str : distinct_dictionary) denominator += r_map.getOrDefault(str, 0);
            denominator += num_vocabulary;
            if (conditional_table == null) conditional_table = new HashMap<Label, Double>();
            conditional_table.put(label, denominator);
        }
        return denominator == 0.0 ? 0.0 : numerator / denominator;
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        double pos_log = pos_prior == 0.0 ? 0.0 : Math.log(pos_prior);
        double neg_log = neg_prior == 0.0 ? 0.0 : Math.log(neg_prior);
        for (int i = 0; i < words.size(); i++) {
            double pos_conditional = p_w_given_l(words.get(i), Label.POSITIVE);
            double neg_conditional = p_w_given_l(words.get(i), Label.NEGATIVE);
            pos_log += pos_conditional == 0 ? 0.0 : Math.log(pos_conditional);
            neg_log += neg_conditional == 0 ? 0.0 : Math.log(neg_conditional);
        }
        ClassifyResult result = new ClassifyResult();
        result.label = pos_log < neg_log ? Label.NEGATIVE : Label.POSITIVE;
        Map<Label, Double> log_map = new HashMap<Label, Double>();
        log_map.put(Label.POSITIVE, pos_log);
        log_map.put(Label.NEGATIVE, neg_log);
        result.logProbPerLabel = log_map;
        return result;
    }
}
