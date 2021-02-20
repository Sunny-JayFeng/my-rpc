package client;

import com.google.gson.Gson;
import exception.ChannelCreateException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 客户端
 */
public class RPCClient {

    // 通道集合
    private static Map<String, SocketChannel> channelMap = new HashMap<>(16);
    // 选择器集合
    private static Map<Long, Selector> selectorMap = new HashMap<>(16);

    /**
     * 发送数据
     * @param ip ip地址
     * @param port 端口号
     * @param paramsMap 参数数据
     * @return 返回响应数据
     */
    public static String sendData(String ip, Integer port, Map<Object, Object> paramsMap) {
        String address = ip + ":" + port;
        SocketChannel channel = channelMap.get(address);
        if (channel == null) {
            channel = createChannel(ip, port);
            channelMap.put(address, channel);
        }
        Gson gson = new Gson();
        if (channel == null) {
            throw new ChannelCreateException("连接创建异常");
        }
        sendData(channel, gson.toJson(paramsMap, Map.class));
        return readData(selectorMap.get(Thread.currentThread().getId()));
    }

    /**
     * 发送数据
     * @param channel 通道
     * @param msg 数据
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
     * 读取响应数据
     * @param selector 选择器
     * @return 返回响应的数据
     */
    private static String readData(Selector selector) {
        try {
            int num = selector.select();
            if (num > 0) {
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    SocketChannel socketChannel = null;
                    try {
                        if (key.isReadable()) {
                            socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            StringBuilder msg = new StringBuilder();
                            while (socketChannel.read(buffer) > 0) {
                                msg.append(new String(buffer.array()));
                                buffer.flip();
                            }
                            return msg.toString();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                        socketChannel.close();
                    }
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个通道，打开选择器
     * @param ip ip地址
     * @param port 端口号
     * @return 返回 SocketChannel
     */
    private static SocketChannel createChannel(String ip, Integer port) {
        SocketChannel channel = null;
        try {
            Selector selector = Selector.open();
            SocketAddress address = new InetSocketAddress(ip, port);
            channel = SocketChannel.open(address);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            selectorMap.put(Thread.currentThread().getId(), selector);
            return channel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
