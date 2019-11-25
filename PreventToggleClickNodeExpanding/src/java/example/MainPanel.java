// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new GridLayout(1, 2, 4, 4));
    File dir = new File(".");
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(dir);
    DefaultTreeModel treeModel = new DefaultTreeModel(root);
    createChildren(dir, root);

    JTree tree1 = new JTree(treeModel);
    tree1.addTreeWillExpandListener(new FileExpandVetoListener());

    JTree tree2 = new JTree(treeModel);
    tree2.setUI(new MetalTreeUI() {
      @Override protected boolean isToggleEvent(MouseEvent e) {
        File file = getFileFromTreePath(tree.getSelectionPath());
        return file == null && super.isToggleEvent(e);
      }
    });

    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(new JScrollPane(initTree(tree1)));
    add(new JScrollPane(initTree(tree2)));
    setPreferredSize(new Dimension(320, 240));
  }

  private static JTree initTree(JTree tree) {
    tree.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    tree.setCellRenderer(new FileTreeCellRenderer());
    tree.addMouseListener(new MouseAdapter() {
      @Override public void mouseClicked(MouseEvent e) {
        boolean isDoubleClick = e.getClickCount() == 2;
        if (isDoubleClick) {
          File file = getFileFromTreePath(tree.getSelectionPath());
          System.out.println(file);
        }
      }
    });
    // tree.setToggleClickCount(0);
    tree.expandRow(0);
    return tree;
  }

  private static void createChildren(File parent, DefaultMutableTreeNode node) {
    File[] list = parent.listFiles();
    if (list == null) {
      return;
    }
    Arrays.asList(list).forEach(file -> {
      DefaultMutableTreeNode child = new DefaultMutableTreeNode(file);
      node.add(child);
      if (file.isDirectory()) {
        createChildren(file, child);
      } else if (file.getName().equals("MainPanel.java")) {
        child.add(new DefaultMutableTreeNode("MainPanel()"));
        child.add(new DefaultMutableTreeNode("createAndShowGui():void"));
        child.add(new DefaultMutableTreeNode("createChildren(File, DefaultMutableTreeNode):void"));
        child.add(new DefaultMutableTreeNode("main(String[]):void"));
      }
    });
  }

  protected static File getFileFromTreePath(TreePath path) {
    Object o = path.getLastPathComponent();
    if (o instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
      Object uo = node.getUserObject();
      if (uo instanceof File && ((File) uo).isFile()) {
        return (File) uo;
      }
    }
    return null;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class FileExpandVetoListener implements TreeWillExpandListener {
  @Override public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
    TreePath path = e.getPath();
    Object o = path.getLastPathComponent();
    if (o instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
      File file = (File) node.getUserObject();
      if (file.isFile()) {
        throw new ExpandVetoException(e, "Tree expansion cancelled");
      }
    }
  }

  @Override public void treeWillCollapse(TreeExpansionEvent e) { // throws ExpandVetoException {
    // throw new ExpandVetoException(e, "Tree collapse cancelled");
  }
}

class FileTreeCellRenderer extends DefaultTreeCellRenderer {
  @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    if (selected) {
      c.setOpaque(false);
      c.setForeground(getTextSelectionColor());
    } else {
      c.setOpaque(true);
      c.setForeground(getTextNonSelectionColor());
      c.setBackground(getBackgroundNonSelectionColor());
    }
    if (value instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Object o = node.getUserObject();
      if (o instanceof File) {
        try {
          File file = ((File) o).getCanonicalFile();
          c.setText(file.getName());
        } catch (IOException ex) {
          c.setText(ex.getMessage());
        }
      }
    }
    return c;
  }
}
