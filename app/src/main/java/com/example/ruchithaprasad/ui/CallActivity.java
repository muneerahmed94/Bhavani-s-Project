package com.example.ruchithaprasad.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


public class CallActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {
    private ListView list;
    private Button loadBtn,listen;
    private EditText name;
    private TextToSpeech engine;
    private SeekBar seekPitch;
    private SeekBar seekSpeed;
    private String check;
    private double pitch=1.0;
    private double speed=1.0;
    private boolean flag,first;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ArrayList<String> contacts;
    private String num[]={"Zero","One","Two","Three","Four","Five","Six","Seven","Eight","NIne"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        list = (ListView) findViewById(R.id.listView1);
        loadBtn = (Button) findViewById(R.id.button1);
        listen= (Button) findViewById(R.id.calllisten);
        name=(EditText)findViewById(R.id.editText);
        engine = new TextToSpeech(this, this);
        seekPitch = (SeekBar) findViewById(R.id.callseekPitch);
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

        seekSpeed = (SeekBar) findViewById(R.id.callseekSpeed);
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
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n=name.getText().toString();
                if(isNumber(n))
                {
                    speech("calling");
                    speech(n);
                    makecall(n);
                }
                else
                {   check=n;
                    getcontacts();
                }

            }
        });
        listen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // speech("would u like to enter number or name");
                speech("yes to number no to contact name");
                first = true;
                promptSpeechInput();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    check = result.get(0);
                    if(first){ first=false;
                    if(check.equalsIgnoreCase("yes")) {
                        flag = true;
                        speech("Enter number ");
                        promptSpeechInput();
                    }
                    else if (check.equalsIgnoreCase("no")){
                        flag=false;
                        speech("Enter contact name ");
                        promptSpeechInput();
                    }
                    else
                    {
                        speech("sorry we could not catch u");
                    }
                    }
                    else{
                    making(check);}
                }
                break;
            }

        }
    }
    private  void making(String n) {

        if(flag) {
            String num="";
            String num1[]=n.split(" ");
            for(int i=0;i<num1.length;i++)
            {
                num=num+num1[i];
            }
            name.setText(num);
            speech("calling");
            speech(num);
            makecall("" + num);
        }
        else {
            //speech("u have entered "+n);
            name.setText(n);
            getcontacts();
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
    void getcontacts(){
        ProgressDialog pd;

        contacts = new ArrayList<String>();
        pd = ProgressDialog.show(CallActivity.this, "Loading Contacts",
                "Please Wait");
        Cursor c = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        while (c.moveToNext()) {

            String contactName = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phNumber = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(contactName.equalsIgnoreCase(check))
                contacts.add(phNumber);}
        c.close();
        pd.cancel();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.text, contacts);
        //list.setAdapter(adapter);
        if(contacts.size()>0) {
            speech("calling" + check);
            makecall(contacts.get(0));
        }
        else
        {
            speech("no contact found please reenter ");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call, menu);
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

    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");

        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            engine.setLanguage(Locale.UK);
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
            engine.setPitch((float) pitch);
            engine.setSpeechRate((float) speed);
            engine.speak(number, TextToSpeech.QUEUE_FLUSH, null, null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void makecall(String number)
    {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
        name.setText("");
    }

    @Override
    public void onBackPressed() {
        engine.stop();
        this.finish();

    }
}
