import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT = 8888;

    public void run() throws IOException {
        // 创建服务器套接字，绑定到指定端口
        ServerSocket serverSocket = new ServerSocket(PORT);
        ServerFrame.addText("服务器已启动，等待客户端连接...");


        // 等待客户端连接
        Socket clientSocket = serverSocket.accept();
        ServerFrame.addText("客户端已连接： " + clientSocket.getInetAddress());


        try (PrintStream out = new PrintStream(clientSocket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String request = in.readLine();
            if (request.startsWith("DOWNLOAD")) {
                String fileName = request.substring(8);
                File file = new File("files/" + fileName);

                if (file.exists()) {
                    // 发送文件内容
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    fis.close();
                } else {
                    out.println("文件不存在");
                    ServerFrame.addText("文件不存在");
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
            ServerFrame.addText("连接失败");
        }

        // 关闭连接
        clientSocket.close();
        serverSocket.close();

    }
}
