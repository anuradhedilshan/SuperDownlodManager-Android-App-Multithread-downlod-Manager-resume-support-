package lk.lab24.sdm.Backend;


import java.net.URL;
import java.net.URLConnection;

public class Connection {
    public static URLConnection getConnection(URL url) throws Exception {
        if (url.getProtocol() == "https") {


        } else if (url.getProtocol() == "http") {
            return url.openConnection();
        } else {
            throw new Exception("not support");
        }
        return null;
    }
}