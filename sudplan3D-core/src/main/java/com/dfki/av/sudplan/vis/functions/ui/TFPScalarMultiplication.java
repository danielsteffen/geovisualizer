/*
 *  TFPScalarMultiplication.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions.ui;

import com.dfki.av.sudplan.vis.core.TFPanel;
import com.dfki.av.sudplan.vis.functions.ScalarMultiplication;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class TFPScalarMultiplication extends TFPanel {

    /*
     *
     */
    private static final Logger log = LoggerFactory.getLogger(TFPScalarMultiplication.class);
    /**
     *
     */
    private ScalarMultiplication function;

    /**
     * Creates new form TFPanelIdentityFunction
     */
    public TFPScalarMultiplication(final ScalarMultiplication f) {
        this.function = f;
        initComponents();
        jTextField1.setText(function.getScaleValue().toString());
        jRadioButton.setActionCommand(f.getClass().getSimpleName());
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
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jRadioButton = new javax.swing.JRadioButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TFPScalarMultiplication.class, "TFPScalarMultiplication.jLabel1.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(TFPScalarMultiplication.class, "TFPScalarMultiplication.jTextField1.text")); // NOI18N
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        jRadioButton.setText(org.openide.util.NbBundle.getMessage(TFPScalarMultiplication.class, "TFPScalarMultiplication.jRadioButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s = jTextField1.getText();
        Double d = Double.parseDouble(s);
        log.debug("Setting scale value to {}", d.toString());
        function.setScaleValue(d);
    }//GEN-LAST:event_jTextField1FocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getSelectedAttribute() {
        return (String) jComboBox1.getSelectedItem();
    }

    @Override
    public void setAttributes(List<String> attributes) {
        for (Iterator<String> it = attributes.iterator(); it.hasNext();) {
            String string = it.next();
            jComboBox1.addItem(string);
        }
    }

    @Override
    public void setButtonGroup(ButtonGroup bg) {
        bg.add(jRadioButton);
    }

    @Override
    public void setActionListener(ActionListener l) {
        jRadioButton.addActionListener(l);
    }

    @Override
    public void setSelected(boolean b) {
        jRadioButton.setSelected(b);
    }
}
