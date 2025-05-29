package spinhex.score;

import java.util.Objects;

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

    /**
     * Compares this Score object with the specified object for equality.
     * Two {@code Score} objects are considered equal if they have the same
     * username and score.
     *
     * @param o the object to compare this Score against
     * @return {@code true} if the given object represents a Score equivalent to
     *         this Score,
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        Score score1 = (Score) o;
        return score == score1.score && Objects.equals(username, score1.username);
    }

    /**
     * Returns a hash code value for this Score object.
     * The hash code is computed based on the username and score.
     *
     * @return a hash code value for this Score
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(username);
        result = 31 * result + score;
        return result;
    }
}
