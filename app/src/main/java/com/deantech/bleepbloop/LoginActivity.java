package com.deantech.bleepbloop;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class LoginActivity extends ActionBarActivity {

    String username;
    String gamerName;
    String password;
    String email;
    String exists;
    EditText uNameText;
    EditText pWOrdText;
    EditText emailText;
    int reset;
    Intent intent;
    TextView existsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            setTrustAllCerts();
        } catch (Exception e) {
            e.printStackTrace();
        }

        existsTV= (TextView) findViewById(R.id.existsTV);
        uNameText = (EditText) findViewById(R.id.uNameText);
        pWOrdText = (EditText) findViewById(R.id.pWordText);
        emailText = (EditText) findViewById(R.id.emailText);

        uNameText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (uNameText.getText().toString().compareTo("Name") == 0) {
                    uNameText.setText("");
                }
                return false;
            }
        });
        emailText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (emailText.getText().toString().compareTo("Email") == 0) {
                    emailText.setText("");
                }
                return false;
            }
        });

        Button login = (Button) findViewById(R.id.loginButton);//make if statements to catch empty fields
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                username = uNameText.getText().toString();
                password = pWOrdText.getText().toString();
                email = emailText.getText().toString();
                gamerName = gamerTagFixer(username);

                System.out.println(username);
                System.out.println(password);
                System.out.println(email);

                if (username.compareTo("Name") != 0 && password.compareTo("") != 0 && email.compareTo("") != 0) {
                    if (checkForFile() == false) {
                        new scrapeXboxTask().execute();
                        reset = 0;
                    }
                    else
                    {
                            new scrapeXboxTask().execute();
                            reset = 1;

                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }


            }
        });
        intent = getIntent();
        exists = intent.getStringExtra("exists");
        if(exists.compareTo("false") == 0)
        {
            existsTV.setVisibility(View.VISIBLE);
            System.out.println("false");
        }
        else
        {
            System.out.println("true");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    public boolean checkForFile()
    {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopLogin.txt");
        if(file.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void saveToFile() throws IOException {
        String userInfo = "/User/" + username + "/Pass/" + password + "/Email/" + email;
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopLogin.txt");

        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(userInfo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }
    public void LoadFromFile()
    {

    }
    public void resetFile()
    {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopLogin.txt");
        file.delete();
    }
    public String gamerTagFixer(String usrName)
    {
        return usrName.replace(" ", "+");
    }
    private class scrapeXboxTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... player) {
            String score = "null";
            int success = 0;
            System.out.println(gamerName);
            try {
                Connection.Response res = Jsoup.connect("https://www.xboxleaders.com/api/profile.json?gamertag=" + gamerName)
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute();

                score = res.body().toString();
                success = 1;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(success == 1) {
                try {
                    JSONObject userJSON = new JSONObject(score);
                    if (userJSON.getString("avatar").compareTo("http://image.xboxlive.com//global/t.FFFE07D1/tile/0/20000") == 0)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return 1;
        }
        protected void onPostExecute(Integer player) {
           if(player == 0)
           {
               if(reset == 1) {
                   resetFile();
               }

               try {
                   saveToFile();
                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(intent);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
            else
           {
               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               intent.putExtra("exists","false");
               startActivity(intent);
           }
        }
    }
    private void setTrustAllCerts() throws Exception
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
                    public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init( null, trustAllCerts, new java.security.SecureRandom() );
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier() {
                        public boolean verify(String urlHostName, SSLSession session) {
                            return true;
                        }
                    });
        }
        catch ( Exception e ) {
            //We can not recover from this exception.
            e.printStackTrace();
        }
    }
}

