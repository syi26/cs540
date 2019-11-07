import java.util.ArrayList;
import java.util.List;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 */
public class DecisionTreeImpl {
	public DecTreeNode root;
	public List<List<Integer>> trainData;
	public int maxPerLeaf;
	public int maxDepth;
	public int numAttr;
    public int best_attr;
    public int best_threshold;
    public double max_info_gain;

	// Build a decision tree given a training set
	DecisionTreeImpl(List<List<Integer>> trainDataSet, int mPerLeaf, int mDepth) {
		this.trainData = trainDataSet;
		this.maxPerLeaf = Math.max(mPerLeaf, 1);
		this.maxDepth = mDepth;
		if (this.trainData.size() > 0) this.numAttr = trainDataSet.get(0).size() - 1;
		this.root = buildTree(this.trainData, 1);
	}
	
	private DecTreeNode buildTree(List<List<Integer>> trainData, int depth) {
        int class_label;
        double label_sum = 0.0;
        for (int i = 0; i < trainData.size(); i++) {
            if (trainData.get(i).get(this.numAttr) == 1) label_sum++;
        }
        class_label = (label_sum / trainData.size()) >= 0.5 ? 1 : 0;
		if (trainData.size() > this.maxPerLeaf && depth <= this.maxDepth && this.numAttr > 0) {
            if (label_sum == 0 || label_sum == trainData.size()) return new DecTreeNode(class_label, 0, 0);
			// get best attr gain TODO might need new methods
			get_attr_threshold(trainData);
			//if (this.best_attr != -1) {
				DecTreeNode curr = new DecTreeNode(-1, this.best_attr, this.best_threshold);
				List<List<Integer>> left = new ArrayList<List<Integer>>();
				List<List<Integer>> right = new ArrayList<List<Integer>>();
				for (int i = 0; i < trainData.size(); i++) {
					if (trainData.get(i).get(this.best_attr) <= this.best_threshold) 
                        left.add(trainData.get(i));
					else 
                        right.add(trainData.get(i));
				}
				
				curr.left = buildTree(left, depth + 1);
				curr.right = buildTree(right, depth + 1);
				return curr;
			}
		//}
        return new DecTreeNode(class_label, 0, 0);
	}
	
	private void get_attr_threshold(List<List<Integer>> train) {
        double left_entropy;
        double right_entropy;
        double weighted_entropy;
        double min_entropy = 1000000;
        List<List<Integer>> left = new ArrayList<List<Integer>>();
        List<List<Integer>> right = new ArrayList<List<Integer>>();
        for (int i = 0; i < 9; i++) {
            for (int j = 1; j < 10; j++) {
                left.clear();
                right.clear();
                left_entropy = 0.0;
                right_entropy = 0.0;
                weighted_entropy = 0;
                for (int k = 0; k < train.size(); k++) {
                    if (train.get(k).get(i) <= j) 
                        left.add(train.get(k));
                    else
                        right.add(train.get(k));
                }
                left_entropy = calc_entropy(left);
                right_entropy = calc_entropy(right);
                weighted_entropy = (double)left.size() / train.size() * left_entropy + (double)right.size() / train.size() * right_entropy;
                if (weighted_entropy < min_entropy) {
                    min_entropy = weighted_entropy;
                    this.best_attr = i;
                    this.best_threshold = j;
                }
            }
        }
        this.max_info_gain = calc_entropy(train) - min_entropy;
	}

    public double calc_entropy(List<List<Integer>> subset) {
    
        int ones = 0;
        int total = subset.size();
        for (int i = 0; i < total; i++) {
            if (subset.get(i).get(numAttr) == 1) ones++;
        }
        double one_prob = 1.0 * ones / total;
        double zero_prob = 1 - one_prob;
        return -one_prob * log2(one_prob) - zero_prob * log2(zero_prob);
    }    
	
	public double log2(double x) {
        if (x == 0) return 0.0;
        return Math.log(x) / Math.log(2);
    }
	
	public int classify(List<Integer> instance) {
		DecTreeNode currNode = this.root;
		while(!currNode.isLeaf()) {
			currNode = instance.get(currNode.attribute) <= currNode.threshold ? currNode.left : currNode.right;
		}
		return currNode.classLabel;
	}
	
	// Print the decision tree in the specified format
	public void printTree() {
		printTreeNode("", this.root);
	}

	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + "X_" + node.attribute;
		System.out.print(printStr + " <= " + String.format("%d", node.threshold));
		if(node.left.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.left.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%d", node.threshold));
		if(node.right.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.right.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
	}
	
	public double printTest(List<List<Integer>> testDataSet) {
		int numEqual = 0;
		int numTotal = 0;
		for (int i = 0; i < testDataSet.size(); i ++)
		{
			int prediction = classify(testDataSet.get(i));
			int groundTruth = testDataSet.get(i).get(testDataSet.get(i).size() - 1);
			System.out.println(prediction);
			if (groundTruth == prediction) {
				numEqual++;
			}
			numTotal++;
		}
		double accuracy = numEqual*100.0 / (double)numTotal;
		System.out.println(String.format("%.2f", accuracy) + "%");
		return accuracy;
	}
}
