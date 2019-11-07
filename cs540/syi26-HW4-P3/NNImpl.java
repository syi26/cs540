import java.util.*;

/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 */

public class NNImpl {
    private ArrayList<Node> inputNodes; //list of the output layer nodes.
    private ArrayList<Node> hiddenNodes;    //list of the hidden layer nodes
    private ArrayList<Node> outputNodes;    // list of the output layer nodes

    private ArrayList<Instance> trainingSet;    //the training set

    private double learningRate;    // variable to store the learning rate
    private int maxEpoch;   // variable to store the maximum number of epochs
    private Random random;  // random number generator to shuffle the training set

    /**
     * This constructor creates the nodes necessary for the neural network
     * Also connects the nodes of different layers
     * After calling the constructor the last node of both inputNodes and
     * hiddenNodes will be bias nodes.
     */

    NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
        this.trainingSet = trainingSet;
        this.learningRate = learningRate;
        this.maxEpoch = maxEpoch;
        this.random = random;

        //input layer nodes
        inputNodes = new ArrayList<>();
        int inputNodeCount = trainingSet.get(0).attributes.size();
        int outputNodeCount = trainingSet.get(0).classValues.size();
        for (int i = 0; i < inputNodeCount; i++) {
            Node node = new Node(0);
            inputNodes.add(node);
        }

        //bias node from input layer to hidden
        Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);

        //hidden layer nodes
        hiddenNodes = new ArrayList<>();
        for (int i = 0; i < hiddenNodeCount; i++) {
            Node node = new Node(2);
            //Connecting hidden layer nodes with input layer nodes
            for (int j = 0; j < inputNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
                node.parents.add(nwp);
            }
            hiddenNodes.add(node);
        }

        //bias node from hidden layer to output
        Node biasToOutput = new Node(3);
        hiddenNodes.add(biasToOutput);

        //Output node layer
        outputNodes = new ArrayList<>();
        for (int i = 0; i < outputNodeCount; i++) {
            Node node = new Node(4);
            //Connecting output layer nodes with hidden layer nodes
            for (int j = 0; j < hiddenNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);                
            	node.parents.add(nwp);
            }
            outputNodes.add(node);
        }
    }

    /**
     * Get the prediction from the neural network for a single instance
     * Return the idx with highest output values. For example if the outputs
     * of the outputNodes are [0.1, 0.5, 0.2], it should return 1.
     * The parameter is a single instance
     */

    public int predict(Instance instance) {
    	update_vals(instance);
    	int prediction = 0;
    	double max_val = Double.NEGATIVE_INFINITY;
    	for (int i = 0; i < outputNodes.size(); i++) {
    		if (outputNodes.get(i).getOutput() > max_val) {
    			max_val = outputNodes.get(i).getOutput();
    			prediction = i;
    		}
    	}
        return prediction;
    }


    /**
     * Train the neural networks with the given parameters
     * <p>
     * The parameters are stored as attributes of this class
     */

    public void train() {
    	for(int i = 0; i < maxEpoch; i++){
	    	double loss = 0;
	    	Collections.shuffle(trainingSet,random);
	    	for (int j = 0; j < trainingSet.size(); j++) {
	    		update_vals(trainingSet.get(j));
	    		for (int k = 0; k < outputNodes.size(); k++)
	    			outputNodes.get(k).calculateDelta(trainingSet.get(j).classValues.get(k), outputNodes, k);
                for (int k = 0; k < hiddenNodes.size(); k++)
	    			hiddenNodes.get(k).calculateDelta(0, outputNodes, k);
	    		for (int k = 0; k < outputNodes.size(); k++)
	    			outputNodes.get(k).updateWeight(learningRate);
	    		for (int k = 0; k < hiddenNodes.size(); k++)
	    			hiddenNodes.get(k).updateWeight(learningRate);
	        }
	    	for (int j = 0; j < trainingSet.size(); j++)
	    		loss += loss(trainingSet.get(j));
	    	loss /= trainingSet.size();
	    	System.out.print("Epoch: " + i + ", Loss: ");
	    	System.out.format("%.3e", loss);
	    	System.out.println();
    	}
    }

    /**
     * Calculate the cross entropy loss from the neural network for
     * a single instance.
     * The parameter is a single instance
     */
    private double loss(Instance instance) {
    	update_vals(instance);
    	double cross_entropy = 0;
    	for(int i = 0 ; i < outputNodes.size(); i++)
    		cross_entropy -= instance.classValues.get(i) * Math.log(outputNodes.get(i).getOutput());
        return cross_entropy;
    }
    
    private void update_vals(Instance instance) {
    	for (int i = 0; i < inputNodes.size() - 1; i++) 
            inputNodes.get(i).setInput(instance.attributes.get(i));
    	for (int i = 0; i < hiddenNodes.size(); i++) {
    		hiddenNodes.get(i).update_input_val();
    		hiddenNodes.get(i).calculateOutput(null);
    	}
    	for (int i = 0; i < outputNodes.size(); i++)
    		outputNodes.get(i).update_input_val();
        for (int i = 0; i < outputNodes.size(); i++) 
            outputNodes.get(i).calculateOutput(outputNodes);
    }
}
