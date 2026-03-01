package com.precisium.agent;

import java.io.IOException;
import java.util.Scanner;

import com.precisium.agent.controller.ActivationClient;
import com.precisium.agent.controller.HttpSender;
import com.precisium.agent.service.AgentRuntime;
import com.precisium.agent.service.LogReader;
import com.precisium.agent.service.Transport;
import com.precisium.agent.utils.AgentStatus;
import com.precisium.agent.utils.FileState;

public final class AgentMain {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Arquivo de log, caminho:");
        String filePathStr = sc.nextLine();

        System.out.println("Envio, uri:");
        String uriStr = sc.nextLine();

        System.out.println("Intervalo de monitoramento (ms):");
        Long newInterval = sc.nextLong();

        Config config =
            Config.arbitrary(filePathStr, uriStr, newInterval);
        Transport transport = new Transport();

        HttpSender sender = new HttpSender(transport);

        ActivationClient activationClient = new ActivationClient(config.getEndpoint(), transport);

        AgentRuntime agent = new AgentRuntime(
                config,
                new LogReader(),
                sender,
                new FileState()
        ); 

        System.out.println("Iniciando agente....");
        AgentMain.initialize(activationClient, agent, sc);
        System.out.println("é isto.....");
        sc.close();
        
    }

    public static void initialize(ActivationClient activationClient, AgentRuntime agent, Scanner sc){
        
        while (true) {
            try {
                AgentStatus agentStatus =  activationClient.fetchCommand("1");
                int option;
                Boolean coincidenceAgentStatus = agentStatus == AgentStatus.STARTED;
                if(coincidenceAgentStatus) {
                    option=1;
                } else {
                    option=2;
                } 
                switch (option) {
                    case 1 ->{ 
                        agent.start();
                        Thread.sleep(5000);
                    }
                    
                    case 2 -> {
                        agent.stop();
                        System.out.println("server desconected...");

                        System.out.println("Reiniciar: 1 ---- Encerrar: 0");
                        int newOption = sc.nextInt();
                        if (newOption==1){
                            System.out.println("Reiniciando...");
                            return;
                        } else {
                            System.out.println("Agent Stoped, Encerrando...");
                            break;
                        }
                    }
                    default -> System.out.println("erro de inicialização!");
                }
            } catch (IOException e) {
                System.out.println("IO Exception: "+ e);
                break;
            
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception: "+ e);
                break;
            
            } catch (Exception e){
                System.out.println("Arbtrary Exception: "+ e);
                break;
            }
        }
    }
}
