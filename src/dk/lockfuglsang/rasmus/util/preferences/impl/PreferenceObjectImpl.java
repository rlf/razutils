package dk.lockfuglsang.rasmus.util.preferences.impl;

import dk.lockfuglsang.rasmus.util.preferences.PreferenceObject;
import dk.lockfuglsang.rasmus.util.resources.Resource;

public class PreferenceObjectImpl<T> implements PreferenceObject<T> {
  
  private String description;
  private String key;
  private String name;
  private T value;
  
  public PreferenceObjectImpl(Resource res, String key, T value) {
    this.key = key;
    this.value = value;
    this.description = res.getString(key + ".Description");
    this.name = res.getString(key + ".Name");
  }
  @Override
  public String getDescription() {
    return description;
  }
  
  @Override
  public String getKey() {
    return key;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public T getValue() {
    return value;
  }
  
}
