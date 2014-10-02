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

import com.vukelic.java4black.gpio.AnalogGPIO;
import com.vukelic.java4black.gpio.GPIO;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Mitja
 */
public class AnalogRead_Example extends GPIO{

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter("/sys/devices/bone_capemgr.*/slots")); 
            wr.write("cape-bone-iio");
            wr.newLine();
            wr.close();
        } catch (Exception e) {
        }
        
       
        AnalogGPIO analog1 = new AnalogGPIO(AIN0);
        AnalogGPIO analog2 = new AnalogGPIO(AIN1);
        analog1.devideValue(10);
        
        while (true) {            
            System.out.println("Analog 1 = " + analog1.analogRead());
            System.out.println("Analog 2 = " + analog2.analogRead());
            Thread.sleep(4000);
        }
        
        
    }
    
}
