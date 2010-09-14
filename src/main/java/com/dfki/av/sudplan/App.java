package com.dfki.av.sudplan;

import com.dfki.av.sudplan.ui.SudplanVis;
import com.sun.j3d.utils.applet.MainFrame;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Martin Weller
 */
public class App {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Frame frame = new MainFrame(new SudplanVis(), 1000, 1000);
    }
}
