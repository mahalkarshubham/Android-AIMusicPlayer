package com.example.aimusic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity
{
   private LinearLayout parentLinear;
   private SpeechRecognizer speechRecognizer;
   private Intent speechRecognizerIntent;
   private String keeper="";

   private ImageView PausePlay, Next, Previous;
   private CircularImageView cimageView;
   private TextView SongName;
   private LinearLayout linLower;
   private Button VoiceEnabledButton;

   private String mode = "ON";

   private MediaPlayer myMediaPlayer;
   private int position;
   private ArrayList<File> mySongs;
   private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkVoiceCommandPermission();

        PausePlay = findViewById(R.id.btn_PlayPause);
        Next = findViewById(R.id.btn_Next);
        Previous = findViewById(R.id.btn_Previous);
        SongName = findViewById(R.id.songName);
        linLower = findViewById(R.id.Lin_Lower);
        cimageView = findViewById(R.id.logo);
        VoiceEnabledButton = findViewById(R.id.btn_VoiceEnabled);

        parentLinear = findViewById(R.id.parentLinear);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(HomeActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceiveValueAndStartPlaying();
        cimageView.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener()
        {
            @Override
            public void onReadyForSpeech(Bundle params)
            {

            }

            @Override
            public void onBeginningOfSpeech()
            {

            }

            @Override
            public void onRmsChanged(float rmsdB)
            {

            }

            @Override
            public void onBufferReceived(byte[] buffer)
            {

            }

            @Override
            public void onEndOfSpeech()
            {

            }

            @Override
            public void onError(int error)
            {

            }

            @Override
            public void onResults(Bundle bundle)
            {
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound != null)
                {
                    keeper = matchesFound.get(0);

                    if (keeper.equals("pause the song") || keeper.equals("pause song"))
                    {
                        playPauseSong();
                        Toast.makeText(HomeActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();
                    }

                    else if (keeper.equals("play the song") || keeper.equals("play song"))
                    {
                        playPauseSong();
                        Toast.makeText(HomeActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults)
            {

            }

            @Override
            public void onEvent(int eventType, Bundle params)
            {

            }
        });

        parentLinear.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent)
            {
               switch (motionEvent.getAction())
               {
                   case MotionEvent.ACTION_DOWN:
                       speechRecognizer.startListening(speechRecognizerIntent);
                       keeper = "";
                       break;

                   case MotionEvent.ACTION_UP:
                       speechRecognizer.stopListening();
                       break;
               }
               return false;
            }
        });

        VoiceEnabledButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mode.equals("ON"))
                {
                    mode = "OFF";
                    VoiceEnabledButton.setText("Voice Enabled Mode - OFF");
                    linLower.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode = "ON";
                    VoiceEnabledButton.setText("Voice Enabled Mode - ON");
                    linLower.setVisibility(View.GONE);
                }
            }
        });

        PausePlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playPauseSong();
            }
        });
    }

    private void validateReceiveValueAndStartPlaying()
    {
        if (myMediaPlayer != null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        SongName.setText(songName);
        SongName.setSelected(true);

        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(HomeActivity.this,uri);
        myMediaPlayer.start();
    }

    private void checkVoiceCommandPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong()
    {
        cimageView.setBackgroundResource(R.drawable.four);

        if (myMediaPlayer.isPlaying())
        {
            PausePlay.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }
        else
        {
            PausePlay.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            cimageView.setBackgroundResource(R.drawable.five);
        }
    }
}
