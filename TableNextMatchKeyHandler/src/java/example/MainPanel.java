// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Position;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    String[] columnNames = {"String", "Integer", "Boolean"};
    Object[][] data = {
      {"aaa", 12, true}, {"bbb", 5, false},
      {"aaa", 15, true}, {"bbb", 6, false},
      {"abc", 92, true}, {"Bbb", 0, false}
    };
    TableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
      }
    };
    JTable table = new JTable(model);
    table.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
    table.setAutoCreateRowSorter(true);
    table.addKeyListener(new TableNextMatchKeyHandler());

    add(new JScrollPane(table));
    setPreferredSize(new Dimension(320, 240));
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

// @see javax/swing/plaf/basic/BasicListUI.Handler
// @see javax/swing/plaf/basic/BasicTreeUI.Handler
class TableNextMatchKeyHandler extends KeyAdapter {
  private static final int TARGET_COLUMN = 0;
  private static final long TIME_FACTOR = 500L;
  private String prefix = "";
  private String typedString;
  private long lastTime;
  // private final long timeFactor;
  // protected TableNextMatchKeyHandler() {
  //   super();
  //   Long l = (Long) UIManager.get("List.timeFactor");
  //   timeFactor = Objects.nonNull(l) ? l.longValue() : 1000L;
  // }

  private boolean isNavigationKey(KeyEvent event) {
    JTable table = (JTable) event.getComponent();
    InputMap inputMap = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    KeyStroke key = KeyStroke.getKeyStrokeForEvent(event);
    return Objects.nonNull(inputMap) && Objects.nonNull(inputMap.get(key));
  }

  @Override public void keyPressed(KeyEvent e) {
    if (isNavigationKey(e)) {
      prefix = "";
      typedString = "";
      lastTime = 0L;
    }
  }

  @Override public void keyTyped(KeyEvent e) {
    JTable src = (JTable) e.getComponent();
    int max = src.getRowCount();
    if (max == 0 || e.isAltDown() || isNavigationKey(e)) { // || BasicGraphicsUtils.isMenuShortcutKeyDown(e)) {
      // Nothing to select
      return;
    }
    boolean startingFromSelection = !src.getSelectionModel().isSelectionEmpty();
    char c = e.getKeyChar();
    int increment = e.isShiftDown() ? -1 : 1;
    long time = e.getWhen();
    int startIndex = src.getSelectedRow();
    if (time - lastTime < TIME_FACTOR) {
      typedString += c;
      if (prefix.length() == 1 && c == prefix.charAt(0)) {
        // Subsequent same key presses move the keyboard focus to the next
        // object that starts with the same letter.
        startIndex += increment;
      } else {
        prefix = typedString;
      }
    } else {
      startIndex += increment;
      typedString = Objects.toString(c);
      prefix = typedString;
    }
    lastTime = time;

    scrollNextMatch(src, max, e, prefix, startIndex, startingFromSelection);
  }

  private void scrollNextMatch(JTable src, int max, KeyEvent e, String prf, int startIdx, boolean startSelection) {
    int start = startIdx;
    boolean startingFromSelection = startSelection;
    if (start < 0 || start >= max) {
      if (e.isShiftDown()) {
        start = max - 1;
      } else {
        startingFromSelection = false;
        start = 0;
      }
    }
    Position.Bias bias = e.isShiftDown() ? Position.Bias.Backward : Position.Bias.Forward;
    int index = getNextMatch(src, prf, start, bias);
    if (index >= 0) {
      src.getSelectionModel().setSelectionInterval(index, index);
      src.scrollRectToVisible(src.getCellRect(index, TARGET_COLUMN, true));
    } else if (startingFromSelection) { // wrap
      index = getNextMatch(src, prf, 0, bias);
      if (index >= 0) {
        src.getSelectionModel().setSelectionInterval(index, index);
        src.scrollRectToVisible(src.getCellRect(index, TARGET_COLUMN, true));
      }
    }
  }

  // @see javax/swing/JList#getNextMatch(String prefix, int startIndex, Position.Bias bias)
  // @see javax/swing/JTree#getNextMatch(String prefix, int startIndex, Position.Bias bias)
  public static int getNextMatch(JTable table, String prefix, int startingRow, Position.Bias bias) {
    // int max = table.getRowCount();
    // if (Objects.isNull(prefix) || startingRow < 0 || startingRow >= max) {
    //   throw new IllegalArgumentException();
    // }
    Objects.requireNonNull(prefix, "Must supply non-null prefix");
    int max = table.getRowCount();
    if (startingRow < 0 || startingRow >= max) {
      throw new IllegalArgumentException("(0 <= startingRow < max) is false");
    }

    String uprefix = prefix.toUpperCase(Locale.ENGLISH);

    // start search from the next/previous element from the
    // selected element
    int increment = bias == Position.Bias.Forward ? 1 : -1;
    int row = startingRow;
    do {
      Object value = table.getValueAt(row, TARGET_COLUMN);
      String text = Objects.toString(value, "");
      if (text.toUpperCase(Locale.ENGLISH).startsWith(uprefix)) {
        return row;
      }
      row = (row + increment + max) % max;
    } while (row != startingRow);
    return -1;
  }
}
