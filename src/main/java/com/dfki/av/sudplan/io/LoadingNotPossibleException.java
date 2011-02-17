/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.io;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class LoadingNotPossibleException extends Exception {

    public LoadingNotPossibleException(Throwable cause) {
        super(cause);
    }

    public LoadingNotPossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadingNotPossibleException(String message) {
        super(message);
    }

    public LoadingNotPossibleException() {
        super();
    }
}
