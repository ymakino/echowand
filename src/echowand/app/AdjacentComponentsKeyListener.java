package echowand.app;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Yoshiki Makino
 */
public class AdjacentComponentsKeyListener extends KeyAdapter {

    private AdjacentComponents adjacents;

    public AdjacentComponentsKeyListener(AdjacentComponents adjacents) {
        this.adjacents = adjacents;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                adjacents.requestLeftFocus();
                break;
            case KeyEvent.VK_RIGHT:
                adjacents.requestRightFocus();
                break;
        }
    }
}
