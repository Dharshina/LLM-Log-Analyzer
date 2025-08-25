package llm_log_analyzer.Log.Analyzer;

import java.util.*;

public class VectorDB {
    private static final Map<String, String> codeStore = new HashMap<>();

    public static void store(String filePath, String content) {
        codeStore.put(filePath, content);
    }

    public static String querySimilar(String logLine) {
        // simple search (improve with embeddings)
        return codeStore.entrySet().stream()
                .filter(e -> e.getValue().contains("Exception") || e.getValue().contains("Error"))
                .limit(3)
                .map(Map.Entry::getValue)
                .reduce("", (a,b) -> a + "\n" + b);
    }
}
