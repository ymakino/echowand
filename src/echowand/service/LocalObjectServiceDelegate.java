package echowand.service;

import echowand.object.LocalObject;
import echowand.object.LocalObjectDelegate;

/**
 *
 * @author ymakino
 */
public interface LocalObjectServiceDelegate extends LocalObjectDelegate {
    public void notifyCreation(LocalObject object);
}
