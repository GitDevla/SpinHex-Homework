package spinhex.Score;

import java.time.ZonedDateTime;

/**
 * Represents a score in the game, associating a username with a numeric score.
 * This class is used to track player performance.
 */
public class Score {
    private String username;
    private int score;

    /**
     * Default constructor that creates an empty Score object.
     * This constructor should be only used implicitly by Jackson for
     * deserialization.
     */
    public Score() {
    }

    /**
     * Creates a Score object with the specified username and score.
     *
     * @param username the player's username
     * @param score    the player's numeric score
     */
    public Score(String username, int score) {
        this.username = username;
        this.score = score;
    }

    /**
     * Returns the username associated with this score.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the numeric score value.
     *
     * @return the player's score
     */
    public int getScore() {
        return score;
    }
}
