package com.application.firstapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button showHideServerSettingsButton;
    private SeekBar volumeBar;
    private EditText serverField;
    private EditText serverPortField;
    private Button okButton;
    private boolean isConnected = false;

    private MainClientSocket clientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        /*init elements*/
        okButton = findViewById(R.id.connectButton);
        showHideServerSettingsButton = findViewById(R.id.show_hide_server_settings);
        volumeBar = findViewById(R.id.volumeLevel);
        serverField = findViewById(R.id.server);
        serverPortField = findViewById(R.id.serverPort);
        volumeBar.setMax(100);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this,
                        "Volume: " + progressChangedValue + "%",
                        Toast.LENGTH_SHORT).show();
                if (isConnected) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                clientSocket.changeVolume(progressChangedValue);
                            } catch (Exception ex) {
                                if (ex.getMessage().contains("java.net.UnknownHostException: Unable to resolve host")) {
                                    Toast.makeText(MainActivity.this,
                                            "Unknown host! Please provide correct server and port",
                                            Toast.LENGTH_SHORT).show();
                                    showHideServerSettings(true);
                                }
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this,
                            "You are not connected to server",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*preferences*/
        final SharedPreferences pref = getPreferences(MODE_PRIVATE);
        String server = pref.getString("server", null); // getting String
        int port = pref.getInt("port", 0);
        /*end*/

        /*Connect if server and port values is not empty*/
        if (null != server && !server.isEmpty() && (port != 0)) {
            System.out.println("Stored settings:");
            System.out.println("Server: " + server + " : " + port);
            Toast.makeText(MainActivity.this, "Loaded server settings:\"" + server + ":" + port + "\"",
                    Toast.LENGTH_SHORT).show();
            connectToServer();
        } else {
            Toast.makeText(MainActivity.this, "Server not configured",
                    Toast.LENGTH_SHORT).show();
            System.out.println("Can't find settings");
        }
        /*end*/

        /*Show server and port fields if not connected*/
        if (!isConnected) {
            showHideServerSettingsButton.setText("Hide server settings");
            showHideServerSettings(true);
        }
    }

    public void refreshButton(View view) {
        try {
            if (isConnected) {
                volumeBar.setVisibility(View.VISIBLE);
                volumeBar.setProgress(clientSocket.getCurrentVolume());
            } else {
                Toast.makeText(MainActivity.this, "You are not connected, please provide server details",
                        Toast.LENGTH_SHORT).show();
                showHideServerSettings(true);
                isConnected = false;
            }
        } catch (IOException e) {
            if (e.getMessage().contains("java.net.UnknownHostException: Unable to resolve host")) {
                Toast.makeText(MainActivity.this, "Unknown host! Please provide correct server and port",
                        Toast.LENGTH_SHORT).show();
                showHideServerSettings(true);
                isConnected = false;
            } else {
                Toast.makeText(MainActivity.this, "Unknown error!",
                        Toast.LENGTH_SHORT).show();
                showHideServerSettings(true);
                isConnected = false;
            }
        }
    }

    public void submitServerSettings(View view) {
        final SharedPreferences pref = getPreferences(MODE_PRIVATE);
         /*init input fields values*/
        //todo add validation!
        String serverValue = serverField.getText().toString();
        String serverPortValue = serverPortField.getText().toString();
                /**/
        System.out.println(serverValue + " : " + serverPortValue);

        /*show hide fields*/
        showHideServerSettings(false);
        volumeBar.setVisibility(View.VISIBLE);
        showHideServerSettingsButton.setText("Show server settings");
        volumeBar.setProgress(1);
        /**/

        try {
            int serverPortParsed = Integer.parseInt(serverPortValue);
            /*save new server data*/
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("server", serverValue);
            editor.apply();
            editor.putInt("port", serverPortParsed);
            editor.apply();
            /*end*/

            /*init socket from preferences*/
            clientSocket = new MainClientSocket();
            clientSocket.setServerPort(pref.getInt("port", 0));
            clientSocket.setServerIp(pref.getString("server", null));
            /*end*/

            /*read current volume value*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int currentVolume = clientSocket.getCurrentVolume();
                        volumeBar.setProgress(currentVolume);
                        Toast.makeText(MainActivity.this, "Volume on PC: " + currentVolume,
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            isConnected = true;
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Not valid server port! It should be a number ",
                    Toast.LENGTH_SHORT).show();
            showHideServerSettings(true);
            isConnected = false;
        }
    }

    public void showHideServerSettings(View view) {
        final SharedPreferences pref = getPreferences(MODE_PRIVATE);
        String server = pref.getString("server", null); // getting String
        int port = pref.getInt("port", 0);

        if (okButton.getVisibility() == View.INVISIBLE) {
            showHideServerSettingsButton.setText("Hide server settings");
            if ((null != server && !server.isEmpty()) && (port != 0)) {
                System.out.println("Stored settings:");
                System.out.println("Server: " + server + " : " + port);
                serverPortField.setText(port + "");
                serverField.setText(server);
            } else {
                System.out.println("Can't find settings");
            }
            showHideServerSettings(true);
        } else {
            showHideServerSettingsButton.setText("Show server settings");
            showHideServerSettings(false);
        }
    }

    //additional methods
    public void connectToServer() {
        try {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            /*init socket from preferences*/
            clientSocket = new MainClientSocket();
            String server = preferences.getString("server", null);
            int port = preferences.getInt("port", 0);
            clientSocket.setServerPort(port);
            clientSocket.setServerIp(server);
            /*end*/

            serverPortField.setText(port + "");
            serverField.setText(server);

            /*read current volume value*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int currentVolume = clientSocket.getCurrentVolume();
                        volumeBar.setProgress(currentVolume);
                        volumeBar.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,
                                "Volume on PC: " + currentVolume + "%",
                                Toast.LENGTH_SHORT).show();
                        isConnected = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        isConnected = false;
                        showHideServerSettings(true);
                    }
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this,
                    "Not valid server port! It should be a number ",
                    Toast.LENGTH_SHORT).show();
            showHideServerSettings(true);
            isConnected = false;
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,
                    "Unexpected error! Please check application settings and server",
                    Toast.LENGTH_SHORT).show();
            showHideServerSettings(true);
            isConnected = false;
        }
    }

    private void showHideServerSettings(boolean isShow) {
        if (isShow) {
            serverField.setVisibility(View.VISIBLE);
            serverPortField.setVisibility(View.VISIBLE);
            okButton.setVisibility(View.VISIBLE);
        } else {
            serverField.setVisibility(View.INVISIBLE);
            serverPortField.setVisibility(View.INVISIBLE);
            okButton.setVisibility(View.INVISIBLE);
        }
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
