package llm_log_analyzer.Log.Analyzer;

import java.io.RandomAccessFile;
import java.nio.file.*;

public class LogTailer {

    public static void startMonitoring(String logFilePath) throws Exception {
        Path logPath = Paths.get(logFilePath);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        logPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Monitoring logs: " + logFilePath);
        long filePointer = logPath.toFile().length();

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.context().toString().equals(logPath.getFileName().toString())) {
                    filePointer = readNewLines(logPath, filePointer);
                }
            }
            key.reset();
        }
    }

    private static long readNewLines(Path logPath, long filePointer) {
        try (RandomAccessFile file = new RandomAccessFile(logPath.toFile(), "r")) {
            file.seek(filePointer);
            String line;
            while ((line = file.readLine()) != null) {
                // Convert ISO-8859-1 to UTF-8 if needed
                String decoded = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                LlmClient.analyzeLog(decoded);
            }
            return file.getFilePointer();
        } catch (Exception e) {
            System.err.println("⚠️ Error reading log: " + e.getMessage());
            return filePointer;
        }
    }
}
