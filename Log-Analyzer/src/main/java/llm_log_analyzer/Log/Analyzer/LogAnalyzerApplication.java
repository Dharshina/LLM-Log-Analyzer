package llm_log_analyzer.Log.Analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogAnalyzerApplication.class, args);
		
		try {
			GitHubIndexer.indexRepo("https://github.com/Dharshina/Sample-Java-Application", "repo");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			LogTailer.startMonitoring("D:\\Dharshina\\Downloads\\Log-Analyzer\\Log-Analyzer\\logs\\app.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
