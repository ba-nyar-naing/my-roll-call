package com.banyar.myrollcall_cumdy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cumdy.banyar.myrollcall_cumdy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private CircularProgressBar circularProgressBar;
    private int roll_call_percentage = 0, roll_call_total = 0, roll_call_minimum = 0, roll_call_your_total = 0, roll_call_status = 0;
    private TextView txtSetPercentageInCircle, txtStudentRCTotal, txtStudentRCMinimum, txtStudentRCYourTotal, txtStudentRCStatus;
    private EditText txtGetYourRollNo;
    private Button btnSearchData;
    private String roll_no, year, month, academicYear;
    private boolean fontUnicode;
    private ProgressDialog progressDialog;
    private String[] years, monthsName;
    private ArrayAdapter<String> arrayAdapterMonths;
    private Spinner spinnerMonth;
    private Student student;
    private FirebaseDatabase firebaseDatabase;
    public static List academicYearList;
    private HashMap<String, Long> range, months;
//    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onStart() {
        super.onStart();

        getSharedValues();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSharedValues();
        getAllMonths(academicYear);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
//        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Internet connection is required!", Snackbar.LENGTH_LONG);
//        snackbar.show();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching initial data... \nInternet connection is required");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        itemAssigning();
        spinnerAssigning();
        getSharedValues();

        academicYearList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRefCurrentAcademicYear = firebaseDatabase.getReference("Current Academic Year");
        myRefCurrentAcademicYear.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                academicYear = dataSnapshot.getValue().toString();
                Log.i("CurAcdYear", academicYear);
                getAllMonths(academicYear);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference myRefAcademicYearList = firebaseDatabase.getReference("Academic Year List");
        myRefAcademicYearList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    academicYearList = (List) dataSnapshot.getValue();
//                Log.i("AcdYearList", academicYearList.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSearchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roll_no = txtGetYourRollNo.getText().toString();
                if (TextUtils.isEmpty(roll_no.trim()))
                    roll_no = "0";
                if (validateRollNo(year, Integer.parseInt(roll_no)) && !TextUtils.equals(roll_no, "0")) {
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setMessage("Fetching data... \nInternet connection is required");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    getData(v.getContext(), year, roll_no, month);
                } else {
                    setDefault();
                    Toast.makeText(v.getContext(), "Invalid roll no!\nTry again", Toast.LENGTH_SHORT).show();
                }
                Log.i("roll_no", year + "-" + roll_no);
            }
        });
    }

    private void getAllMonths(final String academicYear) {
        DatabaseReference myRefRCTotal = firebaseDatabase.getReference(academicYear + "/RollCallTotal");
        myRefRCTotal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
//                    Log.i("Months", dataSnapshot.getValue().toString());
                    if (dataSnapshot.getValue() != null) {
                        months = (HashMap<String, Long>) dataSnapshot.getValue();
                        monthsName = new String[months.size()];
                        int i = 0;
                        for (Map.Entry<String, Long> entry : months.entrySet()) {
                            monthsName[i++] = entry.getKey();
                        }
                        arrayAdapterMonths = new ArrayAdapter<>(getBaseContext(),
                                android.R.layout.simple_dropdown_item_1line, monthsName);
                        spinnerMonth.setAdapter(arrayAdapterMonths);
                        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                month = monthsName[position];
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        getAllRange(academicYear);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getAllRange(String academicYear) {
        DatabaseReference myRefRange = firebaseDatabase.getReference(academicYear + "/Range");
        myRefRange.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Range", dataSnapshot.getValue().toString());
                range = (HashMap<String, Long>) dataSnapshot.getValue();

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void itemAssigning() {
        years = new String[]{"   1CST   ", "   2CS   ", "   2CT   ", "   3CS   ", "   3CT", "   4CS", "   4CT", "   5CS", "   5CT"};
        student = new Student();
        monthsName = new String[]{"December"};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.account_student_roll_call_circular_progressbar);
        circularProgressBar.setProgressWithAnimation(100 - roll_call_percentage, 2500);
        txtSetPercentageInCircle = (TextView) findViewById(R.id.txt_student_roll_call_percentage);
        txtStudentRCTotal = (TextView) findViewById(R.id.txt_student_roll_call_total);
        txtStudentRCMinimum = (TextView) findViewById(R.id.txt_student_roll_call_minimum);
        txtStudentRCYourTotal = (TextView) findViewById(R.id.txt_student_roll_call_your_total);
        txtStudentRCStatus = (TextView) findViewById(R.id.txt_student_roll_call_status);
        txtGetYourRollNo = (EditText) findViewById(R.id.ed_input_your_roll_no);
        btnSearchData = (Button) findViewById(R.id.btn_search_data);
        txtStudentRCTotal.setText(String.valueOf(roll_call_total));
        txtStudentRCMinimum.setText(String.valueOf(roll_call_minimum));
    }

    private void spinnerAssigning() {
        ArrayAdapter<String> arrayAdapterYears = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, years);
        Spinner spinnerYear = (Spinner) findViewById(R.id.years_spinner);
        spinnerYear.setAdapter(arrayAdapterYears);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = years[position];
                year = year.trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth = (Spinner) findViewById(R.id.months_spinner);

    }

    private void getSharedValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        academicYear = sp.getString("academicYear", "2016-2017");
        Log.i("getSharedAcdY", academicYear);
        fontUnicode = sp.getBoolean("fontUnicode", false);
//        Toast.makeText(this, academicYear + " " + fontUnicode, Toast.LENGTH_SHORT).show();
    }

    private void getData(final Context context, String year, String roll_no, String month) {
        student.setMonth(month);
        student.setRoll_no(year + "-" + roll_no);
        student.setUni_total((months.get(month)).intValue());

        DatabaseReference myRefName;
        if (fontUnicode) {
            myRefName = firebaseDatabase.getReference(academicYear + "/NameUnicode/" + year + "/" + roll_no);
            myRefName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Log.i("Std name uni", "No data received");
                        Toast.makeText(context, "No data received", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Std name uni", dataSnapshot.getValue().toString());
                        student.setName(dataSnapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            myRefName = firebaseDatabase.getReference(academicYear + "/NameZawgyi/" + year + "/" + roll_no);
            myRefName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Log.i("Std name zg", "No data received");
                        Toast.makeText(context, "No data received", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Std name zg", dataSnapshot.getValue().toString());
                        student.setName(dataSnapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        DatabaseReference myRefRollCall = firebaseDatabase.getReference(academicYear + "/RollCall/" + month + "/" + year + "/" + roll_no);
        myRefRollCall.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
//                    Toast.makeText(context, "Roll call not found", Toast.LENGTH_SHORT).show();
                    setDefault();
                    progressDialog.dismiss();
                } else {
                    Log.i("Std total", dataSnapshot.getValue().toString());
                    student.setStd_total(dataSnapshot.getValue(Long.class).intValue());
                    assignRollCall();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setDefault() {
        circularProgressBar.setProgressWithAnimation(100, 2500);
        txtStudentRCTotal.setText(String.valueOf("0"));
        txtStudentRCMinimum.setText(String.valueOf("0"));
        txtStudentRCStatus.setText("0");
        txtStudentRCStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
        txtStudentRCYourTotal.setText("0");
        txtSetPercentageInCircle.setText("0 %");
        setTitle("My Roll Call - CU MDY");
    }

    private void assignRollCall() {
        roll_call_total = student.getUni_total();
        roll_call_your_total = student.getStd_total();
        calculateRollCall();
        circularProgressBar.setProgressWithAnimation(100 - roll_call_percentage, 2500);
        txtSetPercentageInCircle.setText(String.valueOf(roll_call_percentage) + " %");
        colourPercentageCircle(roll_call_percentage, circularProgressBar, txtSetPercentageInCircle);
        txtStudentRCTotal.setText(String.valueOf(roll_call_total));
        txtStudentRCMinimum.setText(String.valueOf(roll_call_minimum));
        txtStudentRCYourTotal.setText(String.valueOf(roll_call_your_total));
        txtStudentRCStatus.setText(colourStatus(roll_call_status));
        setTitle(student.getRoll_no() + " " + student.getName());
        colourStatus(roll_call_status);
    }

    private void colourPercentageCircle(int roll_call_percentage, CircularProgressBar circularProgressBar, TextView txtSetPercentageInCircle) {
        if (roll_call_percentage <= 50) {
            circularProgressBar.setBackgroundColor(getResources().getColor(R.color.red));
            txtSetPercentageInCircle.setTextColor(getResources().getColor(R.color.red));
        } else if (roll_call_percentage <= 79) {
            circularProgressBar.setBackgroundColor(getResources().getColor(R.color.yellow));
            txtSetPercentageInCircle.setTextColor(getResources().getColor(R.color.yellow));
        } else {
            circularProgressBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            txtSetPercentageInCircle.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void calculateRollCall() {
        double oneUnit = 100.0 / roll_call_total;
        roll_call_minimum = roll_call_total * 3 / 4;
        roll_call_status = roll_call_your_total - roll_call_minimum;
        roll_call_percentage = (int) (roll_call_your_total * oneUnit);
//        Log.i("one unit", String.valueOf(oneUnit));
//        Log.i("percentage", String.valueOf(roll_call_percentage));
//        Log.i("minimum", String.valueOf(roll_call_minimum));
//        Log.i("total", String.valueOf(roll_call_your_total));
//        Log.i("status", String.valueOf(roll_call_status));

    }

    private String colourStatus(int status) {
        String temp;
        if (status > 0) {
            txtStudentRCStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            temp = "+" + status;
        } else if (status == 0) {
            txtStudentRCStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            temp = "0";
        } else {
            txtStudentRCStatus.setTextColor(getResources().getColor(R.color.red));
            temp = "" + status;
        }
        return temp;
    }

    private boolean validateRollNo(String year, int roll_no) {
        return roll_no <= range.get(year);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingPreferenceActivity.class));
                return true;
            case R.id.menu_exit:
                finishAffinity();

        }
        return super.onOptionsItemSelected(item);
    }


}


//    Google Ad

//    private AdView mAdView;
//    private InterstitialAd mInterstitialAd;

//    private void showAdBanner() {
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice("2D8202527EDC0CA48968FEF93897E1E7").build();
//        mAdView.loadAd(adRequest);
//    }

//    private void showAdFullScreen() {
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
//        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice("2D8202527EDC0CA48968FEF93897E1E7").build();
//        mInterstitialAd.loadAd(adRequest);
//        mInterstitialAd.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                mInterstitialAd.show();
//            }
//        });
//    }
