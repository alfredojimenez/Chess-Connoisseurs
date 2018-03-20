package player.basicAI;

import board.Board;
import board.Move;
import pieces.Alliance;
import player.MoveTransition;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MiniMax(int searchDepth) {
        this.boardEvaluator = new RegularBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "Minimax";
    }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();

        Move bestMove = null;

        int highestEncounteredValue = Integer.MIN_VALUE;
        int lowestEncounteredValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.currentPlayer().getAlliance() + " EVALUATING with depth: " + searchDepth);
        int numMoves = board.currentPlayer().getLegalMoves().size();

        for (Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                if (board.currentPlayer().getAlliance() == Alliance.WHITE) {
                    currentValue = min(moveTransition.getTransitionBoard(), searchDepth - 1);
                } else {
                    currentValue = max(moveTransition.getTransitionBoard(), searchDepth - 1);
                }

                if (board.currentPlayer().getAlliance() == Alliance.WHITE && currentValue >= highestEncounteredValue) {
                    highestEncounteredValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance() == Alliance.BLACK && currentValue <= lowestEncounteredValue) {
                    lowestEncounteredValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("\tTIME TAKEN: " + timeSpent);

        return bestMove;
    }

    private int min(Board board, int searchDepth) {
        if (searchDepth == 0 || isEndGame(board)) {
            return this.boardEvaluator.evaluate(board, searchDepth);
        }
        int lowestEncounteredValue = Integer.MAX_VALUE;

        for (Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitionBoard(), searchDepth - 1);

                if (currentValue <= lowestEncounteredValue) lowestEncounteredValue = currentValue;
            }
        }
        return lowestEncounteredValue;
    }

    private int max(Board board, int searchDepth) {
        if (searchDepth == 0 || isEndGame(board)) {
            return this.boardEvaluator.evaluate(board, searchDepth);
        }
        int highestEncounteredValue = Integer.MIN_VALUE;

        for (Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitionBoard(), searchDepth - 1);

                if (currentValue >= highestEncounteredValue) highestEncounteredValue = currentValue;
            }
        }
        return highestEncounteredValue;
    }

    /**
     * Check if the current player is in checkmate or in a stalemate
     * @param board to evaluate
     * @return true if current player is in checkmate or stalemate, false otherwise
     */
    private boolean isEndGame(Board board) {
        return board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate();
    }

}