public class AlphaBetaPruning {
    private double alpha;
    private double beta;
    private boolean maxPlayer;
    private int next_move;
    private double score;
    private int num_evaluated;
    private int num_visited;
    private int depth_reached;
    private int depth_org;

    public AlphaBetaPruning() {
        this.alpha = Double.NEGATIVE_INFINITY;
        this.beta = Double.POSITIVE_INFINITY;
        this.maxPlayer = true;
        this.next_move = 0;
        this.score = 0;
        this.num_visited = 0;
        this.num_evaluated = 0;
        this.depth_reached = 0;
    }

    /**
     * This function will print out the information to the terminal,
     * as specified in the homework description.
     */
    public void printStats() {
        // TODO Add your code here
        System.out.println("Move: " + this.next_move);
        System.out.println("Value: " + this.score);
        System.out.println("Number of Nodes Visited: " + this.num_visited);
        System.out.println("Number of Nodes Evaluated: " + this.num_evaluated);
        System.out.println("Max Depth Reached: " + this.depth_reached);
        System.out.printf("Avg Effective Branching Factor: %.1f %n", 
             (double)(this.num_visited - 1) / (double)(this.num_visited - this.num_evaluated));
    }

    /**
     * This function will start the alpha-beta search
     * @param state This is the current game state
     * @param depth This is the specified search depth
     */
    public void run(GameState state, int depth) {
        // TODO Add your code here
        this.depth_org = depth;
        this.maxPlayer = state.max_player();
        this.score = alphabeta(state, depth, this.alpha, this.beta, this.maxPlayer);
        for (GameState i : state.getSuccessors()) {
            if (i.get_val() == this.score) {
                this.next_move = i.getLastMove();
                break;
            }
        }
    }

    /**
     * This method is used to implement alpha-beta pruning for both 2 players
     * @param state This is the current game state
     * @param depth Current depth of search
     * @param alpha Current Alpha value
     * @param beta Current Beta value
     * @param maxPlayer True if player is Max Player; Otherwise, false
     * @return int This is the number indicating score of the best next move
     */
    private double alphabeta(GameState state, int depth, double alpha, double beta, boolean maxPlayer) {
        // TODO Add your code here
        if (maxPlayer) {
            return max_val(state, alpha, beta, depth);
        } else {
            return min_val(state, alpha, beta, depth);
        }
    }

    private double max_val(GameState state, double alpha, double beta, int depth) {
        this.num_visited++;
        if (state.is_end() || depth == 0) {
            this.depth_reached = Math.max(this.depth_reached, this.depth_org - depth);
            this.num_evaluated++;
            return state.evaluate(false);
        }
        double value = Double.NEGATIVE_INFINITY;
        for (GameState i : state.setSuccessors()) {
            value = Math.max(value, min_val(i, alpha, beta, depth - 1));
            i.set_val(value);
            if (value >= beta) return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }
    
    private double min_val(GameState state, double alpha, double beta, int depth) {
        this.num_visited++;
        if (state.is_end() || depth == 0) {
            this.depth_reached = Math.max(this.depth_reached, this.depth_org - depth);
            this.num_evaluated++;
            return state.evaluate(true);
        }
        double value = Double.POSITIVE_INFINITY;
        for (GameState i : state.setSuccessors()) {
            value = Math.min(value, max_val(i, alpha, beta, depth - 1));
            i.set_val(value);
            if (value <= alpha) return value;
            beta = Math.min(beta, value);
        }
        return value;
    }
}
