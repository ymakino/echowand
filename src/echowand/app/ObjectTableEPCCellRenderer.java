package echowand.app;

import echowand.common.EPC;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableEPCCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(CENTER);
        EPC epc = (EPC)value;
        if (epc != null) {
            setText(((EPC)value).toString().substring(1));
        } else {
            setText("");
        }

        return this;
    }
}
