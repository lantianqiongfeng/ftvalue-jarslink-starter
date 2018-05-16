package com.ftvalue.jarslink.service;

import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.api.impl.AbstractModuleRefreshScheduler;
import com.ftvalue.jarslink.utils.DateUtils;
import com.ftvalue.jarslink.utils.YmlUtils;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ModuleRefreshSchedulerImpl extends AbstractModuleRefreshScheduler {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModuleRefreshSchedulerImpl.class);

    private String jarPath;

    private String configPath;

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

    public ModuleRefreshSchedulerImpl(String jarPath, String configPath,int jarInitialDelay, int jarRefreshDelay) {
        this.jarPath = jarPath;
        this.configPath = configPath;
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
            Map<String,ModuleConfig> map = new HashMap<>();
            for (int i = 0; i < files.length; i++) {
                File childFile = files[i];
                try {
                    JarFile jarFile = new JarFile(childFile);
                    Manifest manifest = jarFile.getManifest();
                    Attributes attributes = manifest.getMainAttributes();
                    String jarName = attributes.getValue("jar-name");
                    String jarVersion = attributes.getValue("jar-version");
                    String basicPackage = attributes.getValue("jar-package");
                    Map<String, Object> result = YmlUtils.readYml(jarFile,configPath,jarName);
                    ModuleConfig config = buildModuleConfig(new String[]{childFile.getAbsolutePath(), jarName,
                            jarVersion,basicPackage},result);
                    String key = config.getName();
                    if(map.containsKey(key)) {
                        ModuleConfig moduleConfig = map.get(key);
                        if(isNewModule(config,moduleConfig)) {
                            map.put(key,config);
                        }
                    }else {
                        map.put(key,config);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(!map.isEmpty()) {
                for(Map.Entry<String,ModuleConfig> entry : map.entrySet()) {
                    configs.add(entry.getValue());
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
            if(!StringUtils.isEmpty(paras[3])){
                moduleConfig.addScanPackage(paras[3]);
            }
            moduleConfig.withOverridePackages(ImmutableList.of(paras[3]));
            moduleConfig.setModuleUrl(ImmutableList.of(demoModule));
            moduleConfig.setProperties(properties);
            return moduleConfig;
        } catch (MalformedURLException e) {
            LOGGER.error("build module config error",e);
        }
        return moduleConfig;
    }

    //判断是否最新
    private boolean isNewModule(ModuleConfig config,ModuleConfig moduleConfig) {
        boolean isExists = false;
        String moduleVersion = moduleConfig.getVersion();
        String configVersion = config.getVersion();
        Date moduleDate = DateUtils.parseDate(moduleVersion.split("-")[1], DateUtils.DATE_FORMATE_LONG);
        Date configDate = DateUtils.parseDate(configVersion.split("-")[1], DateUtils.DATE_FORMATE_LONG);
        if(configDate.compareTo(moduleDate) > 0) {
            isExists = true;
        }
        return isExists;
    }

}
