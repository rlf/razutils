package dk.lockfuglsang.rasmus.util.ui.editor;

public interface EditorFactory {
  /** Registers an editor in the factory. */
  <T> void register(Class<T> cl, Editor<T> editor);
  /** Returns an editor for the supplied object. */
  <T> Editor<T> getEditor(T obj);
}
