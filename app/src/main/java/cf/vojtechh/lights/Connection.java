package cf.vojtechh.lights;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection extends AsyncTask<Integer, Void, Integer> {

    private AsyncResponse response;
    private String deviceAddress;

    Connection(AsyncResponse response, String deviceAddress) {
        this.response = response;
        this.deviceAddress = deviceAddress;
    }

    @Override
    protected Integer doInBackground(Integer... command) {
        try {
            Socket socket = new Socket(deviceAddress, Constants.DevicePort);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            outputStream.writeByte(command[0]);
            outputStream.flush();

            Integer result = (int) inputStream.readByte();

            inputStream.close();
            outputStream.close();
            socket.close();
            return result;

        } catch (IOException e) {
            return State.Err;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        response.processFinish(integer);
    }
}
