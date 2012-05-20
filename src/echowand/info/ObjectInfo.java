package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;

/**
 * ECHONETオブジェクト基本設定を行なうインタフェース
 * @author Yoshiki Makino
 */
public interface ObjectInfo {
    /**
     * このObjectInfoが表現するECHONETオブジェクトのClassEOJを返す。
     * @return このECHONETオブジェクトのClassEOJ
     */
    public ClassEOJ getClassEOJ();
    
    /**
     * このObjectInfoが表現するECHONETオブジェクトの指定されたEPCに対応するプロパティを返す。
     * 存在しないEPCを指定された場合には、すべての操作を禁止したプロパティを返す。
     * @param epc プロパティのEPC
     * @return 指定されたEPCに対応するプロパティ
     */
    public PropertyInfo get(EPC epc);
    
    /**
     * このObjectInfoが表現するECHONETオブジェクトのindex番目のプロパティを返す。
     * @param index プロパティのインデックス
     * @return index番目のプロパティ
     */
    public PropertyInfo getAtIndex(int index);
    
    /**
     * このObjectInfoが表現するECHONETオブジェクトの全プロパティ数を返す。
     * @return 全プロパティ数
     */
    public int size();
}

