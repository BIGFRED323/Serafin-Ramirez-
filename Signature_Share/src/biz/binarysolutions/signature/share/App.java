package biz.binarysolutions.signature.share;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

/**
 * 
 *
 */
@ReportsCrashes(formKey = "dGhDRXVGekhqckNJc1lidk1yT2RwQXc6MQ") 
public class App extends Application {

	@Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }
}
