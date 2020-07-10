package lk.lab24.sdm.Backend;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

 class Test {


    public static void main(String[] args) throws InterruptedException {

        String src = "http://www.ict24.tk/static/images/fonts.jpg";
        String src2 = "http://www.ict24.tk/static/images/ict24.png";
        URL u;
        try {
            u = new URL(src);
            URLConnection c = u.openConnection();
            c.setReadTimeout(200);
            System.out.println(c.getContentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("after");
    }
}




