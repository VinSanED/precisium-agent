package com.precisium.agent.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public final class MainActivity extends Activity {

    private EditText filePathInput;
    private EditText endpointInput;
    private EditText intervalInput;
    private EditText agentIdInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePathInput = findViewById(R.id.filePathInput);
        endpointInput = findViewById(R.id.endpointInput);
        intervalInput = findViewById(R.id.intervalInput);
        agentIdInput = findViewById(R.id.agentIdInput);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        loadValues();

        startButton.setOnClickListener(v -> {
            saveValues();
            Intent intent = new Intent(this, AgentService.class);
            intent.setAction(AgentService.ACTION_START);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        });

        stopButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgentService.class);
            intent.setAction(AgentService.ACTION_STOP);
            startService(intent);
        });
    }

    private void loadValues() {
        SharedPreferences prefs = AgentPreferences.prefs(this);
        filePathInput.setText(prefs.getString(AgentPreferences.KEY_FILE_PATH, ""));
        endpointInput.setText(prefs.getString(AgentPreferences.KEY_ENDPOINT, ""));
        intervalInput.setText(prefs.getString(AgentPreferences.KEY_INTERVAL, "1000"));
        agentIdInput.setText(prefs.getString(AgentPreferences.KEY_AGENT_ID, ""));
    }

    private void saveValues() {
        SharedPreferences.Editor editor = AgentPreferences.prefs(this).edit();
        editor.putString(AgentPreferences.KEY_FILE_PATH, filePathInput.getText().toString().trim());
        editor.putString(AgentPreferences.KEY_ENDPOINT, endpointInput.getText().toString().trim());
        editor.putString(AgentPreferences.KEY_INTERVAL, intervalInput.getText().toString().trim());
        editor.putString(AgentPreferences.KEY_AGENT_ID, agentIdInput.getText().toString().trim());
        editor.apply();
    }
}
