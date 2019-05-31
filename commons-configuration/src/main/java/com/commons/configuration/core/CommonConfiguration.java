package com.commons.configuration.core;

import com.commons.common.utils.PropUtil;
import com.commons.configuration.ICommonConfiguration;
import com.commons.configuration.model.CommonConfig;
import com.commons.configuration.model.CommonConfigBundle;
import com.commons.configuration.store.ICommonConfigStore;
import com.commons.configuration.support.ICommonConfigurationExtract;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.MachineConfig;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C)
 * CommonConfiguration
 * Author: jameslinlu
 */
public class CommonConfiguration implements ICommonConfiguration {


    private static Logger logger = LoggerFactory.getLogger(CommonConfiguration.class);

    //持久化接口
    private ICommonConfigStore configStore;
    //持久化配置域
    private MachineConfig configMachine;
    //持久化配置信息
    private List<CommonConfigBundle> bundles = new ArrayList<>();
    //配置抽取接口
    private ICommonConfigurationExtract configurationExtract;
    //允许缓存的 配置集群域 名称
    private List<String> acceptConfigDomain;

    public void setConfigStore(ICommonConfigStore configStore) {
        this.configStore = configStore;
    }

    public void setAcceptConfigDomain(List<String> acceptConfigDomain) {
        this.acceptConfigDomain = acceptConfigDomain;
    }

    @Override
    public void setMachineConfig(MachineConfig config) {
        this.configMachine = config;
    }

    @Override
    public void setExtract(ICommonConfigurationExtract extract) throws ServiceException {
        this.configurationExtract = extract;
    }

    @Override
    public void listen() throws ServiceException {
        this.configStore.listen(this);
    }

    @Override
    public void extract() throws ServiceException {
        if (configurationExtract != null) {
            this.register(configurationExtract.processExtract());
        }
    }

    @Override
    public void register(List<CommonConfigBundle> bundles) throws ServiceException {
        if (bundles != null) {
            try {
                for (CommonConfigBundle bundle : bundles) {
                    //配置发布时增加domain ip port 唯一标示
                    bundle.setDomain(configMachine.getDomain());
                    bundle.setIp(configMachine.getIp());
                    bundle.setPort(configMachine.getPort());
                    if (bundle.getExpire() == null) {
                        bundle.setExpire(DateTime.now().plusHours(1).toDate().getTime());
                    }
                    this.configStore.publish(bundle);
                }
                this.bundles.addAll(bundles);

            } catch (ServiceException e) {
                logger.error("Configuration Store Publish Fail", e);
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public List<CommonConfigBundle> getPersistentConfigs() throws ServiceException {
        return this.bundles;
    }

    private List<CommonConfig> validateAcceptDomain(CommonConfigBundle bundle) {
        if (acceptConfigDomain == null || (acceptConfigDomain != null && acceptConfigDomain.contains(bundle.getDomain()))) {
            List<CommonConfig> configs = bundle.getConfigs();
            if (configs != null && !configs.isEmpty()) {
                return configs;
            }
        }
        return null;
    }

    @Override
    public void refreshLocalCache(CommonConfigBundle bundle) throws ServiceException {
        List<CommonConfig> configs = this.validateAcceptDomain(bundle);
        if (configs == null) {
            return;
        }
        for (CommonConfig config : configs) {
            logger.debug("Read Persistent Props {} {}", config.getKey(), config.getValue());
            //同集群内直接存储 不区分domain
            if (configMachine.getDomain().equalsIgnoreCase(bundle.getDomain())) {
                PropUtil.put(config.getKey(), config.getValue());
            }
            //无论是否本集群 存储domain区分配置 通过PropUtil getClusterCode获取
            PropUtil.put(config.getKey(), config.getValue(), bundle.getDomain());

        }
    }

    @Override
    public void deleteLocalCache(CommonConfigBundle bundle) throws ServiceException {
        List<CommonConfig> configs = this.validateAcceptDomain(bundle);
        if (configs == null) {
            return;
        }
        for (CommonConfig config : configs) {
            logger.debug("Read Persistent Props {} {}", config.getKey(), config.getValue());
            if (configMachine.getDomain().equalsIgnoreCase(bundle.getDomain())) {
                PropUtil.delete(config.getKey());
            }
            //无论是否本集群 存储domain区分配置 通过PropUtil getClusterCode获取
            PropUtil.delete(config.getKey(), bundle.getDomain());

        }
    }
}
