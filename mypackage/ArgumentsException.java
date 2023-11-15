package mypackage;

public class ArgumentsException extends Exception {
	public ArgumentsException() {
        super("Incorrect: invalid number of arguments");
    }
}
