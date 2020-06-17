package com.manasmalla.becoronaready;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class COVIDUpdatesActivity extends AppCompatActivity {

    String state = "India";
    String stateCode = "TT";
    JSONObject jsonCountry, jsonOldCountry;
    TextInputLayout textInputLayout;
    ProgressDialog mProgressDialog;
    TextView confirmedTextView, deathsTextView, recoveredTextView, deltaConfirmedTextView, deltaDeathTextView, deltaRecoveredTextView;
    Date dateOldData;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_covid_updates);

        sharedPreferences = getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE);

        textInputLayout = findViewById(R.id.stateTextInputLayout);
        textInputLayout.setEnabled(false);
        textInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayout.setHint("Country");
            }
        });
        Typeface poppins = ResourcesCompat.getFont(COVIDUpdatesActivity.this, R.font.poppins);

        textInputLayout.setTypeface(poppins);

        confirmedTextView = findViewById(R.id.infectedCountTextView);
        deathsTextView = findViewById(R.id.deathsCountTextView);
        recoveredTextView = findViewById(R.id.recoveredCountTextView);
        deltaConfirmedTextView = findViewById(R.id.deltaInfectedTextView);
        deltaDeathTextView = findViewById(R.id.deltaDeathsTextView);
        deltaRecoveredTextView = findViewById(R.id.deltaRecoveredTextView);

        String currentSharedJSONString = sharedPreferences.getString("jsonLatest", null);
        try {
            if (currentSharedJSONString != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
                JSONObject currentSharedJSON = new JSONObject(currentSharedJSONString);
                Date jsonTime = dateFormat.parse((String) currentSharedJSON.get("time"));
                if ((Calendar.getInstance().getTime().getTime() - jsonTime.getTime()) > 3600000) {
                    DownloadTask task = new DownloadTask();
                    task.execute("https://www.mohfw.gov.in/#state-data");
                } else {
                    jsonCountry = currentSharedJSON;
                    JSONArray jsonStateArray = jsonCountry.getJSONArray("India");
                    for (int i = 0; i < jsonStateArray.length(); i++) {
                        JSONObject jsonState = jsonStateArray.getJSONObject(i);
                        String stateCodeJSON = jsonState.getString("stateCode");
                        if (stateCodeJSON.matches("TT")) {
                            String confirmedJSON = jsonState.getString("infected");
                            String deathsJSON = jsonState.getString("deaths");
                            String recoveredJSON = jsonState.getString("recovered");
                            Log.i("stateCode", stateCodeJSON);
                            Log.i("confirmed", confirmedJSON);
                            Log.i("deaths", deathsJSON);
                            Log.i("recovered", recoveredJSON);

                            confirmedTextView.setText(confirmedJSON);
                            deathsTextView.setText(deathsJSON);
                            recoveredTextView.setText(recoveredJSON);
                            if (sharedPreferences.getString("oldJSONDataDelta", null) != null) {
                                JSONObject jsonObjectOld = new JSONObject(sharedPreferences.getString("oldJSONDataDelta", null));
                                JSONArray jsonArrayOld = jsonObjectOld.getJSONArray("India");
                                jsonOldCountry = jsonArrayOld.getJSONObject(i);

                                String deltaConfirmedJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("infected")) - Integer.parseInt(jsonState.getString("infected"))));
                                String deltaDeathsJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("deaths")) - Integer.parseInt(jsonState.getString("deaths"))));
                                String deltaRecoveredJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("recovered")) - Integer.parseInt(jsonState.getString("recovered"))));
                                Log.i("deltaConfirmed", deltaConfirmedJSON);
                                Log.i("deltaDeaths", deltaDeathsJSON);
                                Log.i("deltaRecovered", deltaRecoveredJSON);

                                deltaConfirmedTextView.setText(deltaConfirmedJSON + " ↑");
                                deltaDeathTextView.setText(deltaDeathsJSON + " ↑");
                                deltaRecoveredTextView.setText(deltaRecoveredJSON + " ↑");
                                deltaConfirmedTextView.setVisibility(View.VISIBLE);
                                deltaDeathTextView.setVisibility(View.VISIBLE);
                                deltaRecoveredTextView.setVisibility(View.VISIBLE);
                                dateOldData = dateFormat.parse((String) jsonObjectOld.get("time"));
                                Toast.makeText(COVIDUpdatesActivity.this, "The Delta Data is calculated using the difference withing the last update, which was at " + dateOldData.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                deltaConfirmedTextView.setVisibility(View.INVISIBLE);
                                deltaDeathTextView.setVisibility(View.INVISIBLE);
                                deltaRecoveredTextView.setVisibility(View.INVISIBLE);
                                Toast.makeText(COVIDUpdatesActivity.this, "The Data was last updated at " + jsonTime.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    textInputLayout.setEnabled(true);
                }
            } else {
                DownloadTask task = new DownloadTask();
                task.execute("https://www.mohfw.gov.in/#state-data");
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final String[] states = new String[]{"India", "Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chattisgarh", "Daman And Diu & Dadra and Nagar Haveli",
                "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharastra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "New Delhi", "Odisha", "Puducherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        states);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state = states[position];
                Log.i("state2", state);

                stateCode = checkForCode(state);

                try {

                    JSONArray jsonStateArray = jsonCountry.getJSONArray("India");
                    for (int i = 0; i < jsonStateArray.length(); i++) {
                        JSONObject jsonState = jsonStateArray.getJSONObject(i);
                        String stateCodeJSON = jsonState.getString("stateCode");
                        if (stateCodeJSON.matches(stateCode)) {
                            String confirmedJSON = jsonState.getString("infected");
                            String deathsJSON = jsonState.getString("deaths");
                            String recoveredJSON = jsonState.getString("recovered");
                            //String deltaConfirmedJSON = jsonState.getString("deltaconfirmed");
                            //String deltaDeathsJSON = jsonState.getString("deltadeaths");
                            //String deltaRecoveredJSON = jsonState.getString("deltarecovered");
                            Log.i("stateCode", stateCodeJSON);
                            Log.i("confirmed", confirmedJSON);
                            Log.i("deaths", deathsJSON);
                            Log.i("recovered", recoveredJSON);
                                /*Log.i("deltaConfirmed", deltaConfirmedJSON);
                                Log.i("deltaDeaths", deltaDeathsJSON);
                                Log.i("deltaRecovered", deltaRecoveredJSON);*/

                            confirmedTextView.setText(confirmedJSON);
                            deathsTextView.setText(deathsJSON);
                            recoveredTextView.setText(recoveredJSON);
                            if (sharedPreferences.getString("oldJSONDataDelta", null) != null) {
                                JSONObject jsonObjectOld = new JSONObject(sharedPreferences.getString("oldJSONDataDelta", null));
                                JSONArray jsonArrayOld = jsonObjectOld.getJSONArray("India");
                                jsonOldCountry = jsonArrayOld.getJSONObject(i);

                                String deltaConfirmedJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("infected")) - Integer.parseInt(jsonState.getString("infected"))));
                                String deltaDeathsJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("deaths")) - Integer.parseInt(jsonState.getString("deaths"))));
                                String deltaRecoveredJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("recovered")) - Integer.parseInt(jsonState.getString("recovered"))));
                                Log.i("deltaConfirmed", deltaConfirmedJSON);
                                Log.i("deltaDeaths", deltaDeathsJSON);
                                Log.i("deltaRecovered", deltaRecoveredJSON);

                                deltaConfirmedTextView.setText(deltaConfirmedJSON + " ↑");
                                deltaDeathTextView.setText(deltaDeathsJSON + " ↑");
                                deltaRecoveredTextView.setText(deltaRecoveredJSON + " ↑");
                                deltaConfirmedTextView.setVisibility(View.VISIBLE);
                                deltaDeathTextView.setVisibility(View.VISIBLE);
                                deltaRecoveredTextView.setVisibility(View.VISIBLE);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
                                dateOldData = dateFormat.parse((String) jsonObjectOld.get("time"));
                                Toast.makeText(COVIDUpdatesActivity.this, "The Delta Data is calculated using the difference withing the last update, which was at " + dateOldData.toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                deltaConfirmedTextView.setVisibility(View.INVISIBLE);
                                deltaDeathTextView.setVisibility(View.INVISIBLE);
                                deltaRecoveredTextView.setVisibility(View.INVISIBLE);
                            }
                        }

                    }

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(COVIDUpdatesActivity.this, "Error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


                Log.i("State Code", stateCode);
            }
        });

    }

    public String checkForCode(String state) {
        String stateCode;
        switch (state) {
            case "India":
                stateCode = "TT";
                break;
            case "Andaman and Nicobar Islands":
                stateCode = "AN";
                break;
            case "Andhra Pradesh":
                stateCode = "AP";
                break;
            case "Arunachal Pradesh":
                stateCode = "AR";
                break;
            case "Assam":
                stateCode = "AS";
                break;
            case "Bihar":
                stateCode = "BR";
                break;
            case "Chandigarh":
                stateCode = "CH";
                break;
            case "Chhattisgarh":
                stateCode = "CT";
                break;
            case "Dadar Nagar Haveli":
                stateCode = "DN";
                break;
            case "Goa":
                stateCode = "GA";
                break;
            case "Gujarat":
                stateCode = "GJ";
                break;
            case "Haryana":
                stateCode = "HR";
                break;
            case "Himachal Pradesh":
                stateCode = "HP";
                break;
            case "Jammu and Kashmir":
                stateCode = "JK";
                break;
            case "Jharkhand":
                stateCode = "JH";
                break;
            case "Karnataka":
                stateCode = "KA";
                break;
            case "Kerala":
                stateCode = "KL";
                break;
            case "Ladakh":
                stateCode = "LA";
                break;
            case "Lakshadweep":
                stateCode = "LD";
                break;
            case "Madhya Pradesh":
                stateCode = "MP";
                break;
            case "Maharashtra":
                stateCode = "MH";
                break;
            case "Manipur":
                stateCode = "MPR";
                break;
            case "Meghalaya":
                stateCode = "ML";
                break;
            case "Mizoram":
                stateCode = "MZ";
                break;
            case "Nagaland":
                stateCode = "NL";
                break;
            case "Delhi":
                stateCode = "DL";
                break;
            case "Odisha":
                stateCode = "OR";
                break;
            case "Puducherry":
                stateCode = "PY";
                break;
            case "Punjab":
                stateCode = "PB";
                break;
            case "Rajasthan":
                stateCode = "RJ";
                break;
            case "Sikkim":
                stateCode = "SK";
                break;
            case "Tamil Nadu":
                stateCode = "TN";
                break;
            case "Telengana":
                stateCode = "TG";
                break;
            case "Tripura":
                stateCode = "TR";
                break;
            case "Uttarakhand":
                stateCode = "UT";
                break;
            case "Uttar Pradesh":
                stateCode = "UP";
                break;
            case "West Bengal":
                stateCode = "WB";
                break;
            default:
                stateCode = "DEFAULT";
                break;
        }
        Log.i("stateCode", stateCode);
        return stateCode;
    }

    public void callHelplineOnClick(View view) {
        Intent intentCall = new Intent(Intent.ACTION_DIAL);
        intentCall.setData(Uri.parse("tel:1075"));
        startActivity(intentCall);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                double data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(COVIDUpdatesActivity.this, "Error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(COVIDUpdatesActivity.this, "Error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(COVIDUpdatesActivity.this);
            mProgressDialog.setTitle("Loading");
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Document dataWEB = Jsoup.parse(result);
            Element table = dataWEB.select("table").first();

            Element tableBody = table.select("tbody").first();

            Elements rows = tableBody.select("tr");
            JSONArray jsonArray = new JSONArray();

            for (int counter = 0; counter <= 36; counter++) {
                if (counter != 35) {
                    Element row = rows.get(counter);
                    Element state = row.select("td").get(1);
                    Element stateCases = row.select("td").get(5);
                    Element stateDeaths = row.select("td").get(4);
                    Element stateRecovered = row.select("td").get(3);
                    JSONObject stateJSONObject = new JSONObject();
                    try {
                        String stateName = state.text();
                        if (state.text().matches("Total#")) {
                            stateName = "India";
                        }
                        stateJSONObject.put("stateCode", checkForCode(stateName));
                        stateJSONObject.put("state", stateName);
                        stateJSONObject.put("infected", stateCases.text());
                        stateJSONObject.put("deaths", stateDeaths.text());
                        stateJSONObject.put("recovered", stateRecovered.text());
                        jsonArray.put(stateJSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(COVIDUpdatesActivity.this, "Error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            jsonCountry = new JSONObject();
            try {
                jsonCountry.put("India", jsonArray);
                jsonCountry.put("time", Calendar.getInstance().getTime());
                Log.i("timeJSON", String.valueOf(Calendar.getInstance().getTime()));
                Log.i("India Country", jsonCountry.toString());

                sharedPreferences.edit().putString("jsonLatest", jsonCountry.toString()).commit();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonState = jsonArray.getJSONObject(i);
                    String stateCodeJSON = jsonState.getString("stateCode");
                    if (stateCodeJSON.matches("TT")) {
                        String confirmedJSON = jsonState.getString("infected");
                        String deathsJSON = jsonState.getString("deaths");
                        String recoveredJSON = jsonState.getString("recovered");
                        Log.i("stateCode", stateCodeJSON);
                        Log.i("confirmed", confirmedJSON);
                        Log.i("deaths", deathsJSON);
                        Log.i("recovered", recoveredJSON);
                        confirmedTextView.setText(confirmedJSON);
                        deathsTextView.setText(deathsJSON);
                        recoveredTextView.setText(recoveredJSON);
                        if (sharedPreferences.getString("oldJSONDataDelta", null) != null) {
                            JSONObject jsonObjectOld = new JSONObject(sharedPreferences.getString("oldJSONDataDelta", null));
                            JSONArray jsonArrayOld = jsonObjectOld.getJSONArray("India");
                            jsonOldCountry = jsonArrayOld.getJSONObject(i);

                            String deltaConfirmedJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("infected")) - Integer.parseInt(jsonState.getString("infected"))));
                            String deltaDeathsJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("deaths")) - Integer.parseInt(jsonState.getString("deaths"))));
                            String deltaRecoveredJSON = String.valueOf(-1 * (Integer.parseInt(jsonOldCountry.getString("recovered")) - Integer.parseInt(jsonState.getString("recovered"))));
                            Log.i("deltaConfirmed", deltaConfirmedJSON);
                            Log.i("deltaDeaths", deltaDeathsJSON);
                            Log.i("deltaRecovered", deltaRecoveredJSON);

                            deltaConfirmedTextView.setText(deltaConfirmedJSON + " ↑");
                            deltaDeathTextView.setText(deltaDeathsJSON + " ↑");
                            deltaRecoveredTextView.setText(deltaRecoveredJSON + " ↑");
                            deltaConfirmedTextView.setVisibility(View.VISIBLE);
                            deltaDeathTextView.setVisibility(View.VISIBLE);
                            deltaRecoveredTextView.setVisibility(View.VISIBLE);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
                            dateOldData = dateFormat.parse((String) jsonObjectOld.get("time"));
                            Toast.makeText(COVIDUpdatesActivity.this, "The Delta Data is calculated using the difference withing the last update, which was at " + dateOldData.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            deltaConfirmedTextView.setVisibility(View.INVISIBLE);
                            deltaDeathTextView.setVisibility(View.INVISIBLE);
                            deltaRecoveredTextView.setVisibility(View.INVISIBLE);
                        }
                        if (Calendar.getInstance().getTime().getTime() - dateOldData.getTime() >= 86400000) {
                            sharedPreferences.edit().putString("oldJSONDataDelta", jsonCountry.toString()).commit();
                        }
                    }

                }

                textInputLayout.setEnabled(true);
                mProgressDialog.dismiss();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                Toast.makeText(COVIDUpdatesActivity.this, "Error, " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
