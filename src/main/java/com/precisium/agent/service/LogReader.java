package com.precisium.agent.service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.precisium.agent.utils.FileState;

public final class LogReader {
    public List<String> readNewLines(Path file, FileState state) throws IOException {
        List<String> lines = new ArrayList<>();

        if (!Files.exists(file)) {
            return lines;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            if (state.getOffset() > raf.length()) {
                state.setOffset(0L);
            }

            raf.seek(state.getOffset());
            String line;
            while ((line = raf.readLine()) != null) {
                lines.add(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            }
            state.setOffset(raf.getFilePointer());
        }

        return lines;
    }
}
