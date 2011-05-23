package dk.lockfuglsang.rasmus.util.ui.tree;

import javax.swing.tree.DefaultTreeModel;

public class PreferenceObjectTreeModel extends DefaultTreeModel {
  public PreferenceObjectTreeModel() {
    super(new PreferenceObjectTreeNode(null));
  }
}
