/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.util;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class TransformationException extends Exception {

    public TransformationException(Throwable cause) {
        super(cause);
    }

    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformationException(String message) {
        super(message);
    }

    public TransformationException() {
    }
}
