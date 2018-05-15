/*
This is home page activity. AFter logging in, user will navigate to this page.

Reference:
 */

package com.example.dummy.campusnavforblind;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

import static com.example.dummy.campusnavforblind.SQLiteHelper.DATABASE_NAME;


public class ActivityHomePage extends AppCompatActivity {

    // Object variable for room verification, reminder,security,timetable,setting, notification and navigation button
    private Button verifyRoom,reminder,security,timetable,settings,openNotification,navigation;

    // service call value
    private static final int REQUEST_CALL = 1;
    private final int SPEECH_RECOGNITION_CODE = 1;

    // Object variable for mic button
    private ImageButton btnMicrophone;

    // variables used for google speech
    TextToSpeech tt;
    String res;

    // Objects for database related activity
    SQLiteDatabase sqLiteDatabaseObj;
    SQLiteHelper sqLiteHelper;
    Cursor cursor;

    // variables used in getting current date and time
    SimpleDateFormat simpleDateFormat;
    String time;
    Calendar calander;

    //varibale that holds current location
    String ans = "woodworking entrance";

    // object variable for proximity
    private ProximityObserver proximityObserver;
    ProximityObserver.Handler proximityHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // fetching home page layout file

        verifyRoom = (Button)findViewById(R.id.verifyRoom); // obejct of verify room button
        reminder = (Button)findViewById(R.id.openReminder); // obejct of open reminder button
        security = (Button)findViewById(R.id.security); // obejct of security button
        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic); // obejct of mic button
        timetable = (Button)findViewById(R.id.timetable);// obejct of timetable button
        reminder =(Button)findViewById(R.id.openReminder); // obejct of reminder button
        settings = (Button)findViewById(R.id.settings); // obejct of settings button
        openNotification = (Button)findViewById(R.id.openNotification); // obejct of notification button
        navigation = (Button)findViewById(R.id.navigation); // obejct of navigation button

        // passing database for sqlite helper object
        sqLiteHelper = new SQLiteHelper(getApplicationContext(), DATABASE_NAME, this, 1);

        // passing estimote cloud credential
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("varun-kothiwala-s-proximit-8kf", "94291148934a38a96e7f46a0fdf9d9f6");

        // initialising proximity observer for cloud credential provided
        proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                //Log.e("proximity app", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        // checking if internet connection is available or not
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            // if internet is available then define proximity zone of 2.5 meter for beacons
            ProximityZone zone1 = proximityObserver.zoneBuilder()
                    .forAttachmentKeyAndValue("floor", "8th") // assigning range to those beacons whom have given attachments
                    .inCustomRange(2.5)
                    .withOnChangeAction(new Function1<List<? extends ProximityAttachment>, Unit>() {
                        @Override
                        public Unit invoke(List<? extends ProximityAttachment> attachments) {
                            List<String> desks = new ArrayList<>();
                            for (ProximityAttachment attachment : attachments) {
                                desks.add(attachment.getPayload().get("location")); // fetching location value for scanned beacons
                            }
                            Log.d("app", "Nearby location: " + desks);
                            int count = desks.size(); // getting count of all the beacons getting scanned at a current time
                            String data = "";
                            for (int i = 0; i < count; i++) {
                                data = data + desks.get(i) + ",";
                            }

                            // tv.setText(data);

                            // if scanned beacon's location value is available then, that value is your current location
                            if(data.equals("woodworking entrance,"))
                            {
                                ans = "woodworking entrance";
                            }
                            if(data.equals("student lounge start,"))
                            {
                                ans = "student lounge entrance";
                            }
                            if(data.equals("student lounge mid,"))
                            {
                                ans = "student lounge";
                            }
                            if(data.equals("student lounge end,"))
                            {
                                ans = "student lounge end";
                            }
                            return null;
                        }
                    })
                    .create();
            proximityObserver.addProximityZone(zone1); // adding defined zone to proximity observer


            //  obejct which check for all the requirements which are required befor starting zone to prevent crashing of spp
            RequirementsWizardFactory
                    .createEstimoteRequirementsWizard()
                    .fulfillRequirements(this,
                            // onRequirementsFulfilled
                            new Function0<Unit>() {
                                @Override public Unit invoke() {
                                    Log.d("app", "requirements fulfilled");
                                    proximityHandler =  proximityObserver.start(); // start proximity observer if requirements fulfilled
                                    return null;
                                }
                            },
                            // onRequirementsMissing
                            new Function1<List<? extends Requirement>, Unit>() {
                                @Override public Unit invoke(List<? extends Requirement> requirements) {
                                    Log.e("app", "requirements missing: " + requirements); // log message showing missing things before starting zone
                                    return null;
                                }
                            },
                            // onError
                            new Function1<Throwable, Unit>() {
                                @Override public Unit invoke(Throwable throwable) {
                                    Log.e("app", "requirements error: " + throwable); // show error message if fetched any
                                    return null;
                                }
                            });

        } else {

            // if no internet found, then notify user to start internet
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityHomePage.this);

            builder.setTitle("No internet found");
            builder.setMessage("For navigation purpose, Internet connection is required. Please start internet");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });



            AlertDialog alert = builder.create();
            alert.show();
        }



        // on clicking navigation button
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // send current location fetched value to navigation activity page
                Intent i = new Intent(ActivityHomePage.this,NavigationActivity.class);
                Bundle b = new Bundle();
                b.putString("startLocation", ans); // putting location value in intent
                i.putExtras(b);
                startActivity(i); // starting navigation activity
               // startActivity(i);
            }
        });

        // on clicking notification button
        openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starting viewnotification page
                Intent i = new Intent(ActivityHomePage.this,ViewNotification.class);
                startActivity(i);
            }
        });

        // on clicking setting button
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starting setting page
                Intent i = new Intent(ActivityHomePage.this,MainActivity.class);
                startActivity(i);
            }
        });

        // on clicking reminder button
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //starting reminder home page
                Intent i = new Intent(ActivityHomePage.this,ReminderActivity.class);
                startActivity(i);
            }
        });

        // on clicking timetable button
        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starting timetable home page
                Intent i = new Intent(ActivityHomePage.this,TimeTableHome.class);
                startActivity(i);
            }
        });

        // on clicking verify room button
          verifyRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starting qr scanner page
                Intent i = new Intent(ActivityHomePage.this,QRActivity.class);
                startActivity(i);
            }
        });


          // on clicking security button
        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // notify user if he/she wants to call security
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityHomePage.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to call security?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // if yes then call method to start call
                        makePhoneCall();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if no, just dismiss notification
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        // if micbutton is clicked
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText(); // start method which starts takes user voice commands and retuns output
            }
        });
        }

        // methof for making call
    private void makePhoneCall() {
        String number = "+15195024375"; // telephone number
        if (number.trim().length() > 0) {

            // check if permission for making call is given or not
            if (ContextCompat.checkSelfPermission(ActivityHomePage.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                // if no permission granted, ask for the permission
                ActivityCompat.requestPermissions(ActivityHomePage.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                // if permission already granted, make call
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // on permission aksing, if permission granted then make call
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                // if no permission granted, then give meassing of denying
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // method to interect with google speech
    private void startSpeechToText() {


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");  // if no voice input given then give message to speak something
        try {
            // if voice commands given then start method to give output
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {

            // give message if google speech is not supported
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if voice commands are given, get commands in res varibale
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    // txtOutput.setText(text + "?");
                    res = text; // assigning voice commands to res variable

                }
                break;
            }
        }
        // if command is to get todays date
        if (res.equals("what is today's date")) {

            // getting today's date
            SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd/yyyy");
            Date todayDate = new Date();
            final String thisDate = currentDate.format(todayDate);

            // starting google speech to give output in voice
            tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                public void onInit(int s) {
                   // Toast.makeText(getApplicationContext(), thisDate, Toast.LENGTH_SHORT).show();

                    if (s != TextToSpeech.ERROR) {
                        tt.setLanguage(Locale.CANADA); // setting candian language
                        //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                        // speak current date
                        tt.speak(thisDate, TextToSpeech.QUEUE_FLUSH, null, null);
                        try {
                           // Thread.sleep(8000);
                        }
                        catch (Exception e)
                        {}
                    } else {
                        Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (res.equals("what time is it")) {
            // if voice command is for current time

            // get cuttent time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
            final String strDate = "Current Time : " + mdformat.format(calendar.getTime());

            // ansOutput.setText(strDate);

            tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                public void onInit(int s) {
                   //  Toast.makeText(getApplicationContext(), strDate, Toast.LENGTH_SHORT).show();

                    if (s != TextToSpeech.ERROR) {
                        tt.setLanguage(Locale.CANADA);
                        //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                        // speak current time as output
                        tt.speak(strDate, TextToSpeech.QUEUE_FLUSH, null, null);

                        try {
                           // Thread.sleep(8000);
                        }
                        catch (Exception e)
                        {}
                    } else {
                        Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else  if (res.equals("where am I")) {

// if user want to know current location
            tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                public void onInit(int s) {
                    // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                    if (s != TextToSpeech.ERROR) {
                        tt.setLanguage(Locale.CANADA);
                        //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                        // speak out current location
                        tt.speak("You are near to "+ans, TextToSpeech.QUEUE_FLUSH, null, null);
                        try {
                           // Thread.sleep(8000);
                        }
                        catch (Exception e)
                        {}
                    } else {
                        Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            }
            else if (res.equals("when is my next lecture")) {

            // if user gives commad to know when is the next lecture
            // get day of the week
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);
            final String currentTime,startTime,subject,room;
            String raw="";
            String id;


            // getting calender instance
            calander = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("h:ma");

            time = simpleDateFormat.format(calander.getTime()); // get current time


            Calendar rightNow = Calendar.getInstance();
            int t = rightNow.get(Calendar.HOUR_OF_DAY); // get current hour of the day
            int h,i=0;

            try {

                // fetch value from databse for current day of the weak and hours greater than current hours
                sqLiteDatabaseObj = sqLiteHelper.getReadableDatabase();
                cursor = sqLiteDatabaseObj.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE day = '" +dayOfTheWeek+ "'", null);

                if (cursor.moveToFirst()) {


                    do {
                        id = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID));
                        raw = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6));

                        i = Integer.parseInt(id);
                        // curTime.setText(raw);


                        // getting hour value from lecture time
                        if (raw.equals("12:0AM")) {
                            h = 24;
                        } else if (raw.equals("1:0AM")) {
                            h = 1;
                        } else if (raw.equals("2:0AM")) {
                            h = 2;
                        } else if (raw.equals("3:0AM")) {
                            h = 3;
                        } else if (raw.equals("4:0AM")) {
                            h = 4;
                        } else if (raw.equals("5:0AM")) {
                            h = 5;
                        } else if (raw.equals("6:0AM")) {
                            h = 6;
                        } else if (raw.equals("7:0AM")) {
                            h = 7;
                        } else if (raw.equals("8:0AM")) {
                            h = 8;
                        } else if (raw.equals("9:0AM")) {
                            h = 9;
                        } else if (raw.equals("10:0AM")) {
                            h = 10;
                        } else if (raw.equals("11:0AM")) {
                            h = 11;
                        } else if (raw.equals("12:0AM")) {
                            h = 12;
                        } else if (raw.equals("1:0PM")) {
                            h = 13;
                        } else if (raw.equals("2:0PM")) {
                            h = 14;
                        } else if (raw.equals("3:0PM")) {
                            h = 15;
                        } else if (raw.equals("4:0PM")) {
                            h = 14;
                        } else if (raw.equals("5:0PM")) {
                            h = 17;
                        } else if (raw.equals("6:0PM")) {
                            h = 18;
                        } else if (raw.equals("7:0PM")) {
                            h = 19;
                        } else if (raw.equals("8:0PM")) {
                            h = 20;
                        } else if (raw.equals("9:0PM")) {
                            h = 21;
                        } else if (raw.equals("10:0PM")) {
                            h = 22;
                        } else if(raw.equals("11:0PM")){
                            h = 23;
                        }
                        else
                        {
                            h=101;
                            i=102;
                        }

                        if (h > t) {
                            break; // if current hour is not greter than than lecture time then assign that value
                        }
                        if(h <= t)
                        {

                            h = 101;
                            i = 102;
                        }
                    }while (cursor.moveToNext());
                    //  curTime.setText("h="+Integer.toString(h));
                    //  ansOutput.setText("i="+Integer.toString(i));

                    if(i!=102 || h!=101) {
                        // if lecture is available then select lecture from database for fiven hour for specific day
                        cursor = sqLiteDatabaseObj.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE id = " + i + "", null);
                        if (cursor.moveToFirst()) {

                            subject = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1)); // getting subject name
                            startTime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6)); // start time of subject
                            room = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3)); // room number of subject

                            //  ansOutput.setText("Your next lecture is of " + subject + " in room number " + room + " at " + startTime);

                            tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                                public void onInit(int s) {
                                    // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                    if (s != TextToSpeech.ERROR) {
                                        tt.setLanguage(Locale.CANADA);
                                        //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();


                                        // speak out output
                                        tt.speak("Your next lecture is of " + subject + " in room number " + room + " at " + startTime, TextToSpeech.QUEUE_FLUSH, null, null);
                                        try {
                                          //  Thread.sleep(8000);
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        // if no lecture available
                        //ansOutput.setText("No lecture");
                        tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                            public void onInit(int s) {
                                // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                if (s != TextToSpeech.ERROR) {
                                    tt.setLanguage(Locale.CANADA);
                                    //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                                        // speak out output
                                    tt.speak("You do not have any lecture", TextToSpeech.QUEUE_FLUSH, null, null);
                                    try {
                                       // Thread.sleep(8000);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    cursor.close();

                }
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
                Log.i("Error",e.toString());
            }

        }
        else if(res.equals("how many lectures do I have today"))
        {

            //  ansOutput.setText("answer");
// if user give command to know how many lectures does user has
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d); // getting current day of the week
            String id;
            int i=0;
            try {

                // select subject for current day form databse
                sqLiteDatabaseObj = sqLiteHelper.getReadableDatabase();
                cursor = sqLiteDatabaseObj.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE day = '" +dayOfTheWeek+ "'", null);
                final int count;
                if (cursor.moveToFirst()) {


                    do {
                        id= cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1));
                        i++;
                    } while (cursor.moveToNext());
//speech code
                    count=i; // getting total lecture count
                    //   ansOutput.setText("count="+Integer.toString(count));
                    if(count !=0) {

                        // if user has lectures
                        tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                            public void onInit(int s) {
                                // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                if (s != TextToSpeech.ERROR) {
                                    tt.setLanguage(Locale.CANADA);
                                    //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                                    // speak out number of lectures
                                    tt.speak("You have " + Integer.toString(count) + "lectures today", TextToSpeech.QUEUE_FLUSH, null, null);
                                    try {
                                       // Thread.sleep(8000);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else if(count == 0)
                    {
                        // if user has no lectures
                        tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                            public void onInit(int s) {
                                // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                if (s != TextToSpeech.ERROR) {
                                    tt.setLanguage(Locale.CANADA);
                                    //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                                    // speak out no lectures
                                    tt.speak("You have no lectures today", TextToSpeech.QUEUE_FLUSH, null, null);
                                    try {
                                       // Thread.sleep(8000);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
                cursor.close();
            }catch (Exception e)
            {

            }

        }
        else if(res.equals("how many lectures do I have in this week"))
        {
          //  ansOutput.setText("ans");
            // Toast.makeText(getApplicationContext(),"pass",Toast.LENGTH_SHORT).show();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);
            String id;
            int i=0;
            try {

                // select all subject from datbase
                sqLiteDatabaseObj = sqLiteHelper.getReadableDatabase();
                cursor = sqLiteDatabaseObj.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " ", null);
                final int count;
                if (cursor.moveToFirst()) {


                    do {
                        id= cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1));
                        i++;
                    } while (cursor.moveToNext());

                    count=i; //  getting lecture count
                    if(count != 0) {
                        // if user has lectures
                      //  ansOutput.setText("ans=" + Integer.toString(count));
                        tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                            public void onInit(int s) {
                                // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                if (s != TextToSpeech.ERROR) {
                                    tt.setLanguage(Locale.CANADA);
                                    //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();


                                    // speak out count of lectures
                                    tt.speak("You have " + Integer.toString(count) + "lectures in this week", TextToSpeech.QUEUE_FLUSH, null, null);
                                    try {
                                        //Thread.sleep(8000);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        // if no lectures
                        tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                            public void onInit(int s) {
                                // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                if (s != TextToSpeech.ERROR) {
                                    tt.setLanguage(Locale.CANADA);
                                    //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();

                                    tt.speak("You have no lectures in this week", TextToSpeech.QUEUE_FLUSH, null, null);
                                    try {
                                       // Thread.sleep(8000);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    // curTime.setText(res);
                    // ansOutput.setText("count="+Integer.toString(i));

                }
                cursor.close();
            }catch (Exception e)
            {

            }

        }
            else {
            // ansOutput.setText("Sorry! I did not get you");
// if user ask for something else
            tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                public void onInit(int s) {
                    // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                    if (s != TextToSpeech.ERROR) {
                        tt.setLanguage(Locale.CANADA);
                        //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
// speak out, did not get you output
                        tt.speak("Sorry! I did not get you", TextToSpeech.QUEUE_FLUSH, null, null);
                        try {
                           // Thread.sleep(8000);
                        }
                        catch (Exception e)
                        {}
                    } else {
                        Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    // on killing activity
    @Override
    protected void onDestroy() {
        proximityHandler.stop(); // stop proximity observer
        super.onDestroy();
    }
// on stopping activity
    @Override
    protected void onStop() {
        proximityHandler.stop(); // stop proximity observer
        super.onStop();

    }

}
