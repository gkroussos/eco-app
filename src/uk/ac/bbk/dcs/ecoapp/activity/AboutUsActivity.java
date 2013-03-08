package uk.ac.bbk.dcs.ecoapp.activity;

import uk.ac.bbk.dcs.ecoapp.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * This Activity is simply an about screen displaying information about InMidtown
 * @author 
 *
 */
public class AboutUsActivity extends Activity  {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
    }

}
