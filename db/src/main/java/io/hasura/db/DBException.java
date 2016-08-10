package io.hasura.db;

public class DBException extends Exception {
    private static final long serialVersionUID = 1;
    private DBError code;

    /**
     * Construct a new DBException with a particular error code.
     *
     * @param theCode    The error code to identify the type of exception.
     * @param theMessage A message describing the error in more detail.
     */
    public DBException(DBError theCode, String theMessage) {
        super(theMessage);
        code = theCode;
    }

    /**
     * Construct a new DBException with a particular error code.
     *
     * @param theCode The error code to identify the type of exception.
     * @param cause   The cause of the error.
     */
    public DBException(DBError theCode, Throwable cause) {
        super(cause);
        code = theCode;
    }

    /**
     * Access the code for this error.
     *
     * @return The code for this error.
     */
    public DBError getCode() {
        return code;
    }

    @Override
    public String toString() {
        String message =
                DBException.class.getName() + " "
                        + code.toString() + " : " + super.getLocalizedMessage();
        return message;
    }
}
