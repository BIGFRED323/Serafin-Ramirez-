package biz.binarysolutions.signature.share.jar.activity.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import biz.binarysolutions.signature.share.jar.activity.R;

/**
 * 
 *
 */
public class PreferencesLoader {
	
	private Activity activity;
	private SharedPreferences preferences;

	/**
	 * 
	 * @param activity
	 */
	public PreferencesLoader(Activity activity) {
		this.activity = activity;
		preferences = PreferenceManager.getDefaultSharedPreferences(activity);
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		
		String key          = activity.getString(R.string.key_Title);
		String defaultValue = activity.getString(R.string.default_value_Title);
		
		return preferences.getString(key, defaultValue);
	}

	/**
	 * 
	 * @return
	 */
	public int getStrokeWidth() {
		
		String key          = activity.getString(R.string.key_StrokeWidth);
		String defaultValue = activity.getString(R.string.default_value_StrokeWidth);
		
		String stringValue = preferences.getString(key, defaultValue);
		
		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			return Integer.parseInt(defaultValue);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean getCrop() {
		
		String key          = activity.getString(R.string.key_Crop);
		String defaultValue = activity.getString(R.string.default_value_Crop);
		
		return preferences.getBoolean(key, Boolean.getBoolean(defaultValue));
	}

	/**
	 * 
	 * @return
	 */
	public String getWidth() {
		
		String key          = activity.getString(R.string.key_Width);
		String defaultValue = activity.getString(R.string.default_value_Width);
		
		return preferences.getString(key, defaultValue);
	}

	/**
	 * 
	 * @return
	 */
	public String getHeight() {
		
		String key          = activity.getString(R.string.key_Height);
		String defaultValue = activity.getString(R.string.default_value_Height);
		
		return preferences.getString(key, defaultValue);
	}
}
