package com.deantech.bleepbloop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class PlayActivity extends ActionBarActivity {

    Intent intent;
    String username,gamerTag;
    String password;
    Map<String, String> loginCookies;
    String preParse;
    String postParse1;
    String postParse2;
    String achievementScore = "";
    String trueScore = "";
    int numPlayers;
    String keyS1,keyS2= "";
    TextView uNameText;
    TextView drinksTV;
    TextView keyTV;
    TextView pntsTV;
    int GS,oldGS,TAS,key,diff,drinks;
    boolean firstCheck,gameStarted;
    Timer timer;
    List<Integer> playGS;
    List<Integer> playTAS;
    List<String> userList;
    List<String> gamerList;

    PopupMenu popup;
    RelativeLayout rLayout;
    String friends;

    private TextSwitcher mSwitcher;
    Button btnNext,btnStart,btnAdd;
    int currentIndex=0;
    private ProgressBar spinner;
    boolean isRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        intent = getIntent();
        timer = new Timer();

        username = intent.getStringExtra("gTag");
        password = intent.getStringExtra("pWord");
        keyS1 = intent.getStringExtra("keyValue");

        playGS = new ArrayList<Integer>();
        playTAS = new ArrayList<Integer>();
        userList = new ArrayList<String>();
        gamerList = new ArrayList<String>();

        userList.add(username);
        gamerList.add(gamerTagFixer(username));

        for(int i = 0;i<keyS1.length();i++)
        {
            if(Character.isDigit(keyS1.charAt(i)) == true)  {
                keyS2 += keyS1.charAt(i);
            }
        }

        uNameText = (TextView) findViewById(R.id.uNameText);
        drinksTV = (TextView) findViewById(R.id.drinksTV);
        keyTV = (TextView) findViewById(R.id.keyTV);
        pntsTV = (TextView) findViewById(R.id.pntsText);

        keyTV.setText("Key: " + keyS2);
        uNameText.setText(username);
        diff = 0;
        firstCheck = true;
        isRunning = true;
        gameStarted = false;

        numPlayers = 1;
        key = Integer.parseInt(keyS2);

        gamerTag = gamerTagFixer(username);

        btnNext=(Button)findViewById(R.id.buttonNext);
        btnStart=(Button)findViewById(R.id.buttonStart);
        btnAdd=(Button)findViewById(R.id.buttonAdd);
        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcherGS);

        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                TextView myText = new TextView(PlayActivity.this);
                //myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(24);
                myText.setTextColor(Color.WHITE);
                myText.setTypeface(null, Typeface.BOLD);
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        btnAdd.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
        btnNext.setVisibility(View.INVISIBLE);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.INVISIBLE);


        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                currentIndex++;
                // If index reaches maximum reset it
                if (currentIndex > numPlayers - 1) {
                    currentIndex = 0;
                }

                mSwitcher.setText(gamerList.get(currentIndex) + "\n" + "GS: " + playGS.get(currentIndex).toString() + "\n" + "TAS: " + playTAS.get(currentIndex).toString());
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    rLayout = (RelativeLayout) findViewById(R.id.playAct);
                    LoadFromFile();
                    makeFriendList();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                popup.show();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                timer = new Timer();
                if(isRunning == true) {
                    System.out.println("IS running");
                    btnNext.setClickable(false);
                    spinner.setVisibility(View.VISIBLE);
                }
                checkScore();
                btnAdd.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
            }
        });

        try {
            setTrustAllCerts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new loadPlayerTask().execute(0);
    }

    /////////Gamer Score Timer///////////////////////////
    public void timeDelay() {
        timer.schedule(new
                               TimerTask() {
                                   public void run() {
                                       checkScore();
                                   }
                               }
                , 60000 * 1);
    }

    private class spinnerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... player) {

            return null;
        }
        protected void onPostExecute(Void tmp) {

            System.out.println("IS running");
            btnNext.setClickable(false);
            spinner.setVisibility(View.VISIBLE);

        }
    }

    private class trueAchievementsTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... player) {

            try {
                Connection.Response res = Jsoup.connect("http://www.trueachievements.com/" + gamerList.get(player[0]) + ".htm")
                        .method(Connection.Method.POST)
                        .execute();

                Document doc = res.parse();
                loginCookies = res.cookies();

                Element element = doc.select("div.itemright").first();
                preParse = element.toString();

                postParse1 = preParse.substring(preParse.indexOf("</a>"),preParse.indexOf(" <span class=\"small\">"));
                postParse2 = preParse.substring(preParse.indexOf(" <span class=\"small\">"),preParse.indexOf(")</span>"));

                for(int i = 0;i<postParse1.length();i++)
                {
                    if(Character.isDigit(postParse1.charAt(i)) == true)  {
                        trueScore += postParse1.charAt(i);
                    }
                }

                //System.out.println(postParse1);

                /*for(int i = 0;i<postParse2.length();i++)
                {
                    if(Character.isDigit(postParse2.charAt(i)) == true)  {
                        achievementScore += postParse2.charAt(i);
                    }
                }*/

                //System.out.println(postParse2);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return player[0];

        }
        protected void onPostExecute(Integer player) {
            TAS = TAS + Integer.parseInt(trueScore);
            playTAS.add(player,Integer.parseInt(trueScore));

            if(firstCheck == true)
            {
                oldGS = GS;
            }
            //System.out.println(userList.get(player) + " - " + playGS.get(player) + " - " + playTAS.get(player));
            if(player == 0) {
                mSwitcher.setText(username + "\n" + "GS:  " + achievementScore  + "\n" + "TAS: " + trueScore);
            }
            if(player == (numPlayers -1)) {
                firstCheck = false;
                diff = GS - oldGS + diff;
                if(diff >= key)
                {
                    drinks = (diff-diff%key)/key;
                    diff = diff%key;
                }
                drinksTV.setText("" + drinks);
                pntsTV.setText("" + diff);
                timeDelay();
                if(isRunning == true) {
                    btnNext.setClickable(true);
                    spinner.setVisibility(View.INVISIBLE);
                }
                //System.out.println(userList.get(player) + " - " + playGS.get(player) + " - " + playTAS.get(player));
            }
            achievementScore = "";
            trueScore = "";
            spinner.setVisibility(View.INVISIBLE);
        }
    }
    private class loadPlayerTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... player) {

            try {
                Connection.Response res = Jsoup.connect("http://www.trueachievements.com/" + gamerList.get(player[0]) + ".htm")
                        .method(Connection.Method.POST)
                        .execute();

                Document doc = res.parse();
                loginCookies = res.cookies();

                Element element = doc.select("div.itemright").first();
                preParse = element.toString();

                postParse1 = preParse.substring(preParse.indexOf("</a>"),preParse.indexOf(" <span class=\"small\">"));
                postParse2 = preParse.substring(preParse.indexOf(" <span class=\"small\">"),preParse.indexOf(")</span>"));

                for(int i = 0;i<postParse1.length();i++)
                {
                    if(Character.isDigit(postParse1.charAt(i)) == true)  {
                        trueScore += postParse1.charAt(i);
                    }
                }

                //System.out.println(postParse1);

                for(int i = 0;i<postParse2.length();i++)
                {
                    if(Character.isDigit(postParse2.charAt(i)) == true)  {
                        achievementScore += postParse2.charAt(i);
                    }
                }

                //System.out.println(postParse2);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return player[0];

        }
        protected void onPostExecute(Integer player) {
            GS = GS + Integer.parseInt(achievementScore);
            TAS = TAS + Integer.parseInt(trueScore);
            playGS.add(player,Integer.parseInt(achievementScore));
            playTAS.add(player, Integer.parseInt(trueScore));

            mSwitcher.setText(username + "\n" + "GS:  " + achievementScore + "\n" + "TAS: " + trueScore);

            achievementScore = "";
            trueScore = "";
            btnAdd.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            gameStarted = true;
        }
    }

    private class scrapeXboxTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... player) {
            String score = "null";
            int success = 0;

            try {
                Connection.Response res = Jsoup.connect("https://www.xboxleaders.com/api/profile.json?gamertag=" + gamerList.get(player[0]))
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute();

                score = res.body().toString();
                //System.out.println(score);
                success = 1;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(success == 1) {
                try {
                    JSONObject userJSON = new JSONObject(score);
                    achievementScore = userJSON.getString("gamerscore");
                    System.out.println(achievementScore);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return player[0];

        }
        protected void onPostExecute(Integer player) {
            GS = GS + Integer.parseInt(achievementScore);
            playGS.add(player, Integer.parseInt(achievementScore));
            trueScore = "";
            new trueAchievementsTask().execute(player);
        }
    }

    public String gamerTagFixer(String usrName)
    {
        return usrName.replace(" ", "+");
    }
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                try {
                    rLayout = (RelativeLayout) findViewById(R.id.playAct);
                    LoadFromFile();
                    makeFriendList();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                popup.show();
                return true;
            case R.id.reload:
                timer.cancel();
                timer.purge();
                timer = new Timer();
                checkScore();
                return true;
            case R.id.load:
                try {
                    loadGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.save:
                if(gameStarted == true) {
                    try {
                        saveGame();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void checkScore()
    {
        oldGS = GS;
        GS = 0;
        TAS = 0;
        if(isRunning ==  true) {
            new spinnerTask().execute();
        }
        for(int i = 0;i<numPlayers;i++) {
            new scrapeXboxTask().execute(i);
        }
    }
    public void loadGame() throws IOException
    {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopSave.txt");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream input = new FileInputStream(file);
        try {
            input.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            input.close();
        }

        String userInfo = new String(bytes);
        System.out.println(userInfo);
    }
    public void saveGame() throws IOException, JSONException {

        JSONObject gameFriend = new JSONObject();
        System.out.println(numPlayers);
        char friendNum = '1';
        for(int i = 0;i < numPlayers;i++) {
            try {
                gameFriend.put("Player" + friendNum, userList.get(i));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            friendNum++;
        }

        JSONArray jsonArray = new JSONArray();

        jsonArray.put(gameFriend);

        JSONObject gameSave = new JSONObject();
        gameSave.put("GamerScore",oldGS);
        gameSave.put("Key",key);
        gameSave.put("Drinks",drinks);
        gameSave.put("Remainder",diff);
        gameSave.put("Friend", jsonArray);

        System.out.println(gameSave);
        String gameInfo = gameSave.toString();
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopSave.txt");

        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(gameInfo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }
    public void LoadFromFile() throws IOException {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopFriends.txt");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream input = new FileInputStream(file);
        try {
            input.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            input.close();
        }

        String userInfo = new String(bytes);
        friends = userInfo;

    }
    public void makeFriendList() {
        String tmp, tmp2 = "";
        tmp = friends;
        int i = 1;

        popup = new PopupMenu(PlayActivity.this,(TextView) findViewById(R.id.titleText));
        popup.getMenu().add("Cancel");
        if (tmp.contains("/friend1") == true) {
            tmp = tmp.substring(tmp.indexOf("/friend1"));
            //System.out.println(tmp);
            while (tmp.contains("/friend" + (i + 1)) == true) {
                tmp2 = tmp.substring(tmp.indexOf("/friend" + i), tmp.indexOf("/friend" + (i + 1)));
                tmp2 = tmp2.substring(tmp2.indexOf(i + "/") + ((i / 10) + 2));
                tmp = tmp.substring(tmp.indexOf("/friend" + (i + 1)));
                //System.out.println(tmp2);
                popup.getMenu().add(tmp2);
                i++;
            }
            if (tmp.contains("/friend") == true) {
                popup.getMenu().add(tmp.substring(tmp.indexOf(i + "/") + ((i / 10) + 2)));
            }
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item)
            {
                if(item.getTitle().toString().compareTo("Cancel") != 0)
                {
                    if(userList.indexOf(item.getTitle().toString()) == -1) {
                        userList.add(item.getTitle().toString());
                        gamerList.add(gamerTagFixer(item.getTitle().toString()));
                        numPlayers++;
                    }
                }
                return true;
            }
        });
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
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("paused");
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }
}
