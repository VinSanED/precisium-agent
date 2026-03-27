package com.precisium.agent.desktop;

import java.util.Scanner;

import com.precisium.agent.Config;
import com.precisium.agent.core.ConfigProvider;

public final class CliConfigProvider implements ConfigProvider {

    private final Scanner scanner;

    public CliConfigProvider() {
        this(new Scanner(System.in));
    }

    CliConfigProvider(Scanner scanner) {
        this.scanner = scanner;
    }

    public String loadAgentId() {
        System.out.println("AgentID: ");
        String agentId = scanner.nextLine();
        return agentId;
    }

    @Override
    public Config load() {
        System.out.println("Arquivo de log, caminho: (default: ./logs/AppLog.txt)");
        String filePathStr = scanner.nextLine();

        System.out.println("Endpoint base: https://precisium.vercel.app/api/");
        String endpoint = scanner.nextLine();

        System.out.println("Intervalo de monitoramento (ms): (default: 5000)");
        long newInterval = scanner.nextLong();
        scanner.nextLine();
        System.out.println("------");

        return Config.arbitrary(filePathStr, endpoint, newInterval, loadAgentId());
    }

    public boolean shouldRestart() {
        System.out.println("Reiniciar: 1 ---- Encerrar: 0");
        int newOption = scanner.nextInt();
        scanner.nextLine();
        return newOption == 1;
    }
}
