# LLM-Log-Analyzer

A developer-friendly tool that uses Large Language Models (LLMs) to **analyze, summarize, and triage application/system logs**. Point it at your logs to get human-readable insights, suspected root causes, and follow-up actions.

Built for fast incident triage, noisy log exploration, and turning raw text into answers.

---

## Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [How It Works](#how-it-works)
- [CLI / API Usage](#cli--api-usage)
- [Examples](#examples)
- [Architecture](#architecture)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **LLM-powered log understanding** — summaries, anomalies, suspected causes, and remediation hints  
- **Pluggable providers** — swap between OpenAI, Gemini, local models (e.g., Ollama) via config  
- **Flexible inputs** — plain text logs, rotated files, directories  
- **Deterministic prompts** — reusable prompts for “error triage”, “incident summary”, etc.  
- **Streaming-friendly** — process large logs chunk-by-chunk (configurable)  
- **Extensible** — add your own detectors, parsers, or prompts

---

## Quick Start

> Choose **one** of Maven or Gradle depending on your build.

### Prerequisites
- Java 17+ (adjust if your code targets a different version)
- An LLM provider key (optional if you use a local model)
  - OpenAI: `OPENAI_API_KEY`
  - Google Gemini: `GEMINI_API_KEY`
  - Local (Ollama): running `ollama serve`

### Clone

```bash
git clone https://github.com/Dharshina/LLM-Log-Analyzer.git
cd LLM-Log-Analyzer
Build (Maven)
mvn -v        # sanity check
mvn clean package -DskipTests
Build (Gradle)
./gradlew --version   # sanity check
./gradlew clean build -x test
Run
```bash
java -jar target/llm-log-analyzer.jar --input /path/to/logfile.log --provider openai
```

---

## Configuration

| Variable         | Description                   | Example   |
|------------------|-------------------------------|-----------|
| OPENAI_API_KEY   | OpenAI provider key           | sk-...    |
| GEMINI_API_KEY   | Google Gemini provider key    | AIz...    |
| LLM_PROVIDER     | Provider: openai, gemini, ollama | openai |
| CHUNK_SIZE       | Lines per chunk for log splitting | 200   |

---

## How It Works

1. Reads input log files.
2. Splits logs into manageable chunks.
3. Sends each chunk to the configured LLM provider.
4. Aggregates responses into structured summaries or reports.

---

## CLI / API Usage

Analyze a single log:
```bash
java -jar llm-log-analyzer.jar --input ./server.log
```

Choose provider explicitly:
```bash
java -jar llm-log-analyzer.jar --input ./server.log --provider gemini
```

Analyze directory of logs:
```bash
java -jar llm-log-analyzer.jar --input ./logs/
```

---

## Examples

**Input:**
```
2023-07-12 12:00:01 ERROR [PaymentService] Failed to process transaction 12345: NullPointerException
2023-07-12 12:00:02 WARN  [RetryHandler] Retrying transaction 12345
```

**Output:**
```
critical:true
reason: NullPointerException in PaymentService.processpayment() when processor is null
fix: Check for null pointer before invoking processor.process(). If null, handle it gracefully or throw an exception
```

---

## Architecture

[ Logs ] → [ Chunker ] → [ Prompt Builder ] → [ LLM Provider ] → [ Analyzer ] → [ Reports ]
