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
        String uriStr = "http://192.168.1.102:3000/api/";
        String agentId = sc.nextLine();
        System.out.println(uriStr+agentId);

        System.out.println("Intervalo de monitoramento (ms):");
        long newInterval = sc.nextLong();

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
        AgentMain.initialize(activationClient, agent, sc, agentId);
        System.out.println("é isto.....");
        sc.close();
        
    }
    public static AgentStatus executeOnce(ActivationClient activationClient, AgentRuntime agent, String agentId){
        try {
            AgentStatus status = activationClient.fetchCommand(agentId);
            System.out.println(status);
            if(status == AgentStatus.STARTED) {
                agent.start();
            } else {
                agent.stop();
            } 
            return status;
        } catch (IOException e) {
            System.out.println("IO Exception: esse!;;; "+ e);
            agent.stop();
            return AgentStatus.STOPPED;
        } catch (InterruptedException e) {
            System.out.println("Interrupted Exception: "+ e);
            agent.stop();
            return AgentStatus.STOPPED;

        } catch (RuntimeException e){
            System.out.println("Arbtrary Exception: "+ e);
            agent.stop();
            return AgentStatus.STOPPED;
            
        }
    }

    public static void initialize(ActivationClient activationClient, AgentRuntime agent, Scanner sc, String agentId){
        
        while (true) {
                AgentStatus status =  executeOnce(activationClient, agent, agentId);
                
                switch (status) {
                    case STARTED ->{ 
                        try{
                            System.out.println("Started True, sleeping... ");
                            Thread.sleep(5000);
                        }catch(InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println("interrupted...");
                            return;
                        }
                    }
                    
                    case STOPPED -> {
                        agent.stop();
                        System.out.println("server desconected...");

                        System.out.println("Reiniciar: 1 ---- Encerrar: 0");
                        int newOption = sc.nextInt();
                        if (newOption==1){
                            System.out.println("Reiniciando...");
                            break;
                        } else {
                            System.out.println("Agent Stoped, Encerrando...");
                            return;
                        }
                    }
                    default -> {
                        System.out.println("erro de inicialização!");
                        return;
                    }
                }
            
        }
    }
}
