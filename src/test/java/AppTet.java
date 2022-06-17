import mgd.bootstrap.EventLoopGroup;
import mgd.bootstrap.NioEventLoopGroup;
import mgd.bootstrap.ServerBootstrap;
import mgd.channel.handler.MyChannelHandler;
import mgd.channel.NioServerSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mgd [maoguidong@standard-robots.com]
 * @data 2022/6/13 下午3:41
 */
public class AppTet {
    public static void main(String[] args) {
        ExecutorService boosPool = Executors.newFixedThreadPool(1);
        ExecutorService workPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        boosPool.execute(()->{
            System.out.println("1开始啦");
        });

        workPool.execute(()->{
            System.out.println("2开始啦");
        });
    }
}
