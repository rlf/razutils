package dk.lockfuglsang.rasmus.util.preferences;

import java.lang.reflect.Method;

public class SetterIterator extends MethodIterator {
  static private final String SET = "set";
  public SetterIterator(Object obj) {
    this(obj.getClass().getMethods());
  }
  private SetterIterator(Method[] methods) {
    super(methods, SET, 1);
  }
  @Override
  protected String getKey(String methodName) {
    return getFieldName(methodName);
  }
  static public String getFieldName(String methodName) {
    if (methodName.startsWith(SET)) {
      return methodName.substring(SET.length());
    }
    return methodName;
  }
}
