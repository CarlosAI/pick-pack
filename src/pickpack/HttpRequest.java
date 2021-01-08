/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;

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
    
    public StringBuilder getPedidos(String seller, String seller_sku, String num_order, String orden_mktf) throws Exception {

        String url_final = url_base + "pedidos/get_pedidos";
        String urlParameters = "?seller_name="+URLEncoder.encode(seller, "utf-8")+"&seller_sku="+URLEncoder.encode(seller_sku, "utf-8")+"&shopi_order_name="+URLEncoder.encode(num_order, "utf-8")+"&shopi_order_id="+URLEncoder.encode(orden_mktf, "utf-8");
        url_final = url_final + urlParameters;
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

        String url_final = url_base + "pedidos/verificar_seller_sku_pedido_api?seller_sku="+ URLEncoder.encode(sku, "utf-8")+"&pedido_id="+pedido_id;

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
        String url_final = url_base + "pedidos/registrar_consolidado_api?consolidado_id="+consolidado_id+"&pedido_id="+pedido_id;
        System.out.println(url_final);
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
    
    public StringBuilder consultarBoxes() throws Exception {

        String url_final = url_base + "pedidos/consultar_boxes";

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
    
    public StringBuilder consultarBox(String box_name) throws Exception {

        String url_final = url_base + "pedidos/consultar_box_api?box_name="+ URLEncoder.encode(box_name, "utf-8");

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
    
    public StringBuilder consultarRates(String no_paquete, String carrier) throws Exception {

        String url_final = url_base + "pedidos/consultar_rates_api?no_paquete="+ URLEncoder.encode(no_paquete, "utf-8") + "&paqueteria="+URLEncoder.encode(carrier, "utf-8");

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
    
    public StringBuilder cotizarPaqex(String largo, String ancho, String alto, String tipo_paquete, String peso, String estado, String colonia, String calle, String no_ext, String no_int, String telefono, String codigo_postal, String destinatario, String email, String municipio, String asegurado) throws Exception {

        String url_final = url_base + "labels/cotizar_guia_paquex_api";
        
        String urlParameters = "?largo="+URLEncoder.encode(largo, "utf-8")+"&ancho="+URLEncoder.encode(ancho, "utf-8")+"&alto="+URLEncoder.encode(alto, "utf-8")+"&tipo_paquete="+URLEncoder.encode(tipo_paquete, "utf-8")+"&peso="+URLEncoder.encode(peso, "utf-8")+"&estado="+URLEncoder.encode(estado, "utf-8")+"&colonia="+URLEncoder.encode(colonia, "utf-8")+"&calle="+URLEncoder.encode(calle, "utf-8")+"&no_ext="+URLEncoder.encode(no_ext, "utf-8")+"&no_int="+URLEncoder.encode(no_int, "utf-8")+"&telefono="+URLEncoder.encode(telefono, "utf-8")+"&codigo_postal="+URLEncoder.encode(codigo_postal, "utf-8")+"&destinatario="+URLEncoder.encode(destinatario, "utf-8")+"&email="+URLEncoder.encode(email, "utf-8")+"&municipio="+URLEncoder.encode(municipio, "utf-8")+"&asegurado="+URLEncoder.encode(asegurado, "utf-8");
        
        url_final = url_final + urlParameters;
        
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
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
       
        return response;
    }
    
    public StringBuilder crearguiaPaquex(String largo, String ancho, String alto, String tipo_paquete, String peso, String estado, String colonia, String calle, String no_ext, String no_int, String telefono, String codigo_postal, String destinatario, String email, String municipio, String asegurado, String order_id) throws Exception {

        String url_final = url_base + "labels/crear_guia_paquex_api";
        
        String urlParameters = "?largo="+URLEncoder.encode(largo, "utf-8")+"&ancho="+URLEncoder.encode(ancho, "utf-8")+"&alto="+URLEncoder.encode(alto, "utf-8")+"&tipo_paquete="+URLEncoder.encode(tipo_paquete, "utf-8")+"&peso="+URLEncoder.encode(peso, "utf-8")+"&estado="+URLEncoder.encode(estado, "utf-8")+"&colonia="+URLEncoder.encode(colonia, "utf-8")+"&calle="+URLEncoder.encode(calle, "utf-8")+"&no_ext="+URLEncoder.encode(no_ext, "utf-8")+"&no_int="+URLEncoder.encode(no_int, "utf-8")+"&telefono="+URLEncoder.encode(telefono, "utf-8")+"&codigo_postal="+URLEncoder.encode(codigo_postal, "utf-8")+"&destinatario="+URLEncoder.encode(destinatario, "utf-8")+"&email="+URLEncoder.encode(email, "utf-8")+"&municipio="+URLEncoder.encode(municipio, "utf-8")+"&asegurado="+URLEncoder.encode(asegurado, "utf-8")+"&orden="+URLEncoder.encode(order_id, "utf-8");
        
        url_final = url_final + urlParameters;
        
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
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
       
        return response;
    }
    
    public String[] crearguiaAmpm(String largo, String ancho, String alto, String tipo_paquete, String peso, String estado, String colonia, String calle, String noexterior, String nointerior, String telefono, String codigo_postal, String destinatario, String email, String municipio, String order_id, String token) throws Exception {

        String url_final = url_base + "labels/crear_guia_ampm_api";
        
        String urlParameters = "?largo="+URLEncoder.encode(largo, "utf-8")+"&ancho="+URLEncoder.encode(ancho, "utf-8")+"&alto="+URLEncoder.encode(alto, "utf-8")+"&tipo_paquete="+URLEncoder.encode(tipo_paquete, "utf-8")+"&peso="+URLEncoder.encode(peso, "utf-8")+"&estado="+URLEncoder.encode(estado, "utf-8")+"&colonia="+URLEncoder.encode(colonia, "utf-8")+"&calle="+URLEncoder.encode(calle, "utf-8")+"&noexterior="+URLEncoder.encode(noexterior, "utf-8")+"&nointerior="+URLEncoder.encode(nointerior, "utf-8")+"&telefono="+URLEncoder.encode(telefono, "utf-8")+"&codigo_postal="+URLEncoder.encode(codigo_postal, "utf-8")+"&contacto="+URLEncoder.encode(destinatario, "utf-8")+"&email="+URLEncoder.encode(email, "utf-8")+"&ciudad="+URLEncoder.encode(municipio, "utf-8")+"&orden="+URLEncoder.encode(order_id, "utf-8");
        
        url_final = url_final + urlParameters;
        
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        con.setRequestProperty("Authorization", "Bearer "+token);
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        String resultado[] = new String[5];
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
                resultado[0] = "400";
                resultado[1] = res.getString("errors");
            }else{
                System.out.println("no tiene");
                JSONArray resCrearGuia = res.getJSONArray("result");
                if(resCrearGuia.get(0).toString().equals("200")){
                    resultado[0] = "200";
                    resultado[1] = resCrearGuia.get(1).toString();
                    resultado[2] = resCrearGuia.get(2).toString();
                    resultado[3] = resCrearGuia.get(3).toString();
                    resultado[4] = resCrearGuia.get(4).toString();
                }else{
                    resultado[0] = "400";
                    resultado[1] = resCrearGuia.get(1).toString();
                }
            }
            System.out.println(resultado);
        }else if(responseCode == 403){
            resultado[0] = "400";
            resultado[1] = "Sesion Expirada";
        }
       
        return resultado;
    }
    
    public String guardarGuia(String tipo_envio, String shopi_order_id, String num_guia, String carrier_name, String peso, String alto, String ancho, String largo, Boolean paqueteria_diferente, String file_name, String token) throws Exception {
        String paq_dif = "false";
        if(paqueteria_diferente){
            paq_dif = "true";
        }
        String url_final = url_base + "shopi_orders/guardar_guia_api";
        
        String urlParameters = "?tipo_envio="+URLEncoder.encode(tipo_envio, "utf-8")+"&largo="+URLEncoder.encode(largo, "utf-8")+"&ancho="+URLEncoder.encode(ancho, "utf-8")+"&alto="+URLEncoder.encode(alto, "utf-8")+"&shopi_order_id="+URLEncoder.encode(shopi_order_id, "utf-8")+"&peso="+URLEncoder.encode(peso, "utf-8")+"&num_guia="+URLEncoder.encode(num_guia, "utf-8")+"&carrier_name="+URLEncoder.encode(carrier_name, "utf-8")+"&paqueteria_diferente="+URLEncoder.encode(paq_dif, "utf-8")+"&file_name="+URLEncoder.encode(file_name, "utf-8");
        url_final = url_final + urlParameters;
        
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
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
                JSONArray resCrearGuia = res.getJSONArray("result");
                if(resCrearGuia.get(0).toString().equals("200")){
                    resultado = "200";
                }else{
                    resultado = resCrearGuia.get(1).toString();
                }
            }
            System.out.println(resultado);
        }else if(responseCode == 403){
            resultado = "Sesion Expirada";
        }
        return resultado;
    }
    
    public StringBuilder cotizarFedEx(String tipo_envio, String largo, String ancho, String alto, String peso, String estado, String telefono, String codigo_postal, String destinatario, String line_1, String line_2, String ciudad, String orden_id, String paqueteria) throws Exception {
        
        String url_final = url_base + "labels/cotizar_guia_fedex_api";
        if(paqueteria.equals("Estafeta")){
            url_final = url_base + "labels/cotizar_guia_estafeta_api";
        }
        
        String urlParameters = "?tipo_envio="+tipo_envio+"&largo="+URLEncoder.encode(largo, "utf-8")+"&ancho="+URLEncoder.encode(ancho, "utf-8")+"&alto="+URLEncoder.encode(alto, "utf-8")+"&peso="+URLEncoder.encode(peso, "utf-8")+"&estado="+URLEncoder.encode(estado, "utf-8")+"&telefono="+URLEncoder.encode(telefono, "utf-8")+"&codigo_postal="+URLEncoder.encode(codigo_postal, "utf-8")+"&destinatario="+URLEncoder.encode(destinatario, "utf-8")+"&line_1="+URLEncoder.encode(line_1, "utf-8")+"&line_2="+URLEncoder.encode(line_2, "utf-8")+"&ciudad="+URLEncoder.encode(ciudad, "utf-8")+"&orden_id="+URLEncoder.encode(orden_id, "utf-8");
        
        url_final = url_final + urlParameters;
        
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        URL obj = new URL(url_final);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json"); 
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
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
       
        return response;
    }
    
    public StringBuilder crearGuiaFedex(String rate_id, String order_id, String paqueteria) throws Exception {

        String url_final = url_base + "labels/crear_guia_fedex_api?fedex_rate_id="+rate_id+"&orden="+order_id;
        if(paqueteria.equals("Estafeta")){
            url_final = url_base + "labels/crear_guia_estafeta_api?fedex_rate_id="+rate_id+"&orden="+order_id;
        }
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
       
        return response;
    }
    
    public StringBuilder verificarGuiaRepetida(String shipment_id) throws Exception {

        String url_final = url_base + "shipments/verificar_guia_repetida_api?shipment_id="+shipment_id;

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
    
    public String guardarGuia(String shipment_id, String token) throws Exception {

        String url_final = url_base + "shipments/cancelar_guia_api?shipment_id="+shipment_id;

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
                JSONArray resCrearGuia = res.getJSONArray("result");
                if(resCrearGuia.get(0).toString().equals("200")){
                    resultado = resCrearGuia.get(1).toString();
                }else{
                    resultado = resCrearGuia.get(1).toString();
                }
            }
            System.out.println(resultado);
        }else if(responseCode == 403){
            resultado = "Sesion Expirada";
        }
        return resultado;
    }
}
