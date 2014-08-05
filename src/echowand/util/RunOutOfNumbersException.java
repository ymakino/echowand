package echowand.util;

/**
 * 利用可能な数を使い切ってしまったことを表す例外
 * @author ymakino
 */
public class RunOutOfNumbersException extends Exception {
    
    /**
     * RunOutOfNumbersExceptionを生成する。
     * @param message 例外に関する情報
     */
    public RunOutOfNumbersException(String message) {
        super(message);
    }
    
    /**
     * RunOutOfNumbersExceptionを生成する。
     * @param message 例外に関する情報
     * @param cause この例外の発生原因となった例外
     */
    public RunOutOfNumbersException(String message, Exception cause) {
        super(message, cause);
    }
}
