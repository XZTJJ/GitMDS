package com.zhouhc.ssewesocket.compent;

import com.zhouhc.ssewesocket.cInt.SendMSInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//用于保存所有的订阅的客户端信息，但是实例模式
public class ClientPipeline {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientPipeline.class);
    //单例对象
    private volatile static ClientPipeline clientPipeline;
    //保存的客户端
    private final Map<String, Set<SendMSInt>> clientMaps = new HashMap<String, Set<SendMSInt>>();

    private final Set<SendMSInt> emptySet = new HashSet<SendMSInt>();
    //订阅的客户端数量
    private volatile int count = 0;

    //私有化
    private ClientPipeline() {
    }

    //获取对象
    public static ClientPipeline getInstance() {
        if (clientPipeline == null) {
            synchronized (ClientPipeline.class) {
                if (clientPipeline == null)
                    clientPipeline = new ClientPipeline();
            }
        }
        return clientPipeline;
    }

    //添加对象
    public int addClient(String channel, SendMSInt client) {
        synchronized (this) {
            Set<SendMSInt> clients = clientMaps.getOrDefault(channel, emptySet);
            if (clients == emptySet) {
                clients = new HashSet<SendMSInt>();
                clientMaps.put(channel, clients);
            }
            boolean success = clients.add(client);
            if (success) {
                count += 1;
            }
            return count;
        }
    }

    //移出某个对象
    public int removeClient(String channel, SendMSInt client) {
        synchronized (this) {
            Set<SendMSInt> clients = clientMaps.getOrDefault(channel, emptySet);
            boolean success = clients.remove(client);
            if (success) {
                count -= 1;
                //清除掉没有的key
                if (clients.size() == 0)
                    clientMaps.remove(channel);
            }
            return count;
        }
    }

    //获取订阅的客户数
    public Set<SendMSInt> getClients(String channlName) {
        Set<SendMSInt> clients = null;
        //局部加锁，因此不能做到强一致性，但是满足需求
        synchronized (this) {
            clients = new HashSet(clientMaps.getOrDefault(channlName, emptySet));
        }
        return Collections.unmodifiableSet(clients);
    }

}
