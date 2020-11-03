/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 *
 * @author user
 */
public class Download implements Runnable {
    
    String link;
    File out;
    
    public Download(String link, File out){
        this.link = link;
        this.out = out;
    }

    @Override
    public void run() {
        System.out.println("Entramos en descargar");
        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            double fileSize = (double)http.getContentLength();
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(this.out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            int read = 0;
            while((read = in.read(buffer,0,1024)) >= 0){
                bout.write(buffer, 0, read);
                
            }
            System.out.println("Se descargo");
            bout.close();
            in.close();
            
        }catch (IOException ex) {
           ex.printStackTrace();
        }
    }
    
}
