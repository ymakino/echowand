package echowand.object;

import echowand.common.EOJ;
import echowand.common.EPC;

/**
 * ECHONETオブジェクトの共通インタフェース
 * @author Yoshiki Makino
 */
public interface EchonetObject {
    /**
     * このオブジェクトのEOJを返す。
     * @return このオブジェクトのEOJ
     */
    public EOJ getEOJ();
    
    /**
     * 指定されたEPCのプロパティが存在するかを返す。
     * @param epc EPCの指定
     * @return 存在していればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    public boolean contains(EPC epc) throws EchonetObjectException;
    
    /**
     * 指定されたEPCがGet可能であるかを返す。
     * @param epc EPCの指定
     * @return Get可能であればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    public boolean isGettable(EPC epc) throws EchonetObjectException;
    
    /**
     * 指定されたEPCがSet可能であるかを返す。
     * @param epc EPCの指定
     * @return Set可能であればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    public boolean isSettable(EPC epc) throws EchonetObjectException;
    
    /**
     * 指定されたEPCが通知を行うかを返す。
     * @param epc EPCの指定
     * @return 通知を行うのであればtrue、そうでなければfalse
     * @throws EchonetObjectException データの取得に失敗した場合
     */
    public boolean isObservable(EPC epc) throws EchonetObjectException;
    
    /**
     * 指定されたEPCのデータを返す。
     * 返すデータが存在しない場合にはnullを返す。
     * @param epc EPCの指定
     * @return 指定したEPCのデータ
     * @throws EchonetObjectException データのGet中にエラーが発生した場合
     */
    public ObjectData getData(EPC epc) throws EchonetObjectException;
    
    /**
     * 指定されたEPCに指定されたデータをセットする。
     * @param epc EPCの指定
     * @param data セットするデータの指定
     * @return セットを受け付けた場合にはtrue、そうでなければfalse
     * @throws EchonetObjectException データのSet中にエラーが発生した場合
     */
    public boolean setData(EPC epc, ObjectData data) throws EchonetObjectException;
}
