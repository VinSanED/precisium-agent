package com.precisium.agent.service;

import java.io.IOException;
import java.nio.file.Path;
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
    private final Path stateFile;

    private volatile boolean running = false;
    private volatile boolean stateLoaded = false;

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
        this.stateFile = config.getLogFile().resolveSibling(".precisium_cursor");
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
        ensureStateLoaded();

        Path logFile = config.getLogFile();
        long cursor = state.getOffset();
        long fileLines = logReader.countLines(logFile);

        if (fileLines < cursor) {
            cursor = 0L;
            state.setOffset(cursor);
            state.save(stateFile);
        }

        List<String> newLines = logReader.readFrom(logFile, cursor);
        long sentCount = 0L;
        for (String line : newLines) {
            httpSender.sendLine(config.getEndpoint(), line);
            sentCount++;
            state.setOffset(cursor + sentCount);
            state.save(stateFile);
        }

        return (int) sentCount;
    }

    private void ensureStateLoaded() throws IOException {
        if (stateLoaded) {
            return;
        }

        state.load(stateFile);
        stateLoaded = true;
    }
}
