package uk.ac.dcs.bbk.ecoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {

		// start the location logging service
		Intent sIntent = new Intent(ctx, LocationLoggingService.class);
		ctx.startService(sIntent);
		Log.i("LocationLoggingService", "Start service from BroadcastReceiver.");
	}

}
