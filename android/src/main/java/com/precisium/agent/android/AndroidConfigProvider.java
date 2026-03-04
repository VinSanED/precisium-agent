package com.precisium.agent.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.precisium.agent.Config;
import com.precisium.agent.core.ConfigProvider;

public final class AndroidConfigProvider implements ConfigProvider {

    private final Context context;

    public AndroidConfigProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Config load() {
        SharedPreferences prefs = AgentPreferences.prefs(context);

        String filePath = prefs.getString(AgentPreferences.KEY_FILE_PATH, Config.DEFAULT_LOG_FILE);
        String endpoint = prefs.getString(AgentPreferences.KEY_ENDPOINT, Config.DEFAULT_ENDPOINT);
        // AgentId is read here for full Android-side configuration loading.
        prefs.getString(AgentPreferences.KEY_AGENT_ID, "");

        long interval = Config.DEFAULT_POLL_MS;
        String intervalStr = prefs.getString(AgentPreferences.KEY_INTERVAL, Long.toString(Config.DEFAULT_POLL_MS));
        if (intervalStr != null) {
            try {
                interval = Long.parseLong(intervalStr);
            } catch (NumberFormatException ignored) {
                interval = Config.DEFAULT_POLL_MS;
            }
        }

        return Config.arbitrary(filePath, endpoint, interval);
    }

    public String loadAgentId() {
        return AgentPreferences.prefs(context).getString(AgentPreferences.KEY_AGENT_ID, "");
    }
}
