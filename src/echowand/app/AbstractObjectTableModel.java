package echowand.app;

import echowand.common.EPC;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Yoshiki Makino
 */
public abstract class AbstractObjectTableModel extends AbstractTableModel {
    public abstract void fireEPCDataUpdated(EPC epc, CachedRemoteObject updatedObject);
    public abstract void refreshCache();
    
    /*
     * helper method.
     */
    protected byte[] string2Bytes(String str) {
        if (str.startsWith("0x") || str.startsWith("0X")) {
            str = str.substring(2);
        }
        
        if (str.isEmpty() || (str.length() % 2) == 1) {
            return null;
        }

        try {
            int size = str.length() / 2;
            byte[] ret = new byte[size];
            for (int i = 0; i < size; i++) {
                int baseIndex = i * 2;
                String c1 = str.substring(baseIndex, baseIndex + 1);
                String c2 = str.substring(baseIndex + 1, baseIndex + 2);

                ret[i] = (byte) (Integer.parseInt(c1, 16) * 16 + Integer.parseInt(c2, 16));
            }

            return ret;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
