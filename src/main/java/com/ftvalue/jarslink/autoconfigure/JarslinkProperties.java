package com.ftvalue.jarslink.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ftvalue.jarslink")
public class JarslinkProperties {

    private String jarPath;

    private String configPath;

    private int jarInitialDelay = 20;

    private int jarRefreshDelay = 60;

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public int getJarInitialDelay() {
        return jarInitialDelay;
    }

    public void setJarInitialDelay(int jarInitialDelay) {
        this.jarInitialDelay = jarInitialDelay;
    }

    public int getJarRefreshDelay() {
        return jarRefreshDelay;
    }

    public void setJarRefreshDelay(int jarRefreshDelay) {
        this.jarRefreshDelay = jarRefreshDelay;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
