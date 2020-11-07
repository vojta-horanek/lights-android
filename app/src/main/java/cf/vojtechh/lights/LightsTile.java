package cf.vojtechh.lights;

import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class LightsTile extends TileService implements AsyncResponse {

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PreferencesName, MODE_PRIVATE);

        Tile mTile = getQsTile();
        int state = State.Off;

        if (mTile.getState() == Tile.STATE_INACTIVE) {
            state = State.On;
        }

        new Connection(this, sharedPreferences.getString(Constants.AddressPreference, Constants.DefaultDeviceAddress)).execute(state);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PreferencesName, MODE_PRIVATE);

        Wifi wifi = new Wifi(this);

        if (wifi.check()) {
            new Connection(this, sharedPreferences.getString(Constants.AddressPreference, Constants.DefaultDeviceAddress)).execute(Code.State);
        } else {
            Tile mTile = getQsTile();

            mTile.setState(Tile.STATE_UNAVAILABLE);
            mTile.setLabel(getResources().getString(R.string.lights_net_short));

            mTile.updateTile();
        }

    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void processFinish(Integer state) {
        Tile mTile = getQsTile();

        if (state == State.Off) {
            mTile.setState(Tile.STATE_INACTIVE);
            mTile.setLabel(getResources().getString(R.string.lights_off_short));
        } else if (state == State.On) {
            mTile.setState(Tile.STATE_ACTIVE);
            mTile.setLabel(getResources().getString(R.string.lights_on_short));
        } else if (state == State.Err) {
            mTile.setState(Tile.STATE_UNAVAILABLE);
            mTile.setLabel(getResources().getString(R.string.lights_err));
        }

        mTile.updateTile();
    }
}
