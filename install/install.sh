#!/data/data/com.termux/files/usr/bin/bash

echo "Installing Precisium Agent..."

pkg update -y
pkg install openjdk-21 -y

mkdir -p ~/.precisium

curl -L URI -o ~/.precisium/agent.jar

cat <<EOF > $PREFIX/bin/precisium-agent
#!/data/data/com.termux/files/usr/bin/bash
java -jar ~/.precisium/agent.jar "\$@"
EOF

chmod +x $PREFIX/bin/precisium-agent

echo "Installation complete."
echo "Run: precisium-agent"