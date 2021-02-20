package server;

import com.google.gson.Gson;
import exception.NotFoundClassOrMethodException;
import handler.MethodInvokeHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 服务端
 */
public class RPCServer {

    private static Gson gson = new Gson();
    private static Selector selector = null; // 选择器
    private static ServerSocketChannel channel; // 通道

    /**
     * 开启监听
     * @param port 端口号
     */
    public static void startListener(Integer port) {
        System.out.println("开始监听客户端");
        if (selector == null) createSelector(port);
        while(true) {
            try {
                int num = selector.select();
                if (num == 0) continue;
                serverExecute(selector); // 调用服务
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建选择器
     * @param port 端口号
     */
    private static void createSelector(Integer port) {
        try {
            channel = ServerSocketChannel.open();
            selector = Selector.open();
            SocketAddress address = new InetSocketAddress(port);
            channel.socket().bind(address);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据（响应）
     * @param channel 通道
     * @param data 数据
     */
    private static void sendData(SocketChannel channel, Object data) {
        sendData(channel, gson.toJson(data));
    }

    /**
     * 发送数据（响应）
     * @param channel 通道
     * @param msg 格式化成JSON字符串的数据
     */
    private static void sendData(SocketChannel channel, String msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 服务调用
     * @param selector 选择器
     */
    private static void serverExecute(Selector selector) {
        Set<SelectionKey> set = selector.selectedKeys();
        Iterator<SelectionKey> iterator = set.iterator();
        while (iterator.hasNext()) {
            SocketChannel socketChannel = null;
            SelectionKey key = iterator.next();
            iterator.remove();
            try {
                if (key.isAcceptable()) {
                    SocketChannel clientChannel = channel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    continue;
                }
                if (key.isReadable()) {
                    socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 拼接请求数据字符串
                    StringBuilder msg = new StringBuilder();
                    while (socketChannel.read(buffer) > 0) {
                        msg.append(new String(buffer.array()));
                        buffer.flip();
                    }
                    // 服务方法执行
                    Object returnResult = executeMethod(msg.toString());
                    // 响应执行结果
                    sendData(socketChannel, returnResult);
                }
            } catch (IOException e) {
                e.printStackTrace();
                key.cancel(); // 取消注册关系
                if (socketChannel != null) {
                    try {
                        // 关闭通道
                        socketChannel.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 服务方法执行
     * @param msg 参数数据
     * @return 返回方法执行之后的返回值
     */
    private static Object executeMethod(String msg) {
        if (msg == null || msg.length() == 0) {
            throw new NotFoundClassOrMethodException("无法找到类或方法");
        }
        msg = msg.trim();
        Map<Object, Object> paramsMap = gson.fromJson(msg, Map.class);
        String className = "";
        String methodName = "";
        try {
            className = paramsMap.get("className").toString();
            methodName = paramsMap.get("methodName").toString();
        } catch (NullPointerException e) {
            throw new NotFoundClassOrMethodException("无法找到类或方法");
        }
        return MethodInvokeHandler.methodInvoke(className, methodName, paramsMap);
    }

}
