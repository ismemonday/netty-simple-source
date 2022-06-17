import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.Scanner;

/**
 * socket收发缓存区相关 设置的接收缓冲区最小为1052,每次发送过来的数据是1152
 *
 * @author mgd [maoguidong@standard-robots.com]
 * @data 2022/6/16 上午10:02
 */
public class SocketRS_QTest {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket = serverSocket.accept();
        socket.setOption(StandardSocketOptions.SO_RCVBUF, 20);
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
                 socket.setOption(StandardSocketOptions.SO_SNDBUF, 20);
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
