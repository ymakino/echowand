package echowand.util;

/**
 * プロパティの値の制約を、他の制約の和で表現する。
 * @author Yoshiki Makino
 */
public class ConstraintUnion implements Constraint {
    Constraint constraint1;
    Constraint constraint2;
    
    /**
     * ConstraintUnionを生成する。
     * @param constraint1 和の元となる制約
     * @param constraint2 和の元となる制約
     */
    public ConstraintUnion(Constraint constraint1, Constraint constraint2) {
        this.constraint1 = constraint1;
        this.constraint2 = constraint2;
    }
    
    @Override
    public boolean isValid(byte[] data) {
        return constraint1.isValid(data) || constraint2.isValid(data);
    }
    
    @Override
    public String toString() {
        return String.format("(%s | %s)", constraint1.toString(), constraint2.toString());
    }
}
