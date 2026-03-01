package com.precisium.agent.service;

import com.precisium.agent.Config;
import com.precisium.agent.controller.HttpSender;
import com.precisium.agent.utils.FileState;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AgentRuntimeTest {

   @Test
void runOnce_shouldSendEachNewLineAndReturnTotalCount() throws Exception {

    Config config = new Config(
            Path.of("app.log"),
            "http://localhost:3000/logs",
            Duration.ofMillis(10)
    );

    LogReader logReader = mock(LogReader.class);
    HttpSender httpSender = mock(HttpSender.class);
    FileState state = mock(FileState.class);

    when(logReader.readNewLines(config.getLogFile(), state))
            .thenReturn(List.of("l1", "l2", "l3"));

    AgentRuntime runtime =
            new AgentRuntime(config, logReader, httpSender, state);

    int processed = runtime.runOnce();

    assertEquals(3, processed);

    verify(httpSender).sendLine(config.getEndpoint(), "l1");
    verify(httpSender).sendLine(config.getEndpoint(), "l2");
    verify(httpSender).sendLine(config.getEndpoint(), "l3");
    verifyNoMoreInteractions(httpSender);
}

}
