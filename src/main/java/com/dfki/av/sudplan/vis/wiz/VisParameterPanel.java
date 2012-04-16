/*
 *  VisParameterPanel.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.vis.core.ITransferFunction;
import com.dfki.av.sudplan.vis.core.ITransferFunctionPanel;
import com.dfki.av.sudplan.vis.core.IVisParameter;
import com.dfki.av.sudplan.vis.spi.TransferFunctionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class VisParameterPanel extends javax.swing.JPanel implements ActionListener {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(VisParameterPanel.class);
    /**
     * The IVisParameter for the UI.
     */
    private IVisParameter visParameter;
    /**
     * List of available transfer functions.
     */
    private List<ITransferFunction> transferFunctions;

    /**
     * Creates new form VisParameterPanel
     */
    public VisParameterPanel(final IVisParameter param, final List<String[]> dataAttributes) {
        this.visParameter = param;
        this.transferFunctions = new ArrayList<ITransferFunction>();
        
        initComponents();

        //Create a panel for each available transfer function.
        boolean isFirst = true;
        List<String> list = visParameter.getAvailableTransferFunctions();
        for (String functionName : list) {
            ITransferFunction function = TransferFunctionFactory.newInstance(functionName);
            if (function != null) {
                ITransferFunctionPanel tfpanel = function.getPanel();
                if (tfpanel != null) {
                    boolean attributesAdded = tfpanel.setAttributes(dataAttributes);
                    if (attributesAdded) {
                        
                        if (isFirst) {
                            visParameter.setTransferFunction(function);
                            jPanel1.removeAll();

                            if (function.getPanel() != null) {
                                function.getPanel().setVisible(true);
                                jPanel1.add(function.getPanel());
                            }
                            this.updateUI();
                            isFirst = false;
                        } 
                        
                        transferFunctions.add(function);
                        jComboBox1.addItem(function.getName());
                    }
                } else {
                    log.warn("No {} available for {}", ITransferFunctionPanel.class.getSimpleName(),
                            function.getClass().getSimpleName());
                }
            } else {
                log.warn("Could not create transfer function for {}.", functionName);
            }
        }
        jComboBox1.addActionListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setAlignmentY(0.0F);
        setMinimumSize(new java.awt.Dimension(400, 300));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(VisParameterPanel.class, "VisParameterPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, 417, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @return
     */
    public String getSelectedAttribute() {
        int i = jComboBox1.getSelectedIndex();
        ITransferFunction f = transferFunctions.get(i);
        return f.getPanel().getSelectedAttribute();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JComboBox) {
            JComboBox jComboBox = (JComboBox) source;
            int i = jComboBox.getSelectedIndex();
            ITransferFunction f = transferFunctions.get(i);
            visParameter.setTransferFunction(f);
            
            jPanel1.removeAll();

            if (f.getPanel() != null) {
                f.getPanel().setVisible(true);
                jPanel1.add(f.getPanel());
            }
            this.updateUI();
        } else {
            log.debug("Event for JRadioButton.");
        }
    }
}
