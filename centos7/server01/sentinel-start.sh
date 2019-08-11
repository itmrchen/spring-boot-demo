#/bin/bash
/usr/local/bin/redis-sentinel /etc/redis/26379/sentinel.conf --sentinel
/usr/local/bin/redis-sentinel /etc/redis/26380/sentinel.conf --sentinel
/usr/local/bin/redis-sentinel /etc/redis/26381/sentinel.conf --sentinel
