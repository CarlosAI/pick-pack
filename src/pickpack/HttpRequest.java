/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author user
 */
public class HttpRequest {
    
    private final String USER_AGENT = "Mozilla/5.0";
    private final String url_base = "https://fulfillment.marketful.mx/";
    
    public String[] login(String correo, String password) throws Exception {

        String url_final = url_base + "loginfe?username="+correo+"&password="+password;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url_final);

        BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        JSONObject res = new JSONObject(response.toString());
        String resultado = res.getString("result");
        String res2[] = new String[2];
        res2[0] = resultado;
        if("200".equals(resultado)){
            res2[1] = res.getString("seller_name");
        }else{
            res2[1] = res.getString("status");
        }
        return res2;
    }
    
    public StringBuilder getPedidos() throws Exception {

        String url_final = url_base + "pedidos/get_pedidos";

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url_final);

        BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

//        System.out.println(response.toString());
        JSONObject res = new JSONObject(response.toString());
        String resultado = res.getString("pedidos");
        String res2[] = new String[1];
        res2[0] = resultado;
       
        return response;
    }
}
