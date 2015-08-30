package com.team766.rrb4j;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogOutput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RRB4J{
	
    public static Pin LEFT_GO_PIN = RaspiPin.GPIO_17;
    public static Pin LEFT_DIR_PIN = RaspiPin.GPIO_04;
    public static Pin RIGHT_GO_PIN = RaspiPin.GPIO_10;
    public static Pin RIGHT_DIR_PIN = RaspiPin.GPIO_25;   
    public static Pin SW1_PIN = RaspiPin.GPIO_11;
    public static Pin SW2_PIN = RaspiPin.GPIO_09;
    public static Pin LED1_PIN = RaspiPin.GPIO_07;
    public static Pin LED2_PIN = RaspiPin.GPIO_08;
    public static Pin OC1_PIN = RaspiPin.GPIO_22;
    public static Pin OC2_PIN = RaspiPin.GPIO_27;
    public static Pin OC2_PIN_R1 = RaspiPin.GPIO_21;
    public static Pin OC2_PIN_R2 = RaspiPin.GPIO_27;
    public static Pin TRIGGER_PIN = RaspiPin.GPIO_18;
    public static Pin ECHO_PIN = RaspiPin.GPIO_23;
    
    GpioPinDigitalOutput led1_pin;
    GpioPinDigitalOutput left_go_pin;
    GpioPinAnalogOutput left_dir_pin;
    GpioPinDigitalOutput right_go_pin;
    GpioPinAnalogOutput right_dir_pin;   
    GpioPinDigitalOutput sw1_pin;
    GpioPinDigitalOutput sw2_pin;
    GpioPinDigitalOutput led2_pin;
    GpioPinDigitalOutput oc1_pin;
    GpioPinDigitalOutput oc2_pin;
    GpioPinDigitalOutput oc2_pin_r1;
    GpioPinDigitalOutput oc2_pin_r2;
    GpioPinDigitalOutput trigger_pin;
    GpioPinDigitalOutput echo_pin;
    
    public static GpioPinPwmOutput left_pwm;
    public static GpioPinPwmOutput right_pwm;
    
    final GpioController gpio = GpioFactory.getInstance();
    
    public RRB4J(){
    	this(2);
    	
    }
    
    public RRB4J(int revision){
        gpio.setMode(PinMode.BCM);
        gpio.setwarnings(false);

        Gpio.setup(LEFT_GO_PIN, Gpio.OUT);
        this.left_pwm = gpio.provisionPwmOutputPin(LEFT_GO_PIN, 500);
        this.left_pwm.start(0);
        Gpio.setup(this.LEFT_DIR_PIN, Gpio.OUT);
        Gpio.setup(this.RIGHT_GO_PIN, Gpio.OUT);
        this.right_pwm = Gpio.PWM(this.RIGHT_GO_PIN, 500);
        this.right_pwm.start(0);
        Gpio.setup(this.RIGHT_DIR_PIN, Gpio.OUT);

        Gpio.setup(this.LED1_PIN, Gpio.OUT);
        Gpio.setup(this.LED2_PIN, Gpio.OUT);

        Gpio.setup(this.OC1_PIN, Gpio.OUT);
        if(revision == 1)
            this.OC2_PIN = this.OC2_PIN_R1;
        else
            this.OC2_PIN = this.OC2_PIN_R2;

        Gpio.setup(this.OC2_PIN_R2, Gpio.OUT);

        Gpio.setup(this.SW1_PIN, Gpio.IN);
        Gpio.setup(this.SW2_PIN, Gpio.IN);
        Gpio.setup(this.TRIGGER_PIN, Gpio.OUT);
        Gpio.setup(this.ECHO_PIN, Gpio.IN);
        
        led1_pin = gpio.provisionDigitalOutputPin(LED1_PIN);
        left_go_pin = gpio.provisionDigitalOutputPin(LEFT_GO_PIN);
        left_dir_pin = gpio.provisionAnalogOutputPin(LEFT_DIR_PIN);
        right_go_pin = gpio.provisionDigitalOutputPin(RIGHT_GO_PIN);
        right_dir_pin = gpio.provisionAnalogOutputPin(RIGHT_DIR_PIN);   
        sw1_pin = gpio.provisionDigitalOutputPin(SW1_PIN);
        sw2_pin = gpio.provisionDigitalOutputPin(SW2_PIN);
        led2_pin = gpio.provisionDigitalOutputPin(LED2_PIN);
        oc1_pin = gpio.provisionDigitalOutputPin(OC1_PIN);
        oc2_pin = gpio.provisionDigitalOutputPin(OC2_PIN);
        oc2_pin_r1 = gpio.provisionDigitalOutputPin(OC2_PIN_R1);
        oc2_pin_r2 = gpio.provisionDigitalOutputPin(OC2_PIN_R2);
        trigger_pin = gpio.provisionDigitalOutputPin(TRIGGER_PIN);
        echo_pin = gpio.provisionDigitalOutputPin(ECHO_PIN);
    }



    public void set_motors(double left_go, double left_dir, double right_go, double right_dir){
        left_pwm.ChangeDutyCycle(left_go * 100);
        left_dir_pin.setValue(left_dir);
        this.right_pwm.ChangeDutyCycle(right_go * 100);
        right_dir_pin.setValue(right_dir);
    }
    
    public void forward(){
    	forward(0,0.5);
    }
    
    public void forward(int seconds, double speed){
        set_motors(speed, 0d, speed, 0d);
        if(seconds > 0){
        	try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            this.stop();
        }
     }

    public void stop(){
        set_motors(0d, 0d, 0d, 0d);
    }
    
    public void reverse(){
    	reverse(0,0.5);
    }
 
    public void reverse(int seconds, double speed){
        this.set_motors(speed, 1, speed, 1);
        if(seconds > 0){
        	try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            this.stop();
        }
    }
    
    public void left(){
    	left(0, 0.5);
    }
    
    public void left(int seconds, double speed){
        this.set_motors(speed, 0, speed, 1);
        if(seconds > 0){
        	try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            this.stop();
        }
    }
    
    public void right(){
    	right(0, 0.5);
    }

    public void right(int seconds, double speed){
        this.set_motors(speed, 1d, speed, 0d);
        if(seconds > 0){
        	try{
            Thread.sleep(seconds * 1000);
        	}catch(InterruptedException e){}
            this.stop();
        }
    }

    public boolean sw1_closed(){
        return Gpio.digitalRead(SW1_PIN) == 0;
    }

    public boolean sw2_closed(){
        return Gpio.digitalRead(SW2_PIN) == 0;
    }

    public void set_led1(boolean state){
        Gpio.digitalWrite(LED1_PIN, state);
    }

    public void set_led2(boolean state){
        Gpio.digitalWrite(LED2_PIN, state);
    }

    public void set_oc1(boolean state){
        Gpio.digitalWrite(OC1_PIN, state);
    }
    public void set_oc2(boolean state){
        Gpio.digitalWrite(OC2_PIN, state);    
    }

    private void _send_trigger_pulse(){
        Gpio.digitalWrite(TRIGGER_PIN, true);
        try{
            Thread.sleep(0, 100000);
        	}catch(InterruptedException e){}
        Gpio.digitalWrite(TRIGGER_PIN, false);
	}

    private void _wait_for_echo(int value, int timeout){
        int count = timeout;
        while(Gpio.digitalRead(ECHO_PIN) != value && count > 0)
            count--;
    }

    public float get_distance(){
        this._send_trigger_pulse();
        this._wait_for_echo(1, 10000);
        float start = System.nanoTime() * 1e-9f;
        this._wait_for_echo(0, 10000);
        float finish = System.nanoTime() * 1e-9f;
        float pulse_len = finish - start;
        float distance_cm = pulse_len / 0.000058f;
        return distance_cm;
    }
}