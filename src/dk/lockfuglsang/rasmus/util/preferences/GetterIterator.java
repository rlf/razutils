package dk.lockfuglsang.rasmus.util.preferences;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class GetterIterator extends MethodIterator {
  static final private String IS = "is";
  static final private String GET = "get";
  
  public GetterIterator(Object obj) {
    this(obj.getClass().getMethods());
  }
  private GetterIterator(Method[] methods) {
    super(methods, null, 0);
  }
  @Override
  protected boolean isValidMethod(Method m) {
    return Modifier.isPublic(m.getModifiers())
        && (m.getName().startsWith(GET) || m.getName().startsWith(IS))
        && m.getParameterTypes().length == numparams
        && !m.getDeclaringClass().getName().startsWith("java");  
  }

  protected String getKey(String methodName) {
    return getFieldName(methodName);
  }
  /** Extracts the field-name part of the method-name. */
  static public String getFieldName(String methodName) {
    if (methodName.startsWith(IS)) {
      return methodName.substring(IS.length());
    } else if (methodName.startsWith(GET)) {
      return methodName.substring(GET.length());
    }
    return methodName;
  }
}
