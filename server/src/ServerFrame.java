import javax.swing.*;
import java.io.IOException;

public class ServerFrame extends JFrame {
    private static JTextArea j = new JTextArea();
    public ServerFrame() {
        setTitle("Server");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(j);

        new Thread(() -> {
            try {
                Server server = new Server();
                while (true)
                    server.run();
            } catch (IOException e) {
                addText("连接失败！");
            }
        }).start();

    }
    public static void addText(String text){
        j.append(text + "\n");
    }
}
