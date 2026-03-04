package com.precisium.agent.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.precisium.agent.AgentMain;
import com.precisium.agent.Config;
import com.precisium.agent.controller.ActivationClient;
import com.precisium.agent.controller.HttpSender;
import com.precisium.agent.service.AgentRuntime;
import com.precisium.agent.service.LogReader;
import com.precisium.agent.service.Transport;
import com.precisium.agent.utils.AgentStatus;
import com.precisium.agent.utils.FileState;

public final class AgentService extends Service {

    public static final String ACTION_START = "com.precisium.agent.android.action.START";
    public static final String ACTION_STOP = "com.precisium.agent.android.action.STOP";

    private static final String CHANNEL_ID = "precisium_agent_channel";
    private static final int NOTIFICATION_ID = 1001;

    private volatile boolean loopRunning;
    private Thread workerThread;
    private AgentRuntime agentRuntime;
    private ActivationClient activationClient;
    private String agentId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : ACTION_START;

        if (ACTION_STOP.equals(action)) {
            stopAgent();
            return START_NOT_STICKY;
        }

        startInForeground();
        startAgent();
        return START_STICKY;
    }

    private synchronized void startAgent() {
        if (loopRunning) {
            return;
        }

        AndroidConfigProvider provider = new AndroidConfigProvider(this);
        Config config = provider.load();
        agentId = provider.loadAgentId();

        Transport transport = new Transport();
        activationClient = new ActivationClient(config.getEndpoint(), transport);

        agentRuntime = new AgentRuntime(
            config,
            new LogReader(),
            new HttpSender(transport),
            new FileState()
        );

        loopRunning = true;
        workerThread = new Thread(this::runLoop, "PrecisiumAgentLoop");
        workerThread.start();
    }

    private void runLoop() {
        try {
            while (loopRunning) {
                AgentStatus status = AgentMain.executeOnce(activationClient, agentRuntime, agentId);

                switch (status) {
                    case STARTED -> sleepLoop();
                    case STOPPED -> {
                        agentRuntime.stop();
                        stopAgent();
                    }
                    default -> stopAgent();
                }
            }
        } finally {
            if (agentRuntime != null) {
                agentRuntime.stop();
            }
        }
    }

    private void sleepLoop() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stopAgent();
        }
    }

    public synchronized void stopAgent() {
        loopRunning = false;

        if (agentRuntime != null) {
            agentRuntime.stop();
        }

        if (workerThread != null) {
            workerThread.interrupt();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        stopAgent();
        super.onDestroy();
    }

    private void startInForeground() {
        createNotificationChannel();
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Precisium Agent")
            .setContentText("Monitorando logs em segundo plano")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Precisium Agent",
            NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
