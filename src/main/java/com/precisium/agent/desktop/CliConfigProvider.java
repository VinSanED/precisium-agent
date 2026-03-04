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

    @Override
    public Config load() {
        System.out.println("Arquivo de log, caminho:");
        String filePathStr = scanner.nextLine();

        System.out.println("Endpoint base (ex: http://precisium.vercel.app/api/):");
        String endpoint = scanner.nextLine();

        System.out.println("Intervalo de monitoramento (ms):");
        long newInterval = scanner.nextLong();
        scanner.nextLine();

        return Config.arbitrary(filePathStr, endpoint, newInterval);
    }

    public String loadAgentId() {
        System.out.println("AgentID: ");
        String agentId = scanner.nextLine();
        return agentId;
    }

    public boolean shouldRestart() {
        System.out.println("Reiniciar: 1 ---- Encerrar: 0");
        int newOption = scanner.nextInt();
        scanner.nextLine();
        return newOption == 1;
    }
}
