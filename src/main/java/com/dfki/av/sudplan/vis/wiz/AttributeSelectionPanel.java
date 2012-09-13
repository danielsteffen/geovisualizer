/*
 *  AttributeSelectionPanel.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.vis.core.ISource;
import com.dfki.av.sudplan.vis.io.IOUtils;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public final class AttributeSelectionPanel extends JPanel {

    /**
     * 
     */
    private final static Logger log = LoggerFactory.getLogger(AttributeSelectionPanel.class);
    /**
     * 
     */
    private AttributeTableModel tableModel;
    /**
     * 
     */
    private JTable table;
    /**
     * 
     */
    private JScrollPane spAttributeTable;

    /**
     * Creates new form DataSourceSelectionPanel
     */
    public AttributeSelectionPanel() {
        initComponents();
        // init my components here:
        this.tableModel = new AttributeTableModel();
        this.table = new JTable(tableModel);
        this.table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        this.table.setFillsViewportHeight(true);

        // Set constant size of first column
        TableColumn col = this.table.getColumnModel().getColumn(0);
        col.setMinWidth(60);
        col.setMaxWidth(60);
        col.setPreferredWidth(60);
        // Set constant size of last column
        col = this.table.getColumnModel().getColumn(2);
        col.setMinWidth(60);
        col.setMaxWidth(60);
        col.setPreferredWidth(60);

        this.spAttributeTable = new JScrollPane(this.table);
        jPanel2.add(this.spAttributeTable);
    }

    @Override
    public String getName() {
        return "Select Attributes";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(AttributeSelectionPanel.class, "AttributeSelectionPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(AttributeSelectionPanel.class, "AttributeSelectionPanel.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(430, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(Boolean.TRUE, i, 0);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(Boolean.FALSE, i, 0);
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    /**
     * Returns a list of {@link String} arrays. The size of the array is always
     * 2. At index 0 the name of the attribute and at index 1 the type of the
     * selected attribute.
     *
     * @return the selected attributes to return.
     */
    public List<String[]> getSelectedAttributes() {
        ArrayList<String[]> selectedAttr = new ArrayList<String[]>();
        for (int rowId = 0; rowId < tableModel.getRowCount(); rowId++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(rowId, 0);
            if (isSelected) {
                String[] attribute = new String[2];
                attribute[0] = (String) tableModel.getValueAt(rowId, 1);
                attribute[1] = (String) tableModel.getValueAt(rowId, 2);
                selectedAttr.add(attribute);
                log.debug("Selected: {}", attribute);
            }
        }

        return selectedAttr;
    }

    /**
     * 
     * @param data 
     */
    public void setSelectedDataSource(Object data) {
        AttributeTableFiller worker = new AttributeTableFiller(data);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        worker.execute();
    }

    /**
     *
     * @author Daniel Steffen <daniel.steffen at dfki.de>
     */
    public class AttributeTableFiller extends SwingWorker<ISource, Void> {

        /*
         * Logger.
         */
        private final Logger log = LoggerFactory.getLogger(AttributeTableFiller.class);
        /**
         * Data source for the layer to be produced.
         */
        private Object dataSource;

        /**
         * 
         * @param data 
         */
        public AttributeTableFiller(Object data) {
            this.dataSource = data;
        }

        @Override
        protected ISource doInBackground() throws Exception {
            ISource source = IOUtils.Read(dataSource);
            return source;
        }

        @Override
        protected void done() {
            AttributeSelectionPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            try {
                ISource source = get();
                Map<String, Object> attributes = source.getAttributes();
                TableModel tModel = table.getModel();
                if (tModel instanceof AttributeTableModel) {
                    AttributeTableModel model = (AttributeTableModel) tModel;
                    model.removeAllRows();
                    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                        Object[] rowData = new Object[model.getColumnCount()];
                        rowData[0] = false;
                        rowData[1] = entry.getKey();
                        rowData[2] = entry.getValue();
                        model.addRow(rowData);
                    }
                }
                table.updateUI();
            } catch (InterruptedException ex) {
                log.error(ex.toString());
            } catch (ExecutionException ex) {
                log.error(ex.toString());
            }
            jPanel2.updateUI();
        }
    }
}
