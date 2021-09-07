package com.woowacourse.pickgit.config.database;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datasource")
public class MasterDataSourceProperties {

    private final Map<String, Slave> slave = new HashMap<>();

    private String url;
    private String username;
    private String password;

    public Map<String, Slave> getSlave() {
        return slave;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class Slave {

        private String name;
        private String url;
        private String username;
        private String password;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
