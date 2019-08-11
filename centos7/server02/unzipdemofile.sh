#/bin/bash

su demofile << EOF
cd /home/demofile
rm -rf ./*
EOF

mv /root/demofile.zip /home/demofile/

su demofile << EOF
cd /home/demofile
unzip -o -d ./ demofile.zip
EOF

rm -rf /home/demofile/demofile.zip
