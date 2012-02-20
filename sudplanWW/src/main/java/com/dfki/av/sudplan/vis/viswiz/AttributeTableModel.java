/*
 *  AttributeTableModel.java 
 *
 *  Created by DFKI AV on 14.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.viswiz;

import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class AttributeTableModel extends DefaultTableModel {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(AttributeTableModel.class);
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
        int numRows = getRowCount();
        log.debug("Row count: {}", numRows);
        for (int i = 0; i < numRows; i++) {
            log.debug("Removing row {}", i);
            removeRow(i);
        }
        fireTableRowsDeleted(0, numRows);
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
