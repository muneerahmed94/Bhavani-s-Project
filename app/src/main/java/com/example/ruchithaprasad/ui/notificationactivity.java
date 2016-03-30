package com.example.ruchithaprasad.ui;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class notificationactivity extends ActionBarActivity  implements TextToSpeech.OnInitListener  {
    private Button listen;
    private TextView txtView;
    private NotificationReceiver nReceiver;
    private TextToSpeech engine;
    private SeekBar seekPitch;
    private SeekBar seekSpeed;
    private double pitch=1.0;
    private double speed=1.0;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String num[]={"Zero","One","Two","Three","Four","Five","Six","Seven","Eight","NIne"};
    private boolean flaglist;

    String notificationsString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationactivity);
        txtView = (TextView) findViewById(R.id.NotifytextView);
        listen= (Button) findViewById(R.id.notifylisten);
        engine = new TextToSpeech(this, this);
        seekPitch = (SeekBar) findViewById(R.id.nseekPitch);
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

        seekSpeed = (SeekBar) findViewById(R.id.nseekSpeed);
        seekSpeed.setThumbOffset(3);
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
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promptSpeechInput();
            }
        });
    }
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");

        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            engine.setLanguage(Locale.US);
        }
    }
    public void buttonClicked(View v){


        if(v.getId() == R.id.btnClearNotify){
            Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "clearall");
            sendBroadcast(i);
            txtView.setText("");
            notificationsString = "";
        }
        else if(v.getId() == R.id.btnListNotify){
            Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "list");
            txtView.setText("");
            notificationsString = "";
            sendBroadcast(i);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtView.setText(notificationsString);
                    readNotifications(notificationsString);
                }
            }, 1500);
        }
    }

    private void checkdetails(String check)
    {
        if(check.equalsIgnoreCase("clear"))
        {
            Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "clearall");
            sendBroadcast(i);
            txtView.setText("");
            notificationsString = "";
            speech("cleared all notifications");
        }
        else if(check.equalsIgnoreCase("list"))
        {
            Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "list");
            txtView.setText("");
            notificationsString = "";
            sendBroadcast(i);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtView.setText(notificationsString);
                    readNotifications(notificationsString);
                }
            }, 1500);
        }
        else
        {
            speech("sorry we did not get u try again");
        }
    }

    private void readNotifications(String notificationsString) {
        String stringToBeRead = getStringToBeRead(notificationsString);
        read(stringToBeRead);
    }

    private String getStringToBeRead(String notificationsString) {
        String stringToBeRead = "";
        String[] lines = notificationsString.split("\n");
        for (String line : lines) {
            String[] words = line.split(" ");
            for(String word : words) {
                if(isNumber(word) && word.length() >= 10) {
                    for(int i = 0; i < word.length(); i++) {
                        String digit = new Character(word.charAt(i)).toString();
                        stringToBeRead += digit + " ";
                    }
                }
                else {
                    if(word != null)
                        stringToBeRead += word + " ";
                }
            }
            stringToBeRead += "\n";
        }
        return stringToBeRead;
    }

    private void read(String stringToBeRead) {
        engine.setPitch((float) pitch);
        engine.setSpeechRate((float) speed);
        engine.speak(stringToBeRead, TextToSpeech.QUEUE_FLUSH, null, null);
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
                    // txtSpeechInput.setText(result.get(0));result.get(0);
                    checkdetails(result.get(0));
                }
                break;
            }

        }
    }



    private void speech(String number) {
        engine.setPitch((float) pitch);
        engine.setSpeechRate((float) speed);
        if(isNumber(number)) {
            //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG);
            int index;
            for (int i = 0; i < number.length(); i++) {
                index = Character.getNumericValue(number.charAt(i));
                if (index != -1) {
                    engine.speak(num[index], TextToSpeech.QUEUE_FLUSH, null, null);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            if(number.equalsIgnoreCase("wait"));
            else{engine.setPitch((float) pitch);
                engine.setSpeechRate((float) speed);
                engine.speak(number, TextToSpeech.QUEUE_FLUSH, null, null);}
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private boolean isNumber(String word)
    {

        try
        {
            Long.parseLong(word);
            return true;
        } catch (NumberFormatException e)
        {

            return false;
        }

    }
    class NotificationReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("notification_event").equals("clear")){
                //txtView.setText("");
                notificationsString = "";
            }
            else {
                String pack = intent.getStringExtra("package");
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                String check=intent.getStringExtra("pack");
                // String subtext = intent.getStringExtra("subtext");
                //String temp = txtView.getText() + intent.getStringExtra("notification_event") + "\n"+pack+"\n"+title+"\n"+text+"\n"+subtext+"\n";
                String temp=intent.getStringExtra("notification_event");
                if(check!=null){
                    if((check.equalsIgnoreCase("posted")) && flaglist) {
                        speech("wait");
                        speech1("Notification");
                        speech1("from " + title);
                        speech1("text is "+text);
                    }
                }
                if(pack!=null) temp=temp+"\n"+pack;
                if(title!=null) temp=temp+"\nfrom "+title;
                if(text!=null)temp=temp+"\ntext is "+text;

                temp = temp + "\n";
                txtView.setTextColor(Color.BLACK);

                temp = txtView.getText()+temp;
                temp = new String(notificationsString) + temp;

                //txtView.setText(temp);
                notificationsString = new String(temp);
            }
        }
    }
    private void speech1(String word)
    { if(word.contains("null"))
        return;
    else
    {
        String num[]=word.split(" ");
        for(int i=0;i<num.length;i++)
            speech(num[i]);
    }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notificationactivity, menu);
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
}
