package dk.lockfuglsang.rasmus.util.preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public interface PreferenceObjectWriter {
  
  /** Uses reflection to store the preferenceobject in the {@link Preferences} store. 
   * 
   * @param <T> The type of value object in the preferences object.
   * @param pref
   * @throws BackingStoreException
   */
  public abstract <T> void write(PreferenceObject<T> pref) throws BackingStoreException;
  
  /** Reads the object of class <code>cl</code> from the {@link Preferences} store.
   * @param cl
   * @param key
   * @return
   * @throws BackingStoreException
   */
  public abstract <T> T read(
      Class<T> cl,
      String key) throws BackingStoreException;
  
}