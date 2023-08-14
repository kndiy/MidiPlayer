import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class MyFrame extends JFrame implements Serializable {
    public MyFrame() {
        setTitle("Testing Ground Zero - Cyber Beat Box");
        setBounds(100,100,800,700);
        setPreferredSize(new Dimension(1000,700));
        setSize(800,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }
}
