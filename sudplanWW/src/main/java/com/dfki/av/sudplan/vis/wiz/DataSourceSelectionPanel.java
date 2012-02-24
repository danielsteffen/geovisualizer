/*
 *  DataSourceSelectionPanel.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.io.shapefile.Shapefile;
import com.dfki.av.sudplan.vis.Settings;
import com.dfki.av.utils.AVUtils;
import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataSourceSelectionPanel extends JPanel {

    private final static Logger log = LoggerFactory.getLogger(DataSourceSelectionController.class);
    private AttributeTableModel tableModel;
    private JTable tabAttributes;
    private File file;
    private JScrollPane spAttributeTable;

    /**
     * Creates new form DataSourceSelectionPanel
     */
    public DataSourceSelectionPanel() {
        initComponents();
        // init my components here:
        this.jRadioButton2.setVisible(false);
        this.jTextField2.setVisible(false);
        this.jButton2.setVisible(false);
        this.file = null;
        
        this.tableModel = new AttributeTableModel();
        this.tabAttributes = new JTable(tableModel);
        this.tabAttributes.setPreferredScrollableViewportSize(new Dimension(500, 70));
        this.tabAttributes.setFillsViewportHeight(true);
        this.spAttributeTable = new JScrollPane(this.tabAttributes);
        jPanel2.add(this.spAttributeTable);
    }

    @Override
    public String getName() {
        return "Select Data Source";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jPanel1.border.title"))); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton1, org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jRadioButton1.text")); // NOI18N

        buttonGroup1.add(jRadioButton2);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton2, org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jRadioButton2.text")); // NOI18N
        jRadioButton2.setEnabled(false);

        jTextField2.setText(org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jTextField2.text")); // NOI18N
        jTextField2.setEnabled(false);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jButton2.text")); // NOI18N
        jButton2.setEnabled(false);
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DataSourceSelectionPanel.class, "DataSourceSelectionPanel.jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("ESRI Shapfile (.shp)", "shp"));
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("ESRI Shapfile (.zip)", "zip"));
        int retValue = jfc.showOpenDialog(this);

        if (retValue == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            if (f != null) {
                jLabel1.setText(f.getAbsolutePath());
                file = f;
                ShapefileLoader shapefileLoader = new ShapefileLoader(file);
                shapefileLoader.execute();
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No shp file selected.");
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

    public File getSelectedDataSource() {
        return this.file;
    }
    
    public List getSelectedAttributes(){
        ArrayList<String> selectedAttr = new ArrayList<String>();
        for(int rowId = 0; rowId < tableModel.getRowCount(); rowId++){
            Boolean isSelected = (Boolean)tableModel.getValueAt(rowId, 0);
            if(isSelected){
                String attr = (String)tableModel.getValueAt(rowId, 1);
                selectedAttr.add(attr);
                log.debug("Selected: {}", attr);
            }
        }
        return selectedAttr;
    }

    /**
     *
     * @author Daniel Steffen <daniel.steffen at dfki.de>
     */
    public class ShapefileLoader extends SwingWorker<Map<String, Object>, Void> {

        /*
         * Logger.
         */
        private final Logger log = LoggerFactory.getLogger(ShapefileLoader.class);
        /**
         * Data source for the layer to be produced.
         */
        private Object dataSource;

        public ShapefileLoader(Object data) {
            this.dataSource = data;
        }

        @Override
        protected Map<String, Object> doInBackground() throws Exception {
            File tmpFile = null;
            if (dataSource instanceof File) {
                tmpFile = (File) dataSource;
            } else if (dataSource instanceof URL) {
                URL url = (URL) dataSource;
                tmpFile = AVUtils.DownloadFileToDirectory(url, Settings.SUDPLAN_3D_USER_HOME);
            } else if (dataSource instanceof URI) {
                URI uri = (URI) dataSource;
                tmpFile = AVUtils.DownloadFileToDirectory(uri.toURL(), Settings.SUDPLAN_3D_USER_HOME);
            } else {
                log.error("No valid data source."
                        + "Must be of type File, URL, or URI.");
                throw new IllegalArgumentException("No valid data source for LayerWorker. "
                        + "Must be of type File, URL, or URI.");
            }

            String fileName = tmpFile.getName();
            File file = null;
            if (fileName.endsWith(".zip")) {
                AVUtils.Unzip(tmpFile, Settings.SUDPLAN_3D_USER_HOME);
                // Here, we assume that the name of the shape file equals
                // the name of the zip and vice versa.
                String shpFileName = fileName.replace(".zip", ".shp");
                file = new File(Settings.SUDPLAN_3D_USER_HOME + File.separator + shpFileName);
                log.debug("Source file: {}", file.getAbsolutePath());
            } else if (fileName.endsWith(".shp")) {
                file = tmpFile;
            } else {
                log.debug("Data type not supported yet.");
            }

            Shapefile shpFile = new Shapefile(file.getAbsolutePath());

            return shpFile.getAttributes();
        }

        @Override
        protected void done() {
            try {
                Map<String, Object> attributes = get();
                TableModel tModel = tabAttributes.getModel();
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
                tabAttributes.updateUI();
            } catch (InterruptedException ex) {
                log.error(ex.toString());
            } catch (ExecutionException ex) {
                log.error(ex.toString());
            }
            jPanel2.updateUI();
        }
    }
}