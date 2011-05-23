package dk.lockfuglsang.rasmus.util.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import dk.lockfuglsang.rasmus.util.preferences.PreferenceObject;

public class PreferenceObjectTreeNode extends DefaultMutableTreeNode {
  public PreferenceObjectTreeNode(PreferenceObject<?> po) {
    super(po);
  }
  public PreferenceObject<?> getPreferenceObject() {
    Object usrObj = getUserObject();
    if (usrObj instanceof PreferenceObject) {
      return (PreferenceObject<?>) usrObj;
    }
    return null;
  }
  @Override
  public boolean isRoot() {
    return getUserObject() == null;
  }
}
