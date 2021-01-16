class MatrixException extends Exception {                                                                               //exception class (extends exceptions) -> throws matrix exceptions
    MatrixException(String text, String operation) {
        super(text + " " + operation + " ist leider nicht möglich!");
    }
}