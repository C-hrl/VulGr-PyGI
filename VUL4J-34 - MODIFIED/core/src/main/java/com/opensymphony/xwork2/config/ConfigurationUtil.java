
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;


public class ConfigurationUtil {

    private static final Logger LOG = LogManager.getLogger(ConfigurationUtil.class);

    private ConfigurationUtil() {
    }

    
    public static List<PackageConfig> buildParentsFromString(Configuration configuration, String parent) {
        List<String> parentPackageNames = buildParentListFromString(parent);
        List<PackageConfig> parentPackageConfigs = new ArrayList<>();
        for (String parentPackageName : parentPackageNames) {
            PackageConfig parentPackageContext = configuration.getPackageConfig(parentPackageName);

            if (parentPackageContext != null) {
                parentPackageConfigs.add(parentPackageContext);
            }
        }

        return parentPackageConfigs;
    }

    
    public static List<String> buildParentListFromString(String parent) {
        if (StringUtils.isEmpty(parent)) {
            return Collections.emptyList();
        }

        StringTokenizer tokenizer = new StringTokenizer(parent, ",");
        List<String> parents = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            String parentName = tokenizer.nextToken().trim();

            if (StringUtils.isNotEmpty(parentName)) {
                parents.add(parentName);
            }
        }

        return parents;
    }
}
