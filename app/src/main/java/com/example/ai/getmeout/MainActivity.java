package com.example.ai.getmeout;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private static final String LOG_TAG = "AudioRecordTest";

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private void startPlaying() {
        Log.v("prince", "begin playig");
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void stopRecording() {
        Log.v("prince", "stoped recording");
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void startRecording() {
        mFileName = getFilesDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        Log.v("prince", mFileName);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private final int KEY_BUTTON = 0;
    private final int KEY_VIBRATE = 1;
    private final int MIDDLE_BUTTON = 1;
    private final int TOP_BUTTON = 0;
    private final int BOTTOM_BUTTON = 2;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static final UUID WATCHAPP_UUID = UUID.fromString("05ba28d1-14b9-4b09-a5a3-bf4c622d24e0");

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkPebble();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
               //checkconnection

        Button newButton = (Button) findViewById(R.id.checkconnection);
//        newButton.setOnClickListener(
//                new View.OnClickListener()
//                {
//                    public void onClick(View v) {
//                        if(PebbleKit.isWatchConnected(getApplicationContext())){
//                            Toast.makeText(MainActivity.this,"Pebble connected!",Toast.LENGTH_LONG).show();
//                        }else{
//                            Toast.makeText(MainActivity.this,"Pebble not connected! \n Please Connect",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });




                // Set up the drawer.
                mNavigationDrawerFragment.setUp(
                        R.id.navigation_drawer,
                        (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    private void linkPebble() {
        PebbleKit.PebbleDataReceiver appMessageReceive = new PebbleKit.PebbleDataReceiver(WATCHAPP_UUID) {
            public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                PebbleKit.sendAckToPebble(context, transactionId);
                if (data.getInteger(KEY_BUTTON) != null) {
                    final int button = data.getInteger(KEY_BUTTON).intValue();
                    handler.post(new Runnable() {
                        public void run() {
                            if (button == MIDDLE_BUTTON) {
//                                Toast.makeText(MainActivity.this, "Middle Button",Toast.LENGTH_SHORT).show();
                                final Toast toast = Toast.makeText(MainActivity.this, "Middle Button", Toast.LENGTH_SHORT);
                                toast.show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.cancel();
                                    }
                                }, 500);
                            }else if (button == TOP_BUTTON) {

//                                Toast.makeText(MainActivity.this, "TOP Button", Toast.LENGTH_SHORT).show();
                                final Toast toast = Toast.makeText(MainActivity.this, "Top Button", Toast.LENGTH_SHORT);
                                toast.show();

                                startRecording();
                                final Handler handler2 = new Handler();
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopRecording();
                                        startPlaying();
                                    }
                                }, 2000);
                            }else if (button == BOTTOM_BUTTON) {
//                                Toast.makeText(MainActivity.this, "BOTTOM Button", Toast.LENGTH_SHORT).show();
                                final Toast toast = Toast.makeText(MainActivity.this, "BOTTOM Button", Toast.LENGTH_SHORT);
                                toast.show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.cancel();
                                    }
                                }, 500);
                            }else{
                                //Toast.makeText(MainActivity.this, "Ay! Don't Touch Me Bitch!", Toast.LENGTH_SHORT).show();

                                    final Toast toast = Toast.makeText(MainActivity.this, "Ay! Don't Touch Me Bitch!", Toast.LENGTH_SHORT);
                                    toast.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, 500);

                            }
                        }
                    });
                }
            }
        };

        PebbleKit.registerReceivedDataHandler(this, appMessageReceive);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button btn = (Button) rootView.findViewById(R.id.checkconnection);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(PebbleKit.isWatchConnected(getActivity())){
                            Toast.makeText(getActivity(),"Pebble connected!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(),"Pebble not connected! \n Please Connect",Toast.LENGTH_SHORT).show();
                        }
                }

            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
