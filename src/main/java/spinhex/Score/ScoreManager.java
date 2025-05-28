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

public class ScoreManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Path filePath;

    public ScoreManager(Path filePath) {
        this.filePath = filePath;
    }

    public List<Score> add(Score result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            MAPPER.writeValue(out, results);
        }
        return results;
    }

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
