package ch.bailu.aat.preferences;

import ch.bailu.aat.helpers.ContextWrapperInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class Storage  implements ContextWrapperInterface {
    private final static String DEF_VALUE="0";
    
    private final static String GLOBAL_NAME="Preferences";
    private final static String PRESET_NAME="preset";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    private final Context context;

    
    private Storage(Context c, String fileName) {
        context=c;
        preferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public static Storage global(Context c) {
        return new Storage(c, GLOBAL_NAME);
    }
    
    
    public static Storage map(Context context) {
        return global(context);  // TODO remove
    }
    
    
    public static Storage activity(Context c, String mainKey) {
        return new Storage(c, mainKey);
    }
    
    
    public static Storage activity(Context c) {
        return global(c); // TODO remove
    }
    
    
    public static Storage preset(Context c) {
        return global(c); // TODO remove
    }
    
    
    public static Storage preset(Context c, int i) {
        return new Storage(c, PRESET_NAME + i);
    }
    
    
    public String readString(String key) {
        return preferences.getString(key, DEF_VALUE);
    }

    public void writeString(String key, String value) {
        if (!readString(key).equals(value)) {
            editor.putString(key, value);
            editor.commit();
        }
    }

    public int readInteger(String key) {
        return preferences.getInt(key, 0);
    }

    public void writeBoolean(String key, boolean v) {
        if (readBoolean(key) != v) {
            editor.putBoolean(key, v);
            editor.commit();
        }        
    }

    public void writeInteger(String key, int v) {
        if (readInteger(key) != v) {
            editor.putInt(key, v);
            editor.commit();
        }
    }

    public boolean readBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public long readLong(String key) {
        return preferences.getLong(key, 0);
    }

    public void writeLong(String key, long v) {
        if (readLong(key) != v) {
            editor.putLong(key, v);
            editor.commit();
        }
    }

    public void register(OnSharedPreferenceChangeListener listener)  {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }


    public void unregister(OnSharedPreferenceChangeListener l) {
        preferences.unregisterOnSharedPreferenceChangeListener(l);        
    }


    @Override
    public Context getContext() {
        return context;
    }


    
}
