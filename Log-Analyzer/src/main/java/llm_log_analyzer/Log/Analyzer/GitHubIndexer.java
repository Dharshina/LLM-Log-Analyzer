package llm_log_analyzer.Log.Analyzer;

import java.io.File;
import java.nio.file.*;
import java.util.stream.Stream;

public class GitHubIndexer {

    public static void indexRepo(String url, String targetDir) throws Exception {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            System.out.println("📥 Cloning repo...");
            org.eclipse.jgit.api.Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(dir)
                    .call();
        }

        // ✅ Find the first src/main/java folder inside the repo
        Path srcRoot = findSourceRoot(dir.toPath());

        if (srcRoot == null) {
            System.err.println("❌ Could not find src/main/java inside " + dir);
            return;
        }

        System.out.println("📂 Indexing only source folder: " + srcRoot);

        try (Stream<Path> files = Files.walk(srcRoot)) {
            files.filter(Files::isRegularFile)
                 .filter(p -> p.toString().matches(".*\\.(java|xml|properties|txt|md)$"))
                 .forEach(path -> {
                     try {
                         String content = Files.readString(path);
                         VectorDB.store(path.toString(), content);
                     } catch (Exception e) {
                         System.err.println("⚠️ Skipped unreadable file: " + path);
                     }
                 });
        }
    }

    private static Path findSourceRoot(Path root) throws Exception {
        try (Stream<Path> paths = Files.walk(root, 4)) { // search up to depth 4
            return paths.filter(Files::isDirectory)
                        .filter(p -> p.endsWith("src/main/java"))
                        .findFirst()
                        .orElse(null);
        }
    }
}
