package com.example.phoneloggenerator;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.provider.CallLog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import android.Manifest.permission;


public class MainActivity extends AppCompatActivity {
    private static final String[] CALL_TYPES = {Integer.toString(CallLog.Calls.INCOMING_TYPE), Integer.toString(CallLog.Calls.OUTGOING_TYPE), Integer.toString(CallLog.Calls.MISSED_TYPE)};
    private static final String[] NUMBERS = {"+NUMBERS"};
    private static final long START_TIME = 1640995200000L; // UNIX TIME ZONE
    private static final long END_TIME = 1679990399000L; //
    private static final String SUBSCRIPTION_ID = "subscription_id";
    private static final String SUBSCRIPTION_COMPONENT_NAME = "subscription_component_name";
    private static final String GEOCODED_LOCATION = "geocoded_location";
    private static final String NAME = "name";
    private int numberOfCalls = 0;

    private static final int PERMISSIONS_REQUEST_READ_PRIVILEGED_PHONE_STATE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button generateCallsButton = findViewById(R.id.generate_calls_button);
        generateCallsButton.setOnClickListener(view -> generateCalls());
        EditText numberEditText = findViewById(R.id.number_edit_text);
        EditText durationEditText = findViewById(R.id.duration_edit_text);
        if (checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_PRIVILEGED_PHONE_STATE);
        }
        if (checkSelfPermission(permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.WRITE_CALL_LOG}, PERMISSIONS_REQUEST_READ_PRIVILEGED_PHONE_STATE);
        }


    }


    private void generateCalls() {
        EditText numberEditText = findViewById(R.id.number_edit_text);
        EditText durationEditText = findViewById(R.id.duration_edit_text);
        EditText startTimeEditText = findViewById(R.id.start_time);
        EditText endTimeEditText = findViewById(R.id.end_time);
        EditText numberOfTimesEditText = findViewById(R.id.number_of_times);
        EditText simCardIdEditText = findViewById(R.id.sim_card_id);
        if (simCardIdEditText.getText().toString().trim().isEmpty()) {
            simCardIdEditText.setText("XXXXXXXXXXXXXXXXXXXX");
        }

        EditText cachednamedit = findViewById(R.id.name);
        if (cachednamedit.getText().toString().trim().isEmpty()) {
            cachednamedit.setText("XXXXXXXXX");
        }
        EditText countryedittext = findViewById(R.id.country);
        if (countryedittext.getText().toString().trim().isEmpty()) {
            countryedittext.setText("XXXXXXXXXXXXX");
        }
        String countrystr = countryedittext.getText().toString();
        String cachednamee = cachednamedit.getText().toString();
        String phoneNumber = numberEditText.getText().toString();
        int maxDuration = Integer.parseInt(durationEditText.getText().toString());
        int minDuration = 60; // минимальная продолжительность - 1 минута

        long startTime = convertToUnixTime(startTimeEditText.getText().toString());
        long endTime = convertToUnixTime(endTimeEditText.getText().toString());
        int numberOfTimes = Integer.parseInt(numberOfTimesEditText.getText().toString());

        ContentResolver resolver = getContentResolver();
        Uri callLogUri = CallLog.Calls.CONTENT_URI;

        Random random = new Random();
        for (int i = 0; i < numberOfTimes; i++) {

            String countryname = countrystr;
            String Name = cachednamee;
            String number = phoneNumber;
            String type = CALL_TYPES[random.nextInt(CALL_TYPES.length)];
            String subscriptionId = simCardIdEditText.getText().toString();
            String subscriptionComponentName = "com.android.phone/com.android.services.telephony.TelephonyConnectionService";

            long date = randomLongBetween(startTime, endTime);
            int callDuration = type.equals(Integer.toString(CallLog.Calls.MISSED_TYPE)) ? random.nextInt(10) + 1 : random.nextInt(maxDuration - minDuration + 1) + minDuration;
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NUMBER, number);
            //values.put(NAME, );
            values.put(CallLog.Calls.TYPE, type);
            values.put(CallLog.Calls.DATE, date);
            values.put(CallLog.Calls.CACHED_NAME, Name);
            values.put(GEOCODED_LOCATION, countryname);
            values.put(CallLog.Calls.DURATION, callDuration);
            values.put(SUBSCRIPTION_ID, subscriptionId);
            values.put(SUBSCRIPTION_COMPONENT_NAME, subscriptionComponentName);
            resolver.insert(callLogUri, values);
            numberOfCalls++;
        }


        // query the call log and display the number of generated calls
        Cursor cursor = resolver.query(callLogUri, null, CallLog.Calls.DATE + " BETWEEN ? AND ?", new String[]{Long.toString(START_TIME), Long.toString(END_TIME)}, null);
        int callCount = cursor.getCount();
        cursor.close();

        TextView callCountTextView = findViewById(R.id.call_count_text_view);
        callCountTextView.setText("Generated " + numberOfCalls + " calls\n" + "Displayed " + callCount + " calls in call log");
    }

    private long randomLongBetween(long startInclusive, long endInclusive) {
        long range = endInclusive - startInclusive + 1;
        return startInclusive + (long) (new Random().nextDouble() * range);
    }

    private long convertToUnixTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}