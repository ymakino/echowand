package echowand.info;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * データ長によるプロパティの制約を表現する。
 * @author Yoshiki Makino
 */
public class PropertyConstraintSize implements PropertyConstraint {
    private int minSize;
    private int maxSize;
    private Set<Integer> sizeSet;
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
     * PropertyConstraintSizeを生成する。
     * @param sizeSet プロパティデータサイズの集合
     */
    public PropertyConstraintSize(Set<Integer> sizeSet) {
        if (sizeSet.isEmpty()) {
            this.minSize = 0;
            this.maxSize = 0;
        } else {
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (int value : sizeSet) {
                if (max < value) {
                    max = value;
                }
                
                if (min > value) {
                    min = value;
                }
            }
            
            this.minSize = min;
            this.maxSize = max;
            this.sizeSet = new HashSet<Integer>(sizeSet);
        }
    }
    
    /**
     * PropertyConstraintSizeを生成する。
     * @param sizeSet プロパティデータサイズの集合
     * @param initialData プロパティの初期データ
     */
    public PropertyConstraintSize(Set<Integer> sizeSet, byte[] initialData) {
        this(sizeSet);
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
        if (sizeSet == null) {
            return (this.minSize <= data.length) && (data.length <= this.maxSize);
        } else {
            return sizeSet.contains(data.length);
        }
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
        if (this.initialData != null) {
            return Arrays.copyOf(this.initialData, this.initialData.length);
        } else {
            return new byte[minSize];
        }
    }
    
    /**
     * 初期プロパティデータを設定する。
     * @param initialData 初期プロパティデータ
     */
    public void setInitialData(byte[] initialData) {
        this.initialData = Arrays.copyOf(initialData, initialData.length);
    }
}
