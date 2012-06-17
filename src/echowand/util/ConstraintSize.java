package echowand.util;

import echowand.util.Constraint;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * データ長によるプロパティの制約を表現する。
 * @author Yoshiki Makino
 */
public class ConstraintSize implements Constraint {
    private int minSize;
    private int maxSize;
    private Set<Integer> sizeSet;
    
    /**
     * ConstraintSizeを生成する。
     * @param size プロパティデータサイズの制約
     */
    public ConstraintSize(int size) {
        this(size, size);
    }
    
    /**
     * ConstraintSizeを生成する。
     * @param minSize プロパティデータの最小サイズ
     * @param maxSize プロパティデータの最大サイズ
     */
    public ConstraintSize(int minSize, int maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    
    /**
     * ConstraintSizeを生成する。
     * @param sizeSet プロパティデータサイズの集合
     */
    public ConstraintSize(Set<Integer> sizeSet) {
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
    
    private boolean isValidSize(byte[] data) {
        if (sizeSet == null) {
            return (this.minSize <= data.length) && (data.length <= this.maxSize);
        } else {
            return sizeSet.contains(data.length);
        }
    }
    
    @Override
    public boolean isValid(byte[] data) {
        if (data == null) {
            return false;
        }
        
        return isValidSize(data);
    }
    
    @Override
    public String toString() {
        return String.format("Size(%d, %d)", minSize, maxSize);
    }
}
