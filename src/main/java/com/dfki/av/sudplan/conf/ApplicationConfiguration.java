/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.conf;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: create Class with default Configuration (nec.?)
public class ApplicationConfiguration {
 
    private boolean loggingEnabled=true;

    public final boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public final void setLoggingEnabled(final boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }      
}
