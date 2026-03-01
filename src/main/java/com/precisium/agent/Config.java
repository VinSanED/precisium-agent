package com.precisium.agent;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

public final class Config {
    public static final String DEFAULT_LOG_FILE = "application.log";
    public static final String DEFAULT_ENDPOINT = "http://localhost:3000";
    public static final long DEFAULT_POLL_MS = 1000L;

    private final Path logFile;
    private final String endpoint;
    private final Duration pollInterval;

    public Config(Path logFile, String endpoint, Duration pollInterval) {
        this.logFile = logFile;
        this.endpoint = endpoint;
        this.pollInterval = pollInterval;
    }


    
    public static Config from(Map<String, String> env) {
        String logFile = env.getOrDefault("AGENT_LOG_FILE", DEFAULT_LOG_FILE);
        String endpoint = env.getOrDefault("AGENT_ENDPOINT", DEFAULT_ENDPOINT);
        long intervalMs = Long.parseLong(env.getOrDefault("AGENT_POLL_MS", Long.toString(DEFAULT_POLL_MS)));
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("AGENT_POLL_MS precisa ser maior que 0");
        }

        return new Config(Path.of(logFile), endpoint, Duration.ofMillis(intervalMs));
    }
    public static Config fromEnv(){
        return from(System.getenv());
    }

    public static Config arbitrary(String logFile, String endpoint, long interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("interval precisa ser maior que 0");
        }
        return new Config(Path.of(logFile), endpoint, Duration.ofMillis(interval));
    }

    public Path getLogFile() {
        return logFile;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Duration getPollInterval() {
        return pollInterval;
    }
}
