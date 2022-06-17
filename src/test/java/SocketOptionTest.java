import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.Scanner;

/**
 * socket参数配置相关
 *  TCP_NODELAY 不延迟发送数据
 *  SO_LINER:有待发送的数据.等数据发送完再关闭
 * @author mgd [maoguidong@standard-robots.com]
 * @data 2022/6/16 上午10:02
 */
public class SocketOptionTest {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);

        Socket socket = serverSocket.accept();
        socket.setOption(StandardSocketOptions.SO_RCVBUF, 1052);
        socket.setOption(StandardSocketOptions.SO_SNDBUF, 1052);
        //socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        socket.setOption(StandardSocketOptions.TCP_NODELAY, true);
        for (;;){
            Thread.sleep(10);
        }
    }

    static class Client{
        public static void main(String[] args) throws Exception {
            OutputStream outputStream=null;
            Socket socket = null;
            try {
                 socket = new Socket("localhost", 8888);
                socket.setOption(StandardSocketOptions.SO_RCVBUF, 1052);
                socket.setOption(StandardSocketOptions.SO_SNDBUF, 1052);
                socket.setOption(StandardSocketOptions.TCP_NODELAY, true);
                //socket.setOption(StandardSocketOptions.SO_KEEPALIVE, Boolean.TRUE);
                //socket.setOption(StandardSocketOptions.SO_LINGER, 0);
                 outputStream=socket.getOutputStream();
                Scanner scanner = new Scanner(System.in);
                for (;;){
                    String s = scanner.nextLine();
                    outputStream.write(s.getBytes());
                }
            }catch (Exception e){

            }finally {
                outputStream.close();
                socket.close();
            }
        }
    }
}
