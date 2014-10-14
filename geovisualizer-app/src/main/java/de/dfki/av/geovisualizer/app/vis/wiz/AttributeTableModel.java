/*
 * AttributeTableModel.java
 *
 * Created by DFKI AV on 14.10.2011.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis.wiz;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AttributeTableModel extends DefaultTableModel {

    private final static Object[] DATA_TYPES = new Object[]{
        Boolean.FALSE,
        "none",
        "none"
    };
    private final static String[] TABLE_HEADER = new String[]{
        "Selection",
        "Attribute",
        "Type"
    };

    public AttributeTableModel() {
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
        return column == 0 ? true : false;
    }
}
