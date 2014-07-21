package echowand.net;

/**
 * データ構造が不正である場合の例外であることを表す
 * @author Yoshiki Makino
 */
public class InvalidDataException extends Exception {
    
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
     * @param cause この例外の発生原因となった例外
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
