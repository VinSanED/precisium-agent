package com.precisium.agent.service;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LogReader {
    public long countLines(Path file) throws IOException {
        if (!Files.exists(file)) {
            return 0L;
        }

        long total = 0L;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            while (reader.readLine() != null) {
                total++;
            }
        }

        return total;
    }

    public List<String> readFrom(Path file, long startLine) throws IOException {
        List<String> lines = new ArrayList<>();
        long safeStartLine = Math.max(0L, startLine);

        if (!Files.exists(file)) {
            return lines;
        }

        long currentLine = 0L;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (currentLine > safeStartLine) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }
}
