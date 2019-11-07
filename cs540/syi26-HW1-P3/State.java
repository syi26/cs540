import java.util.ArrayList;

/**
 * A state in the search represented by the (x,y) coordinates of the square and
 * the parent. In other words a (square,parent) pair where square is a Square,
 * parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

	private Square square;
	private State parent;

	// Maintain the gValue (the distance from start)
	// You may not need it for the BFS but you will
	// definitely need it for AStar
	private int gValue;

	// States are nodes in the search tree, therefore each has a depth.
	private int depth;

	/**
	 * @param square
	 *            current square
	 * @param parent
	 *            parent state
	 * @param gValue
	 *            total distance from start
	 */
	public State(Square square, State parent, int gValue, int depth) {
		this.square = square;
		this.parent = parent;
		this.gValue = gValue;
		this.depth = depth;
	}

	/**
	 * @param visited
	 *            explored[i][j] is true if (i,j) is already explored
	 * @param maze
	 *            initial maze to get find the neighbors
	 * @return all the successors of the current state
	 */
	public ArrayList<State> getSuccessors(boolean[][] explored, Maze maze) {
		// FILL THIS method

        // Arraylist to store states
        ArrayList<State> successors = new ArrayList<State>();
        
        // The x and y coordinates of the state which calls this method
        int x_cor = this.getX();
        int y_cor = this.getY();

        // Set the state which calls this method to explored
        explored[x_cor][y_cor] = true;

        // Add the left state to successors if it satisfies the situation   
        if (y_cor != 0) {
            Square left_sq = new Square(x_cor, y_cor - 1);
            char left_ch = maze.getSquareValue(x_cor, y_cor - 1);
            if (left_ch != '%' && !explored[x_cor][y_cor - 1]) {
                State left_st = new State(left_sq, this, this.gValue + 1, this.depth + 1);
                successors.add(left_st);
            }
        }

        // Add the down state to successors if it satisfies the situation
        if (x_cor != (maze.getNoOfRows() - 1)) {
            Square down_sq = new Square(x_cor + 1, y_cor);
            char down_ch = maze.getSquareValue(x_cor + 1, y_cor);
            if (down_ch != '%' && !explored[x_cor + 1][y_cor]) {
                State down_st = new State(down_sq, this, this.gValue + 1, this.depth + 1);
                successors.add(down_st);
            }
        }

        // Add  the right state to successors if it satisfies the situation
        if (y_cor != (maze.getNoOfCols() - 1)) {
            Square right_sq = new Square(x_cor, y_cor + 1);
            char right_ch = maze.getSquareValue(x_cor, y_cor + 1);
            if (right_ch != '%' && !explored[x_cor][y_cor + 1]) {
                State right_st = new State(right_sq, this, this.gValue + 1, this.depth + 1);
                successors.add(right_st);
            }
        }

        // Add the right state to successors if it satisfies the situation 
        if (x_cor != 0) {
            Square up_sq = new Square(x_cor - 1, y_cor);
            char up_ch = maze.getSquareValue(x_cor - 1, y_cor);
            if (up_ch != '%' && !explored[x_cor - 1][y_cor]) {
                State up_st = new State(up_sq, this, this.gValue + 1, this.depth + 1);
                successors.add(up_st);
            }
        }

        return successors;

		// TODO check all four neighbors in left, down, right, up order
		// TODO remember that each successor's depth and gValue are
		// +1 of this object.
	}

	/**
	 * @return x coordinate of the current state
	 */
	public int getX() {
		return square.X;
	}

	/**
	 * @return y coordinate of the current state
	 */
	public int getY() {
		return square.Y;
	}

	/**
	 * @param maze initial maze
	 * @return true is the current state is a goal state
	 */
	public boolean isGoal(Maze maze) {
		if (square.X == maze.getGoalSquare().X
				&& square.Y == maze.getGoalSquare().Y)
			return true;

		return false;
	}

	/**
	 * @return the current state's square representation
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * @return parent of the current state
	 */
	public State getParent() {
		return parent;
	}

	/**
	 * You may not need g() value in the BFS but you will need it in A-star
	 * search.
	 * 
	 * @return g() value of the current state
	 */
	public int getGValue() {
		return gValue;
	}

	/**
	 * @return depth of the state (node)
	 */
	public int getDepth() {
		return depth;
	}
}
