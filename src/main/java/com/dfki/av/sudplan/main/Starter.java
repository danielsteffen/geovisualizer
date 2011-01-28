/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.main;

import com.dfki.av.sudplan.conf.ApplicationConfiguration;
import com.dfki.av.sudplan.conf.ComponentFactory;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class Starter {

    
    public static void main(String[] args) {
       
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: CLI support
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ComponentFactory.getStandaloneApplication(new ApplicationConfiguration()).setVisible(true);
            }   
        });
    }
}
