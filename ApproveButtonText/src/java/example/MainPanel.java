// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new GridLayout(2, 1));
    add(makeDefaultChooserPanel());
    add(makeCustomChooserPanel());
    setPreferredSize(new Dimension(320, 240));
  }

  private JPanel makeCustomChooserPanel() {
    // for JDK 1.6.0_11
    // UIManager.put("FileChooser.saveButtonText", "保存(S)");
    // UIManager.put("FileChooser.openButtonText", "開く(O)");
    UIManager.put("FileChooser.cancelButtonText", "キャンセル");
    JPanel p = new JPanel();
    p.setBorder(BorderFactory.createTitledBorder("custom"));

    JButton showOpenDialog = new JButton("Open:取消し->キャンセル");
    showOpenDialog.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      // fileChooser.setApproveButtonText("開く(O)");
      // fileChooser.setApproveButtonMnemonic('O');
      int retValue = fileChooser.showOpenDialog(p);
      System.out.println(retValue);
    });
    JButton showSaveDialog = new JButton("Save:取消し->キャンセル");
    showSaveDialog.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      // fileChooser.addPropertyChangeListener(e -> {
      //   String prop = e.getPropertyName();
      //   System.out.println("----\n" + prop);
      //   if (prop == JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY ||
      //     prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
      //     System.out.println("sss");
      //     fileChooser.setApproveButtonText("保存(S)");
      //     fileChooser.setApproveButtonMnemonic('S');
      //   }
      // });
      int retValue = fileChooser.showSaveDialog(p);
      System.out.println(retValue);
    });
    p.add(showOpenDialog);
    p.add(showSaveDialog);
    return p;
  }

  private JPanel makeDefaultChooserPanel() {
    JFileChooser defaultChooser = new JFileChooser();
    JPanel p = new JPanel();
    p.setBorder(BorderFactory.createTitledBorder("default"));
    JButton showOpenDialog = new JButton("showOpenDialog");
    showOpenDialog.addActionListener(e -> {
      int retValue = defaultChooser.showOpenDialog(p);
      System.out.println(retValue);
    });
    JButton showSaveDialog = new JButton("showSaveDialog");
    showSaveDialog.addActionListener(e -> {
      int retValue = defaultChooser.showSaveDialog(p);
      System.out.println(retValue);
    });
    p.add(showOpenDialog);
    p.add(showSaveDialog);
    return p;
  }

  // private String saveButtonText = null;
  // private String openButtonText = null;
  // private String cancelButtonText = null;
  // private void initDefaultButtonText(boolean default) {
  //   if (default) {
  //     if (saveButtonText == null) {
  //       Locale l = fc.getLocale();
  //       saveButtonText = UIManager.getString("FileChooser.saveButtonText", l);
  //       openButtonText = UIManager.getString("FileChooser.openButtonText", l);
  //       cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", l);
  //     }
  //     UIManager.put("FileChooser.saveButtonText", saveButtonText);
  //     UIManager.put("FileChooser.openButtonText", openButtonText);
  //     UIManager.put("FileChooser.cancelButtonText", cancelButtonText);
  //   } else {
  //     UIManager.put("FileChooser.saveButtonText", "保存(S)");
  //     UIManager.put("FileChooser.openButtonText", "開く(O)");
  //     UIManager.put("FileChooser.cancelButtonText", "キャンセル");
  //   }
  // }

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
