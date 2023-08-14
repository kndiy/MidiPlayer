import javax.sound.midi.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class MidiPlayer implements Serializable {
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private final int[] instrument = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    private MyPanel[] panel;
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    static MyPanel backGround;
    String chatHistory = "";
    static int state = 0;
    public void play() {
        setUpGui();
        setUpMidi();
        setUpSocket();
    }
    private void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();

            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(120);
        } catch (InvalidMidiDataException ex) {
            System.out.println("Sequence not in correct format");
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        } catch (MidiUnavailableException ex) {
            System.out.println("Damn, Sequencer not available");
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
    private void setUpGui() {
        MyFrame frame = new MyFrame();
        int x = 10;
        int y = 10;

        String[] instrumentName = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
                "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
                "Maracas", "Whistle", "Low Conga", "Cowbell",
                "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};

        panel = new MyPanel[16];

        for (int i = 0; i < 16; i++) {
            panel[i] = new MyPanel(instrumentName[i], x, y);
            frame.getContentPane().add(panel[i]);
            y += 40;
        }

        backGround = new MyPanel();

        for (int i = 0; i < 6; i++) {
            switch (backGround.commandButton[i].getText()) {
                case "Start" -> backGround.commandButton[i].addActionListener(e -> {
                    buildTrackAndStart();
                });
                case "Stop" -> backGround.commandButton[i].addActionListener(e -> {
                    sequencer.stop();
                });
                case "Tempo Up" -> backGround.commandButton[i].addActionListener(e -> {
                    float tempoFactor = sequencer.getTempoFactor();
                    sequencer.setTempoFactor((float) (tempoFactor * 1.03));
                });
                case "Tempo Down" -> backGround.commandButton[i].addActionListener(e -> {
                    float tempoFactor = sequencer.getTempoFactor();
                    sequencer.setTempoFactor((float) (tempoFactor * 0.97));
                });
                case "Save" -> backGround.commandButton[i].addActionListener(e -> {
                    JFileChooser fileToSave = new JFileChooser();
                    fileToSave.showSaveDialog(frame);
                    saveFile(fileToSave.getSelectedFile());
                });
                case "Load" -> backGround.commandButton[i].addActionListener(e -> {
                    JFileChooser fileToLoad = new JFileChooser();
                    fileToLoad.showOpenDialog(frame);
                    loadFile(fileToLoad.getSelectedFile());
                });
            }
        }

        frame.getContentPane().add(backGround);

        frame.pack();

        backGround.buttonSendChat.addActionListener(e -> {
            writer.write(backGround.chatField.getText());
            writer.println();
            writer.flush();
            backGround.chatField.setText("");
            backGround.chatField.requestFocus();

            try {
                chatHistory += reader.readLine();
                backGround.chatPane.setText(chatHistory);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        backGround.chatField.addKeyListener(new ChatFieldEnterListener());

    }
    public void setUpSocket() {
        try {
            socket = new Socket("127.0.0.1", 42424);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Network Established");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    private void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];

            int key = instrument[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jCheckBox = (JCheckBox) panel[i].checkBox[j];
                if (jCheckBox.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeMidiEvent(176, 1, 127, 0, 16));
        }
        track.add(makeMidiEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeMidiEvent(144, 9, key, 100, i));
                track.add(makeMidiEvent(128, 9, key, 100, i + 1));
            }
        }
    }
    private MidiEvent makeMidiEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
        }
        return event;
    }
    private void saveFile(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            StringBuilder builder = new StringBuilder();

            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    if (panel[j].checkBox[k].isSelected()) {
                        builder.append('1');
                    } else {
                        builder.append('0');
                    }
                }
                writer.write(builder + "\n");
                builder.setLength(0);
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void loadFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (int j = 0; j < 16; j++) {
                String currentString = reader.readLine();
                for (int k = 0; k < 16; k++) {
                    if (currentString.charAt(k) == '1') {
                        panel[j].checkBox[k].setSelected(true);
                    }
                    else {
                        panel[j].checkBox[k].setSelected(false);
                    }
                }
            }
            reader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public class ChatFieldEnterListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                writer.write(backGround.chatField.getText());
                writer.println();
                writer.flush();
                backGround.chatField.setText("");
                backGround.chatField.requestFocus();

                try {
                    chatHistory += reader.readLine() + "\n";
                    backGround.chatPane.setText(chatHistory);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}