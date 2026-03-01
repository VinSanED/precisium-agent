package com.precisium.agent.service;


import com.precisium.agent.utils.FileState;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void readNewLines_shouldPerformInitialRead() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "line-1\nline-2\n");

        FileState state = new FileState();
        LogReader reader = new LogReader();

        List<String> lines = reader.readNewLines(logFile, state);

        assertEquals(List.of("line-1", "line-2"), lines);
        assertEquals(Files.size(logFile), state.getOffset());
    }

    @Test
    void readNewLines_shouldReadOnlyIncrementalContent() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "line-1\nline-2\n");

        FileState state = new FileState();
        LogReader reader = new LogReader();
        reader.readNewLines(logFile, state);
        long previousOffset = state.getOffset();

        Files.writeString(logFile, "line-3\n", StandardOpenOption.APPEND);
        List<String> newLines = reader.readNewLines(logFile, state);

        assertEquals(List.of("line-3"), newLines);
        assertEquals(previousOffset + "line-3\n".getBytes(StandardCharsets.UTF_8).length, state.getOffset());
    }

    @Test
    void readNewLines_shouldReturnCorrectOffsetAfterEachRead() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "a\n");

        FileState state = new FileState();
        LogReader reader = new LogReader();

        reader.readNewLines(logFile, state);
        assertEquals(Files.size(logFile), state.getOffset());

        Files.writeString(logFile, "b\n", StandardOpenOption.APPEND);
        reader.readNewLines(logFile, state);
        assertEquals(Files.size(logFile), state.getOffset());
    }

    @Test
    void readNewLines_shouldResetOffsetWhenFileIsTruncated() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "new-file-line\n");

        FileState state = new FileState();
        state.setOffset(10_000L);
        LogReader reader = new LogReader();

        List<String> lines = reader.readNewLines(logFile, state);

        assertEquals(List.of("new-file-line"), lines);
        assertEquals(Files.size(logFile), state.getOffset());
    }

    @Test
    void readNewLines_shouldReturnEmptyWhenThereIsNoNewContent() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "line-1\n");

        FileState state = new FileState();
        LogReader reader = new LogReader();
        reader.readNewLines(logFile, state);
        long offsetAfterFirstRead = state.getOffset();

        List<String> lines = reader.readNewLines(logFile, state);

        assertTrue(lines.isEmpty());
        assertEquals(offsetAfterFirstRead, state.getOffset());
    }
}
