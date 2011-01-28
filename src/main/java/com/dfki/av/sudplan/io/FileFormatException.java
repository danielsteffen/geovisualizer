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
class FileFormatException extends Exception {

  public FileFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public FileFormatException(String message) {
    super(message);
  }

}
