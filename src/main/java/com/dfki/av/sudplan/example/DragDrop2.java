/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;

//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:copied from the web credit to the author
public class DragDrop2 extends JFrame
    implements DragGestureListener, DragSourceListener, DropTargetListener
{
    static String items[] = { "Item 1", "Item No. 2", "The Third Item!" };

    Container cpane;
    DropTarget mytarget;
    DragSource mysource;
    DragGestureRecognizer dgr;

    JLabel source1;
    JList  list1;
    JTextArea text1;
    DefaultListModel lmod;

    public DragDrop2() {
   super("Drag1 Test");
   cpane = getContentPane();
   cpane.setLayout(new BorderLayout());

   source1 = new JLabel("Drag Source Label - drag me!");
   lmod = new DefaultListModel();
   list1 = new JList(lmod);
   for(int i = 0; i < items.length; i++)
       lmod.addElement(items[i]);
   text1 = new JTextArea("Drag from the label to\nthe list, then drop!",
      5, 24);

   mysource = new DragSource();

   mytarget = new DropTarget(list1,
      DnDConstants.ACTION_COPY_OR_MOVE, this);

   dgr = mysource.createDefaultDragGestureRecognizer(source1,
      DnDConstants.ACTION_COPY_OR_MOVE, this);

   cpane.add("North", source1);
   cpane.add("Center", list1);
   cpane.add("South", text1);
   text1.setBackground(Color.pink);
   setBounds(100, 100, 320, 460);
   setVisible(true);
    }

    public void dragGestureRecognized(DragGestureEvent e) {
   String sel = source1.getText();
   e.startDrag(DragSource.DefaultCopyDrop,
          new StringSelection(sel), this);
    }

    public void dragEnter(DropTargetDragEvent e) {
   System.out.println("dragEnter");
   e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }
    public void drop(DropTargetDropEvent e) {
   System.out.println("drop");
   try {
       if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      Transferable tr = e.getTransferable();
      e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
      String s = (String)tr.getTransferData(DataFlavor.stringFlavor);
      lmod.addElement(s);
      e.dropComplete(true);
       }
       else {
      e.rejectDrop();
       }
   }
   catch (Exception ex) {
       System.out.println("Data transfer exception: " + ex);
   }
    }
    public void dragExit(DropTargetEvent e) {
   System.out.println("dragExit");
    }
    public void dragOver(DropTargetDragEvent e) {
   System.out.println("dragOver (drop)");
    }
    public void dropActionChanged(DropTargetDragEvent e) {
   System.out.println("dropActionChanged");
    }

    public void dragDropEnd(DragSourceDropEvent e) {
   System.out.println("dragDropEnd");
    }
    public void dragEnter(DragSourceDragEvent e) {
   System.out.println("dragEnter");
    }
    public void dragExit(DragSourceEvent e) {
   System.out.println("dragExit");
    }
    public void dragOver(DragSourceDragEvent e) {
   System.out.println("dragOver (drag)");
    }
    public void dropActionChanged(DragSourceDragEvent e) {
   System.out.println("dragActionChanged (drag)");
    }


    public static void main(String [] args) {
   DragDrop2 d1;
   d1 = new DragDrop2();
   d1.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
          System.exit(0);
      } } );
    }
}
