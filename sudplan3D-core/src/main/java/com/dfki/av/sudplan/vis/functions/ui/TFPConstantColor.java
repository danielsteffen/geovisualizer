/*
 *  TFPConstantColor.java 
 *
 *  Created by DFKI AV on 07.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions.ui;

import com.dfki.av.sudplan.vis.core.TFPanel;
import com.dfki.av.sudplan.vis.functions.ConstantColor;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;

/**
 *
 * @author steffen
 */
public class TFPConstantColor extends TFPanel {

    /**
     *
     */
    private ConstantColor function;

    /**
     * Creates new form TFPConstantColor
     */
    public TFPConstantColor(final ConstantColor f) {
        this.function = f;
        initComponents();
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

        jRadioButton = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();

        jRadioButton.setText(org.openide.util.NbBundle.getMessage(TFPConstantColor.class, "TFPConstantColor.jRadioButton.text")); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setText(org.openide.util.NbBundle.getMessage(TFPConstantColor.class, "TFPConstantColor.jTextField1.text")); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(389, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
        Color c = JColorChooser.showDialog(this, "Choose your color", function.getColor());
        if (c != null) {
            jTextField1.setBackground(c);
            function.setColor(c);
        }
    }//GEN-LAST:event_jTextField1MouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton jRadioButton;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getSelectedAttribute() {
        return "<<NO_ATTRIBUTE>>";
    }

    @Override
    public void setAttributes(List<String> attributes) {
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