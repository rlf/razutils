package dk.lockfuglsang.rasmus.util.preferences;

public interface PreferenceObject<T> {
  /** Returns the key of the preference.
   * This key is the one used in the property-file.
   * @return A unique key for this {@link PreferenceObject}.
   */
  String getKey();
  /** Returns a displayable name, that can be used to describe the preference.
   * 
   * @return A localised name of the preference.
   */
  String getName();
  /** Returns a description of what this preference is about. */
  String getDescription();
  /** Returns an object holding the data of this preference. */
  T getValue();
}
