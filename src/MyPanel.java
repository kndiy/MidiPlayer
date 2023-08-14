import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.Flow;

public class MyPanel extends JPanel implements Serializable {
    JCheckBox[] checkBox;
    JButton[] commandButton;
    JButton buttonSendChat;
    JTextField chatField;
    JTextPane chatPane;
    public MyPanel() {
        setBounds(0,0,1000,700);
        setLayout(null);

        JPanel leftCommands = new JPanel();
        leftCommands.setBounds(630,10,300,300);
        leftCommands.setLayout(new BoxLayout(leftCommands, BoxLayout.Y_AXIS));

        String[] commandButtonNames = {"Start","Stop","Tempo Up","Tempo Down","Save","Load"};
        commandButton = new JButton[6];

        for (int i = 0; i < 6; i ++) {
            commandButton[i] = new JButton(commandButtonNames[i]);
            if (i < 2) {
                commandButton[i].setPreferredSize(new Dimension(150,40));
            }
            else {
                commandButton[i].setPreferredSize(new Dimension(250,40));
            }
            leftCommands.add(commandButton[i]);
            leftCommands.add(Box.createVerticalGlue());
        }

        add(leftCommands);

        JPanel chatArea = new JPanel();
        chatArea.setBounds(630, 310, 300, 390);
        BoxLayout boxLayout = new BoxLayout(chatArea,BoxLayout.Y_AXIS);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300,290));

        chatField = new JTextField();
        chatField.setPreferredSize(new Dimension(220,30));
        chatField.setBackground(Color.GREEN);
        buttonSendChat = new JButton("Send");

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        JPanel chatEntry = new JPanel(flowLayout);
        chatEntry.setPreferredSize(new Dimension(300,50));
        chatEntry.add(chatField);
        chatEntry.add(buttonSendChat);

        chatArea.add(scrollPane);
        chatArea.add(Box.createVerticalGlue());
        chatArea.add(chatEntry);

        add(chatArea);

    }
    public MyPanel(String label, int x, int y) {
        setBounds(x,y,600,30);
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        setLayout(layout);
        setBackground(Color.LIGHT_GRAY);

        Font labelFont = new Font("light calibri",Font.BOLD, 16);


        JLabel panelLabel = new JLabel();
        panelLabel.setText(label);
        panelLabel.setFont(labelFont);
        panelLabel.setPreferredSize(new Dimension(150,20));
        add(panelLabel);

        checkBox = new JCheckBox[16];
        for (int i = 0; i < 16; i++) {
            checkBox[i] = new JCheckBox();
            checkBox[i].setPreferredSize(new Dimension(20,20));
            checkBox[i].setSelected(false);
            add(checkBox[i]);
        }
    }

}
