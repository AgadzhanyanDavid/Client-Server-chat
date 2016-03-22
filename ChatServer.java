/**
 * Created by AgadzhanyanDavid on 21.03.16.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Многопоточный клиент/сервер чат(для каждого потока создается отдельный поток)
 * Когда клиент подключается к серверу, он вводит свое имя и отправляет запрос, если пользователя
 * с таким именем не существует, то сервер пропускает в чат.
 */
public class ChatServer {
    private static final int PORT = 9001; //порт сервера
    private static HashSet<String> names = new HashSet<String>(); //список имен всех пользователей
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception { //слушаем порт и и создаем клиентов
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread { //создаем для каждого обработчик(новый поток)
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public Handler(Socket socket) { //конструктор
            this.socket = socket;
        }

        public void run() { //
            try {

                //создание стримов
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // после ввода клиентом имени, отправляется запрос на подтверждение уникальности имени
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                out.println("NAMEACCEPTED"); // заносит этого пользователя в writers, для получения сообщений от других пользователей
                writers.add(out);
                
                
                    while (true) { // отправка сообщение пользователем
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) { //клиент вышел
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}



