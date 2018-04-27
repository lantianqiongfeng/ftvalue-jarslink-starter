package com.ftvalue.jarslink.service;

import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.api.impl.AbstractModuleRefreshScheduler;
import com.ftvalue.jarslink.YmlUtils;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ModuleRefreshSchedulerImpl extends AbstractModuleRefreshScheduler {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModuleRefreshSchedulerImpl.class);

    private String jarPath;

    private int jarInitialDelay;

    private int jarRefreshDelay;

    @Override
    public void setModuleManager(ModuleManager moduleManager) {
        super.setModuleManager(moduleManager);
    }

    @Override
    public void setModuleLoader(ModuleLoader moduleLoader) {
        super.setModuleLoader(moduleLoader);
    }

    public ModuleRefreshSchedulerImpl(String jarPath, int jarInitialDelay, int jarRefreshDelay) {
        this.jarPath = jarPath;
        this.jarInitialDelay = jarInitialDelay;
        this.jarRefreshDelay = jarRefreshDelay;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setInitialDelay(jarInitialDelay);
        super.setRefreshDelay(jarRefreshDelay);
        super.afterPropertiesSet();
    }

    @Override
    public List<ModuleConfig> queryModuleConfigs() {
        List<ModuleConfig> configs = new ArrayList<>();
        File jarLibFile=new File(jarPath);
        if(jarLibFile.exists()){
            File[] files = jarLibFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                File childFile = files[i];
                try {
                    JarFile jarFile = new JarFile(childFile);
                    Manifest manifest = jarFile.getManifest();
                    Attributes attributes = manifest.getMainAttributes();
                    String jarName = attributes.getValue("jar-name");
                    String jarVersion = attributes.getValue("jar-version");
                    String basicPackage = attributes.getValue("jar-package");
                    Map<String, Object> result = YmlUtils.readYml(jarFile);
                    ModuleConfig config = buildModuleConfig(new String[]{childFile.getAbsolutePath(), jarName,
                            jarVersion,basicPackage},result);
                    configs.add(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configs;
    }

    private static ModuleConfig buildModuleConfig(String[] paras,Map<String, Object> properties) {
        ModuleConfig moduleConfig = new ModuleConfig();
        try {
            URL demoModule = new URL("file", "", -1, paras[0]);
            moduleConfig.setName(paras[1]);
            moduleConfig.setEnabled(true);
            moduleConfig.setVersion(paras[2]);
            moduleConfig.addScanPackage(paras[3]);
            moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
            moduleConfig.setProperties(properties);
            return moduleConfig;
        } catch (MalformedURLException e) {
            LOGGER.error("build module config error",e);
        }
        return moduleConfig;
    }

}
