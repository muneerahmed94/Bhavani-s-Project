package com.example.ruchithaprasad.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class messageactivity extends ActionBarActivity implements TextToSpeech.OnInitListener{
    private ListView list;
    private Button listen;
    private Button buttonSend;
    private EditText textPhoneNo;
    private EditText textSMS;
    private TextToSpeech engine;
    private SeekBar seekPitch;
    private SeekBar seekSpeed;
    private String check,sms,phoneNo,validate;
    private double pitch=1.0;
    private double speed=1.0;
    private boolean flagnumber,flagmessage;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ArrayList<String> contacts;
    private String num[]={"Zero","One","Two","Three","Four","Five","Six","Seven","Eight","NIne"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageactivity);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.messageText);
         textSMS = (EditText) findViewById(R.id.messageSMS);
        phoneNo=sms=null;flagnumber=flagmessage=false;
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String n = textPhoneNo.getText().toString();
                if(isNumber(n))
                    phoneNo=n;
                else{
                    getcontacts(n);
                }
                sms = textSMS.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    //smsManager.sendTextMessage();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                textPhoneNo.setText("");
                textSMS.setText("");
            }
        });
        listen= (Button) findViewById(R.id.messagelisten);
        engine = new TextToSpeech(this, this);
        seekPitch = (SeekBar) findViewById(R.id.messageseekPitch);
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

        seekSpeed = (SeekBar) findViewById(R.id.messageseekSpeed);
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
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   // txtSpeechInput.setText(result.get(0));
                    check=result.get(0);
                    checkdetails();
                }
                break;
            }

        }
    }
    private void checkdetails()
    {
        if(check.equalsIgnoreCase("number")&& !flagnumber)
        {
            flagnumber=true;
            speech("Enter number");
            validate="number";
            promptSpeechInput();
        }
       else if(check.equalsIgnoreCase("name") && !flagnumber)
        {
            flagnumber=true;
            speech("Enter contact name");
            validate="phone";
            promptSpeechInput();
        }
        else if(check.equalsIgnoreCase("message") && !flagmessage)
        {   flagmessage=true;
            speech("Enter Message");
            validate="sms";
            promptSpeechInput();

        }
        else if(check.equalsIgnoreCase("send"))
        {
            if(phoneNo==null){
                speech("No number Entered");
                speech("please Enter number");}
            else
            {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    //Toast.makeText(getApplicationContext(), "SMS Sent!",Toast.LENGTH_LONG).show();
                    speech("Message Sent");
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(),"SMS faild, please try again later!",Toast.LENGTH_LONG).show();
                    //e.printStackTrace();
                    speech("SMS faild, please try again later!");
                }

                textPhoneNo.setText("");flagmessage=false;phoneNo=null;
                textSMS.setText("");flagnumber=false;sms=null;
                textPhoneNo.setEnabled(true);
                textSMS.setEnabled(true);
            }
        }
        else if(check.equalsIgnoreCase("clear message"))
        {
            flagmessage=false;
            textSMS.setText("");
            textSMS.setEnabled(true);
        }
        else if(check.equalsIgnoreCase("clear number"))
        {
            flagnumber=false;
            textPhoneNo.setText("");
            textPhoneNo.setEnabled(true);
        }
        else if(validate.equalsIgnoreCase("sms"))
        {   Toast.makeText(getApplicationContext(),"sms",Toast.LENGTH_LONG).show();
            if(check!=null){
            Toast.makeText(getApplicationContext(),""+check,Toast.LENGTH_LONG).show();
            sms=check;
            textSMS.setText(sms);
            textSMS.setEnabled(false);validate="";}
            else {
            flagmessage=false;
        }
        }
        else if(validate.equals("phone"))
        {   if(check!=null){
            getcontacts(check);validate="";}
        }
        else if(validate.equals("number")) {
            if (check != null) {
                String num = "";
                String num1[] = check.split(" ");
                for (int i = 0; i < num1.length; i++) {
                    num = num + num1[i];
                }
                phoneNo = num;
                textPhoneNo.setText(phoneNo);
                textPhoneNo.setEnabled(false);validate="";
            }
        }
        else
        {
            speech("sorry we didnt get try again");
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
    void getcontacts(String check){
        ProgressDialog pd;

        contacts = new ArrayList<String>();
        pd = ProgressDialog.show(messageactivity.this, "Loading Contacts",
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
        if(contacts.size()>0)
        {
            phoneNo=contacts.get(0);
            textPhoneNo.setText(phoneNo);
            textPhoneNo.setEnabled(false);
        }
        else {
            flagnumber=false;
            speech("No contact found");
        }
    }
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");

        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            engine.setLanguage(Locale.UK);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messageactivity, menu);
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
