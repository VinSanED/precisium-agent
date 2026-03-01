package com.precisium.agent.service;

import java.time.Duration;
import java.util.List;

import com.precisium.agent.Config;
import com.precisium.agent.controller.HttpSender;
import com.precisium.agent.utils.FileState;

public class AgentRuntime {

    private final Config config;
    private final LogReader logReader;
    private final HttpSender httpSender;
    private final FileState state;

    private volatile boolean running = false;

    public AgentRuntime(
        Config config, 
        LogReader logReader, 
        HttpSender httpSender, 
        FileState fileState
    ) {
        this.config = config;
        this.logReader = logReader;
        this.httpSender = httpSender;
        this.state = fileState;
    }

    public void start() {
        if (running) return;

        running = true;

        new Thread(this::runLoop).start();
    }

    public void stop() {
        running = false;
    }

    private void runLoop() {
        Duration interval = config.getPollInterval();

        while (running) {
            try {
                runOnce();
                Thread.sleep(interval.toMillis());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; 
            } catch (Exception e) {
                System.err.println("Erro no agente: " + e.getMessage());
            }
        }

        System.out.println("Agente parado.");
    }

    int runOnce() throws Exception {
        List<String> newLines =
            logReader.readNewLines(config.getLogFile(), state);

        for (String line : newLines) {
            httpSender.sendLine(config.getEndpoint(), line);
        }

        return newLines.size();
    }
}
