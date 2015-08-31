package com.team766.rrb4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogOutput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
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
    
    GpioPinDigitalInput sw1_pin;
    GpioPinDigitalInput sw2_pin;
    GpioPinDigitalInput echo_pin;
    
    GpioPinDigitalOutput led1_pin;
    GpioPinDigitalOutput left_go_pin;
    GpioPinDigitalOutput right_go_pin;
    GpioPinDigitalOutput led2_pin;
    GpioPinDigitalOutput oc1_pin;
    GpioPinDigitalOutput oc2_pin;
    GpioPinDigitalOutput oc2_pin_r1;
    GpioPinDigitalOutput oc2_pin_r2;
    GpioPinDigitalOutput trigger_pin;
    
    GpioPinAnalogOutput left_dir_pin;
    GpioPinAnalogOutput right_dir_pin;   
    
    public static GpioPinPwmOutput left_pwm;
    public static GpioPinPwmOutput right_pwm;
    
    final GpioController gpio = GpioFactory.getInstance();
    
    public RRB4J(){
    	this(2);
    	
    }
    
    public RRB4J(int revision){
        left_pwm = gpio.provisionPwmOutputPin(LEFT_GO_PIN, 0);

        right_pwm = gpio.provisionPwmOutputPin(RIGHT_GO_PIN, 0);

        if(revision == 1)
            OC2_PIN = OC2_PIN_R1;
        else
            OC2_PIN = OC2_PIN_R2;
        
        led1_pin = gpio.provisionDigitalOutputPin(LED1_PIN);
        left_go_pin = gpio.provisionDigitalOutputPin(LEFT_GO_PIN);
        left_dir_pin = gpio.provisionAnalogOutputPin(LEFT_DIR_PIN);
        right_go_pin = gpio.provisionDigitalOutputPin(RIGHT_GO_PIN);
        right_dir_pin = gpio.provisionAnalogOutputPin(RIGHT_DIR_PIN);   
        sw1_pin = gpio.provisionDigitalInputPin(SW1_PIN);
        sw2_pin = gpio.provisionDigitalInputPin(SW2_PIN);
        led2_pin = gpio.provisionDigitalOutputPin(LED2_PIN);
        oc1_pin = gpio.provisionDigitalOutputPin(OC1_PIN);
        oc2_pin = gpio.provisionDigitalOutputPin(OC2_PIN);
        oc2_pin_r1 = gpio.provisionDigitalOutputPin(OC2_PIN_R1);
        oc2_pin_r2 = gpio.provisionDigitalOutputPin(OC2_PIN_R2);
        trigger_pin = gpio.provisionDigitalOutputPin(TRIGGER_PIN);
        echo_pin = gpio.provisionDigitalInputPin(ECHO_PIN);
    }
    
    public void set_motors(double left, double right){
        setLeftMotor(left);
        setRightMotor(right);
    }
    
    public void setLeftMotor(double left){
        left_dir_pin.setValue(left);
    }
    
    public void setRightMotor(double right){
        right_dir_pin.setValue(right);
    }
    
    public void forward(){
    	forward(0,0.5);
    }
    
    public void forward(double speed){
    	forward(0,speed);
    }
    
    public void forward(int seconds, double speed){
        set_motors(speed, speed);
        if(seconds > 0){
        	try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            stop();
        }
     }

    public void stop(){
        set_motors(0d, 0d);
    }
    
    public void reverse(){
    	reverse(0,0.5);
    }
    
    public void reverse(int speed){
    	reverse(0,speed);
    }
 
    public void reverse(int seconds, double speed){
        set_motors(speed, speed);
        if(seconds > 0){
        	try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            stop();
        }
    }
    
    public void left(){
    	left(0, 0.5);
    }
    
    public void left(int seconds, double speed){
        set_motors(0, speed);
        if(seconds > 0){
        	try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            stop();
        }
    }
    
    public void right(){
    	right(0, 0.5);
    }

    public void right(int seconds, double speed){
        set_motors(speed, 0d);
        if(seconds > 0){
        	try{
            Thread.sleep(seconds * 1000);
        	}catch(InterruptedException e){}
            stop();
        }
    }

    public boolean sw1_closed(){
        return sw1_pin.isLow();
    }

    public boolean sw2_closed(){
    	return sw2_pin.isLow();
    }

    public void set_led1(boolean state){
        led1_pin.setState(state);
    }

    public void set_led2(boolean state){
    	led2_pin.setState(state);
    }

    public void set_oc1(boolean state){
        oc1_pin.setState(state);
    }
    public void set_oc2(boolean state){
    	oc2_pin.setState(state);
    }

    private void _send_trigger_pulse(){
        trigger_pin.setState(true);
        try{
            Thread.sleep(0, 100000);
        }catch(InterruptedException e){}
        
        trigger_pin.setState(false);
	}

    private void _wait_for_echo(boolean high, int timeout){
        int count = timeout;
        while(echo_pin.isHigh() != high && count > 0)
            count--;
    }

    public float get_distance(){
        _send_trigger_pulse();
        _wait_for_echo(true, 10000);
        float start = System.nanoTime() * 1e-9f;
        _wait_for_echo(false, 10000);
        float finish = System.nanoTime() * 1e-9f;
        return (finish - start) / 0.000058f;
    }
}