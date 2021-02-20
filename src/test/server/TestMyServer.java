package test.server;

import annotation.RPCService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JayFeng
 * @date 2021/2/19
 */
@RPCService("TestMyServer")
public class TestMyServer {

    public String test(Map<Object, Object> paramsMap) {
        System.out.println("请求paramsMap: " + paramsMap);
        return "我响应了abc";
    }

}
