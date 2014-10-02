/*
 * Copyright 2014 Mitja.
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

import com.vukelic.java4black.gpio.DigitalGPIO;
import com.vukelic.java4black.gpio.GPIO;
import java.io.IOException;

/**
 *
 * @author Mitja
 */
public class DigitalWrite_example extends GPIO{
     
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws ExceptionInInitializerError, IOException, InterruptedException {
        // TODO code application logic here
        
        DigitalGPIO greenLed = new DigitalGPIO(PIN7,OUTPUT,LOW);
        DigitalGPIO redLed = new DigitalGPIO(PIN20,OUTPUT,HIGH);
        
        while (true) {
            greenLed.setTOGGLE();
            redLed.setTOGGLE();
            
            System.out.println("Green LED state = " + greenLed.digitalRead());
            System.out.println("Red LED state = " + redLed.digitalRead());
            
            Thread.sleep(4000);
        }
    }

    
    
}
