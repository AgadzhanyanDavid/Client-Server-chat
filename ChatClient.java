/**
 * Created by AgadzhanyanDavid on 21.03.16.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

    // Фрейм с текстовым полем для ввода имени и самим полем чата
    // Отправляет имя с запросом на подтверждение, если все ок, то сервер подключает к серверу
public class ChatClient {
    //тест гитаб
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Чат");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    public ChatClient() {

        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private String getServerAddress() { // ввод айпи сервера и получение
        return JOptionPane.showInputDialog(
                frame,
                "Введите IP сервера:",
                "Добро пожаловать",
                JOptionPane.QUESTION_MESSAGE);
    }

    private String getName() { // ввод имени
        return JOptionPane.showInputDialog(
                frame,
                "Введите имя:",
                "Выбор имени",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException { // подключение к серверу
        // подключение
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // обработка сообщений от сервера в соответствии с протоколом
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
