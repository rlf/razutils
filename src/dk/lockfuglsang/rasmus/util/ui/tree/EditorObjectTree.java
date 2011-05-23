package dk.lockfuglsang.rasmus.util.ui.tree;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import dk.lockfuglsang.rasmus.util.ui.editor.EditorFactory;

public class EditorObjectTree extends JPanel {
  private final JTree tree;
  private final JPanel editor;
  
  public EditorObjectTree(final TreeModel treeModel, final EditorFactory editorFactory) {
    super(new BorderLayout());
    tree = new JTree(treeModel);
    this.add(tree, BorderLayout.WEST);
    editor = new JPanel(new BorderLayout());
    this.add(new JScrollPane(editor), BorderLayout.CENTER);
    
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        Object obj = e.getPath().getLastPathComponent();
        if (obj instanceof DefaultMutableTreeNode) {
          obj = ((DefaultMutableTreeNode) obj).getUserObject();
        }
        editor.removeAll();
        editor.add(editorFactory.getEditor(obj).renderer(), BorderLayout.CENTER);
      }
    });
  }
}
