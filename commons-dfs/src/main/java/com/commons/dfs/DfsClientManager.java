package com.commons.dfs;

import com.commons.dfs.client.IDfsClient;
import com.commons.dfs.config.CommonDfsConfig;
import com.commons.dfs.operations.IDfsOperation;
import com.commons.metadata.exception.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C)
 * DfsClientManager
 * Author: jameslinlu
 */
public class DfsClientManager {

    private List<CommonDfsConfig> configs = null;
    private Map<String, IDfsClient> dfsClientMap = new HashMap<>();

    public DfsClientManager() {
    }

    public DfsClientManager(List<CommonDfsConfig> configs) {
        this.configs = configs;
    }

    /**
     * 初始化方法 缓存实例
     */
    public void initialize() {
        IDfsClient client = null;
        for (CommonDfsConfig config : configs) {
            client = DfsClientFactory.create(config);
            if (client != null) {
                dfsClientMap.put(config.getName(), client);
            }
        }
    }

    /**
     * 获取操作实例
     *
     * @param dfsKey 对config key对应
     * @return
     * @throws ServiceException
     */
    public IDfsOperation getOpts(String dfsKey) throws ServiceException {
        IDfsOperation operation = dfsClientMap.get(dfsKey).getOpts();
        return operation;
    }

    /**
     * 销毁实例
     */
    public void destroy() {
        for (IDfsClient client : dfsClientMap.values()) {
            try {
                client.destroy();
                client = null;
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        dfsClientMap = null;
    }

    /*
    //测试
    public static void main(String[] args) throws Exception {
        List<CommonDfsConfig> configs = new ArrayList<>();
        FastDfsConfig fastdfsConfig = new FastDfsConfig();
        fastdfsConfig.setName("DFS");
        fastdfsConfig.setProperties(new HashMap<String, Object>() {{
            put("tracker_server", "192.168.112.173:22122");
        }});
        configs.add(fastdfsConfig);
        long a = System.currentTimeMillis();

        DfsClientManager dfsClientManager = new DfsClientManager(configs);
        dfsClientManager.initialize();
        IDfsOperation dfs = dfsClientManager.getOpts("DFS");
        for (int i = 0; i < 1000; i++) {
            String path = dfs.createFile("yaolei a a a aa a a a a dsadas  @#@$%".getBytes(), "txt");
            System.out.println(i + "upload : " + path);
            System.out.println(i + "read   :" + new String(dfs.readFile(path)));
            System.out.println(i + "delete :" + dfs.deleteFile(path));
        }

        System.out.println(System.currentTimeMillis() - a);
    }
    */

}
