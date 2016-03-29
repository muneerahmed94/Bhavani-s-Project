package com.example.ruchithaprasad.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity  implements TextToSpeech.OnInitListener {
    private Button Call,Message,Email,Notification;
    private Button listen;
    private TextToSpeech engine;
    private SeekBar seekPitch;
    private SeekBar seekSpeed;
    private String Spell;
    private double pitch=1.0;
    private double speed=1.0;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String check;
    View.OnClickListener Click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             switch (v.getId())
             {
                 case R.id.Call :
                 {
                     Spell="Call";
                     speech();
                     break;
                 }
                 case R.id.Message:
                 {
                     Spell="Message";
                     speech();
                     break;
                 }
                 case R.id.email:
                 {
                     Spell="Email";
                     speech();
                     break;
                 }
                 case R.id.Notification:
                 {
                     Spell="Notifications";
                     speech();
                     break;
                 }
                 case R.id.listen:{
                     promptSpeechInput();


                 }

             }
        }
    };

    View.OnLongClickListener Open=new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.Call: {
                    Spell = "Opening Call";
                    speech();
                    Intent i=new Intent(getApplicationContext(),CallActivity.class);
                    startActivity(i);
                    break;
                }
                case R.id.Message: {
                    Spell = "Opening Message";
                    speech();
                    Intent i=new Intent(getApplicationContext(),messageactivity.class);
                    startActivity(i);
                    break;
                }
                case R.id.email: {
                    Spell = "Opening Email";
                    speech();
                    break;
                }
                case R.id.Notification: {
                    Spell = "Opening Notifications";
                    speech();
                    Intent i=new Intent(getApplicationContext(),notificationactivity.class);
                    startActivity(i);
                    break;
                }

            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Call=(Button)findViewById(R.id.Call);
        Message=(Button)findViewById(R.id.Message);
        Email=(Button)findViewById(R.id.email);
        Notification=(Button)findViewById(R.id.Notification);
        listen=(Button)findViewById(R.id.listen);
        engine = new TextToSpeech(this, this);
        seekPitch = (SeekBar) findViewById(R.id.seekPitch);
        seekPitch.setThumbOffset(5);
        seekPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("Speech", "Progress ["+progress+"]");
                pitch = (float) progress / (seekBar.getMax() / 2);
                //Log.d("Speech", "Pitch ["+pitch+"]");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekSpeed = (SeekBar) findViewById(R.id.seekSpeed);
        seekSpeed.setThumbOffset(5);
        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("Speech", "Progress ["+progress+"]");
                speed = (float) progress / (seekBar.getMax() / 2);
                //Log.d("Speech", "Pitch ["+pitch+"]");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Call.setOnClickListener(Click);Call.setOnLongClickListener(Open);
        Message.setOnClickListener(Click);Message.setOnLongClickListener(Open);
        Email.setOnClickListener(Click);Email.setOnLongClickListener(Open);
        Notification.setOnClickListener(Click);Notification.setOnLongClickListener(Open);
        listen.setOnClickListener(Click);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    @Override
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");

        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            engine.setLanguage(Locale.UK);
        }
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    check = result.get(0);
                    opening();
                }

                break;
            }

        }
    }
    private void opening()
    {
        Spell="Opening"+check;
        speech();
        if(check.equalsIgnoreCase("call"))
        {
            Intent i=new Intent(getApplicationContext(),CallActivity.class);
            startActivity(i);
        }
        if(check.equalsIgnoreCase("message"))
        {
            Intent i=new Intent(getApplicationContext(),messageactivity.class);
            startActivity(i);
        }
        if(check.equalsIgnoreCase("email"))
        {
            Intent i=new Intent(getApplicationContext(),CallActivity.class);
            startActivity(i);
        }
        if(check.equalsIgnoreCase("notifications"))
        {
            Intent i=new Intent(getApplicationContext(),notificationactivity.class);
            startActivity(i);
        }

    }
    private void speech() {
        engine.setPitch((float) pitch);
        engine.setSpeechRate((float) speed);
        engine.speak(Spell.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
