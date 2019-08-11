package com.example.ftp;

import com.jcraft.jsch.ChannelSftp;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sftp")
public class SftpProperties {

    private String host;
    private int port = 22;
    private String username = "root";
    private String password = "root";
    private Pool pool = new Pool();

    public static class Pool extends GenericObjectPoolConfig<ChannelSftp> {

        private int maxTotal = DEFAULT_MAX_TOTAL;
        private int maxIdle = DEFAULT_MAX_IDLE;
        private int minIdle = DEFAULT_MIN_IDLE;

        public Pool() {
            super();
        }
        @Override
        public int getMaxTotal() {
            return maxTotal;
        }
        @Override
        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }
        @Override
        public int getMaxIdle() {
            return maxIdle;
        }
        @Override
        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }
        @Override
        public int getMinIdle() {
            return minIdle;
        }
        @Override
        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

    }

}
