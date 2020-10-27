/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class PickPack {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        new Login().setVisible(true);
        HttpRequest request = new HttpRequest();
        try {
            String respuesta[] = request.login("charlie.carlosalberto@gmail.com", "edfghjuhgbh");
            System.out.println(respuesta[0]);
            System.out.println(respuesta[1]);
        } catch (Exception ex) {
            Logger.getLogger(PickPack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
