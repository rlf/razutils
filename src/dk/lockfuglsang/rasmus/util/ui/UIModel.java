/*
 * UIModel.java
 *
 * Created on 12. juli 2007, 21:49
 *
 */

package dk.lockfuglsang.rasmus.util.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rasmus
 * @version 1.0
 */
public class UIModel {
  
  protected Map<String, Set<PropertyChangeListener>> listeners;
  
  /** Empty Constructor. */
  public UIModel() {
    listeners = new HashMap<String, Set<PropertyChangeListener>>();
  }
  
  // ------------------------------------------------------------------------
  // PropertyChangeListener Support
  // ------------------------------------------------------------------------
  /** Adds a PropertyChangeListener to the listener list. */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    addPropertyChangeListener(null, listener);
  }
  
  /** Adds a <code>PropertyChangeListener</code> to a specific property. */
  public void addPropertyChangeListener(String propertyName,
      PropertyChangeListener listener) {
    synchronized (listeners) {
      Set<PropertyChangeListener> set = listeners.get(propertyName);
      if (set == null) {
        set = new HashSet<PropertyChangeListener>();
        listeners.put(propertyName, set);
      }
      set.add(listener);
    }
  }
  
  /**
   * Removes a <code>PropertyChangeListener</code> from the listener list.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    removePropertyChangeListener(null, listener);
  }
  
  /**
   * Removes a <code>PropertyChangeListener</code> from the listener list for
   * a specific property.
   */
  public void removePropertyChangeListener(String propertyName,
      PropertyChangeListener listener) {
    synchronized (listeners) {
      Set<PropertyChangeListener> set = listeners.get(propertyName);
      if (set != null) {
        set.remove(listener);
        if (set.size() == 0) {
          listeners.remove(propertyName);
        }
      }
    }
  }
  
  /**
   * Fires all the listeners for the specific property with a <code>null</code>
   * value for the <code>oldValue</code>.
   *
   * @see #firePropertyChangeEvent(String, Object, Object)
   */
  public void firePropertyChangeEvent(String propertyName, Object newValue) {
    firePropertyChangeEvent(propertyName, null, newValue);
  }
  
  /**
   * Fires all the listeners for the specific property. <strong>Note:</strong>
   * This may be fired in a new thread.
   */
  public void firePropertyChangeEvent(final String propertyName,
      Object oldValue, Object newValue) {
    PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName,
        oldValue, newValue);
    firePropertyChangeEvent(evt);
  }
  
  /** Helper method for actually firing the event. */
  private void firePropertyChangeEvent(final PropertyChangeEvent evt) {
    synchronized (listeners) {
      // First take all the general listeners
      Set<PropertyChangeListener> set = listeners.get(null);
      if (set != null) {
        for (Iterator<PropertyChangeListener> it = set.iterator(); it.hasNext();) {
          // System.err.println("Firing: ALL: " + evt);
          it.next().propertyChange(evt);
        }
      }
      // Then the specific ones
      String propName = evt.getPropertyName();
      set = listeners.get(propName);
      if (set != null) {
        for (Iterator<PropertyChangeListener> it = set.iterator(); it.hasNext();) {
          // System.err.println("Firing: " +propName + ": " + evt);
          it.next().propertyChange(evt);
        }
      }
    }
  }
}
