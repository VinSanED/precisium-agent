package com.precisium.agent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.precisium.agent.utils.FileState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileStateTest {

    @TempDir
    Path tempDir;

    @Test
    void setOffset_shouldClampNegativeValuesToZero() {
        FileState state = new FileState();

        state.setOffset(-10L);

        assertEquals(0L, state.getOffset());
    }

    @Test
    void saveAndLoad_shouldPersistOffsetValue() throws IOException {
        Path stateFile = tempDir.resolve("offset.state");

        FileState toSave = new FileState();
        toSave.setOffset(12345L);
        toSave.save(stateFile);

        FileState loaded = new FileState();
        loaded.load(stateFile);

        assertEquals(12345L, loaded.getOffset());
    }

    @Test
    void load_shouldResetToZeroWhenStateFileIsMissing() throws IOException {
        Path missingStateFile = tempDir.resolve("missing.state");

        FileState state = new FileState();
        state.setOffset(9L);
        state.load(missingStateFile);

        assertEquals(0L, state.getOffset());
    }

    @Test
    void load_shouldResetToZeroWhenStateFileIsEmpty() throws IOException {
        Path stateFile = tempDir.resolve("empty.state");
        Files.writeString(stateFile, "   \n");

        FileState state = new FileState();
        state.setOffset(9L);
        state.load(stateFile);

        assertEquals(0L, state.getOffset());
    }
}
