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
import java.util.Arrays;
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
        String res2[] = new String[5];
        res2[0] = resultado;
        if("200".equals(resultado)){
            res2[1] = res.getString("seller_name");
            res2[2] = res.getString("lastName");
            res2[3] = res.getString("token");
            res2[4] = res.getString("id");
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

        JSONObject res = new JSONObject(response.toString());
        String resultado = res.getString("pedidos");
        String res2[] = new String[1];
        res2[0] = resultado;
       
        return response;
    }
    
    public int setGenerarPedidos() throws Exception {

        String url_final = url_base + "shopi_orders/generar_etiquetas_pedidos_api?seller_id=3";

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        return responseCode;
    }
    
    public String getStatusEtiquetas() throws Exception {

        String url_final = url_base + "shopi_orders/status_informe_api?seller_id=3&informe=generar_etiquetas_pedidos";

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "error";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            resultado = res.getString("progreso");
        }
        return resultado;
    }
    
    public String getPdfEtiquetas() throws Exception {

        String url_final = url_base + "shopi_orders/traer_documento_api?seller_id=3";

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "error";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            resultado = res.getString("url");
        }
        return resultado;
    }
    
    public String[] verificarPositionName(String position_name, String pedido_id) throws Exception {

        String url_final = url_base + "pedidos/verificar_posicion_pedido_api?position_name="+position_name+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "error";
         String res2[] = new String[5];
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println(res);
            res2[0] = res.getString("result");
            res2[1] = res.getString("message");
            res2[2] = res.getString("cantidad_pendiente");
        }else{
            res2[0] ="404";
            res2[1] = "Error interno Marketful";
        }
//        System.out.println(Arrays.toString(res2));
        return res2;
    }
    
    public String[] verificarSKUPedido(String sku, String pedido_id) throws Exception {

        String url_final = url_base + "pedidos/verificar_seller_sku_pedido_api?seller_sku="+sku+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "error";
         String res2[] = new String[5];
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println(res);
            res2[0] = res.getString("result");
            res2[1] = res.getString("message");
        }else{
            res2[0] ="404";
            res2[1] = "Error interno Marketful";
        }
//        System.out.println(Arrays.toString(res2));
        return res2;
    }
    
    public String verificarTrackingPedido(String tracking_number, String pedido_id) throws Exception {

        String url_final = url_base + "pedidos/verificar_tracking_pedido_api?tracking_number="+tracking_number+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "400";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println(res);
            resultado = res.getString("result");
        }
        return resultado;
    }
    
    public String surtirPedido(Integer cantidad, String pedido_id, String token) throws Exception {
        System.out.println("Vamos a surtir pedido");
        String url_final = url_base + "pedidos/sacar_de_posicion_api?cantidad="+cantidad+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+token);
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "400";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println("here");
            System.out.println(res);
            if(res.has("status")){
                System.out.println("Si tiene");
                resultado = res.getString("errors");
            }else{
                System.out.println("no tiene");
                resultado = res.getString("result");
            }
            System.out.println(resultado);
        }else if(responseCode == 403){
            resultado = "Sesion Expirada";
        }
        return resultado;
    }
    
     public String[] verificarPedioRuta(String order_id, String pedido_id) throws Exception {

        String url_final = url_base + "pedidos/verificar_pedido_ruta_pedido_api?order_id="+order_id+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "error";
         String res2[] = new String[5];
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println(res);
            res2[0] = res.getString("result");
            res2[1] = res.getString("message");
        }else{
            res2[0] ="404";
            res2[1] = "Error interno Marketful";
        }
//        System.out.println(Arrays.toString(res2));
        return res2;
    }
     
     public String verificarConsolidado(String consolidado_id, String pedido_id) throws Exception {

        String url_final = url_base + "pedidos/verificar_consolidado_api?consolidado_id="+consolidado_id+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "400";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println(res);
            resultado = res.getString("result");
        }
        return resultado;
    }
     
    public String registrarConsolidadoApi(String consolidado_id, String pedido_id, String token) throws Exception {
        System.out.println("Vamos a surtir pedido");
        String url_final = url_base + "pedidos/sacar_de_posicion_api?consolidado_id="+consolidado_id+"&pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+token);
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "400";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println("here");
            System.out.println(res);
            if(res.has("status")){
                System.out.println("Si tiene");
                resultado = res.getString("errors");
            }else{
                System.out.println("no tiene");
                resultado = res.getString("result");
            }
            System.out.println(resultado);
        }else if(responseCode == 403){
            resultado = "Sesion Expirada";
        }
        return resultado;
    }
    
    public String generarPedidos(String token) throws Exception {
        System.out.println("Vamos a surtir pedido");
        String url_final = url_base + "pedidos/generar_pedidos_api";

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+token);
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "400";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println("here");
            System.out.println(res);
            if(res.has("status")){
                System.out.println("Si tiene");
                resultado = res.getString("errors");
            }else{
                System.out.println("no tiene");
                resultado = res.getString("result");
            }
            System.out.println(resultado);
        }else if(responseCode == 403){
            resultado = "Sesion Expirada";
        }
        return resultado;
    }
    
    public String cambiarPosicion(String pedido_id) throws Exception {

        String url_final = url_base + "pedidos/actualizar_posiciones_api?pedido_id="+pedido_id;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado = "400";
        if(responseCode == 200){
            System.out.println("\nSending 'GET' request to URL : " + url_final);
            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            System.out.println(res);
            resultado = res.getString("result");
        }
        return resultado;
    }
    
    public StringBuilder getEnvios(String seller, String orden_mktf, String num_orden, String paqueteria, String canal, Integer page, String status) throws Exception {
        System.out.println("params son");
        System.out.println(seller);
        System.out.println(orden_mktf);
        System.out.println(num_orden);
        System.out.println(paqueteria);
        System.out.println(canal);
        String url_final = url_base + "shipments/envios?seller_name="+seller+"&orden_mktf="+orden_mktf+"&numero_orden="+num_orden+"&carrier_name="+paqueteria+"&canal="+canal+"&page="+page+"&status_envio="+status;

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

        JSONObject res = new JSONObject(response.toString());
        String resultado = res.getString("envios");
        String res2[] = new String[1];
        res2[0] = resultado;
       
        return response;
    }
    
    public StringBuilder consultarPaquete(String no_paquete) throws Exception {

        String url_final = url_base + "pedidos/consultar_paquete_api?no_paquete="+no_paquete;

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
       
        return response;
    }
    
    public StringBuilder verificarGuiaPrepagada(String carrier, String order_id) throws Exception {

        String url_final = url_base + "pedidos/consultar_carrier_name_api?shopi_order_id="+order_id+"&carrier_name="+carrier;

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
       
        return response;
    }
    
    public StringBuilder consultarDireccion(String no_paquete) throws Exception {

        String url_final = url_base + "pedidos/consultar_direccion_api?no_paquete="+no_paquete;

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
       
        return response;
    }
}
