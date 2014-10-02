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
public class DigitalPool_example extends GPIO{

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args)  throws IOException, InterruptedException {
        // TODO code application logic here
        DigitalGPIO greenLed = new DigitalGPIO(PIN7,OUTPUT,LOW);      
        greenLed.pool(1000);
        
        while (true) {            
            if(greenLed.isHigh()){
                System.out.println("LED is HIGH");
                greenLed.setLOW();
            }else{
                System.out.println("LED is LOW");
                greenLed.setHIGH();
            }
           Thread.sleep(2000);
        }
    }
    
}
