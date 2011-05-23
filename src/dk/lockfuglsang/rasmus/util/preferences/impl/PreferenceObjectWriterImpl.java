package dk.lockfuglsang.rasmus.util.preferences.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import dk.lockfuglsang.rasmus.util.preferences.GetterIterator;
import dk.lockfuglsang.rasmus.util.preferences.PreferenceObject;
import dk.lockfuglsang.rasmus.util.preferences.PreferenceObjectWriter;
import dk.lockfuglsang.rasmus.util.preferences.SetterIterator;

/** Writes a {@link PreferenceObject} to the {@link Preferences}.
 * 
 */
public class PreferenceObjectWriterImpl implements PreferenceObjectWriter {
  static final private String CLASSNAME = "CLASS";
  
  public PreferenceObjectWriterImpl() {
    // Singleton protection
  }
  /* (non-Javadoc)
   * @see dk.lockfuglsang.rasmus.util.preferences.impl.PreferenceObjectWriter#write(dk.lockfuglsang.rasmus.util.preferences.PreferenceObject)
   */
  public <T> void write(PreferenceObject<T> pref) throws BackingStoreException {
    Preferences prefs = Preferences.userNodeForPackage(this.getClass());
    prefs.node(pref.getKey()).removeNode();
    prefs.flush();
    writeValue(prefs, pref.getKey(), pref.getValue());
  }
  private <T> void writeMethod(Preferences prefs, Method m, T obj) 
  {
    try {
      String mname = m.getName();
      String key = GetterIterator.getFieldName(mname);
      Object value = m.invoke(obj);
      writeValue(prefs, key, value);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }
  private <T> void writeValue(Preferences prefs, String mname, T value) {
    if (value instanceof String) {
      prefs.put(mname, (String)value);
    } else if (value instanceof Float) {
      prefs.putFloat(mname, ((Float)value).floatValue());
    } else if (value instanceof Double) {
      prefs.putDouble(mname, ((Double)value).doubleValue());
    } else if (value instanceof Integer) {
      prefs.putInt(mname, ((Integer)value).intValue());
    } else if (value instanceof Long) {
      prefs.putLong(mname, ((Long)value).longValue());
    } else if (value instanceof Boolean) {
      prefs.putBoolean(mname, ((Boolean)value).booleanValue());
    } else if (value instanceof Collection<?>) {
      Collection<?> list = (Collection<?>) value;
      Preferences prefs2 = prefs.node(mname);
      int ix = 0;
      Object collObj = null;
      for (Iterator<?> it = list.iterator(); it.hasNext(); ) {
        String k = "" + ix++;
        Object obj = it.next();
        if (collObj == null) {
          collObj = obj;
          prefs2.put(CLASSNAME, collObj.getClass().getName());
        }
        if (obj.getClass() != collObj.getClass()) {
          throw new IllegalStateException("Only homogenious collections can be written to the preferences!");
        }
        writeValue(prefs2, k, obj);
      }
    } else {
      Preferences prefs2 = prefs.node(mname);
      for (Iterator<Method> it = new GetterIterator(value); it.hasNext(); ) {
        writeMethod(prefs2, it.next(), value);
      }
    }
  }
  /* (non-Javadoc)
   * @see dk.lockfuglsang.rasmus.util.preferences.impl.PreferenceObjectWriter#read(java.lang.Class, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public <T> T read(Class<T> cl, String key) {
    Preferences prefs = Preferences.userNodeForPackage(this.getClass());
    try {
      if (isSimple(cl)) {
        return (T) readValue(prefs, key, cl);
      } else {
        T obj = cl.newInstance();
        return readObject(prefs, key, obj);
      }
    } catch (SecurityException e) {
      throw new IllegalStateException(e);
    } catch (InstantiationException e) {
      throw new IllegalStateException("the class " + cl + " doesn't have a no-arg constructor", e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
  @SuppressWarnings("unchecked")
  private <T> T readObject(Preferences prefs, String key, T obj) {
    if (isSimple(obj)) {
      obj = (T) readValue(prefs, key, obj.getClass());
    } else if (obj instanceof Collection<?>) {
      obj = (T) readCollection(prefs.node(key));
    } else {
      Preferences prefs2 = prefs.node(key);
      for (Iterator<Method> it = new SetterIterator(obj); it.hasNext(); ) {
        readMethod(prefs2, obj, it.next());
      }
    }
    return obj;
  }
  @SuppressWarnings("unchecked")
  private <T> T readValue(Preferences prefs, String key, Class<T> cl) {
    T value = null;
    if (cl.isAssignableFrom(String.class)) {
      value = (T) prefs.get(key, null);
    } else if (cl.isAssignableFrom(Integer.class) || cl.getName().equals("int")) {
      value = (T) new Integer(prefs.getInt(key, 0));
    } else if (cl.isAssignableFrom(Long.class) || cl.getName().equals("long")) {
      value = (T) new Long(prefs.getLong(key, 0));
    } else if (cl.isAssignableFrom(Float.class) || cl.getName().equals("float")) {
      value = (T) new Float(prefs.getFloat(key, 0));
    } else if (cl.isAssignableFrom(Double.class) || cl.getName().equals("double")) {
      value = (T) new Double(prefs.getDouble(key, 0));
    } else if (cl.isAssignableFrom(Boolean.class) || cl.getName().equals("boolean")) {
      value = (T) new Boolean(prefs.getBoolean(key, false));
    } else if (cl.isAssignableFrom(Collection.class)) {
      value = (T) readCollection(prefs.node(key));
    } else {
      throw new IllegalArgumentException("class must be a simple type or a collection, not " + cl);
    }
    return value;
  }
  @SuppressWarnings("unchecked")
  private void readMethod(Preferences prefs, Object obj, Method m) {
    Class<?> cl = m.getParameterTypes()[0];
    if (cl.isAssignableFrom(List.class)) {
      cl = ArrayList.class;
    } else if (cl.isAssignableFrom(Set.class)) {
      cl = HashSet.class;
    }
    String key = SetterIterator.getFieldName(m.getName());
    Object value = null;
    if (isSimple(cl)) {
      value = readValue(prefs, key, cl);
    } else {
      value = readObject(prefs, key, cl);
    }
    // The above may have lead to an ArrayList being returned...
    if (cl == HashSet.class && value instanceof Collection<?>) {
      Set<Object> set = new HashSet<Object>();
      set.addAll((Collection<Object>)value);
      value = set;
    }
    try {
      m.invoke(obj, value);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }
  private Collection<Object> readCollection(Preferences prefs) {
    String clname = prefs.get(CLASSNAME, "java.lang.String");
    Class<?> cl = null;
    try {
      cl = Class.forName(clname);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
    Collection<Object> list = new ArrayList<Object>();
    int ix = 0;
    String k = "" + ix;
    try {
      if (isSimple(cl)) {
        Object obj = null;
        while ((obj = readObject(prefs, k, cl)) != null) {
          list.add(obj);
          ix++;
          k = "" + ix;
        }
      } else {
        while (prefs.nodeExists(k)) {
          Object obj = readObject(prefs, k, cl);
          list.add(obj);
          ix++;
          k = "" + ix;
        }
      }
    } catch (BackingStoreException e) {
      // Will this always happen?
      throw new IllegalStateException(e);
    }
    return list;
  }
  private Object readObject(Preferences prefs, String key, Class<?> cl) {
    Object obj = null;
    if (isSimple(cl)) {
      obj = readValue(prefs, key, cl);
    } else {
      try {
        obj = cl.newInstance();
      } catch (InstantiationException e) {
        throw new IllegalStateException("the class " + cl
            + " doesn't have a no-arg constructor", e);
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
      obj = readObject(prefs, key, obj);
    }
    return obj;
  }
  private boolean isSimple(Object obj) {
    return (obj instanceof Number || obj instanceof String || obj instanceof Boolean);
  }
  private boolean isSimple(Class<?> cl) {
    return (cl.isAssignableFrom(Integer.class)
        || cl.isAssignableFrom(Long.class) 
        || cl.isAssignableFrom(Float.class)
        || cl.isAssignableFrom(Double.class)
        || cl.isAssignableFrom(String.class) 
        || cl.isAssignableFrom(Boolean.class)
        || cl.isPrimitive());
  }
}
