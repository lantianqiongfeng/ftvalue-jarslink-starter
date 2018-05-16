package com.ftvalue.jarslink.autoconfigure;

import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import com.ftvalue.jarslink.service.ModuleRefreshSchedulerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableConfigurationProperties(JarslinkProperties.class)
@ImportResource("classpath*:META-INF/spring/jarslink.xml")
public class JarslinkParseAutoConfiguration {

    @Autowired
    private JarslinkProperties jarslinkProperties;

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private ModuleLoader moduleLoader;


    @Bean
    public ModuleRefreshSchedulerImpl buildModuleRefreshScheduler() {
        ModuleRefreshSchedulerImpl moduleRefreshScheduler =
                new ModuleRefreshSchedulerImpl(jarslinkProperties.getJarPath(),
                        jarslinkProperties.getConfigPath(),
                        jarslinkProperties.getJarInitialDelay(),
                        jarslinkProperties.getJarRefreshDelay());
        moduleRefreshScheduler.setModuleLoader(moduleLoader);
        moduleRefreshScheduler.setModuleManager(moduleManager);
        return moduleRefreshScheduler;
    }

}
