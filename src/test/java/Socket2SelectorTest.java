import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 使用两个监听器,一个只为接受,一个只为处理读写
 *
 * @author mgd [maoguidong@standard-robots.com]
 * @data 2022/6/14 下午3:28
 */
public class Socket2SelectorTest {
    public static void main(String[] args) throws Exception {
        SelectorProvider provider = SelectorProvider.provider();
        //启动一个ssc
        ServerSocketChannel ssc = provider.openServerSocketChannel();
        AbstractSelector acceptSelector = provider.openSelector();
        Selector readWriteSelector = provider.openSelector();
        ssc.configureBlocking(false);
        ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8888));
        ssc.setOption(StandardSocketOptions.SO_RCVBUF, 65535);
        //创建两个线程池,一个作为监听连接事件一个作为处理读写事件
        ExecutorService boosPool = Executors.newFixedThreadPool(1);
        ExecutorService workPool = Executors.newFixedThreadPool(1);
        //初始化一个读的容器
        ByteBuffer buff = ByteBuffer.allocate(1024);
        boosPool.execute(() -> {
            for (; ; ) {
                try {
                    acceptSelector.select();
                    Set<SelectionKey> acceptKeys = acceptSelector.selectedKeys();
                    if (!acceptKeys.isEmpty()) {
                        for (SelectionKey acceptKey : acceptKeys) {
                            acceptKeys.remove(acceptKey);
                            ServerSocketChannel channel = (ServerSocketChannel) acceptKey.channel();
                            SocketChannel accept = channel.accept();
                            if (accept != null) {
                                accept.configureBlocking(false);
                                //accept.setOption(StandardSocketOptions.SO_LINGER, 0);
                                accept.register(readWriteSelector, SelectionKey.OP_READ);
                                System.out.println("有客户端链接上来" + accept.getRemoteAddress().toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        workPool.execute(() -> {
            for (; ; ) {
                try {
                    //不能阻塞
                    readWriteSelector.select(10);
                    Set<SelectionKey> readKeys = readWriteSelector.selectedKeys();
                    if (!readKeys.isEmpty()) {
                        for (SelectionKey readKey : readKeys) {
                            readKeys.remove(readKey);
                            SocketChannel sc = (SocketChannel) readKey.channel();
                            if (readKey.isReadable()) {
                                if (sc.isOpen() && sc.isConnected()) {
                                    buff.clear();
                                    int read = sc.read(buff);
                                    if(read==-1){
                                        System.out.println("客户端主动结束"+sc.getRemoteAddress());
                                        sc.close();
                                        break;
                                    }
                                    buff.flip();
                                    System.out.println(Thread.currentThread().getName() + "客户端数据:" + new String(buff.array(),0,buff.limit()));
                                }
                            }else {
                                readKey.cancel();
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
