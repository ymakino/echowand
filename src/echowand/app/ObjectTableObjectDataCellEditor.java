package echowand.app;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectTableObjectDataCellEditor extends DefaultCellEditor {
    private JTextField textField;
    
    public ObjectTableObjectDataCellEditor() {
        super(new JTextField());
        textField = (JTextField)this.getComponent();
        textField.setBorder(new LineBorder(Color.BLACK, 1, true));
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}
