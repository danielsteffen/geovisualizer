/*
 * ClassificationTableModel.java
 *
 * Created by DFKI AV on 26.01.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions.ui;

import java.awt.Color;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ClassificationTableModel extends DefaultTableModel {

    /**
     *
     */
    private final static Object[] DATA_TYPES = new Object[]{
        "none",
        "none",
        Color.GRAY
    };
    /**
     *
     */
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
