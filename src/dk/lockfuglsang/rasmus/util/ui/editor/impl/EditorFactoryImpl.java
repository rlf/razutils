package dk.lockfuglsang.rasmus.util.ui.editor.impl;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import dk.lockfuglsang.rasmus.util.ui.editor.Editor;
import dk.lockfuglsang.rasmus.util.ui.editor.EditorFactory;

public class EditorFactoryImpl implements EditorFactory {

  static final private EditorFactory INSTANCE = new EditorFactoryImpl(); 
  
  private Map<Class<?>, Editor<?>> editors;
  
  private EditorFactoryImpl() {
    // Singleton protection
    editors = new HashMap<Class<?>, Editor<?>>();
  }
  @Override
  public <T> Editor<T> getEditor(T obj) {
    Editor<T> editor = (Editor<T>) editors.get(obj.getClass());
    if (editor == null) {
      editor = new DummyEditor<T>();
    }
    editor.update(obj);
    return editor;
  }
  
  @Override
  public <T> void register(Class<T> cl, Editor<T> editor) {
    editors.put(cl, editor);
  }
  
  static public EditorFactory getInstance() {
    return INSTANCE;
  }
  private class DummyEditor<T> implements Editor<T> {
    private JPanel pane;
    public DummyEditor() {
      pane = new JPanel();
    }
    @Override
    public JComponent renderer() {
      return pane;
    }

    @Override
    public void update(T obj) {
      // Do nothing...
    }
  }
}
