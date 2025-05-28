package spinhex.Score;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the storage and retrieval of Score objects.
 * This class provides functionality to add new scores and retrieve all scores
 * from a file.
 */
public class ScoreManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Path filePath;

    /**
     * Constructs a new ScoreManager with the specified file path.
     *
     * @param filePath the path to the file where scores will be stored
     */
    public ScoreManager(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Adds a new score to the list of scores and saves it to the file.
     *
     * @param result the Score object to add
     * @return the updated list of all scores including the newly added score
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public List<Score> add(Score result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            MAPPER.writeValue(out, results);
        }
        return results;
    }

    /**
     * Retrieves all scores from the file.
     * If the file doesn't exist, returns an empty list.
     *
     * @return a list of all Score objects stored in the file
     * @throws IOException if an I/O error occurs while reading from the file
     */
    public List<Score> getAll() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (var in = Files.newInputStream(filePath)) {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, Score.class);
            return MAPPER.readValue(in, type);
        }
    }
}
