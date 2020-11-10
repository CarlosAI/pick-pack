/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickpack;

/**
 *
 * @author user
 */
public class Sesion {
    private String nombre;
    private String token;
    private Integer user_id;
    
    public Sesion(){
    }
    
    public Sesion(String nombre, String token, Integer user_id){
        this.nombre = nombre;
        this.token = token;
        this.user_id = user_id;
    }
    
    public String getSessionToken(){
        return this.token;
    }
    
    public String getSessionUserName(){
        return this.nombre;
    }
    
    public Integer getSessionUserId(){
        return this.user_id;
    }
    
    public void setSessionToken(String token){
        this.token = token;
    }
    
    public void setSessionNombre(String nombre){
        this.nombre = nombre;
    }
    
    public void setSessionUserId(Integer user_id){
        this.user_id = user_id;
    }
}
