package echowand.net;

/**
 * データ構造が不正である場合の例外であることを表す
 * @author Yoshiki Makino
 */
public class InvalidDataException extends Exception {

    private Exception internalException;
    
    /**
     * データ構造が不正である場合の例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     */
    public InvalidDataException(String message) {
        super(message);
    }
    
    /**
     * データ構造が不正である場合の例外のオブジェクトを生成する。
     * @param message 例外に関する情報
     * @param exception この例外の発生原因となった例外
     */
    public InvalidDataException(String message, Exception exception) {
        super(message);
        this.internalException = exception;
    }
    
    /**
     * この例外の発生原因となった例外を返す。
     * @return この例外の発生原因となった例外
     */
    public Exception getInternalException() {
        return this.internalException;
    }
}
