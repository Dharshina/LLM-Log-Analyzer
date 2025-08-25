package llm_log_analyzer.Log.Analyzer;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LlmClient {
    //private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void analyzeLog(String logLine) {
        try {
            String context = VectorDB.querySimilar(logLine);
           
            String prompt = "Code Context:\n" + context + 
                    "\n\nLog:\n" + logLine +
                    "\n\nReturn a JSON object with fields:\n" +
                    "- critical: boolean (true if it's a critical error, false otherwise)\n" +
                    "- reason: short explanation of what part of the code is responsible\n" +
                    "- fix: suggested code-level fix.";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // time to establish connection
                    .readTimeout(1600, TimeUnit.SECONDS)   // time to wait for LLM response
                    .writeTimeout(60, TimeUnit.SECONDS)   // time to send data
                    .build();
            
            String jsonPayload = mapper.writeValueAsString(new GenerateRequest("llama3", prompt, false));

            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));
            System.out.println(body);
            Request request = new Request.Builder()
                    .url("http://localhost:11434/api/generate")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("LLM API returned: " + response.code());
                    return;
                }

                String respBody = response.body().string();

                // The API returns JSON like {"response":"..."} when stream=false
                JsonNode json = mapper.readTree(respBody);
                String llmText = json.has("response") ? json.get("response").asText() : respBody;
                

                System.out.println("Final LLM Output: " + llmText);

                // Parse the returned JSON from LLM
                try {
                    JsonNode decision = mapper.readTree(llmText);
                    boolean critical = decision.get("critical").asBoolean();
                    String reason = decision.get("reason").asText();
                    String fix = decision.has("fix") ? decision.get("fix").asText() : "No fix suggested";

                    System.out.println("🔍 Parsed -> Critical: " + critical + 
                                       ", Reason: " + reason + 
                                       ", Fix: " + fix);

                    if (critical) {
                        sendIfCritical(reason + " | Suggested Fix: " + fix);
                    }
                } catch (Exception e) {
                    System.err.println("LLM returned unparseable output: " + llmText);
                }

            }
        } catch (Exception e) {
            System.err.println("LLM Request Failed: " + e.getMessage());
        }
    }
    
    public static void sendIfCritical(String llmResponse) {
        if (llmResponse.toLowerCase().contains("error") || llmResponse.toLowerCase().contains("exception")) {
            System.out.println("ALERT: " + llmResponse);
        } else {
            System.out.println("INFO: " + llmResponse);
        }
    }
    
    static class GenerateRequest {
        public String model;
        public String prompt;
        public boolean stream;
        public GenerateRequest(String model, String prompt, boolean stream) {
            this.model = model; this.prompt = prompt; this.stream = stream;
        }
    }
    
    public static void analyzeCode() {
        try {
        	String s = "what is the heartbeat fix message.\nRequired Tags for Heartbeat\nMinimal FIX Heartbeat Message Example";
            String context = VectorDB.querySimilar(s);
            
            String prompt = "Code Context:\n" + context + 
                    "\n" +  s;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // time to establish connection
                    .readTimeout(1600, TimeUnit.SECONDS)   // time to wait for LLM response
                    .writeTimeout(60, TimeUnit.SECONDS)   // time to send data
                    .build();
            
            String jsonPayload = mapper.writeValueAsString(new GenerateRequest("llama3", prompt, false));

            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));
            System.out.println(body);
            Request request = new Request.Builder()
                    .url("http://localhost:11434/api/generate")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("LLM API returned: " + response.code());
                    return;
                }

                String respBody = response.body().string();

                // The API returns JSON like {"response":"..."} when stream=false
                JsonNode json = mapper.readTree(respBody);
                String llmText = json.has("response") ? json.get("response").asText() : respBody;
                

                System.out.println("Final LLM Output: " + llmText);

                

            }
        } catch (Exception e) {
            System.err.println("LLM Request Failed: " + e.getMessage());
        }
    }
    
}
