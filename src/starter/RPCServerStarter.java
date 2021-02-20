package starter;

import management.ServiceClassManagement;
import server.RPCServer;

/**
 * 服务端启动器
 * @author JayFeng
 * @date 2021/2/19
 */
public class RPCServerStarter {

    /**
     * 启动服务
     * @param port 端口号
     */
    public void rpcServerStart(Integer port) {
        // 加载服务类
        ServiceClassManagement.loadClassMap(this.getClass());
        // 启动服务监听
        RPCServer.startListener(port);
    }

}
