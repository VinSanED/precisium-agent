package com.precisium.agent.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
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
    void readFrom_shouldReadAllLinesWhenCursorIsZero() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "line-1\nline-2\n");

        LogReader reader = new LogReader();

        List<String> lines = reader.readFrom(logFile, 0L);

        assertEquals(List.of("line-1", "line-2"), lines);
    }

    @Test
    void readFrom_shouldReadOnlyIncrementalContent() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "line-1\nline-2\n");

        LogReader reader = new LogReader();
        Files.writeString(logFile, "line-3\n", StandardOpenOption.APPEND);
        List<String> newLines = reader.readFrom(logFile, 2L);

        assertEquals(List.of("line-3"), newLines);
    }

    @Test
    void countLines_shouldReturnTotalLines() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "a\nb\nc\n");

        LogReader reader = new LogReader();

        assertEquals(3L, reader.countLines(logFile));
    }

    @Test
    void countLines_shouldReturnZeroForMissingFile() throws IOException {
        Path logFile = tempDir.resolve("missing.log");
        LogReader reader = new LogReader();

        assertEquals(0L, reader.countLines(logFile));
    }

    @Test
    void readFrom_shouldReturnEmptyWhenThereIsNoNewContent() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, "line-1\n");

        LogReader reader = new LogReader();
        List<String> lines = reader.readFrom(logFile, 1L);

        assertTrue(lines.isEmpty());
    }
}
