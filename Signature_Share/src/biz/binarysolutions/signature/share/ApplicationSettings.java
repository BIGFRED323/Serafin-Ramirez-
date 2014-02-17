package biz.binarysolutions.signature.share;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.Display;
import biz.binarysolutions.android.commons.DimensionConverter;
import biz.binarysolutions.android.commons.StringUtil;

/**
 * TODO: add color picker
 * http://code.google.com/p/color-picker-view/
 *
 */
public class ApplicationSettings extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener  {
	
	private String keyTitle;
	private String keyWidth;
	private String keyHeight;
	private String keyBackgroundColor;
	private String keyStrokeWidth;
	private String keyStrokeColor;
	
	private EditTextPreference preferenceTitle;
	private EditTextPreference preferenceWidth;
	private EditTextPreference preferenceHeight;
	private EditTextPreference preferenceBackgroundColor;
	private EditTextPreference preferenceStrokeWidth;
	private EditTextPreference preferenceStrokeColor;
	
	/**
	 * 
	 * @return
	 */
	private int getMaxScreenDimension() {
		
		Display display = getWindowManager().getDefaultDisplay();
		
		int width  = display.getWidth();
    	int height = display.getHeight();
    	int max    = (width > height) ? width : height;

    	return max;
	}

	/**
	 * 
	 * @param preference
	 */
    private void roundToMaxScreenDimension(EditTextPreference preference) {
    	
    	String dimension = preference.getText();
    	if (StringUtil.doesExist(dimension)) {
			
    		DisplayMetrics metrics = DimensionConverter.getDisplayMetrics(this);
    		try {
				int px  = DimensionConverter.stringToPixel(dimension, metrics);
				int max = getMaxScreenDimension();
				
				if (px > max) {
					preference.setText(String.valueOf(max));
				}
			} catch (Exception e) {
				preference.setText(String.valueOf(0));
			}
		} else {
			preference.setText(String.valueOf(0));
		}
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        keyTitle           = getString(R.string.key_Title);
        keyWidth           = getString(R.string.key_Width);
        keyHeight          = getString(R.string.key_Height);
        keyBackgroundColor = getString(R.string.key_BackgroundColor);
        keyStrokeWidth     = getString(R.string.key_StrokeWidth);
        keyStrokeColor     = getString(R.string.key_StrokeColor);
       
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
        preferenceBackgroundColor = 
        	(EditTextPreference) preferenceScreen.findPreference(keyBackgroundColor);
        preferenceStrokeWidth = 
        	(EditTextPreference) preferenceScreen.findPreference(keyStrokeWidth);
        preferenceStrokeColor = 
        	(EditTextPreference) preferenceScreen.findPreference(keyStrokeColor);
        
        preferenceTitle.setSummary(preferenceTitle.getText());
        preferenceWidth.setSummary(preferenceWidth.getText());
        preferenceHeight.setSummary(preferenceHeight.getText());
        preferenceBackgroundColor.setSummary(preferenceBackgroundColor.getText());
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
        	roundToMaxScreenDimension(preferenceWidth);
            preferenceWidth.setSummary(preferenceWidth.getText());
        } else if (key.equals(keyHeight)) {
        	roundToMaxScreenDimension(preferenceHeight);
            preferenceHeight.setSummary(preferenceHeight.getText());
        } else if (key.equals(keyBackgroundColor)) {
        	preferenceBackgroundColor.setSummary(preferenceBackgroundColor.getText());
		} else if (key.equals(keyStrokeWidth)) {
            preferenceStrokeWidth.setSummary(preferenceStrokeWidth.getText());
        } else if (key.equals(keyStrokeColor)) {
            preferenceStrokeColor.setSummary(preferenceStrokeColor.getText());
        }
	}
}
