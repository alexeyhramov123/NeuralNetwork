/*
  5.Pruefungskomponente

  @version 3.5.7 vom 24.05.2020
  @AlexeyHramov
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NetGUI extends JFrame {
    //network variables
    private NeuralNetwork neuralNet;
    private CSVReader reader;
    NetThread nThread = new NetThread();
    private int iMax;

    //GUI (erstellen der Objekte)
    //title
    private final JLabel l_title = new JLabel("artificial neural network", SwingConstants.CENTER);

    //info components
    private final JPanel p_infoPane = new JPanel();
    private final JLabel l_target = new JLabel("target: ");
    private final JLabel l_result = new JLabel("result: ");
    private final JLabel l_accuracy = new JLabel("accuracy: ");
    private final JLabel l_pass = new JLabel("pass: ");
    private final JLabel l_confidence = new JLabel("confidence: ");
    private final DigitPanel digitPane = new DigitPanel();

    //settings components
    private final JPanel p_settingsPane = new JPanel();
    private final JTextField tf_filepath = new JTextField("filepath");
    private final JTextField tf_numElements = new JTextField("number records");
    private final JTextField tf_delay = new JTextField("delay");
    private final JButton btn_filepath = new JButton("...");
    private final JButton btn_newNet = new JButton("new network");
    private final JButton btn_newFIle = new JButton("new File");
    private final JButton btn_query = new JButton("query");
    private final JButton btn_train = new JButton("train");
    private final JButton btn_stop = new JButton("stop");
    private final JTextArea ta_debug = new JTextArea("debug");

    private NetGUI() {
        //window
        super();
        Dimension dDesktop = Toolkit.getDefaultToolkit().getScreenSize();
        int desktopHeight = (int) dDesktop.getHeight();
        int desktopWidth = (int) dDesktop.getWidth();
        this.setSize(desktopWidth / 2, desktopHeight / 2);
        this.setTitle("artificial neural network");
        this.setVisible(true);
        Container cp = this.getContentPane();
        cp.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //adding resize-listener to window (executes code when frame gets resized)
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                resizeAll(cp);
            }
        });

        //adding components to container / panels
        //adding title, panel for information and panel for settings to container
        cp.add(l_title);
        cp.add(p_infoPane);
        cp.add(p_settingsPane);

        //adding components to information-panel
        p_infoPane.setLayout(null);
        p_infoPane.add(digitPane);
        p_infoPane.add(l_target);
        p_infoPane.add(l_result);
        p_infoPane.add(l_confidence);
        p_infoPane.add(l_accuracy);
        p_infoPane.add(l_pass);


        //adding components to settings-panel
        p_settingsPane.setLayout(null);
        p_settingsPane.add(tf_filepath);
        p_settingsPane.add(tf_numElements);
        p_settingsPane.add(tf_delay);
        p_settingsPane.add(btn_newNet);
        p_settingsPane.add(btn_query);
        p_settingsPane.add(btn_newFIle);
        p_settingsPane.add(btn_train);
        p_settingsPane.add(btn_filepath);
        p_settingsPane.add(btn_stop);
        p_settingsPane.add(ta_debug);

        //activating lineWrap for debug textarea
        ta_debug.setLineWrap(true);
        //adding focus-listener to text fields
        tf_filepath.addFocusListener(new TfListener(tf_filepath, "filepath"));
        tf_numElements.addFocusListener(new TfListener(tf_numElements, "number records"));
        tf_delay.addFocusListener(new TfListener(tf_delay, "delay"));
        //adding action-listeners to buttons
        btn_newNet.addActionListener(e -> this.newNetwork());
        btn_filepath.addActionListener(e -> this.selectFile());
        btn_query.addActionListener(e -> {                                                                              //starts query
            this.newTask();
            nThread.isTraining = false;
            nThread.execute();
        });
        btn_train.addActionListener(e -> {
            this.newTask();
            nThread.isTraining = true;
            nThread.execute();
        });
        btn_newFIle.addActionListener(e -> {                                                                            //creates new file and deletes old file
            this.correctDelay();
            try {
                iMax = Integer.parseInt(tf_numElements.getText());
                reader = new CSVReader(tf_filepath.getText(), this);
                neuralNet.newFile(this.reader);
                nThread = new NetThread();
            } catch (IOException | NumberFormatException ex) {
                ex.printStackTrace();
                this.printErrorToDebug(ex);
            }
        });
        btn_stop.addActionListener(e -> {                                                                               //stops all tasks
            if (!nThread.isCancelled()) {
                nThread.cancel(true);
                this.enableButtons();
                nThread = new NetThread();
            }
            this.newTask();
        });

        this.repaint();                                                                                                 //repaints frame
    }

    private void newTask(){                                                                                              //deletes old tasks, creates new one and resets reader (to first line)
        nThread = new NetThread();
        try {
            reader.reset();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            this.printErrorToDebug(ex);
        }
    }

    private void resizeAll(Container cp) {                                                                              //resizes all components of the GUI
        //new dimensions
        int cpWidth = cp.getWidth();
        int cpHeight = cp.getHeight();

        //title resizing
        l_title.setBounds((cpWidth / 2) - (cpWidth / 4), cpHeight / 36, cpWidth / 2, cpHeight / 20);
        l_title.setFont(new Font("", Font.BOLD, cpHeight / 20));

        //info resizing
        int freeHeight = cpHeight * 8 / 9;
        int ipWidth = freeHeight / 2;
        int lHeight = freeHeight / 2;
        Font fSubTitle = new Font("", Font.PLAIN, cpHeight / 40);                                           //resizing the new font size dependent on window size
        l_target.setFont(fSubTitle);
        l_result.setFont(fSubTitle);
        l_confidence.setFont(fSubTitle);
        l_accuracy.setFont(fSubTitle);
        l_pass.setFont(fSubTitle);
        p_infoPane.setBounds(0, cpHeight / 9, ipWidth, freeHeight);
        digitPane.setBounds(0, freeHeight / 2, ipWidth, ipWidth);
        l_target.setBounds(ipWidth / 10, 0, ipWidth * 9 / 10, cpHeight / 20);
        l_result.setBounds(ipWidth / 10, lHeight / 5, ipWidth * 9 / 10, cpHeight / 20);
        l_confidence.setBounds(ipWidth / 10, lHeight * 2 / 5, ipWidth * 9 / 10, cpHeight / 20);
        l_accuracy.setBounds(ipWidth / 10, lHeight * 3 / 5, ipWidth * 9 / 10, cpHeight / 20);
        l_pass.setBounds(ipWidth / 10, lHeight * 4 / 5, ipWidth * 9 / 10, cpHeight / 20);

        //settings resizing
        int spWidth = cpWidth - ipWidth * 11 / 10;
        p_settingsPane.setBounds(ipWidth * 11 / 10, cpHeight / 9, spWidth, freeHeight);
        tf_filepath.setBounds(0, 0, spWidth * 9 / 10, freeHeight / 20);
        btn_filepath.setBounds((int) (spWidth * 9.1 / 10), 0, spWidth / 25, freeHeight / 20);
        tf_numElements.setBounds(0, freeHeight / 15, spWidth / 5, freeHeight / 20);
        tf_delay.setBounds(spWidth / 4, freeHeight / 15, spWidth / 5, freeHeight / 20);
        btn_newFIle.setBounds(spWidth / 2, freeHeight / 15, spWidth / 5, freeHeight / 20);
        btn_newNet.setBounds(spWidth * 3 / 4, freeHeight / 15, spWidth / 5, freeHeight / 20);
        btn_stop.setBounds(0, freeHeight * 3 / 4, spWidth / 5, freeHeight / 20);
        btn_query.setBounds(0, freeHeight * 17 / 20, spWidth / 5, freeHeight / 20);
        btn_train.setBounds(0, freeHeight * 19 / 20, spWidth / 5, freeHeight / 20);
        ta_debug.setBounds(spWidth * 2 / 5, freeHeight * 17 / 20, spWidth * 3 / 5, freeHeight  * 3 / 20);
    }

    private void newNetwork() {                                                                                          //creates a new csv-reader and neural network
        this.correctDelay();
        try {
            iMax = Integer.parseInt(tf_numElements.getText());
            reader = new CSVReader(tf_filepath.getText(), this);
            neuralNet = new NeuralNetwork(784, 200, 10, 0.4, reader);
        } catch (IOException | MatrixException | NumberFormatException ex) {
            ex.printStackTrace();
            this.printErrorToDebug(ex);
        }
    }

    void printErrorToDebug(Exception ex){                                                                        //prints error to debug-textarea
        ta_debug.setText(String.valueOf(ex));
    }

    void printToDebug(String str){
        ta_debug.setText(str);
    }

    void correctDelay(){
        if (tf_delay.getText().equals("delay")) {
            tf_delay.setText("0");
        }
    }

    private void infoRefresh(double[] pixelValue, int target, int result, int pass, double confidence, double accuracy) {                 //refreshes information about current digit
        double[] value = new double[784];
        System.arraycopy(pixelValue, 1, value, 0, 784);
        digitPane.paintImage(value);
        l_target.setText("target: " + target);
        l_result.setText("result: " + result);
        l_confidence.setText("confidence: " + confidence);
        l_pass.setText("pass: " + pass);
        l_accuracy.setText("accuracy: " + accuracy);
    }

    private void disableButtons(){                                                                                      //disables buttons (while training or querying)
        btn_query.setEnabled(false);
        btn_train.setEnabled(false);
        btn_newNet.setEnabled(false);
        btn_newFIle.setEnabled(false);
    }

    private void enableButtons(){                                                                                       //enables buttons
        btn_query.setEnabled(true);
        btn_train.setEnabled(true);
        btn_newNet.setEnabled(true);
        btn_newFIle.setEnabled(true);
    }

    private void selectFile() {                                                                                         //selects file from file explorer
        FileDialog fd = new FileDialog(new JFrame());
        fd.setFile("*.csv");                                                                                            //shows only csv files
        fd.setVisible(true);
        File[] f = fd.getFiles();
        if (f.length > 0) {
            tf_filepath.setText(fd.getFiles()[0].getAbsolutePath());
        }
    }

    class NetThread extends SwingWorker<Void, Object> {                                                          //new Thread (swing worker) performs training or querying in background, so the JFrame can refresh simultaneously
        boolean isTraining = false;                                                                                     //decides, whether the thread trains or queries

        @Override
        protected Void doInBackground() throws Exception {
            disableButtons();
            int right = 0;
            double accuracy = 0;
            for (int pass = 0; pass < iMax; pass++) {
                try {
                    int highest;
                    int target;
                    double[] line;
                    if (isTraining) {
                        highest = neuralNet.train();                                                                    //trains the network
                        line = reader.getInputs();
                        target = (int) line[0];
                        if (highest == target) {                                                                        //makes result label green, if network was right and red if it was wrong
                            l_result.setForeground(Color.GREEN);
                            right += 1;
                        }else {
                            l_result.setForeground(Color.RED);
                        }
                        if ((pass + 1) % 100 == 0) {                                                                    //calculating accuracy
                            accuracy = (double) right / 100;
                            right = 0;
                        }
                    } else {
                        highest = neuralNet.query();                                                                    //queries the network
                        line = reader.getInputs();
                        target = (int) line[0];
                        if (highest == target) {                                                                        //makes result label green, if network was right and red if it was wrong
                            l_result.setForeground(Color.GREEN);
                            right += 1;
                        } else {
                            l_result.setForeground(Color.RED);
                            right += 0;
                        }
                        accuracy = (double) right / (pass + 1);                                                         //calculating accuracy
                    }
                    double confidence = neuralNet.getConfidence().get(highest, 0);                              //gets confidence (value of highest output)
                    infoRefresh(line, target, highest, pass + 1, confidence, accuracy);
                } catch (MatrixException ex) {
                    ex.printStackTrace();
                    ta_debug.setText(String.valueOf(ex));
                }
                Thread.sleep(Integer.parseInt(tf_delay.getText()));
                if (this.isCancelled()) {                                                                               //cancels thread and reenables buttons
                    enableButtons();
                    return null;
                }
            }
            enableButtons();
            return null;
        }

    }

    private static class TfListener implements FocusListener {                                                          //focus listener for textfield
        final JTextField tf;
        final String text;
        TfListener(JTextField tf, String text){                                                                         //needs textfield that he is applied to and the text that should be displayed if it is empty
            this.tf = tf;
            this.text = text;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if(tf.getText().equals(text)){
                tf.setText("");
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(tf.getText().equals("")){
                tf.setText(text);
            }
        }
    }

    public static void main(String[] args) {                                                                            //starts program
            new NetGUI();
    }

}