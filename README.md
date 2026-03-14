# Precisium Agent

**Precisium Agent** é um agente leve de monitoramento que coleta eventos e logs do sistema e os envia para o backend do **Precisium** para processamento, análise e visualização.

O objetivo do agente é permitir **coleta confiável de dados operacionais** em ambientes distribuídos, mantendo baixo consumo de recursos e instalação simples.

O agente pode rodar em:

* Linux
* Termux (Android)
* servidores ou máquinas locais

---

# Arquitetura

O agente funciona em três etapas principais:

### 1. Coleta

* leitura de arquivos de log
* captura de eventos relevantes

### 2. Processamento

* parsing dos eventos
* controle de offset dos arquivos

### 3. Envio

* envio periódico para o backend Precisium via HTTPS
* controle de retry e backoff

Fluxo simplificado:

```
Logs do sistema
       ↓
Precisium Agent
       ↓
Processamento / parsing
       ↓
Envio HTTPS
       ↓
Backend Precisium
       ↓
Dashboard / Monitoramento
```

---

# Funcionalidades

* leitura contínua de logs
* controle de offset de arquivos
* envio de eventos para API
* retry automático em falhas
* baixo consumo de memória
* compatível com Linux e Termux

---

# Requisitos

* Java 21 ou superior
* curl
* conexão com internet

No **Termux**, o instalador instalará o openjdk-21 automaticamente.
Para instalar o 'curl' no termux, 
```bash
pkg install curl -y
```
---

# Instalação rápida (Termux)

A forma mais simples de instalar é usando o script de instalação:

```bash
curl -fsSL https://raw.githubusercontent.com/VinSanED/precisium-agent/main/install/install.sh | bash
```

Esse comando irá:

1. instalar dependências
2. baixar o agente
3. configurar diretórios
4. iniciar o agente

---

# O que o instalador faz

O script `install.sh` executa os seguintes passos:

```
1. instala dependências
2. cria diretório do agente
3. baixa o .jar
4. cria um comando global precisium-agent
5. torna esse comando executável
```
---
# Execução

Para iniciar o agente basta executar o comando:

```
precisium-agent
```
---

# Remover o agente

Para remover:

```bash
rm -rf ~/.precisium
```

---

# Segurança

O agente:

* envia dados apenas para o endpoint configurado
* não executa comandos remotos
* não coleta dados pessoais, a menos q estes estejam explicitos no arquivo monitorado pelo agente.

---

# Funcionamento do agente

* controle remoto de ativação e desativação do agente.
* leitura periodica dos logs.
* compressão de payload.
* envia apenas o delta do arquivo de log monitorado.

---

# Licença

MIT License
