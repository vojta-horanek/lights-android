package cf.vojtechh.lights;

import java.io.IOException;

public class Tools {
    public static boolean hasRoot() {
        try {
            Runtime.getRuntime().exec("su");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
