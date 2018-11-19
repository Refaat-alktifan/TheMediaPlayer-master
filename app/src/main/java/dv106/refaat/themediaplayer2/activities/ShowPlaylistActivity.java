package dv106.refaat.themediaplayer2.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import dv106.refaat.themediaplayer2.R;
import dv106.refaat.themediaplayer2.database.DataSource;
import dv106.refaat.themediaplayer2.dialogfragments.AddTrackToPlaylistDialogFragment;
import dv106.refaat.themediaplayer2.dialogfragments.AddTrackToPlaylistDialogFragment.AddTrackToPlaylistDialogListener;
import dv106.refaat.themediaplayer2.dialogfragments.RemovedSongFromPlaylistAlertDialogFragment;
import dv106.refaat.themediaplayer2.listadapters.SongListAdapter;
import dv106.refaat.themediaplayer2.pojo.Song;
import dv106.refaat.themediaplayer2.preferences.PreferencesActivity;

public class ShowPlaylistActivity extends FragmentActivity implements
		OnItemClickListener, OnItemLongClickListener, OnClickListener, AddTrackToPlaylistDialogListener {

	private ListView lwShowPlaylist;
	private TextView tvShowPlaylistName;
	private DataSource datasource;
	private SongListAdapter adapter;
	private String playlistName;
	private ArrayList<Song> allSongs;
	private ActionMode mActionMode;
	private int selectedItem;
	private TextView tvShowPlaylistTotalDuration;
	private Button btnAddTrack;


	private InterstitialAd interstitial;

	private void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_playlist);







		int Permission_All = 1;

		String[] Permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.INTERNET, };
		if(!hasPermissions(this, Permissions)){
			ActivityCompat.requestPermissions(this, Permissions, Permission_All);
		}


		String[] Permissions1 = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.INTERNET, };
		if(!hasPermissions(this, Permissions)){
			ActivityCompat.requestPermissions(this, Permissions, Permission_All);
		}
		AdRequest adRequest1 = new AdRequest.Builder().build();
		interstitial = new InterstitialAd(ShowPlaylistActivity.this);
		interstitial.setAdUnitId("ca-app-pub-8493173606515444/6527547509");
		interstitial.loadAd(adRequest1);
		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				displayInterstitial();
			}
		});


		this.playlistName = getIntent().getExtras().getString("playlistName");
		setUpComponents();
		getPlaylistSongs(playlistName);
	}

	private void getPlaylistSongs(String playlistName) {
		boolean removed = false;
		try {
			datasource.open();
			allSongs = datasource.getSongsFromPlaylist(playlistName);
			/* check if the songs still exists on memory, removes them if not */
			for(Song song : allSongs){
				File file = new File(song.getDATA());
				if(!file.exists()){
					datasource.removeSongFromPlaylist(song.getDATA(), playlistName);		
					removed = true;
				}
			}
			allSongs = datasource.getSongsFromPlaylist(playlistName);
			datasource.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setTotalDuration();
		adapter = new SongListAdapter(this, allSongs);
		lwShowPlaylist.setAdapter(adapter);
		tvShowPlaylistName.setText(playlistName);
		if(removed){
			RemovedSongFromPlaylistAlertDialogFragment removedDialog = new RemovedSongFromPlaylistAlertDialogFragment();
			removedDialog.show(getSupportFragmentManager(), "removedDialog");
		}
	}

	private void setUpComponents() {
		datasource = new DataSource(this);
		
		lwShowPlaylist = (ListView) findViewById(R.id.lwShowPlaylist);
		tvShowPlaylistName = (TextView) findViewById(R.id.tvShowPlaylistName);
		tvShowPlaylistTotalDuration = (TextView) findViewById(R.id.tvShowPlaylistTotalDuration);
		btnAddTrack = (Button) findViewById(R.id.btnShowPlaylistAddTrack);

		lwShowPlaylist.setOnItemClickListener(this);
		lwShowPlaylist.setOnItemLongClickListener(this);
		btnAddTrack.setOnClickListener(this);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int txtColor = sharedPref.getInt(PreferencesActivity.KEY_PREF_TXT_COLOR, 1);
		tvShowPlaylistName.setTextColor(txtColor);
		tvShowPlaylistTotalDuration.setTextColor(txtColor);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnShowPlaylistAddTrack:
			try {
				datasource.open();
				ArrayList<Song> allSongs = datasource.getAllSongs();
				datasource.close();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("allSongs", allSongs);
                bundle.putString("playlistName", playlistName);
				DialogFragment addTrackDialog = new AddTrackToPlaylistDialogFragment();
                addTrackDialog.setArguments(bundle);
				addTrackDialog.show(getSupportFragmentManager(), "addTrackDialog");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent playIntent = new Intent(this, PlaySongActivity.class);
		String songPath = allSongs.get(position).getDATA();
		String artist = allSongs.get(position).getARTIST();
		String title = allSongs.get(position).getTITLE();
		String duration = allSongs.get(position).getDURATION();

		playIntent.putExtra("songPath", songPath);
		playIntent.putExtra("artist", artist);
		playIntent.putExtra("title", title);
		playIntent.putExtra("duration", duration);
		playIntent.putExtra("playlist", playlistName);

		SharedPreferences settings = getSharedPreferences("play_mode_prefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("from_playlist", true);
		editor.putString("playlist_name", playlistName);
		editor.commit();

		startActivity(playIntent);		
	}

	/** CONTEXT MENU **/

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_remove_song_from_playlist,
					menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.remove_from_playlist:
				String songPath = allSongs.get(selectedItem).getDATA();
				try {
					datasource.open();
					datasource.removeSongFromPlaylist(songPath, playlistName);
					allSongs = datasource.getSongsFromPlaylist(playlistName);
					datasource.close();
					adapter.clear();
					adapter.addAll(allSongs);
					setTotalDuration();
					Toast.makeText(getApplication(), getResources().getString(R.string.remove_song_from_playlist_feedback), Toast.LENGTH_LONG).show();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				mode.finish();
				return true;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}

	};
	
	private void setTotalDuration() {
		long l_totalDuration = 0;
		try{
			datasource.open();
			allSongs = datasource.getSongsFromPlaylist(playlistName);
			datasource.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		for(Song song : allSongs){
			l_totalDuration += Long.valueOf(song.getDURATION());
		}
		long hr = TimeUnit.MILLISECONDS.toHours(l_totalDuration);
		long min = TimeUnit.MILLISECONDS.toMinutes(l_totalDuration - TimeUnit.HOURS.toMillis(hr));
		long sec = TimeUnit.MILLISECONDS.toSeconds(l_totalDuration - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		String s_totalDuration = String.format(Locale.getDefault(), "%02d:%02d:%02d", hr,min,sec);
		tvShowPlaylistTotalDuration.setText(getResources().getString(R.string.playlist_total_duration)+ " " +s_totalDuration);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mActionMode != null) {
			return false;
		}
		selectedItem = position;
		mActionMode = startActionMode(mActionModeCallback);
		view.setSelected(true);
		return true;
	}

	@Override
	public void AddTrackToPlaylistDialogClick() {
		try{
			datasource.open();
			allSongs = datasource.getSongsFromPlaylist(playlistName);
			datasource.close();
			adapter.clear();
			adapter.addAll(allSongs);
			setTotalDuration();
		}catch(SQLException e){
			e.printStackTrace();
		}

	}


	public static boolean hasPermissions(Context context, String... permissions){

		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP && context!=null && permissions!=null){
			for(String permission: permissions){
				if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
					return  false;
				}
			}
		}
		return true;
	}  public static boolean hasPermissions1 (Context context, String... permissions){

		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT && context!=null && permissions!=null){
			for(String permission: permissions){
				if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
					return  false;
				}
			}
		}
		return true;
	}
}
