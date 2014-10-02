/*
*  Copyright [2014] [Dimitrij Vukelić]
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  
*       You may obtain a copy of the License at
* 
*       http://www.apache.org/licenses/LICENSE-2.0
* 
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License. 
*/

package com.vukelic.java4black.gpio;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AnalogGPIO  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6742322737388552680L;

	public static final String PROP_ADCVALUE = "ANALOG_VALUE";
    
    //private final String path = "/sys/devices/ocp.*/helper.*/";
    private final String path = "/sys/bus/iio/devices/iio:device0/";
    private final int pin;
    private float adcValue = 0;
    private int minTr,maxTr;
    private int out_min,out_max;
    private int divider;
    private boolean remap = false;
    private boolean devide = false;
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    private final Timer read = new Timer(true);
   
    /**
     * Analog input 
     * @param pin AnalogGPIO.(AIN0-7)
     */
    public AnalogGPIO (short pin)
    {
        
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter("/sys/devices/bone_capemgr.*/slots")); 
            wr.write("cape-bone-iio");
            wr.newLine();
            wr.close();
        } catch (IOException e) {
        }
        this.pin = pin; 
       
    }
    
    /**
     * 
     * @param time 
     */
    public void pool(final long time) {
     
              
       Thread thread = new Thread(new TimerTask() {

           @Override
           public void run() {
               
                read.scheduleAtFixedRate(new TimerTask() {
                   @Override
                   public void run() {
                       try {
                          // Logger.getLogger(AnalogRead_Example.class.getName()).log(Level.INFO, null,"obavio " +a );
                           analogRead();
                           //a++;
                           
                       } catch (IOException | InterruptedException ex) {
                           Logger.getLogger(AnalogGPIO.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
               }, 0, time);
               
           }
       });thread.setDaemon(true);
          thread.start();
            
    }

    
    /**
     * Read analog pin value 
     * @return float
     * @throws FileNotFoundException
     * @throws IOException 
     * @throws java.lang.InterruptedException 
     */
    public float analogRead() throws FileNotFoundException, IOException, InterruptedException{
       
        float tempAdc = 0;
        Scanner s = new Scanner(new FileReader(path+"in_voltage"+pin+"_raw"));
        while(s.hasNext()){
            tempAdc = s.nextInt();  
        }

         tempAdc = (float) (tempAdc * 0.439453);
                
        if(remap){
            this.setAdcValue(Float.valueOf(String.format("%.2f", (tempAdc - 0) * (out_max - out_min) / (1799 - 0) + out_min)));
            s.close();
            return getAdcValue();
           // return 1;
        }
        else if (devide){
            this.setAdcValue(Float.valueOf(String.format("%.2f", tempAdc / divider)));
            s.close();
            return getAdcValue();
            
            //return 2;
        }
        else{
            this.setAdcValue(Float.valueOf(String.format("%.2f", tempAdc)));
            s.close();
            return  getAdcValue();
           // return 3;
        }
    }
    
    /**
     * Scale adc value
     * @param out_min minimum output value
     * @param out_max maximum output value
     */
    public void scaleValue(int out_min,int out_max){
       remap = true;
       this.out_min = out_min;
       this.out_max = out_max;  
    }
    
    /**
     * divide adc value
     * @param divider 
     */
    public void devideValue(int divider){
        devide = true;
        this.divider = divider;
    }
    
    /**
     * Setting adc threshold value for alarm - edge both
     * @param minTr minimum threshold 
     * @param maxTr 
     */
    public void setThreshold(int minTr,int maxTr){
        this.minTr = minTr;
        this.maxTr = maxTr;  
    }
    
    /**
     * Set edge for alarm
     * @param tr threshold value
     * @param edge low|high
     */
    public void setEdge(int tr,String edge){
        if(edge.equalsIgnoreCase("low")){
         this.minTr = tr;    
        }
        else if(edge.equalsIgnoreCase("high")){
         this.maxTr = tr;    
        } 
    }

    /**
     * @return the adcValue
     */
    public float getAdcValue() {
        return adcValue;
    }

    /**
     * @param adcValue the adcValue to set
     */
    public void setAdcValue(float adcValue) {
        float oldAdcValue = this.adcValue;
        this.adcValue = adcValue;
        propertyChangeSupport.firePropertyChange(PROP_ADCVALUE, oldAdcValue, adcValue);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Alarm for threshold or edge
     * @param edge low|high|both
     * @return 1 if true, 0 if false, -1 if error
     * @throws IOException 
     */
    public int getAlarm(String edge) throws IOException, Exception{
        Exception wrongInit;
        wrongInit = new Exception("Wrong direction");
        if(edge.equalsIgnoreCase("low")){
            return analogRead() < minTr ? 1:0;   
        }
        else if(edge.equalsIgnoreCase("high")){
         return analogRead() > maxTr ? 1:0;    
        } 
        else if(edge.equalsIgnoreCase("both")){
            return analogRead() < minTr || analogRead() > maxTr ? 1:0;
        }
        else{
        throw wrongInit;
        }
    }
}
