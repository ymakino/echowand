package echowand.app;

import java.awt.Component;

/**
 *
 * @author Yoshiki Makino
 */
public class AdjacentComponents {
    private Component left;
    private Component right;
    private AdjacentComponentsRequestFocus leftFocus = null;
    private AdjacentComponentsRequestFocus rightFocus = null;
            
    class DefaultRequestFocus extends AdjacentComponentsRequestFocus {
        @Override
        public void requestFocus(Component component) {
            component.requestFocus();
        }
    }
    
    public AdjacentComponents(Component left, Component right) {
        this.left = left;
        this.right = right;
        this.leftFocus = new DefaultRequestFocus();
        this.rightFocus = new DefaultRequestFocus();
    }
    
    public void setRequestRightFocus(AdjacentComponentsRequestFocus rightFocus) {
        this.rightFocus = rightFocus;
    }
    
    public void setRequestLeftFocus(AdjacentComponentsRequestFocus leftFocus) {
        this.leftFocus = leftFocus;
    }
    
    public void setLeft(Component left) {
        this.left = left;
    }
    
    public void setRight(Component right) {
        this.right = right;
    }
    
    public Component getLeft() {
        return left;
    }
    
    public Component getRight() {
        return right;
    }

    public boolean requestLeftFocus() {
        if (left == null) {
            return false;
        }

        leftFocus.requestFocus(left);

        return true;
    }
    
    public boolean requestRightFocus() {
        if (right == null) {
            return false;
        }
        
        rightFocus.requestFocus(right);
        
        return true;
    }
}
