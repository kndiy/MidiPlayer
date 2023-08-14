import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyFirstChatServer {

    ArrayList<PrintWriter> clientOutputStreams;

    public static void main(String[] args) {
        MyFirstChatServer test = new MyFirstChatServer();
        test.clientOutputStreams = new ArrayList<>();
        test.go();
    }
    public void go() {
        try {
            ServerSocket serverSocket = new ServerSocket(42424);

            while (true) {
                //Only check for connection and create corresponding PrintWriter
                //that points to each connection-requesting socket
                //then add each PrintWriter to the prepared ArrayList
                //for future usages (sending jobs)

                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                System.out.println("Receiving connection request from: " + clientSocket.toString());

                //Now create a thread to check for incoming InputStream from the connected socket
                //each connection socket create a new listening job and a new thread to run that job

                //Each thread read and then write to all connected sockets saved in the ArrayList
                //read and write are a pair of jobs, there is no write without read, and read without write is meaningless
                //so each thread do one pair of jobs then dies and leave way for the next thread

                //THREAD SHOULD LIVE AND DIE IN A CYCLE,
                //IT IS NOT NATURAL TO LET THREADS LIVE FOREVER IN A WHILE LOOP

                String sender = clientSocket.getPort() +"";

                Thread thread = new Thread(new ClientReader(clientSocket, sender));
                thread.start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class ClientReader implements Runnable {
        BufferedReader reader;
        Socket clientSocket;
        String sender;
        public ClientReader(Socket socket, String sender) {
            this.sender = sender;
            try {
                clientSocket = socket;
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Receive: " + message);
                    tellEveryone(message, sender);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void tellEveryone(String message, String sender) {
        System.out.println("Now doing the sending of message \"" + message +"\"");

        Iterator<PrintWriter> iterator = clientOutputStreams.iterator();

        while (iterator.hasNext()) {
            try {
                PrintWriter writer = iterator.next();
                writer.println(sender + ": " + message);
                writer.flush();
            }
            catch (NoSuchElementException ex) {
                ex.printStackTrace();
            }
        }
    }
}
