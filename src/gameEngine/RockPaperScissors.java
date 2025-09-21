package gameEngine;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Game engine for Rock-Paper-Scissors
 * @author: Andreas Raschle
 * @date: September 21 2025
 */
public class RockPaperScissors {

    //Simple Game Model, packed in an enum
    public enum Move {
        LIZARD("Lizard"),
        PAPER("Paper"),
        ROCK("Rock"),
        SCISSORS("Scissors"),
        SPOCK("Spock");

        public final String label;

        Move(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        // Determine result of this vs other
        public Result playAgainst(Move other) {
            if (this == other) return Result.DRAW;
            //Use traditional switch statement for compatibilty with older JDK
            switch (this) {
                case ROCK:
                    return (other == SCISSORS || other == LIZARD) ? Result.WIN : Result.LOSE;
                case PAPER:
                    return (other == ROCK || other == SPOCK) ? Result.WIN : Result.LOSE;
                case SCISSORS:
                    return (other == PAPER || other == LIZARD) ? Result.WIN : Result.LOSE;
                case LIZARD:
                    return (other == SPOCK || other == PAPER) ? Result.WIN : Result.LOSE;
                case SPOCK:
                    return (other == SCISSORS || other == ROCK) ? Result.WIN : Result.LOSE;
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }

    public enum Result {
        WIN,
        LOSE,
        DRAW
    }

    private static final RockPaperScissors  instance = new RockPaperScissors();

    private final Random rng = new Random();
    private int userScore = 0;
    private int cpuScore = 0;
    private int draws = 0;
    private int targetWins = 1; // majority needed to win the match
    private String statusText;
    private String scoreText;
    private boolean isLizardSpockOn = false;
    private boolean isGameFinished = false;
    private String resultMessage;

    public static RockPaperScissors getInstance(){
        return instance;
    }

    protected RockPaperScissors(){

    }

    //Getter and setter
    //========================================================
    public void setIsLizardSpockOn(boolean isLizardSpockOn) {
        this.isLizardSpockOn = isLizardSpockOn;
    }
    public boolean isLizardSpockOn() {
        return isLizardSpockOn;
    }

    public String getStatusText(){
        return statusText;
    }

    public String getScoreText(){
        return scoreText;
    }

    public void setTargetWins(int targetWins) {
        this.targetWins = targetWins;
    }
    public int getTargetWins() {
        return targetWins;
    }

    public List<Move> getMoves(){
        return this.getActiveMoves();
    }

    public String getResultMessage(){
        return this.resultMessage;
    }

    public boolean getIsGameFinished() {
        return this.isGameFinished;
    }

    /**
     * Reset last Results
     */
    public void resetScore(){
        this.userScore = 0;
        this.cpuScore = 0;
        this.draws = 0;
        this.updateScoreText();
        this.isGameFinished = false;
    }

    /**
     * Play method
     * @param userMove
     */
    public void playRound(Move userMove) {
        List<Move> moves = this.getActiveMoves();
        Move cpuMove = moves.get(rng.nextInt(moves.size()));
        Result result = userMove.playAgainst(cpuMove);

        switch (result) {
            case WIN -> {
                userScore++;
                statusText = String.format("You: %s | CPU: %s → You WIN this round!", userMove, cpuMove);
            }
            case LOSE -> {
                cpuScore++;
                statusText = String.format("You: %s | CPU: %s → You lose this round.", userMove, cpuMove);
            }
            case DRAW -> {
                draws++;
                statusText = String.format("You: %s | CPU: %s → It's a draw.", userMove, cpuMove);
            }
        }

        this.updateScoreText();
        this.checkResult();
    }

    /**
     * Init possible moves
     * @return list with Moves
     */
    private List<Move> getActiveMoves() {
        if (this.isLizardSpockOn) {
            return Arrays.asList(Move.ROCK, Move.PAPER, Move.SCISSORS, Move.LIZARD, Move.SPOCK);
        }
        return Arrays.asList(Move.ROCK, Move.PAPER, Move.SCISSORS);
    }

    /**
     * Update the score to show in UI
     */
    private void updateScoreText() {
        this.scoreText= String.format("Score → You %d : %d CPU  (Draws: %d)", userScore, cpuScore, draws);
    }

    /**
     * Check the results and set flag to end the game
     */
    private void checkResult() {
        if (this.userScore >= this.targetWins || this.cpuScore >= this.targetWins) {
            boolean youWin = userScore > cpuScore;
            StringBuilder message = new StringBuilder();
            if (youWin) {
                message.append("You win the match!");
            }
            else{
                message.append("CPU wins the match.");
            }
            message.append(String.format("Final score: You %d : %d CPU (draws: %d)", this.userScore,
                    cpuScore,
                    draws));
            this.resultMessage = message.toString();
            this.statusText = "Match over — start a new one or change settings.";
            this.isGameFinished = true;
        }
    }

}
