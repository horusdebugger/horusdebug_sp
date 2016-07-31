/*
 * Copyright (c) 2004 NNL Technology AB
 * All rights reserved.
 *
 * "Work" shall mean the contents of this file.
 *
 * Redistribution, copying and use of the Work, with or without
 * modification, is permitted without restrictions.
 *
 * Visit www.infonode.net for information about InfoNode(R)
 * products and how to contact NNL Technology AB.
 *
 * THE WORK IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THE WORK, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

// $Id: DockingWindowsExample.java,v 1.28 2007/01/28 21:25:10 jesper Exp $
package org.uso.depurador;

import net.infonode.docking.*;
import net.infonode.docking.drag.DockingWindowDragSource;
import net.infonode.docking.drag.DockingWindowDragger;
import net.infonode.docking.drag.DockingWindowDraggerProvider;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.*;
import net.infonode.docking.util.*;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;

/**
 * A small example on how to use InfoNode Docking Windows. This example shows how to handle both static and
 * dynamic views in the same root window.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.28 $
 */
public class DockingWindowsExample {
	
  //private static final int ICON_SIZE = 8;

  /**
   * Custom view icon.
   */
//  private static final Icon VIEW_ICON = new Icon() {
//
//    public int getIconHeight() {
//      return ICON_SIZE;
//    }
//
//    public int getIconWidth() {
//      return ICON_SIZE;
//    }
//
//    public void paintIcon(Component c, Graphics g, int x, int y) {
//      Color oldColor = g.getColor();
//
//      g.setColor(new Color(70, 70, 70));
//      g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
//
//      g.setColor(new Color(100, 230, 100));
//      g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);
//
//      g.setColor(oldColor);
//    }
//  };

  /**
   * Custom view button icon.
   */
//  private static final Icon BUTTON_ICON = new Icon() {
//    public int getIconHeight() {
//      return ICON_SIZE;
//    }
//
//    public int getIconWidth() {
//      return ICON_SIZE;
//    }
//
//    public void paintIcon(Component c, Graphics g, int x, int y) {
//      Color oldColor = g.getColor();
//
//      g.setColor(Color.red);
//      g.fillOval(x, y, ICON_SIZE, ICON_SIZE);
//
//      g.setColor(oldColor);
//    }
//  };

  /**
   * The one and only root window
   */
  private RootWindow rootWindow;

  /**
   * An array of the static views
   */
  private View[] views = new View[10];

  /**
   * Contains all the static views
   */
  private ViewMap viewMap = new ViewMap();

  /**
   * The view menu items
   */


  /**
   * Contains the dynamic views that has been added to the root window
   */
  //private HashMap dynamicViews = new HashMap();

  /**
   * The currently applied docking windows theme
   */
  private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();

  /**
   * A dynamically created view containing an id.
   */
  

  /**
   * In this properties object the modified property values for close buttons etc. are stored. This object is cleared
   * when the theme is changed.
   */
  private RootWindowProperties properties = new RootWindowProperties();

  /**
   * Where the layouts are stored.
   */
  private byte[][] layouts = new byte[3][];

  /**
   * Menu item for enabling/disabling adding of a menu bar and a status label to all new floating windows.
   */
 

  /**
   * The application frame
   */
  private JFrame frame = new JFrame("InfoNode Docking Windows Example");

  public DockingWindowsExample() {
    createRootWindow();
    setDefaultLayout();
    showFrame();
  }

  /**
   * Creates a view component containing the specified text.
   *
   * @param text the text
   * @return the view component
   */
  private static JComponent createViewComponent(String text) {
    StringBuffer sb = new StringBuffer();

    for (int j = 0; j < 100; j++)
      sb.append(text + ". This is line " + j + "\n");

    return new JScrollPane(new JTextArea(sb.toString()));
  }

  /**
   * Returns a dynamic view with specified id, reusing an existing view if possible.
   *
   * @param id the dynamic view id
   * @return the dynamic view
   */


  /**
   * Returns the next available dynamic view id.
   *
   * @return the next available dynamic view id
   */


  /**
   * Creates the root window and the views.
   */
  private void createRootWindow() {
    // Create the views
    for (int i = 0; i < views.length; i++) {
      views[i] = new View("View " + i, null, createViewComponent("View " + i));
      viewMap.addView(i, views[i]);
    }

    // Create a custom view button and add it to view 2
//    JButton button = new JButton(BUTTON_ICON);
//    button.setOpaque(false);
//    button.setBorder(null);
//    button.setFocusable(false);
//    button.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        JOptionPane.showMessageDialog(frame,
//                                      "You clicked the custom view button.",
//                                      "Custom View Button",
//                                      JOptionPane.INFORMATION_MESSAGE);
//      }
//    });
//    views[3].getCustomTabComponents().add(button);

    // The mixed view map makes it easy to mix static and dynamic views inside the same root window

    rootWindow = DockingUtil.createRootWindow(viewMap, null, true);

    // Set gradient theme. The theme properties object is the super object of our properties object, which
    // means our property value settings will override the theme values
    properties.addSuperObject(currentTheme.getRootWindowProperties());

    // Our properties object is the super object of the root window properties object, so all property values of the
    // theme and in our property object will be used by the root window
    rootWindow.getRootWindowProperties().addSuperObject(properties);

    // Enable the bottom window bar
    rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);

    // Add a listener which shows dialogs when a window is closing or closed.
    rootWindow.addListener(new DockingWindowAdapter() {
      public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
        //updateViews(addedWindow, true);

        // If the added window is a floating window, then update it
        if (addedWindow instanceof FloatingWindow){}
          //updateFloatingWindow((FloatingWindow) addedWindow);
      }

      public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow) {
        //updateViews(removedWindow, false);
      }

      public void windowClosing(DockingWindow window) throws OperationAbortedException {
        // Confirm close operation
        if (JOptionPane.showConfirmDialog(frame, "Really close window '" + window + "'?") != JOptionPane.YES_OPTION){}
          //throw new OperationAbortedException("Window close was aborted!");
      }

      public void windowDocking(DockingWindow window) throws OperationAbortedException {
        // Confirm dock operation
        if (JOptionPane.showConfirmDialog(frame, "Really dock window '" + window + "'?") != JOptionPane.YES_OPTION)
        {} //throw new OperationAbortedException("Window dock was aborted!");
      }

      public void windowUndocking(DockingWindow window) throws OperationAbortedException {
        // Confirm undock operation 
        if (JOptionPane.showConfirmDialog(frame, "Really undock window '" + window + "'?") != JOptionPane.YES_OPTION)
        {} //throw new OperationAbortedException("Window undock was aborted!");
      }

    });

    // Add a mouse button listener that closes a window when it's clicked with the middle mouse button.
    rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
  }

  /**
   * Update view menu items and dynamic view map.
   *
   * @param window the window in which to search for views
   * @param added  if true the window was added
   */


  /**
   * Sets the default window layout.
   */
  private void setDefaultLayout() {
    TabWindow tabWindow = new TabWindow(views);
    
    rootWindow.setWindow(new SplitWindow(true,
                                         0.3f,
                                         //false es para saber si es horizontal o vertical
                                         new SplitWindow(false,
                                                         0.7f,
                                                         new TabWindow(new View[]{views[0], views[1]}),
                                                         views[2]),
                                         tabWindow));

    WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);

    while (windowBar.getChildWindowCount() > 0)
      windowBar.getChildWindow(0).close();

    windowBar.addTab(views[3]);
    //windowBar.addTab(views[4]);
  }

  /**
   * Initializes the frame and shows it.
   */
  private void showFrame() {
    //frame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
    frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
    //frame.setJMenuBar(createMenuBar());
    frame.setSize(900, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  /**
   * Creates the frame tool bar.
   *
   * @return the frame tool bar
   */
 /* private JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    JLabel label = new JLabel("Drag New View");
    toolBar.add(label);
    new DockingWindowDragSource(label, new DockingWindowDraggerProvider() {
      public DockingWindowDragger getDragger(MouseEvent mouseEvent) {
        return getDynamicView(getDynamicViewId()).startDrag(rootWindow);
      }
    });
    return toolBar;
  }*/

  /**
   * Creates the frame menu bar.
   *
   * @return the menu bar
   */
//  private JMenuBar createMenuBar() {
//    JMenuBar menu = new JMenuBar();
//    menu.add(createFloatingWindowMenu());
//    return menu;
//  }



  public static void main(String[] args) throws Exception {
    // Set InfoNode Look and Feel
    //UIManager.setLookAndFeel());

    // Docking windwos should be run in the Swing thread
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new DockingWindowsExample();
      }
    });
  }
}
