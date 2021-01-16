import java.io.IOException;

class NeuralNetwork {

    //global variables
    private Matrix weightsHiddenInput, weightsOutputHidden, hidden, output, confidence, outputError, hiddenError;
    private final Matrix input, targetOutput;
    private final int inputNodes, outputNodes;
    private final double learningRate;
    private CSVReader reader;

    NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, double learningRate, CSVReader reader) throws MatrixException {
        //assign values to variables
        this.learningRate = learningRate;
        this.outputNodes = outputNodes;
        this.inputNodes = inputNodes;
        this.reader = reader;

        input = new Matrix(new double[this.inputNodes][1]);
        targetOutput = new Matrix(new double[this.outputNodes][1]);

        hidden = new Matrix(new double[hiddenNodes][1]);
        output = new Matrix(new double[this.outputNodes][1]);
        outputError = new Matrix(new double[this.outputNodes][1]);
        hiddenError = new Matrix(new double[hiddenNodes][1]);
        weightsHiddenInput = new Matrix(new double[hiddenNodes][this.inputNodes]);
        weightsOutputHidden = new Matrix(new double[this.outputNodes][hiddenNodes]);

        double limitHiddenInput = 1 / Math.sqrt(this.inputNodes);
        weightsHiddenInput = Matrix.randomMatrix(hiddenNodes, this.inputNodes, -limitHiddenInput, limitHiddenInput);

        double limitOutputHidden = 1 / Math.sqrt(hiddenNodes);
        weightsOutputHidden = Matrix.randomMatrix(this.outputNodes, hiddenNodes, -limitOutputHidden, limitOutputHidden);
    }

    int train() throws MatrixException, IOException {                                             //trains network (backpropagation)
        int highest = this.query();                                                                                     //queries network (forwardpropagation)
        //error-calculation
        outputError = targetOutput.subtract(output);                                                                    //Eo = T - O
        hiddenError = weightsOutputHidden.transpose().multiply(outputError);                                            //Eh = woh^T * Eo
        //adjusting weights
        Matrix deltaWeightsOutputHidden = outputError
                .elementMultiply(output.elementMultiply(output.multiply(-1).add(1)))
                .multiply(hidden.transpose())
                .multiply(learningRate);                                                                                //lr * E * sigmoid`(O) * I^T
        weightsOutputHidden = weightsOutputHidden.add(deltaWeightsOutputHidden);                                        //wwneu = walt + deltaw
        Matrix deltaWeightsHiddenInput = hiddenError
                .elementMultiply(hidden.elementMultiply(hidden.multiply(-1).add(1)))
                .multiply(input.transpose())
                .multiply(learningRate);
        weightsHiddenInput = weightsHiddenInput.add(deltaWeightsHiddenInput);
        return highest;
    }

    int query() throws MatrixException, IOException {                                                                   //query (forward propagation)
        reader.nextLine();
        for (int i = 0; i < this.inputNodes; i++) {                                                                     //setting inputs according to brightness level of pixels
            input.set(i, 0, reader.getInputs()[i + 1]);
        }
        for (int i = 0; i < this.outputNodes; i++) {                                                                    //setting target
            targetOutput.set(i, 0, reader.getTarget()[i]);
        }
        //forward propagation
        hidden = Matrix.applySigmoid(weightsHiddenInput.multiply(input));
        Matrix outputUnnormalized = weightsOutputHidden.multiply(hidden);
        output = Matrix.applySigmoid(outputUnnormalized);
        confidence = Matrix.softmax(outputUnnormalized);
        int highest = 0;
        double highestValue = 0;
        for (int i = 0; i < 10; i++) {                                                                                  //getting digit with highest confidence
            if (confidence.get(i, 0) > highestValue) {
                highestValue = confidence.get(i, 0);
                highest = i;
            }
        }
        return highest;
    }

    void newFile(CSVReader reader) {                                                                                    //saves new file (reader) globally
        this.reader = reader;
    }

    public Matrix getConfidence() {                                                                                     //returns confidence
        return confidence;
    }
}
