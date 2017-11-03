#!/usr/bin/env sh

version=$(echo $(ls) | sed -n 's/.* \(.*\)\.zip.*/\1/p')
zipfile=$version.zip

echo $version

# stop old server
sudo systemctl stop webServer
sudo systemctl disable webServer

# redirect 8080 to 80
sudo iptables -A PREROUTING -t nat -i ens4 -p tcp --dport 80 -j REDIRECT --to-port 8080

# install the latest snapshot
unzip -o $zipfile
rm ./startserver.sh
ln -s ./$version/bin/CSSE477_Web_Server ./startserver.sh
mv ./$version/application.properties .
cp -rp ./$version/web .\

# install the default plugins
cp -rp ./$version/plugins ./

# stop old server
sudo systemctl stop webServer
sudo systemctl disable webServer

# start new server
sudo systemctl daemon-reload
sudo systemctl start webServer
sudo systemctl enable webServer

# clean up zip file
rm *.zip
