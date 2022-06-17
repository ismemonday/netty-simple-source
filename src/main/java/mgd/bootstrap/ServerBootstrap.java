package mgd.bootstrap;

import mgd.channel.NioServerSocketChannel;
import mgd.channel.handler.MyChannelHandler;

import java.util.prefs.Preferences;

/**
 * @author mgd [maoguidong@standard-robots.com]
 * @data 2022/6/13 下午3:49
 */
public class ServerBootstrap {
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    public ServerBootstrap group(EventLoopGroup parentGroup) {
        if(parentGroup!=null){
            throw new IllegalStateException("group set already");
        }
        this.parentGroup= parentGroup;
        this.childGroup=childGroup;
        return this;
    }

    public Preferences bind(int i) {
        validate();
        return null;
    }

    /**
     * bind的时候校验基础配置是否完成
     */
    private void validate() {

    }

    public ServerBootstrap channel(Class<NioServerSocketChannel> nioServerSocketChannelClass) {
        return this;
    }

    public void childHandler(MyChannelHandler myChannelHandler) {

    }
}
