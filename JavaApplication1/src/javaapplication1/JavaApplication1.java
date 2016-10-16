/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;
import client_sock.Client_Sock;
import java.util.Random;
import com.espertech.esper.client.*;
import com.espertech.esper.event.map.MapEventBean;
import com.sun.corba.se.impl.orbutil.ObjectWriter;
import io.socket.IOAcknowledge;
import java.util.Date;
import org.apache.log4j.ConsoleAppender; 
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Level; 
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

import io.socket.emitter.Emitter;
import java.net.URISyntaxException;
import java.util.HashMap;





/**
 *
 * @author Umeriftikhar
 */
public class JavaApplication1 {

    //public static Client_Sock mySocket;

    public static Socket mSocket;
            
    public static void initiate_Connection(){
            try{
                mSocket = IO.socket("http://localhost:5000/");
                mSocket.connect();
                //mSocket.emit("event", "HELLLLLLLLLLLLLLO");
                
//////////////////Generating stream.
    Configuration cepConfig = new Configuration();
    cepConfig.addEventType("EventA", EventA.class.getName());
    cepConfig.addEventType("EventB", EventB.class.getName());
    EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig); 
    EPRuntime cepRT = cep.getEPRuntime(); 
    EPAdministrator cepAdm = cep.getEPAdministrator(); 
    
//EPStatement cepStatement = cepAdm.createEPL("select * from pattern [every a=EventA -> every  b=EventB(a.recipeID = b.recipeID AND a.percentageCompleted > 50) ].win:length(20)");  
EPStatement cepStatement = cepAdm.createEPL("select * from pattern [ (every a=EventA -> every  b=EventB) ]");  

        cepStatement.addListener(new CEPListener());
        
        // Receiving an object of EventA
        mSocket.on("EventA", new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            JSONObject obj = (JSONObject)args[0];
            //System.out.println("Server said: " + obj);
           
            try{
                
            
            String recipeId = obj.get("recipeId").toString();
            String percentage = obj.get("percentageCompleted").toString(); 
            float percentageCompleted = Float.parseFloat(percentage);
            
            createMyEvent(cepRT, recipeId, percentageCompleted );            
            
            }
            catch(Exception e){
                    e.printStackTrace();            
            }

            
          }
        });

        // Receiving an object of EventB
        mSocket.on("EventB", new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            JSONObject obj = (JSONObject)args[0];
            //System.out.println("Server said: " + obj);
           
            try{
                
            
            String recipeId = obj.get("recipeId").toString();
            String index = obj.get("indexOfrecipe").toString(); 
                       
            createMyEventB(cepRT, recipeId, index );            
            
            }
            catch(Exception e){
                    e.printStackTrace();            
            }

            
          }
        });        
        
        
//////////////////Generating stream.
                
                
            }
        

            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
                      
        

    }

    public static void createMyEvent(EPRuntime cepRT, String rId, float pcd){
            
        EventA tick = new EventA(rId, pcd );        
        System.out.println("Sending EventA: " + tick); 
        cepRT.sendEvent(tick);     
    
    }

    public static void createMyEventB(EPRuntime cepRT, String rId, String index){
            
        EventB tick = new EventB(rId, index );        
        System.out.println("Sending EventB: " + tick); 
        cepRT.sendEvent(tick);     
    
    }
    
        public static class EventA {
       
        float percentageCompleted;
        String recipeId;
        
        public EventA(String rId, float pcd ) {
         
            recipeId = rId;
            percentageCompleted = pcd;
            
        } 
        
        public String getRecipeID() {return recipeId;}
        public float getPercentageCompleted() {return percentageCompleted;}       
        @Override 
        public String toString() { return "recipeId: " + recipeId + ",  percentage: " + Float.toString(percentageCompleted); }     
    
    } 

        public static class EventB {
       
        String recipeId;
        String indexOfrecipe;
        
        public EventB(String rId, String ior ) {
         
            recipeId = rId;
            indexOfrecipe = ior;
            
        } 
        
        public String getRecipeID() {return recipeId;}
        public String getIndexOfRecipe() {return indexOfrecipe;}       
        @Override 
        public String toString() { return "recipeId: " + recipeId + ",  index: " + indexOfrecipe; }     
    
    }         
        
    public static class CEPListener implements UpdateListener {
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("-----------------------------------------------");
            //System.out.println("Event received: " + newData[0].getUnderlying());
            //System.out.println("Event received: " + newData);
            //System.out.println("WSSSSSSSSSSSSSS: " + newData[0].get("workst") );     
           System.out.println("Event received: " + newData.length);
            System.out.println("-----------------------------------------------");
            //System.out.println("Event received: " + newData[0].get("price"));
                            
            try {
                    
                int checkCount = 0;
                int valuesToAnalyse = 30;
                boolean checkReciped = false;
                boolean startProduction = true;
                    if(newData.length>valuesToAnalyse){
                        checkCount = newData.length - valuesToAnalyse;
                    }
                
              JSONObject jsonObjB = new JSONObject(newData[0].get("b"));
              
              //System.out.println( "Object B :" + jsonObjB);
              int indexOfRecipe = Integer.parseInt( jsonObjB.get("indexOfRecipe").toString() );                    
                    
            for(int count=checkCount;count<newData.length;count++){
            
              JSONObject jsonObj = new JSONObject(newData[count].get("a"));
              //jsonObj.get("percentageCompleted");
              //System.out.println("Separated event: " + jsonObj.get("recipeID") + "    " + jsonObj.get("percentageCompleted") );
              String currentRecipeId = jsonObj.get("recipeID").toString();              
              float percentageCompleted = Float.parseFloat(jsonObj.get("percentageCompleted").toString());
              
                     if(jsonObjB.get("recipeID").toString().equals(currentRecipeId) ){
                     
                                checkReciped = true;
                                
                                if( percentageCompleted < 20 ){
                                    startProduction = false;
                                    break;
                                }
                     
                     
                     }  
              
              
            }
            
            
            
                JSONObject myObj = new JSONObject();
                myObj.put("indexOfRecipe",indexOfRecipe);
                myObj.put("startProduction",startProduction);
                mSocket.emit("production", myObj);
                
                System.out.println("Check Status: " + startProduction );     
              
                
            }
            catch (Exception e){
                    e.printStackTrace();
            }
                    
                    
        } 
    } 
    public static void main(String[] args) throws InterruptedException {
      
        
        initiate_Connection();       

    
    
} 


    
    
    
    
    
}
