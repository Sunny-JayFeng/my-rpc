package test.start;

import annotation.RPCServicePackage;
import starter.RPCServerStarter;

/**
 * @author JayFeng
 * @date 2021/2/19
 */
@RPCServicePackage({"test.server"})
public class RPCStart extends RPCServerStarter {

    public static void main(String[] args) {
        RPCStart rpcStart = new RPCStart();
        rpcStart.rpcServerStart(9999);
    }

}
