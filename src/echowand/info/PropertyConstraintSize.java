package echowand.info;

import java.util.Arrays;

/**
 * データ長によるプロパティの制約を表現する。
 * @author Yoshiki Makino
 */
public class PropertyConstraintSize implements PropertyConstraint {
    private int minSize;
    private int maxSize;
    private byte[] initialData;
    
    /**
     * PropertyConstraintSizeを生成する。
     * @param size プロパティデータサイズの制約
     */
    public PropertyConstraintSize(int size) {
        this(size, size);
    }
    
    /**
     * PropertyConstraintSizeを生成する。
     * @param size プロパティデータサイズの制約
     * @param initialData プロパティの初期データ
     */
    public PropertyConstraintSize(int size, byte[] initialData) {
        this(size, size, initialData);
    }
    
    /**
     * PropertyConstraintSizeを生成する。
     * @param minSize プロパティデータの最小サイズ
     * @param maxSize プロパティデータの最大サイズ
     */
    public PropertyConstraintSize(int minSize, int maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.initialData = new byte[minSize];
    }
    
    /**
     * PropertyConstraintSizeを生成する。
     * @param minSize プロパティデータの最小サイズ
     * @param maxSize プロパティデータの最大サイズ
     * @param initialData プロパティの初期データ
     */
    public PropertyConstraintSize(int minSize, int maxSize, byte[] initialData) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.initialData = Arrays.copyOf(initialData, initialData.length);
    }
    
    /**
     * プロパティデータの最小サイズを返す。
     * @return プロパティデータの最小サイズ
     */
    public int getMinSize() {
        return this.minSize;
    }
    
    /**
     * プロパティデータの最大サイズを返す。
     * @return プロパティデータの最大サイズ
     */
    public int getMaxSize() {
        return this.maxSize;
    }
    
    private boolean isAcceptableSize(byte[] data) {
        return (this.minSize <= data.length) && (data.length <= this.maxSize);
    }
    
    @Override
    public boolean isAcceptable(byte[] data) {
        if (data == null) {
            return false;
        }
        
        return isAcceptableSize(data);
    }
    
    @Override
    public byte[] getInitialData() {
        return Arrays.copyOf(this.initialData, this.initialData.length);
    }
    
    /**
     * 初期プロパティデータを設定する。
     * @param initialData 初期プロパティデータ
     */
    public void setInitialData(byte[] initialData) {
        this.initialData = Arrays.copyOf(initialData, initialData.length);
    }
}
