package com.precisium.agent.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileState {
    private long offset;

    public FileState(){
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = Math.max(0L, offset);
    }

    public void load(Path stateFile) throws IOException {
        if (!Files.exists(stateFile)) {
            setOffset(0L);
            return;
        }

        String value = Files.readString(stateFile).trim();
        if (value.isEmpty()) {
            setOffset(0L);
            return;
        }

        setOffset(Long.parseLong(value));
    }

    public void save(Path stateFile) throws IOException {
        Files.writeString(
                stateFile,
                Long.toString(offset),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }
}
