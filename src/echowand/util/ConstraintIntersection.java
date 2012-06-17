package echowand.util;

/**
 * プロパティの値の制約を、他の制約の交わりで表現する。
 * @author Yoshiki Makino
 */
public class ConstraintIntersection implements Constraint {
    Constraint constraint1;
    Constraint constraint2;
    
    /**
     * ConstraintIntersectionを生成する。
     * @param constraint1 交わりの元となる制約
     * @param constraint2 交わりの元となる制約
     */
    public ConstraintIntersection(Constraint constraint1, Constraint constraint2) {
        this.constraint1 = constraint1;
        this.constraint2 = constraint2;
    }
    
    @Override
    public boolean isValid(byte[] data) {
        return constraint1.isValid(data) && constraint2.isValid(data);
    }
    
    @Override
    public String toString() {
        return String.format("(%s & %s)", constraint1.toString(), constraint2.toString());
    }
}
