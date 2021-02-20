package test.client;

import client.RPCClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TestMyClient {

    public static void main(String[] args) {
        Map<Object, Object> map = new HashMap<>();
        map.put("className", "TestMyServer");
        map.put("methodName", "test");
        String result = null;
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()) {
            String str = scanner.nextLine();
            map.put(str, str);
            result = RPCClient.sendData("127.0.0.1", 9999, map);
            System.out.println(result);
        }
    }

}
