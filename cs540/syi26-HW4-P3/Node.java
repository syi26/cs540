import java.util.*;

/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details.
 * Feel free to modify the provided function signatures to fit your own implementation
 */

public class Node {
    private int type = 0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
    public ArrayList<NodeWeightPair> parents = null; //Array List that will contain the parents (including the bias node) with weights if applicable
    
    private double inputValue = 0.0;
    private double outputValue = 0.0;
    private double outputGradient = 0.0;
    private double delta = 0.0; //input gradient

    //Create a node with a specific type
    Node(int type) {
        if (type > 4 || type < 0) {
            System.out.println("Incorrect value for node type");
            System.exit(1);

        } else {
            this.type = type;
        }

        if (type == 2 || type == 4) {
            parents = new ArrayList<>();
        }
    }

    //For an input node sets the input value which will be the value of a particular attribute
    public void setInput(double inputValue) {
        if (type != 1 && type != 3) {
            this.inputValue = inputValue;
        }
    }

    /**
     * Calculate the output of a node.
     * You can get this value by using getOutput()
     */
    public void calculateOutput(ArrayList<Node> outputNodes) {
    	if (type == 2 || type == 4) {
            if (type == 2) 
                outputValue = Math.max(inputValue, 0);
            else {
                double sum = 0;
                for (int i = 0; i < outputNodes.size(); i++) sum += Math.exp(outputNodes.get(i).inputValue); 
                outputValue = Math.exp(inputValue) / sum;
            }
        }
    }

    //Gets the output value
    public double getOutput() {

        if (type == 0) {    //Input node
            return inputValue;
        } else if (type == 1 || type == 3) {    //Bias node
            return 1.00;
        } else {
            return outputValue;
        }

    }

    //Calculate the delta value of a node.
    public void calculateDelta(double targetValue, ArrayList<Node> outputNodes, int nodeIndex) {
        if (type == 2 || type == 4)  {
            double output_delta = 0.0;
            for (int i = 0; i < outputNodes.size(); i++)
                output_delta += outputNodes.get(i).parents.get(nodeIndex).weight * outputNodes.get(i).delta;
        	if (type == 2)
        		delta = inputValue < 0 ? 0 : output_delta;
        	else 
        		delta = targetValue - outputValue; 
        }
    }


    //Update the weights between parents node and current node
    public void updateWeight(double learningRate) {
        if (type == 2 || type == 4) {
            for (int i = 0; i < parents.size(); i++)
                parents.get(i).weight += learningRate * parents.get(i).node.getOutput() * delta;
        }
    }

    public void update_input_val() {
    	if (type == 2 || type == 4) {
    		inputValue = 0;
            for (int i = 0; i < parents.size(); i++) 
                inputValue += parents.get(i).node.getOutput() * parents.get(i).weight;
    	}
    }
}
