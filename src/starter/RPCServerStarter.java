package starter;

import management.ServiceClassManagement;

/**
 * 服务端启动器
 * @author JayFeng
 * @date 2021/2/19
 */
public class RPCServerStarter {

    public void rpcServerStart(Integer port) {
        ServiceClassManagement.loadClassMap(this.getClass());
    }

}
