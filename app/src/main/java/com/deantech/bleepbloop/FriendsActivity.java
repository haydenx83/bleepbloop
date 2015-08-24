package com.deantech.bleepbloop;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;


public class FriendsActivity extends ActionBarActivity {

    Intent intent;
    String username,gamerTag;
    String password;
    Map<String, String> loginCookies;
    String preParse;
    String postParse1;
    String friends = "";
    TextView friendTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        intent = getIntent();
        friendTV = (TextView) findViewById(R.id.friendTV);

        username = intent.getStringExtra("gTag");
        password = intent.getStringExtra("pWord");

        System.out.println(username);
        System.out.println(password);

        gamerTagFixer();
        if(checkForFile() == true)
        {
            try {
                if(checkFile() == false)
                {
                    new trueAchievementsTask().execute();
                }
                else
                {
                    LoadFromFile();
                    parseFriend();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            new trueAchievementsTask().execute();
        }
    }

    private class trueAchievementsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... urls) {

            System.out.println("http://www.trueachievements.com/" + gamerTag + ".htm");

            try {
                Connection.Response res = Jsoup.connect("http://www.trueachievements.com/" + gamerTag + ".htm")
                        .method(Connection.Method.POST)
                        .execute();

                Document doc = res.parse();
                loginCookies = res.cookies();

                Element element = doc.select("div.friendspanel").first();
                preParse = element.toString();

                String tmp;
                int j = 1;
                int i = 1;
                friends = "";
                while(preParse.contains("<td class=\"pos\">" + i) == true)
                {
                    postParse1 = preParse.substring(preParse.indexOf("htm\">"),preParse.indexOf("</a>"));

                    tmp = postParse1.substring(5);
                    System.out.println("tmp - " + tmp);
                    if(tmp.compareTo(username) != 0)
                    {
                        friends += "/friend" + ("" + j) + "/" + tmp;
                        j++;
                    }
                    if((preParse.contains("<td class=\"pos\">" + (i+1)) == true))
                    {
                        preParse = preParse.substring(preParse.indexOf("<td class=\"pos\">" + (i+1)));
                    }
                    i++;
                    //System.out.println(preParse);
                }
                System.out.println(friends);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(Void blahblahblah) {
            if(checkForFile() == true)
            {
                System.out.println("file exists");
                resetFile();
                try {
                    saveToFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    saveToFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            parseFriend();
        }
    }
    public void gamerTagFixer()
    {
        //System.out.println("prefix - " + username);
        gamerTag = username.replace(" ", "+");
        //System.out.println("fixed - " + username);
    }
    public boolean checkForFile()
    {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopFriends.txt");
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
        String userInfo = "/User/" + username + friends;
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopFriends.txt");

        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(userInfo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }
    public boolean checkFile() throws IOException {
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
        if(userInfo.contains("/User/" + username) == true)
        {
            return true;
        }
        else {
            return false;
        }

    }
    public void resetFile()
    {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopFriends.txt");
        file.delete();
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
        System.out.println(friends);

    }
    public void parseFriend()
    {
        String tmp,tmp2 = "",names = "";
        tmp = friends;
        int i = 1;
        System.out.println(tmp);
        if(tmp.contains("/friend1") == true) {
            tmp = tmp.substring(tmp.indexOf("/friend1"));
            System.out.println(tmp);
            while (tmp.contains("/friend" + (i + 1)) == true) {
                tmp2 = tmp.substring(tmp.indexOf("/friend" + i), tmp.indexOf("/friend" + (i + 1)));
                System.out.println(tmp2);
                tmp2 = tmp2.substring(tmp2.indexOf(i +"/") + (String.valueOf(i).length() + 1));
                names += tmp2 + "\n";
                tmp = tmp.substring(tmp.indexOf("/friend" + (i + 1)));
                System.out.println(tmp);
                i++;
            }
            if (tmp.contains("/friend") == true) {
                names += tmp.substring(tmp.indexOf(i+"/") + (String.valueOf(i).length()  + 1)) + "\n";
            }

            friendTV.setText(names);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.reset:
                new trueAchievementsTask().execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
