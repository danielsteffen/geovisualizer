/*
 *  ShapefileTableModel.java 
 *
 *  Created by DFKI AV on 14.10.2011.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.ui.table;

import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class ShapefileTableModel extends DefaultTableModel {

    private final static Object[] DATA_TYPES = new Object[]{
        Boolean.FALSE,
        "none",
        "none",
        "none"
    };
    private final static String[] TABLE_HEADER = new String[]{
        "Selection",
        "Attribute",
        "Type",
        "Visualization Technique"
    };
    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ShapefileTableModel() {
        super(TABLE_HEADER, 0);
    }

    public void removeAllRows() {
        if (log.isDebugEnabled()) {
            log.debug("Row count: {}", getRowCount());
        }

        for (int i = 0; i < getRowCount(); i++) {
            if (log.isDebugEnabled()) {
                log.debug("Removing row {}", i);
            }
            removeRow(i);
        }
    }

    @Override
    public Class getColumnClass(int id) {
        Class c = DATA_TYPES[id].getClass();
        return c;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0 || column == 3 ? true : false;
    }
}
