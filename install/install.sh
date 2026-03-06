#!/usr/bin/env bash

set -e

echo "🚀 Iniciando a instalação do Precisium Agent..."

if ! command -v curl >/dev/null; then
    echo "❌ curl não encontrado. Instale curl primeiro."
    exit 1
fi

echo "🚀 detectando ambiente..."
if [[ -d /data/data/com.termux/files/usr ]]; then
    echo "📦 Ambiente: Termux detectado."
    pkg update -y
    pkg install openjdk-21  -y
else
    echo "📦 Ambiente: Linux detectado."

    if command -v pacman >/dev/null; then
        sudo pacman -S --noconfirm --needed jdk21-openjdk
    elif command -v apt >/dev/null; then
        sudo apt update
        sudo apt install -y openjdk-21-jdk
    else
        echo "⚠️ Gerenciador de pacotes não suportado automaticamente."
        echo "Certifique-se de ter Java 21 e Curl instalados."
    fi
fi

echo "📁 Criando diretórios..."
PREFIX="$HOME/.local"
BIN_DIR="$PREFIX/bin"
AGENT_DIR="$HOME/.precisium"

mkdir -p "$BIN_DIR"
mkdir -p "$AGENT_DIR"

echo "📥 Baixando o Precisium Agent..."
URL="https://raw.githubusercontent.com/VinSanED/precisium-agent/main/install/precisium-agent.jar"

if ! curl -fsSL "$URL" -o "$AGENT_DIR/agent.jar"; then
    echo "❌ Erro ao baixar o arquivo. Verifique se a URL está correta e pública."
    exit 1
fi

echo "⚙️ Configurando o comando 'precisium-agent'..."
cat <<EOF > "$BIN_DIR/precisium-agent"
#!/usr/bin/env bash

java -jar "$AGENT_DIR/agent.jar" "\$@"
EOF

chmod +x "$BIN_DIR/precisium-agent"

echo ""
echo "✅ Instalação concluída com sucesso!"

if [[ ":$PATH:" != *":$BIN_DIR:"* ]]; then
    echo "📢 AVISO: Adicione '$BIN_DIR' ao seu PATH para rodar o comando de qualquer lugar."
    echo "   Dica: Adicione 'export PATH=\$PATH:$BIN_DIR' ao seu ~/.bashrc ou ~/.zshrc"
else
    echo "🎉 Tudo pronto! Digite 'precisium-agent' para começar."
fi
