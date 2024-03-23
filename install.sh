#!/bin/bash
# this code is for installing the project in a new POS machine
# It install pcsc, java, drivers, and the jcapuchino .jar file as a systemctl service
# It also creates a user for the service and sets the service to start at boot
# It install also the chromium browser
sudo apt update && sudo apt upgrade -y

sudo mkdir /var/jcapuchino

# Install pcsc and lib
sudo apt-get install libpcsclite1 pcscd pcsc-tools -y

# Install java
sudo apt-get install openjdk-17-jdk -y

# Install chromium
sudo apt-get install chromium-browser -y

# Install drivers
sudo apt-get install libpcsclite1 pcscd pcsc-tools -y

# download the jcapuchino.jar file at https://pic.assos.utc.fr/deploy/jcapuchino.jar
# and place it in the /var/jcapuchino/ folder
wget -O /tmp/jcapuchino.jar https://pic.assos.utc.fr/deploy/jcapuchino.jar

cp /tmp/jcapuchino.jar /var/jcapuchino/jcapuchino.jar
hash=$(sha256sum /var/jcapuchino/jcapuchino.jar | awk '{print $1}')
echo $hash > /var/jcapuchino/.hash
# Create a user for the service
sudo useradd -r -s /bin/false jcapuchino

# Create a service file
sudo touch /etc/systemd/system/jcapuchino.service
sudo echo "[Unit]
Description=JCapuchino
After=network.target

[Service]
Type=simple
User=jcapuchino
ExecStart=/usr/bin/java -jar /var/jcapuchino/jcapuchino.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target" > /etc/systemd/system/jcapuchino.service

# Enable the service
sudo systemctl daemon-reload
sudo systemctl enable jcapuchino.service
sudo systemctl start jcapuchino.service
sudo systemctl status jcapuchino.service

# add chromium to the autostart of "https://beethoven.picasso-utc.fr/sales" raspbian os (start in kiosk mode incognito)
sudo echo "chromium-browser --kiosk --incognito https://beethoven.picasso-utc.fr/sales" >> /etc/xdg/lxsession/LXDE-pi/autostart