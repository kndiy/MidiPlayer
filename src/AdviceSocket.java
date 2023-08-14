import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AdviceSocket {

    public void getAdvice() {
        try {
            Socket adviceSocket = new Socket("127.0.0.1", 4242);
            InputStreamReader streamReader = new InputStreamReader(adviceSocket.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);

            String advice = reader.readLine();
            System.out.println("Today's advice is " + advice);

            reader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AdviceSocket test = new AdviceSocket();
        test.getAdvice();
    }
}
