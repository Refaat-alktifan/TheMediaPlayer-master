package dv106.refaat.themediaplayer2.intentreceiver;

import dv106.refaat.themediaplayer2.service.SongService;
import android.content.Context;
import android.content.Intent;

public class MusicIntentReceiver extends android.content.BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			// signal the service to stop playback
            Intent pauseIntent = new Intent(context, SongService.class);
            pauseIntent.putExtra("action", "pause");
            context.startService(pauseIntent);
		}
	}
}