package com.dfki.av.sudplan.vis.functions;

import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class ClassificationTableModel extends DefaultTableModel{
    /*
     * Logger.
     */
    private final static Logger log = LoggerFactory.getLogger(ClassificationTableModel.class);
    private final static Object[] DATA_TYPES = new Object[]{
        "none",
        "none",
        Color.GRAY
    };
    private final static String[] TABLE_HEADER = new String[]{
        "Min",
        "Max",
        "Color"
    };

    public ClassificationTableModel() {
        super(TABLE_HEADER, 0);
    }

    public void removeAllRows() {
        final int rowCount = getRowCount(); 
        dataVector.clear();
        fireTableRowsDeleted(0, rowCount);
    }

    @Override
    public Class getColumnClass(int id) {
        Class c = DATA_TYPES[id].getClass();
        return c;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
