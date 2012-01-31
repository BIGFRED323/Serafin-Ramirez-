package biz.binarysolutions.signature.share.jar.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * 
 *
 */
public class ApplicationSettings extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener  {
	
	private String keyTitle;
	private String keyWidth;
	private String keyHeight;
	private String keyStrokeWidth;
	private String keyStrokeColor;
	
	private EditTextPreference preferenceTitle;
	private EditTextPreference preferenceWidth;
	private EditTextPreference preferenceHeight;
	private EditTextPreference preferenceStrokeWidth;
	private EditTextPreference preferenceStrokeColor;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        keyTitle       = getString(R.string.key_Title);
        keyWidth       = getString(R.string.key_Width);
        keyHeight      = getString(R.string.key_Height);
        keyStrokeWidth = getString(R.string.key_StrokeWidth);
        keyStrokeColor = getString(R.string.key_StrokeColor);
       
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceTitle = 
        	(EditTextPreference) preferenceScreen.findPreference(keyTitle);
        preferenceWidth = 
        	(EditTextPreference) preferenceScreen.findPreference(keyWidth);
        preferenceHeight = 
        	(EditTextPreference) preferenceScreen.findPreference(keyHeight);
        preferenceStrokeWidth = 
        	(EditTextPreference) preferenceScreen.findPreference(keyStrokeWidth);
        preferenceStrokeColor = 
        	(EditTextPreference) preferenceScreen.findPreference(keyStrokeColor);
        
        preferenceTitle.setSummary(preferenceTitle.getText());
        preferenceWidth.setSummary(preferenceWidth.getText());
        preferenceHeight.setSummary(preferenceHeight.getText());
        preferenceStrokeWidth.setSummary(preferenceStrokeWidth.getText());
        preferenceStrokeColor.setSummary(preferenceStrokeColor.getText());
        
        getPreferenceScreen().
        	getSharedPreferences().
        		registerOnSharedPreferenceChangeListener(this);
    }    

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().
        	getSharedPreferences().
        		unregisterOnSharedPreferenceChangeListener(this);    
    }    

	@Override
	public void onSharedPreferenceChanged
		(
				SharedPreferences sharedPreferences, 
				String            key
		) {

		if (key.equals(keyTitle)) {
            preferenceTitle.setSummary(preferenceTitle.getText());
        } else if (key.equals(keyWidth)) {
            preferenceWidth.setSummary(preferenceWidth.getText());
        } else if (key.equals(keyHeight)) {
            preferenceHeight.setSummary(preferenceHeight.getText());
        } else if (key.equals(keyStrokeWidth)) {
            preferenceStrokeWidth.setSummary(preferenceStrokeWidth.getText());
        } else if (key.equals(keyStrokeColor)) {
            preferenceStrokeColor.setSummary(preferenceStrokeColor.getText());
        }
	}
}
