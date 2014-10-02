/*
 * Copyright (c) 2014 Dimitrij Vukelić
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vukelic.java4black.trunk;

import com.vukelic.java4black.gpio.AnalogGPIO;
import com.vukelic.java4black.gpio.GPIO;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mitja
 */
public class AnalogPool_example extends GPIO implements PropertyChangeListener {

    //PropertyChangeEvent pce = new PropertyChangeEvent(this, INPUT, this, temperature);
    AnalogGPIO temperature;
   // Console console = System.console();
    //read user name, using java.util.Formatter syntax :
    //String username = console.readLine("User Name? ");

    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new AnalogPool_example();
        while (HIGH) {            
            
        }
    }

    public AnalogPool_example() {
        Properties prop = new Properties();
        InputStream load;
        try {
            load = new FileInputStream("config.properties");
            prop.load(load);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AnalogPool_example.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AnalogPool_example.class.getName()).log(Level.SEVERE, null, ex);
        }        

        temperature = new AnalogGPIO(AIN0);
        temperature.devideValue(10);
        temperature.pool(Integer.valueOf(prop.getProperty("analogPool")));
        temperature.addPropertyChangeListener(this);
       
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(temperature.getAdcValue());

    }

}
