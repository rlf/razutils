package dk.lockfuglsang.rasmus.util.preferences;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public abstract class MethodIterator implements Iterator<Method>{
  private Method[] methods;
  private int index;
  protected String prefix;
  protected int numparams;
  
  protected MethodIterator(Method[] methods, String prefix, int numparams) {
    index = -1;
    this.methods = methods.clone();
    this.prefix = prefix;
    this.numparams = numparams;
    
    Arrays.sort(this.methods, new Comparator<Method> () {
      @Override
      public int compare(Method o1, Method o2) {
        int cmp = getKey(o1.getName()).compareTo(getKey(o2.getName()));
        if (cmp == 0) {
          cmp = o1.getDeclaringClass().getName().compareTo(o2.getDeclaringClass().getName());
        }
        return cmp;
      }
    });
  }
  private int getNextIndex() {
    int ix = index + 1;
    while (ix < methods.length && !isValidMethod(methods[ix])) {
      ix++;
    }
    if (ix < methods.length) {
      return ix;
    }
    return -1;
  }
  protected boolean isValidMethod(Method m) {
    return Modifier.isPublic(m.getModifiers()) && m.getName().startsWith(prefix) && m.getParameterTypes().length == numparams && !m.getDeclaringClass().getName().startsWith("java");  
  }
  @Override
  public boolean hasNext() {
    return getNextIndex() != -1;
  }

  @Override
  public Method next() {
    index = getNextIndex();
    return methods[index];
  }
  protected Method getCurrent() {
    if (index >= 0 && index < methods.length) {
      return methods[index];
    }
    return null;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove is not supported!");
  }
  protected String getKey(String methodName) {
    return getFieldName(methodName);
  }
  static public String getFieldName(String methodName) {
    return methodName;
  }
}
