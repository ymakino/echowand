package echowand.object;

import echowand.common.Data;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class ExtraData {
    ArrayList<Data> extraDataList;
    public ExtraData() {
        extraDataList = new ArrayList<Data>();
    }
    public ExtraData(List<Data> data) {
        extraDataList = new ArrayList<Data>(data);
    }
    
    public boolean isEmpty() {
        return extraDataList.isEmpty();
    }
    
    public int size() {
        return extraDataList.size();
    }
    
    public Data get(int i) {
        return extraDataList.get(i);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExtraData)) {
            return false;
        }
        
        ExtraData other = (ExtraData) o;
        if (this.size() != other.size()) {
            return false;
        }
        int len = this.size();
        for (int i = 0; i < len; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

/**
 * オブジェクト内のプロパティのデータ表現
 * @author Yoshiki Makino
 */
public class ObjectData {
    private Data data;
    private ExtraData extraData;
    
    /**
     * 指定されたデータを用いてObjectDataを生成する。
     * @param data データのバイト配列
     */
    public ObjectData(Data data) {
        this.data = data;
        this.extraData = new ExtraData();
    }
    
    /**
     * 指定されたバイト列を用いてObjectDataを生成する。
     * @param data データのバイト配列
     */
    public ObjectData(byte... data) {
        this.data = new Data(data);
        this.extraData = new ExtraData();
    }
    
    /**
     * 指定されたバイト配列のリストを用いてObjectDataを生成する。
     * リストの最初のバイト配列以外は拡張データ領域に保存する。
     * @param dataList データのバイト配列リスト
     */
    public ObjectData(List<Data> dataList) {
        if (dataList.isEmpty()) {
            this.data = new Data();
        } else {
            this.data = dataList.get(0);
            int len = dataList.size();
            LinkedList<Data> extras = new LinkedList<Data>();
            for (int i=1; i<len; i++) {
                extras.add(dataList.get(i));
            }
            this.extraData = new ExtraData(extras);
        }
    }
    
    /**
     * データを返す。
     * @return データ
     */
    public Data getData() {
        return data;
    }
    /**
     * 拡張データ領域のバイト列の数を返す。
     * @return バイト列の数
     */
    public int getExtraSize() {
        return extraData.size();
    }
    
    /**
     * i番目の拡張データ領域のバイト列を返す。
     * @param i 拡張データ領域のインデックス
     * @return 拡張データ領域のバイト列
     */
    public Data getExtraDataAt(int i) {
        return extraData.get(i);
    }
    
    /**
     * データ長を返す。
     * @return データ長
     */
    public int size() {
        return data.size();
    }
    
    /**
     * データが存在するかどうかを返す。
     * @return データの有無
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    /**
     * データのi番目のバイトを返す
     * @param i バイトのインデックス
     * @return i番目のバイト
     */
    public byte get(int i) {
        return data.get(i);
    }
    
    /**
     * データのバイト配列を返す。
     * 拡張データ領域は無視をする。
     * @return データのバイト配列
     */
    public byte[] toBytes() {
        return data.toBytes();
    }
    
    /**
     * データの文字列表現を返す。
     * 拡張データ領域は無視をする。
     * @return データの文字列表現
     */
    @Override
    public String toString() {
        return data.toString();
    }
    
    /**
     * このDataが指定されたオブジェクトと等しいかどうか調べる。
     * 拡張データ領域も等しいかどうか調べる。
     * @param otherObj 比較されるオブジェクト
     * @return オブジェクトが等しい場合にはtrue、そうでない場合にはfalse
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof ObjectData)) {
            return false;
        }
        
        ObjectData other = (ObjectData) otherObj;
        return this.data.equals(other.data)
                && this.extraData.equals(other.extraData);
    }
    
    /**
     * このObjectDataのハッシュコードを返す。
     * @return このEOJのハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.data.hashCode();
        hash = 53 * hash + (this.extraData != null ? this.extraData.hashCode() : 0);
        return hash;
    }
}
