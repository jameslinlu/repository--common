package com.commons.configuration.adapter;

import com.commons.configuration.ICommonConfiguration;
import com.commons.configuration.model.CommonConfig;
import com.commons.configuration.model.CommonConfigBundle;
import com.commons.configuration.support.ICommonConfigurationExtract;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.MachineConfig;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 监听spring初始化并根据配置把需持久化的文件存储
 * Copyright (C)
 * CommonConfigurationAdapter
 * Author: jameslinlu
 */
public class CommonConfigurationAdapter implements InitializingBean {


    private static Logger logger = LoggerFactory.getLogger(CommonConfigurationAdapter.class);

    //持久化配置文件合集
    private Resource[] locations;
    //持久化配置域
    private MachineConfig configMachine;
    //统一配置操作接口
    private ICommonConfiguration commonConfig;
    //自定义提取接口
    private ICommonConfigurationExtract configExtract;
    //默认超时时间
    private Integer expireHour = 1;
    private Boolean enableRegister = true;
    private Boolean enableListen = true;

    public void setConfigExtract(ICommonConfigurationExtract configExtract) {
        this.configExtract = configExtract;
    }

    public void setEnableListen(Boolean enableListen) {
        this.enableListen = enableListen;
    }

    public void setEnableRegister(Boolean enableRegister) {
        this.enableRegister = enableRegister;
    }

    public void setExpireHour(Integer expireHour) {
        this.expireHour = expireHour;
    }

    public void setCommonConfig(ICommonConfiguration commonConfig) {
        this.commonConfig = commonConfig;
    }

    public void setLocations(Resource[] locations) {
        this.locations = locations;
    }

    public void setConfigMachine(MachineConfig configMachine) {
        this.configMachine = configMachine;
    }

    //本机配置合集
    private List<CommonConfigBundle> configBundles = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        commonConfig.setMachineConfig(this.configMachine);
        if (configExtract != null) {
            commonConfig.setExtract(this.configExtract);
        }
        configBundles = new ArrayList<>();
        CommonConfigBundle configBundle = null;
        if (this.locations != null) {
            Properties props = null;
            List<CommonConfig> configs = null;
            CommonConfig config = null;
            for (Resource location : this.locations) {
                if (logger.isInfoEnabled()) {
                    logger.info("Loading properties file from " + location);
                }
                try {
                    props = new Properties();
                    PropertiesLoaderUtils.fillProperties(props, location);
                } catch (IOException ex) {
                    logger.error("Could not load properties from " + location + ": " + ex.getMessage());
                }
                configBundle = new CommonConfigBundle();
                configBundle.setExpire(DateTime.now().plusHours(expireHour).toDate().getTime());
                configBundle.setGroup(location.getFilename());
                configs = new ArrayList();
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    config = new CommonConfig();
                    config.setKey(entry.getKey().toString());
                    config.setValue(entry.getValue().toString());
                    configs.add(config);
                }
                if (!configs.isEmpty()) {
                    configBundle.setConfigs(configs);
                    configBundles.add(configBundle);
                }
            }
        }
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        if (!event.getSource().toString().startsWith("Root")) {
            return;
        }
        logger.debug("Configuration Listener Start. ");
        try {
            //注册配置
            if (enableRegister) {
                commonConfig.extract();
                commonConfig.register(this.configBundles);
            }
            if (enableListen) {
                //监听变更
                commonConfig.listen();
            }
        } catch (ServiceException e) {
            logger.error("Configuration Store Publish Fail", e);
        }
    }
}
