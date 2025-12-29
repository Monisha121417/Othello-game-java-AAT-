import java.util.*;

public class Othello6 {

    static final int EMPTY = 0;
    static final int BLACK = 1; // Black moves first
    static final int WHITE = 2;

    static final int SIZE = 6; // 6x6 board

    // Directions (8)
    static final int[][] DIRS = {
        {-1,-1}, {-1,0}, {-1,1},
        {0,-1},         {0,1},
        {1,-1},  {1,0}, {1,1}
    };

    public static class Board {
        int[][] grid = new int[SIZE][SIZE];

        public Board() {
            initialize();
        }

        void initialize() {
            for (int i = 0; i < SIZE; i++)
                Arrays.fill(grid[i], EMPTY);

            // center start for 6x6: indices 2 and 3
            grid[2][2] = WHITE;
            grid[2][3] = BLACK;
            grid[3][2] = BLACK;
            grid[3][3] = WHITE;
        }

        boolean inBounds(int r, int c) {
            return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
        }

        boolean isValidMove(int player, int r, int c) {
            if (!inBounds(r, c) || grid[r][c] != EMPTY) return false;
            int opp = opponent(player);

            for (int[] d : DIRS) {
                int i = r + d[0], j = c + d[1];
                boolean foundOpp = false;
                while (inBounds(i,j) && grid[i][j] == opp) {
                    foundOpp = true;
                    i += d[0]; j += d[1];
                }
                if (foundOpp && inBounds(i,j) && grid[i][j] == player) return true;
            }
            return false;
        }

        List<int[]> getValidMoves(int player) {
            List<int[]> moves = new ArrayList<>();
            for (int r = 0; r < SIZE; r++)
                for (int c = 0; c < SIZE; c++)
                    if (isValidMove(player, r, c))
                        moves.add(new int[]{r, c});
            return moves;
        }

        void applyMove(int player, int r, int c) {
            if (!isValidMove(player, r, c)) return;
            grid[r][c] = player;
            int opp = opponent(player);

            for (int[] d : DIRS) {
                int i = r + d[0], j = c + d[1];
                List<int[]> toFlip = new ArrayList<>();
                while (inBounds(i,j) && grid[i][j] == opp) {
                    toFlip.add(new int[]{i,j});
                    i += d[0]; j += d[1];
                }
                if (!toFlip.isEmpty() && inBounds(i,j) && grid[i][j] == player) {
                    for (int[] p : toFlip) grid[p[0]][p[1]] = player;
                }
            }
        }

        boolean hasAnyValidMove(int player) {
            return !getValidMoves(player).isEmpty();
        }

        boolean isFull() {
            for (int r = 0; r < SIZE; r++)
                for (int c = 0; c < SIZE; c++)
                    if (grid[r][c] == EMPTY) return false;
            return true;
        }

        boolean isGameOver() {
            return isFull() || (!hasAnyValidMove(BLACK) && !hasAnyValidMove(WHITE));
        }

        int[] score() {
            int b = 0, w = 0;
            for (int r = 0; r < SIZE; r++)
                for (int c = 0; c < SIZE; c++) {
                    if (grid[r][c] == BLACK) b++;
                    else if (grid[r][c] == WHITE) w++;
                }
            return new int[]{b,w};
        }

        void printBoard() {
            System.out.print("   ");
            for (int c = 0; c < SIZE; c++) System.out.print(c + " ");
            System.out.println();
            System.out.print("   ");
            for (int c = 0; c < SIZE; c++) System.out.print("--");
            System.out.println();
            for (int r = 0; r < SIZE; r++) {
                System.out.print(r + "| ");
                for (int c = 0; c < SIZE; c++) {
                    if (grid[r][c] == BLACK) System.out.print("B ");
                    else if (grid[r][c] == WHITE) System.out.print("W ");
                    else System.out.print(". ");
                }
                System.out.println();
            }
            int[] s = score();
            System.out.println("\nScore -> Black: " + s[0] + "  White: " + s[1] + "\n");
        }

        int opponent(int p) {
            return p == BLACK ? WHITE : BLACK;
        }
    } // end Board

    // helper
    static int opponent(int p) { return p == BLACK ? WHITE : BLACK; }

    // MAIN: simple 2-player console interaction
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Board board = new Board();
        int current = BLACK; // black moves first

        System.out.println("Welcome to 6x6 Othello (Reversi) — Console Version");
        System.out.println("Enter moves as: row col  (example: 2 3)\n");

        while (!board.isGameOver()) {
            board.printBoard();
            if (!board.hasAnyValidMove(current)) {
                System.out.println((current==BLACK? "Black":"White") + " has no valid moves. Turn passed.");
                current = opponent(current);
                continue;
            }

            System.out.println("Current player: " + (current==BLACK? "Black (B)" : "White (W)"));
            List<int[]> moves = board.getValidMoves(current);
            System.out.print("Valid moves: ");
            for (int[] m : moves) System.out.print("[" + m[0] + "," + m[1] + "] ");
            System.out.println();

            System.out.print("Enter move (row col): ");
            int r, c;
            try {
                r = sc.nextInt();
                c = sc.nextInt();
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Enter two integers like: 2 3");
                sc.nextLine(); // clear
                continue;
            }

            if (!board.inBounds(r,c)) {
                System.out.println("Out of bounds. Use 0 to " + (SIZE-1));
                continue;
            }
            if (!board.isValidMove(current, r, c)) {
                System.out.println("Invalid move. That position doesn't flip any opponent pieces.");
                continue;
            }

            board.applyMove(current, r, c);
            current = opponent(current);
        } // game loop

        // final state
        board.printBoard();
        int[] finalScore = board.score();
        System.out.println("Game Over!");
        System.out.println("Final Score -> Black: " + finalScore[0] + "  White: " + finalScore[1]);
        if (finalScore[0] > finalScore[1]) System.out.println("Black wins!");
        else if (finalScore[1] > finalScore[0]) System.out.println("White wins!");
        else System.out.println("It's a draw!");

        sc.close();
    }
}