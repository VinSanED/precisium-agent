package com.precisium.agent;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.precisium.agent.controller.ActivationClient;
import com.precisium.agent.service.AgentRuntime;
import com.precisium.agent.utils.AgentStatus;

public class initializeTest {
    @Test
    void  executeOnce_shouldStartAgent_whenStatusIsStarted() throws Exception {
        ActivationClient activationClient = mock(ActivationClient.class);
        AgentRuntime agent = mock(AgentRuntime.class);

        when(activationClient.fetchCommand("agentId"))
            .thenReturn(AgentStatus.STARTED);

        AgentStatus result = AgentMain.executeOnce(activationClient, agent, "agentId");

        assertEquals(AgentStatus.STARTED, result);
        verify(agent).start();
        verify(agent, never()).stop();
    }   
    @Test
    void executeOnce_shouldStopAgent_whenStatusIsStopped() throws Exception {
        ActivationClient activationClient = mock(ActivationClient.class);
        AgentRuntime agent = mock(AgentRuntime.class);

        when(activationClient.fetchCommand("agentId"))
            .thenReturn(AgentStatus.STOPPED);

        AgentStatus result = AgentMain.executeOnce(activationClient, agent, "agentId");

        assertEquals(AgentStatus.STOPPED, result);
        verify(agent).stop();
        verify(agent, never()).start();
    }
    @Test
    void executeOnce_shouldStopAgent_whenIOException() throws Exception {
        ActivationClient activationClient = mock(ActivationClient.class);
        AgentRuntime agent = mock(AgentRuntime.class);

        when(activationClient.fetchCommand("agentId"))
            .thenThrow(new IOException("network fail"));

        AgentStatus result = AgentMain.executeOnce(activationClient, agent, "agentId");

        assertEquals(AgentStatus.STOPPED, result);
        verify(agent).stop();
    }
    @Test
    void executeOnce_shouldStopAgent_whenRuntimeException() throws Exception {
        ActivationClient activationClient = mock(ActivationClient.class);
        AgentRuntime agent = mock(AgentRuntime.class);

        when(activationClient.fetchCommand("agentId"))
            .thenThrow(new RuntimeException("erro runtime ---BOOM!---"));

        AgentStatus result = AgentMain.executeOnce(activationClient, agent, "agentId");

        assertEquals(AgentStatus.STOPPED, result);
        verify(agent).stop();
    }
}
