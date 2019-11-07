import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    private int size;            // The number of stones
    private boolean[] stones;    // Game state: true for available stones, false for taken ones
    private int lastMove;        // The last move
    private boolean max_player;  // True for Max Player's turn, false for Min Player's turn
    private double val;
    private List<GameState> succs;

    /**
     * Class constructor specifying the number of stones.
     */
    public GameState(int size, int taken) {

        this.size = size;

        //  For convenience, we use 1-based index, and set 0 to be unavailable
        this.stones = new boolean[this.size + 1];
        this.stones[0] = false;

        // Set default state of stones to available
        for (int i = 1; i <= this.size; ++i) {
            this.stones[i] = true;
        }

        // Set the last move be -1
        this.lastMove = -1;
        if (taken % 2 == 1) {
            this.max_player = false;
        } else {
            this.max_player = true;
        }
    }

    /**
     * Copy constructor
     */
    public GameState(GameState other) {
        this.size = other.size;
        this.stones = Arrays.copyOf(other.stones, other.stones.length);
        this.lastMove = other.lastMove;
    }


    /**
     * This method is used to compute a list of legal moves
     *
     * @return This is the list of state's moves
     */
    public List<Integer> getMoves() {
        List<Integer> legal_moves;
        Integer temp[];
        int count = 0;
        int idx = 0;
        if (this.lastMove == -1) {
            for (int i = 1; i < (this.size - 1) / 2 + 1; i++) {
                if (i % 2 == 1) count++;
            }
            temp = new Integer[count];
            for (int i = 1; i < (this.size - 1) / 2 + 1; i++) {
                if (i % 2 == 1) temp[idx++] = i;  
            }
            legal_moves = Arrays.asList(temp);
        } else if (this.lastMove == 1) {
            for (int i = 2; i <= this.size; i++) {
                if (stones[i]) count++;
            }
            temp = new Integer[count];
            for (int i = 2; i <= this.size; i++) {
                if (stones[i]) temp[idx++] = i;
            }
            legal_moves = Arrays.asList(temp);
        } else {
            for (int i = 1; i < this.lastMove; i++) {
                if (this.lastMove % i == 0 && stones[i] && this.lastMove != i) count++;
            }
            for (int j = this.size; j > this.lastMove; j--) {
                if (j % this.lastMove == 0 && stones[j] && this.lastMove != j) count++;
            }
            temp = new Integer[count];
            for (int i = 1; i < this.lastMove; i++) {
                if (this.lastMove % i == 0 && stones[i] && this.lastMove != i) temp[idx++] = i;
            }
            for (int j = this.lastMove; j <= this.size; j++) {
                if (j % this.lastMove == 0 && stones[j] && this.lastMove != j) temp[idx++] = j;
            }
            legal_moves = Arrays.asList(temp);
        }
        return legal_moves;
    }


    /**
     * This method is used to generate a list of successors
     * using the getMoves() method
     *
     * @return This is the list of state's successors
     */
    public List<GameState> setSuccessors() {
        if (this.succs == null) {
        
        this.succs = this.getMoves().stream().map(move -> {
            var state = new GameState(this);
            state.removeStone(move);
            return state;
        }).collect(Collectors.toList());
        }
        return this.succs;
    }
    
    public List<GameState> getSuccessors() {
        return this.succs;
    }

    /**
     * This method is used to evaluate a game state based on
     * the given heuristic function
     *
     * @return int This is the static score of given state
     */
    public double evaluate(boolean max_player) {
        // TODO Add your code here
        if (this.is_end()) {
            if (max_player) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (this.getStone(1)) {
                return 0;
            } else if (this.lastMove == 1) {
                int count = this.getMoves().size();
                if (count % 2 == 0) {
                    if (max_player) {
                        return 0.5;
                    } else {
                        return -0.5;
                    }
                } else {
                    if (max_player) {
                        return -0.5;
                    } else {
                        return 0.5;
                    }
                }
            } else if (Helper.isPrime(this.lastMove)) {
                int count = this.getMoves().size();
                if (count % 2 == 0) {
                    if (max_player) {
                        return 0.7;
                    } else {
                        return -0.7;
                    }
                } else {
                    if (max_player) {
                        return -0.7;
                    } else {
                        return 0.7;
                    }
                }
            } else if (!Helper.isPrime(this.lastMove)) {
                int largest_prime = Helper.getLargestPrimeFactor(this.lastMove);
                int count = 0;
                for (int i = largest_prime; i < this.size; i++) {
                    if (i % largest_prime == 0 && stones[i]) {
                        count++;
                    }
                }
                if (count % 2 == 1) {
                    if (max_player) {
                        return -0.6;
                    } else {
                        return 0.6;
                    }
                } else {
                    if (max_player) {
                        return 0.6;
                    } else {
                        return -0.6;
                    }
                }
            }
        }
        return 0.0;
    }

    /**
     * This method is used to take a stone out
     *
     * @param idx Index of the taken stone
     */
    public void removeStone(int idx) {
        this.stones[idx] = false;
        this.lastMove = idx;
    }

    /**
     * These are get/set methods for a stone
     *
     * @param idx Index of the taken stone
     */
    public void setStone(int idx) {
        this.stones[idx] = true;
    }

    public boolean getStone(int idx) {
        return this.stones[idx];
    }

    /**
     * These are get/set methods for lastMove variable
     *
     * @param move Index of the taken stone
     */
    public void setLastMove(int move) {
        this.lastMove = move;
    }

    public int getLastMove() {
        return this.lastMove;
    }

    /**
     * This is get method for game size
     *
     * @return int the number of stones
     */
    public int getSize() {
        return this.size;
    }
    public void setPlayer(boolean is_max) {
        this.max_player = is_max;
    }
    public boolean max_player() {
        return max_player;
    }
    public void set_val(double value) {
        this.val = value;
    }
    public double get_val() {
        return this.val;
    }
    public boolean is_end() {
        return this.getMoves().size() == 0;
    }
}	
