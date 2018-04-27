package com.ftvalue.jarslink;

import com.ftvalue.jarslink.service.ModuleRefreshSchedulerImpl;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class YmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(YmlUtils.class);

    public static Map<String, Object> readYml(JarFile jarFile) {
        try {
            JarEntry entry = jarFile.getJarEntry("application.yml");
            if(entry != null) {
                InputStream input = jarFile.getInputStream(entry);
                Yaml yaml = new Yaml();
                Map map = yaml.loadAs(input,Map.class);
                Map<String, Object> properties = getFlattenedMap(map);
                return properties;
            }
        } catch (IOException e) {
            LOGGER.info("read yml error [{}]",e);
        }
        return Maps.newHashMap();
    }

    private static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private static void buildFlattenedMap(Map<String, Object>  result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                }
                else {
                    key = path + '.' + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            }
            else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            }
            else if (value instanceof Collection) {
                // Need a compound key
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result,
                            Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            }
            else {
                result.put(key, (value != null ? value : ""));
            }
        }
    }
}
