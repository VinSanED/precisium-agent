package com.precisium.agent;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ConfigTest {

    @Test
    void fromEnv_shouldReadConfiguredEnvironmentVariables() {
        // Ensures explicit environment values are mapped to Config.
        Map<String, String> fakeEnv = Map.of(
            "AGENT_LOG_FILE", "/tmp/agent.log",
            "AGENT_ENDPOINT", "http://localhost:9999/ingest",
            "AGENT_POLL_MS", "2500"
        );

        Config config = Config.from(fakeEnv);

        assertEquals(Path.of("/tmp/agent.log"), config.getLogFile());
        assertEquals("http://localhost:9999/ingest", config.getEndpoint());
        assertEquals(Duration.ofMillis(2500), config.getPollInterval());
    }

    @Test
    void fromEnv_shouldUseDefaultsWhenVariablesAreMissing() {

        Config config = Config.from(Map.of());

        assertEquals(Path.of("application.log"), config.getLogFile());
        assertEquals("http://localhost:3000", config.getEndpoint());
        assertEquals(Duration.ofMillis(1000), config.getPollInterval());
    }

    @Test
    void fromEnv_shouldBuildEndpointFromBaseUrlWhenAgentEndpointIsMissing() {
        Map<String, String> fakeEnv = Map.of(
            "BASE_URL", "localhost:3000"
        );

        Config config = Config.from(fakeEnv);

        assertEquals("http://localhost:3000", config.getEndpoint());
    }

    @Test
    void fromEnv_shouldThrowWhenPollIntervalIsInvalid() {
        Map<String, String> fakeEnv = Map.of(
            "AGENT_LOG_FILE", "/tmp/agent.log",
            "AGENT_ENDPOINT", "http://localhost:9999/ingest",
            "AGENT_POLL_MS", "ABC"
        );

        assertThrows(NumberFormatException.class,() -> Config.from(fakeEnv));
    }

    @Test
    void fromArbitraryConfiguration() {
        String pathFile = "testLogFile.txt";
        String endPoint = "http://localhost:3000/api/logs";
        long interval = 1000L;
        String agentId = "1";

        Config config = Config.arbitrary(pathFile, endPoint, interval, agentId);

        assertEquals(Path.of("testLogFile.txt"), config.getLogFile());
        assertEquals("http://localhost:3000/api/logs", config.getEndpoint());
        assertEquals(Duration.ofMillis(1000), config.getPollInterval());
        assertEquals("1", config.getAgentId());

    }

    @Test
    void fromArbitraryConfiguration_shouldThrowWhenIntervalIsInvalid() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Config.arbitrary("testLogFile.txt", "http://localhost:8080/logs", 0L, "1")
        );
    }
}
