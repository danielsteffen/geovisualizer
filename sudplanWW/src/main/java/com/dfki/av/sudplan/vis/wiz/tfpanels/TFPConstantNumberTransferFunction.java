/*
 *  TFPConstantNumberTransferFunction.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz.tfpanels;

import com.dfki.av.sudplan.vis.algorithm.functions.ConstantNumberTansferFunction;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class TFPConstantNumberTransferFunction extends JPanel implements TFPanel{

    /*
     *
     */
    private static final Logger log = LoggerFactory.getLogger(TFPConstantNumberTransferFunction.class);
    /**
     * 
     */
    private ConstantNumberTansferFunction function;

    /**
     * Creates new form TFPanelIdentityFunction
     */
    public TFPConstantNumberTransferFunction(final ConstantNumberTansferFunction f, final List<String> attributes, ButtonGroup bg, ActionListener l) {
        this.function = f;
        
        initComponents();
        jTextField1.setText(f.getConstant().toString());
        // Finally, add the jRadioButton to the buttongroup
        jRadioButton.setActionCommand(f.getClass().getSimpleName());
        jRadioButton.addActionListener(l);
        bg.add(jRadioButton);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jRadioButton = new javax.swing.JRadioButton();

        jTextField1.setText(org.openide.util.NbBundle.getMessage(TFPConstantNumberTransferFunction.class, "TFPConstantNumberTransferFunction.jTextField1.text")); // NOI18N
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        jRadioButton.setText(org.openide.util.NbBundle.getMessage(TFPConstantNumberTransferFunction.class, "TFPConstantNumberTransferFunction.jRadioButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(239, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s = jTextField1.getText();
        Double d = Double.parseDouble(s);
        log.debug("Setting constant to {}", d.toString());
        function.setConstant(d);
    }//GEN-LAST:event_jTextField1FocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton jRadioButton;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getSelectedAttribute() {
        return "<<NO_ATTRIBUTE>>";
    }
}