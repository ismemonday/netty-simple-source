import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/**
 * @author mgd [maoguidong@standard-robots.com]
 * @data 2022/6/14 下午3:28
 */
public class Socket1SelectorTest {
    public static void main(String[] args) throws IOException {
        SelectorProvider provider = SelectorProvider.provider();
        //启动一个ssc
        ServerSocketChannel ssc = provider.openServerSocketChannel();
        AbstractSelector selector = provider.openSelector();
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8888));
        ssc.setOption(StandardSocketOptions.SO_RCVBUF, 65535);
        //启动第二个ssc
        ServerSocketChannel ssc2 = provider.openServerSocketChannel();
        ssc2.configureBlocking(false);
        ssc2.register(selector, SelectionKey.OP_ACCEPT);
        ssc2.bind(new InetSocketAddress(9999));
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        //监听selector的状态
        for (; ; ) {
            selector.select();
            if(selectionKeys.size()>0){
                for (SelectionKey selectionKey : selectionKeys) {
                    selectionKeys.remove(selectionKey);
                    if(selectionKey.isAcceptable()){
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel accept = channel.accept();
                        if(accept!=null){
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_CONNECT);
                            accept.register(selector, SelectionKey.OP_READ);
                            //accept.register(selector, SelectionKey.OP_WRITE);
                            System.out.println("有客户端链接上来" + accept.getRemoteAddress().toString());
                        }
                    }
                    if(selectionKey.isReadable()){
                        ByteBuffer buff = ByteBuffer.allocate(10);
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        boolean open = sc.isOpen();
                        boolean connected = sc.isConnected();
                        if(open&&connected){
                            int read = sc.read(buff);
                            if(read==-1){
                                sc.close();
                                System.out.println("服务关闭");
                                continue;
                            }
                        }
                        buff.flip();
                        System.out.println("读数据:"+buff.toString()+"来自");
                    }
                    if(selectionKey.isWritable()){
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        boolean connected = sc.isConnected();
                        boolean open = sc.isOpen();
                        sc.write(ByteBuffer.wrap("hi".getBytes()));
                        System.out.println("写数据事件");
                    }
                    if(selectionKey.isConnectable()){
                        System.out.println("链接事件");
                    }

                }
            }
        }
    }
}
