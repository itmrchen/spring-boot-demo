#/bin/bash

rm -rf /root/demofile.zip

su demofile << EOF
cd /home/demofile
zip -r demofile.zip ./
EOF

cd /root
mv /home/demofile/demofile.zip ./
