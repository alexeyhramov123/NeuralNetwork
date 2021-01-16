import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class CSVReader {
    private double[] inputs;
    private final double[] target = new double[10];
    final String filePath;
    String line;
    BufferedReader br;
    private final NetGUI netGUI;

    CSVReader(String filePath, NetGUI netGUI) throws IOException {
        this.filePath = filePath;
        this.reset();
        this.netGUI = netGUI;
    }

    public void nextLine() throws IOException {
        if ((line = br.readLine()) != null) {
            inputs = this.toDouble(line.split(","));
        }else {
            netGUI.nThread.cancel(true);
            netGUI.printToDebug("number records is to large");
        }
        this.interpretInputs();
    }

    private void interpretInputs() {
        for (int i = 1; i < inputs.length; i++) {
            inputs[i] /= 255 * 0.999 + 0.001;
        }

        for (int i = 0; i < 10; i++) {
            if (i == (int) inputs[0]) {
                target[i] = 0.999;
            } else {
                target[i] = 0.001;
            }
        }
    }

    private double[] toDouble(String[] values) {
        double[] doubleValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            doubleValues[i] = Double.parseDouble(values[i]);
        }
        return doubleValues;
    }

    double[] getInputs() {
        return inputs;
    }

    double[] getTarget() {
        return target;
    }

    void reset() throws FileNotFoundException {
        br = new BufferedReader(new FileReader(filePath));
    }
}
