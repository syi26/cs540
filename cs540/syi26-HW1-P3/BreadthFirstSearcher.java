import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public BreadthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main breadth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// ...

		// Queue implementing the Frontier 
		LinkedList<State> queue = new LinkedList<State>();
        // Arraylist that stores the successors 
        ArrayList<State> successors = new ArrayList<State>();
        // Initializing the player state
        State init_st = new State(maze.getPlayerSquare(), null, 0, 0);
        // Add the root node to the Frontier
        queue.add(init_st);

        // Keeping track of the total cost, number of frontiers, 
        // number of expanded nodes and max number of frontiers
        int total_cost = 1;
        int num_frtr = 1;
        int num_exp = 0;
        int max_num_frtr = 0;

		while (!queue.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use queue.pop() to pop the queue.
			// use queue.add(...) to add elements to queue
            
            // Pop the state at the leftmost index in the queue
            State curr = queue.pop();
            // Decrease the number of frontiers
            --num_frtr;
            // Get the successors of the popped state
            successors = curr.getSuccessors(explored, maze);
            
            // Traverse through all successors
            for (State i : successors) {
                explored[i.getX()][i.getY()] = true;
                // Check if it is already in the queue at this moment
                if (queue.contains(i)) {
                    continue;
                } else {
                    // If it's not already in the queue, add it to Frontier
                    queue.add(i);
                    // Increase the number of frontiers
                    ++num_frtr;
                    // Update the max number of frontiers if needed
                    if (num_frtr > max_num_frtr) max_num_frtr = num_frtr;
                    // Deal with the situation that a solution is found
                    if (i.getX() == maze.getGoalSquare().X 
                            && i.getY() == maze.getGoalSquare().Y) {
                        for (int j = 0; j < maze.getNoOfRows(); j++) {
                            for (int k = 0; k < maze.getNoOfCols(); k++) {
                                if (explored[j][k]) num_exp++;
                            }
                        }
                        super.maxSizeOfFrontier = max_num_frtr;
                        super.maxDepthSearched = i.getDepth();
                        super.noOfNodesExpanded = num_exp;
                        while (i.getParent() != init_st) {
                            i = i.getParent();
                            total_cost++;
                            maze.setOneSquare(i.getSquare(), '.');
                        }
                        super.cost = total_cost;
                        return true;
                    }
                }
            }
		}
        // When no solution found, return false
		return false;
	}
}
