/*
 *  Copyright [2014] [Dimitrij Vukelic]
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

import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DigitalGPIO  {

    private final int pin;
    private int value;
    private final String path;
    private String direction;
    private BufferedWriter bw;
    private Scanner s;
    private boolean success;
    private boolean state = false;
    private final ExceptionInInitializerError ePin;
    private final ExceptionInInitializerError eDirection;
    private final Timer read = new Timer(true);
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    public static final String PROP_VALUE = "DIGITAL_VALUE";

    /**
     * Define digital GPIO pin, number and direction ( input or output)
     *
     * @param pin pin number
     * @param direction DigitalGPIO.OUTPUT or DigitalGPIO.INPUT
     * @throws java.io.IOException
     */
    
    public DigitalGPIO(int pin, String direction) throws ExceptionInInitializerError, IOException {
        this.ePin = new ExceptionInInitializerError("Wrong pin number");
        this.eDirection = new ExceptionInInitializerError("Wrong use of direction");
        this.success = false;
        this.value = 0;
        this.bw = null;
        this.s = null;
        this.path = "/sys/class/gpio";

        if (pin < 0 || pin > 60) {
            throw ePin;
        } else {
            this.pin = pin;
        }

        if (direction.equalsIgnoreCase("input")) {
            this.direction = "in";
        } else if (direction.equalsIgnoreCase("output")) {
            this.direction = "out";
        } else {
            throw eDirection;
        }
        activatePIN();

    }

    /**
     * Define digital GPIO pin, number and direction ( input or output)
     *
     * @param pin pin number - PINXX
     * @param direction OUTPUT or INPUT
     * @param state HIGH-LOW , true-false, ON-OFF
     * @throws java.io.IOException
     */
    public DigitalGPIO(int pin, String direction, boolean state) throws ExceptionInInitializerError, IOException {
        this.ePin = new ExceptionInInitializerError("Wrong pin number");
        this.eDirection = new ExceptionInInitializerError("Wrong use of direction");
        this.success = false;
        this.state = state;
        this.value = 0;
        this.bw = null;
        this.s = null;
        this.path = "/sys/class/gpio";

        if (pin < 0 || pin > 60) {
            throw ePin;
        } else {
            this.pin = pin;
        }

        if (direction.equalsIgnoreCase("input")) {
            this.direction = "in";
        } else if (direction.equalsIgnoreCase("output")) {
            this.direction = "out";
        } else {
            throw eDirection;
        }
        activatePIN();

    }

    /**
     * Activate pin
     *
     * @throws IOException
     * @see deactivatePin()
     */
    public void activatePIN() throws IOException {

        try {
            this.bw = new BufferedWriter(new FileWriter(path + "/export"));
            this.bw.write(String.valueOf(pin));
            this.bw.newLine();
            this.bw.close();

        } catch (IOException ex) {

        }
        this.bw = new BufferedWriter(new FileWriter(path + "/gpio" + this.pin + "/direction"));
        this.bw.write(direction);
        this.bw.newLine();
        this.bw.close();

        digitalWrite(this.state);
    }

    /**
     * Deactivate pin
     *
     * @throws IOException
     * @see activatePin()
     */
    public void deactivatePIN() throws IOException {
        this.bw = new BufferedWriter(new FileWriter(path + "/unexport"));
        this.bw.write(String.valueOf(this.pin));
        this.bw.newLine();
        this.bw.close();
    }

    /**
     * Pool GPIO file modified, and write value use method isHigh() or isLow()
     * for reading value
     *
     * @param time time in millis
     * @see isHigh()
     * @see isLow()
     */
   public void pool(long time) {
                
               read.scheduleAtFixedRate(new TimerTask() {
                   @Override
                   public void run() {
                       try {
                          
                           digitalRead();
   
                       } catch (IOException ex) {
                           Logger.getLogger(AnalogGPIO.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
               }, 0, time);
            
    }

    /**
     * set GPIO high (ON). Use if direction is set to OUT.
     *
     * @return true if success
     * @see setLOW()
     */
    public boolean setHIGH() {
        this.success = false;
        this.setValue(1);
        try {
            this.bw = new BufferedWriter(new FileWriter(path + "/gpio" + this.pin + "/value"));
            this.bw.write(String.valueOf(getValue()));
            this.bw.newLine();
            this.bw.close();
            this.success = true;
        } catch (IOException ex) {
            Logger.getLogger(DigitalGPIO.class.getName()).log(Level.SEVERE, null, ex);
            this.success = false;
        }

        return this.success;
    }

    /**
     * set GPIO low (OFF). Use if direction is set to OUT.
     *
     * @return true if success
     * @see setHIGH()
     */
    public boolean setLOW() {
        this.success = false;
        this.setValue(0);
        try {
            this.bw = new BufferedWriter(new FileWriter(path + "/gpio" + this.pin + "/value"));
            this.bw.write(String.valueOf(getValue()));
            this.bw.newLine();
            this.bw.close();
            this.success = true;
        } catch (IOException ex) {
            Logger.getLogger(DigitalGPIO.class.getName()).log(Level.SEVERE, null, ex);
            this.success = false;
        }

        return this.success;
    }

    /**
     * Set GPIO state true or false. Use is direction is OUT
     *
     * @param state boolean
     * @see setHIGH()
     * @see setLOW()
     */
    public void digitalWrite(boolean state) {
        this.state = state;

        if (this.state) {
            setHIGH();
        } else {
            setLOW();
        }
    }

    /**
     * TOGGLE digital GPIO value. Use if direction is set to OUT
     *
     * @return integer value of pin (0 or 1)
     * @throws java.io.IOException
     * @see setHIGH()
     * @see setLOW()
     */
    public boolean setTOGGLE() throws IOException {
        this.success = false;
        if (isHigh()) {
            setLOW();
            return this.getState();

        } else {
            setHIGH();
            return this.getState();
        }
    }

    /**
     * Check if value is HIGH. Use if direction is set to IN.
     *
     * @return TRUE if input is HIGH, and FALSE if input is LOW
     * @throws IOException
     */
    public boolean isHigh() throws IOException {

        return getValue() == 1;
    }

    /**
     * Check if value is LOW. Use if direction is set to IN.
     *
     * @return TRUE if input is LOW, and FALSE if input is HIGH
     * @throws IOException
     */
    public boolean isLow() throws IOException {

        return getValue() == 0;
    }

    /**
     * read digital GPIO value
     *
     * @return integer value of pin
     * @throws FileNotFoundException
     * @throws IOException
     */
    public int digitalRead() throws FileNotFoundException, IOException {
        this.s = new Scanner(new FileReader(path + "/gpio" + this.pin + "/value"));
        this.setValue(s.nextInt());
        return this.getValue();
    }

    /**
     * Get digital GPIO state
     *
     * @return boolean value of pin
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean getState() throws FileNotFoundException, IOException {
        this.s = new Scanner(new FileReader(path + "/gpio" + this.pin + "/value"));
        return s.nextInt() != 0;

    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        int oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
    }

}
