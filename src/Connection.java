import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {

    public void go() throws IOException {
        Socket chatSocket = new Socket("127.0.0.1", 6000);
        InputStreamReader stream = new InputStreamReader(chatSocket.getInputStream());
        BufferedReader reader = new BufferedReader(stream);

        String message = reader.readLine();

        PrintWriter writer = new PrintWriter(chatSocket.getOutputStream());




    }
}
