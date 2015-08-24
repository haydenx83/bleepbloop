package com.deantech.bleepbloop;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    String gamerTag,email,password,key;
    Button play;
    Button login;
    Button friend;
    Button about;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.playButton);
        login = (Button) findViewById(R.id.loginButton);
        friend = (Button) findViewById(R.id.friendButton);
        about = (Button) findViewById(R.id.About);

        if(checkForFile() == false)
        {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            try {
                LoadFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /////////////////Button///////////////////////
        //make if statements to catch empty fields
        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("exists","true");
                startActivity(intent);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checkForFile() == true) {

                    intent = new Intent(getApplicationContext(), PlayActivity.class);
                    intent.putExtra("gTag",gamerTag);
                    intent.putExtra("pWord", password);

                    PopupMenu popup = new PopupMenu(MainActivity.this, play);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_menu, popup.getMenu());
                    popup.getMenu().findItem(R.id.zero).setEnabled(false);

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            key = item.getTitle().toString();
                            System.out.println(key);
                            Toast.makeText(
                                    MainActivity.this,
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            intent.putExtra("keyValue",item.getTitle().toString());
                            startActivity(intent);
                            return true;
                        }
                    });
                    popup.show(); //showing popup menu
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        //make if statements to catch empty fields
        friend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                intent.putExtra("gTag",gamerTag);
                intent.putExtra("pWord", password);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void LoadFromFile() throws IOException {
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path + "/bleepbloopLogin.txt");
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
        parseString(userInfo);

    }
    public void parseString(String userInfo)
    {
        System.out.println(userInfo);
        gamerTag = userInfo.substring(userInfo.indexOf("/User/"),userInfo.indexOf("/Pass/"));
        gamerTag = gamerTag.substring(6);
        System.out.println(gamerTag);

        password = userInfo.substring(userInfo.indexOf("/Pass/"),userInfo.indexOf("/Email/"));
        password = password.substring(6);
        System.out.println(password);

        email = userInfo.substring(userInfo.indexOf("/Email/"));
        email = email.substring(7);
        System.out.println(email);

    }
}
