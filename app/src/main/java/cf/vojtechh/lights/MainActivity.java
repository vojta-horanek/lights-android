package cf.vojtechh.lights;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static cf.vojtechh.lights.Tools.hasRoot;

public class MainActivity extends AppCompatActivity
        implements AsyncResponse {


    TextView statusText;
    Switch toggleButton;
    RelativeLayout content;
    ProgressBar progress;
    Integer currentState = State.Off;

    SharedPreferences sharedPreferences;

    boolean fireOnClick = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        sharedPreferences = getSharedPreferences(Constants.PreferencesName, MODE_PRIVATE);

        statusText = findViewById(R.id.status_text);
        toggleButton = findViewById(R.id.toggle_button);
        content = findViewById(R.id.content);
        progress = findViewById(R.id.loading);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fireOnClick) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PreferencesName,
                            MODE_PRIVATE);
                    Connection switcher = new Connection(MainActivity.this,
                            sharedPreferences.getString(Constants.AddressPreference, Constants.DefaultDeviceAddress));
                    if (currentState == State.Off) {
                        switcher.execute(State.On);
                    } else if (currentState == State.On) {
                        switcher.execute(State.Off);
                    }
                }
            }
        });

        findViewById(R.id.btn_device_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Device Address");

                final LinearLayout ll = new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(50, 20, 50, 0);

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                input.setText(sharedPreferences.getString(Constants.AddressPreference,
                        Constants.DefaultDeviceAddress));
                ll.addView(input, layoutParams);

                builder.setView(ll);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.AddressPreference, input.getText().toString());
                        editor.apply();
                        onResume();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        if (!hasRoot() &&
                ActivityCompat.checkSelfPermission
                        (MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        int check = Wifi.check(this);
        if (check == WifiResult.Connected) {

            new Connection(this, sharedPreferences.getString(Constants.AddressPreference,
                    Constants.DefaultDeviceAddress)).execute(Code.State);
            progress.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            toggleButton.setVisibility(View.VISIBLE);
            toggleButton.setEnabled(true);

        } else if (check == WifiResult.Unknown) {
            processFinish(State.On);
        } else {

            progress.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.GONE);
            toggleButton.setEnabled(false);
            statusText.setText(getResources().getString(R.string.lights_net));
        }
    }

    @Override
    public void processFinish(Integer result) {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);

        this.currentState = result;
        int toggleVisible = View.VISIBLE;
        boolean toggleEnabled = true;
        boolean toggleChecked = false;
        int text = R.string.lights_off;

        if (result == State.On) {
            text = R.string.lights_on;
            toggleChecked = true;
        } else if (result == State.Err) {
            text = R.string.lights_err;
            toggleEnabled = false;
            toggleVisible = View.GONE;
        }
        toggleButton.setVisibility(toggleVisible);
        toggleButton.setEnabled(toggleEnabled);
        fireOnClick = false;
        toggleButton.setChecked(toggleChecked);
        fireOnClick = true;
        statusText.setText(getResources().getString(text));
    }
}
