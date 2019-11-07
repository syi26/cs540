import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// ...
        
        // PriorityQueue that keeps a list of frontiers
		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
        // ArrayList that keeps a list of successors 
        ArrayList<State> successors = new ArrayList<State>();
        // Initialize the player state
        State init_st = new State(maze.getPlayerSquare(), null, 0, 0);
        // The x and y coordinates of the goal state
        double dest_xcor = maze.getGoalSquare().X;
        double dest_ycor = maze.getGoalSquare().Y;
        // The h value of the player state
        double init_hval = 
            Math.sqrt(Math.pow((maze.getGoalSquare().X - maze.getPlayerSquare().X), 2) 
                    + Math.pow((maze.getGoalSquare().Y - maze.getPlayerSquare().Y), 2));
        // The fvalue of the player state
        double init_fval = init_hval + init_st.getGValue();
        // The pair of the player state
        StateFValuePair init_st_pair = new StateFValuePair(init_st, init_fval);
        // Add the initial pair to the frontier
        frontier.add(init_st_pair);
        // Mark the initial state as explored
        explored[init_st.getX()][init_st.getY()] = true;
		// TODO initialize the root state and add
		// to frontier list
		// ...
        
        // Keeping track of the total cose, number of frontiers,
        // number of expanded nodes and max number of frontiers
        int total_cost = 1;
        int num_frtr = 1;
        int max_num_frtr = 0;
        int num_exp = 1;
        
		while (!frontier.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use frontier.poll() to extract the minimum stateFValuePair.
			// use frontier.add(...) to add stateFValue pairs
            
            // Poll the pair that has the highest priority
            StateFValuePair curr_pair = frontier.poll();
            // Decrease the number of frontiers
            num_frtr--;
            // Get the state and f value from the pair
            State curr_st = curr_pair.getState();
            double curr_fval = curr_pair.getFValue();

            // Get the successorss of the state 
            successors = curr_st.getSuccessors(explored, maze);

            // Traverse through all successors
            for (State i : successors) {
                // Deal with the situation that a solution is found
                if (i.getX() == dest_xcor && i.getY() == dest_ycor) {
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

                // Get the f value of the state being dealt with
                double i_fval = i.getGValue() + 
                    Math.sqrt(Math.pow(i.getX() - dest_xcor, 2)
                    + Math.pow(i.getY() - dest_ycor, 2));
                // Create a pair using the state and its f value
                StateFValuePair i_pair = new StateFValuePair(i, i_fval);
                // Set a temporary pair for repetition checking
                StateFValuePair temp = new StateFValuePair(null, 0);
                boolean flag1 = false;
                boolean flag2 = false;
                // Check if a state already exists in a pair 
                for (StateFValuePair j : frontier) {
                    if (j.getState().getX() == i.getX() 
                        && j.getState().getY() == i.getY()) {
                        flag1 = true;
                        // Check if a better pair is found
                        if (j.compareTo(i_pair) == 1) {
                            temp = j;
                            flag2 = true;
                            break;
                        } 
                    }
                }
                // Remove the pair in frontier if a better one found
                if (flag1 && flag2) {
                    frontier.remove(temp);
                    frontier.add(i_pair);
                } else if (flag1 && !flag2) {
                    continue;
                } else if (!flag1) {
                    frontier.add(i_pair);
                    num_frtr++;
                    if (num_frtr > max_num_frtr) max_num_frtr = num_frtr;
                }
            }
        }
		
        return false;
		// TODO return false if no solution
	}

}
