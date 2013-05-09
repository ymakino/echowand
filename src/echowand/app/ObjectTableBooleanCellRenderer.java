package echowand.app;

import java.awt.Component;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableBooleanCellRenderer extends DefaultTableCellRenderer {
    private static String TrueString = "\u25ef";
    private static String FalseString = "";
    private static HashMap<Boolean, String> bool2str;
    
    static {
        bool2str = new HashMap<Boolean, String>();
        bool2str.put(true, TrueString);
        bool2str.put(false, FalseString);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(CENTER);

        setText(bool2str.get((Boolean)value));

        return this;
    }
}
