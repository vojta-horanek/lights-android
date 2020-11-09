package cf.vojtechh.lights;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static cf.vojtechh.lights.Tools.hasRoot;

class Wifi {
    static int check(Context context) {
        if (hasRoot()) {
            try {
                Process process = Runtime.getRuntime().exec("su");
                OutputStream stdin = process.getOutputStream();
                InputStream stdout = process.getInputStream();
                boolean retVal = false;

                stdin.write(("iw dev | awk '/ssid/ { print $2 }'\n").getBytes());
                stdin.write("exit\n".getBytes());
                stdin.flush();
                stdin.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
                String line = br.readLine();

                if (line != null && line.equals(Constants.AllowedSSID)) {
                    retVal = true;
                }
                br.close();

                process.waitFor();
                process.destroy();

                if (retVal) {
                    return WifiResult.Connected;
                } else {
                    return WifiResult.Disconnected;
                }
            } catch (IOException | InterruptedException ex) {
                return WifiResult.Unknown;
            }
        } else {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiMgr != null && wifiMgr.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                if (wifiInfo.getNetworkId() == -1) {
                    // We don't have location permission, lets proceed anyway
                    return WifiResult.Unknown;
                }
                String SSID = wifiInfo.getSSID().replace("\"", "");
                if (SSID.equals(Constants.AllowedSSID)) {
                    return WifiResult.Connected;
                } else {
                    return WifiResult.Disconnected;
                }
            } else return WifiResult.Disconnected;
        }
    }
}
