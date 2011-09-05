/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.control;

import java.util.TooManyListenersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
class DragAndDropException extends Exception{
    private final static Logger logger = LoggerFactory.getLogger(DragAndDropException.class);

    public DragAndDropException(Throwable cause) {
        super(cause);
    }

    public DragAndDropException(String message, Throwable cause) {
        super(message, cause);
    }

    public DragAndDropException(String message) {
        super(message);
    }

    public DragAndDropException() {
        super();
    }
}
