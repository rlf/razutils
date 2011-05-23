package dk.lockfuglsang.rasmus.util.ui.editor;

import javax.swing.JComponent;

public interface Editor<T> {
  JComponent renderer();
  void update(T obj);
}
