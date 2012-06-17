package echowand.util;

/**
 * プロパティの値の制約を、他の制約の補集合で表現する。
 * @author Yoshiki Makino
 */
public class ConstraintComplement implements Constraint {
    private Constraint constraint;
    
    /**
     * ConstraintComplementを生成する。
     * @param constraint 補集合の元となる制約
     */
    public ConstraintComplement(Constraint constraint) {
        this.constraint = constraint;
    }
    
    @Override
    public boolean isValid(byte[] data) {
        return !constraint.isValid(data);
    }
    
    @Override
    public String toString() {
        return String.format("C(%s)", constraint.toString());
    }
}
