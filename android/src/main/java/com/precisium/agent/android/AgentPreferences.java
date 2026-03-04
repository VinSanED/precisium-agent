package com.precisium.agent.android;

import android.content.Context;
import android.content.SharedPreferences;

final class AgentPreferences {
    static final String PREFS_NAME = "precisium_agent_prefs";
    static final String KEY_FILE_PATH = "filePath";
    static final String KEY_ENDPOINT = "endpoint";
    static final String KEY_INTERVAL = "interval";
    static final String KEY_AGENT_ID = "agentId";

    private AgentPreferences() {
    }

    static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    static String readAgentId(Context context) {
        return prefs(context).getString(KEY_AGENT_ID, "");
    }
}
