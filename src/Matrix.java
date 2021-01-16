import static java.lang.Double.NaN;

//Rechenoperationen sind an den Methodennamen zu erkennen und brauchen keine weitere Kommentierung
class Matrix {
    public final double[][] matrix;
    public final int rows;
    public final int columns;

    Matrix(double[][] inputArray) throws MatrixException {
        String operation = "Das Erstellen der Matrix";
        matrix = inputArray;
        rows = matrix.length;
        columns = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            if (matrix[i].length != columns) {
                throw new MatrixException("nicht alle Zeilen des Arrays haben die gleiche Laenge!", operation);
            }
        }
    }

    static Matrix randomMatrix(int rowsRandom, int columnsRandom, double rangeMin, double rangeMax) {                   //creates new Matrix with random values inside the given range
        double[][] result = new double[rowsRandom][columnsRandom];
        for (int i = 0; i < rowsRandom; i++) {
            for (int j = 0; j < columnsRandom; j++) {
                result[i][j] = rangeMin + (rangeMax - rangeMin) * Math.random();
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    void printMatrix() {
        System.out.println(rows + " x " + columns + " Matrix:");
        for (int i = 0; i < rows; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < columns; j++) {
                line.append("     ").append(matrix[i][j]);
            }
            System.out.println(line);
        }
    }

    //Rechen-Operationen Start
    Matrix transpose() {
        double[][] result = new double[columns][rows];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                result[i][j] = matrix[j][i];
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    Matrix add(Matrix Matrix2) throws MatrixException {
        String operation = "Die Addition";
        double[][] result = new double[rows][columns];
        this.check(Matrix2, true, operation);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = matrix[i][j] + Matrix2.matrix[i][j];
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    Matrix add(double summand) {
        double[][] result = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = matrix[i][j] + summand;
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    Matrix subtract(Matrix Matrix2) throws MatrixException {
        String operation = "Die Subtraktion";
        this.check(Matrix2, true, operation);
        double[][] result = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = matrix[i][j] - Matrix2.matrix[i][j];
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    Matrix multiply(Matrix Matrix2) throws MatrixException {
        String operation = "Die Multiplikation";
        this.check(Matrix2, false, operation);
        double[][] result = new double[rows][Matrix2.columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < Matrix2.columns; j++) {
                for (int k = 0; k < columns; k++) {
                    result[i][j] += matrix[i][k] * Matrix2.matrix[k][j];
                }
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    Matrix multiply(double skalar) {
        double[][] result = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = matrix[i][j] * skalar;
            }
        }
        try {
            return new Matrix(result);
        } catch (MatrixException e) {
            e.printStackTrace();
            return null;
        }
    }

    Matrix elementMultiply(Matrix Matrix2) throws MatrixException {
        String operation = "Die elementweise Multiplikation";
        this.check(Matrix2, true, operation);
        double[][] result = new double[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                result[i][j] = matrix[i][j] * Matrix2.matrix[i][j];
            }
        }
        return new Matrix(result);
    }

    static Matrix applySigmoid(Matrix A) {
        for (int i = 0; i < A.getNumElements(); i++) {
            A.set(i, 0, sigmoid(A.get(i, 0)));
        }
        return A;
    }

    static Matrix softmax(Matrix in) throws MatrixException {
        double sumExpInput = 0;
        int length = in.rows;
        Matrix out = new Matrix(new double[length][1]);
        for (int i = 0; i < length; i++) {
            sumExpInput += Math.exp(in.get(i, 0));
        }
        for (int i = 0; i < length; i++) {
            out.set(i, 0, Math.exp(in.get(i, 0)) / sumExpInput);
        }
        return out;
    }

    static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
    //Rechen-Operationen Ende

    //Ueberpruefung der Eignung der Matrizen
    void check(Matrix Matrix2, boolean checkType, String operation) throws MatrixException {
        if (checkType) {
            if (Matrix2.rows != rows) {
                throw new MatrixException("Anzahl der Zeilen der beiden Matrizen ist ungleich!", operation);
            } else {
                if (Matrix2.columns != columns) {
                    throw new MatrixException("Anzahl der Spalten der beiden Matrizen ist ungleich!", operation);
                }
            }
        } else {
            if (Matrix2.rows != columns) {
                throw new MatrixException("Anzahl der Zeilen der ersten Matrize entspricht nicht der Anzahl der Spalten der zweiten!", operation);
            }
        }
    }

    //Getter und Setter Start
    void set(int row, int column, double value) {
        this.matrix[row][column] = value;
    }

    double get(int row, int column) {
        return this.matrix[row][column];
    }

    int getNumElements() {
        return rows * columns;
    }

    double[][] getArray() {
        return matrix;
    }
    //Getter und Setter Ende
}
