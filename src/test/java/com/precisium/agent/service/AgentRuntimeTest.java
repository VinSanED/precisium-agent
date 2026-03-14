package com.precisium.agent.service;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.precisium.agent.Config;
import com.precisium.agent.controller.HttpSender;
import com.precisium.agent.utils.FileState;

class AgentRuntimeTest {

   @Test
void runOnce_shouldSendEachNewLineAndReturnTotalCount() throws Exception {

    Config config = new Config(
            Path.of("app.log"),
            "http://localhost:3000/logs",
            Duration.ofMillis(10),

            "1"
    );

    LogReader logReader = mock(LogReader.class);
    HttpSender httpSender = mock(HttpSender.class);
    FileState state = mock(FileState.class);

    when(state.getOffset()).thenReturn(0L);
    when(logReader.countLines(config.getLogFile())).thenReturn(3L);
    when(logReader.readFrom(config.getLogFile(), 0L))
            .thenReturn(List.of("l1", "l2", "l3"));

    AgentRuntime runtime =
            new AgentRuntime(config, logReader, httpSender, state);

    int processed = runtime.runOnce();

    assertEquals(3, processed);

    verify(httpSender).sendLine(config.getEndpoint(), "l1", config.getAgentId());
    verify(httpSender).sendLine(config.getEndpoint(), "l2", config.getAgentId());
    verify(httpSender).sendLine(config.getEndpoint(), "l3", config.getAgentId());
    verify(state).load(Path.of(".precisium_cursor"));
    verify(state).setOffset(1L);
    verify(state).setOffset(2L);
    verify(state).setOffset(3L);
    verify(state, times(3)).save(Path.of(".precisium_cursor"));
    verifyNoMoreInteractions(httpSender);
}

}
