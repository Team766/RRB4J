package com.team766.rrb4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

public class RRB4J{
    
    //public static Pin LEFT_GO_PIN = RaspiPin.GPIO_00;
    public static int LEFT_GO_PIN = 0;
    public static Pin LEFT_DIR_PIN = RaspiPin.GPIO_07;
    //public static Pin RIGHT_GO_PIN = RaspiPin.GPIO_12;
    public static int RIGHT_GO_PIN = 12;
    public static Pin RIGHT_DIR_PIN = RaspiPin.GPIO_06;
    public static Pin SW1_PIN = RaspiPin.GPIO_14;
    public static Pin SW2_PIN = RaspiPin.GPIO_13;
    public static Pin LED1_PIN = RaspiPin.GPIO_11;
    public static Pin LED2_PIN = RaspiPin.GPIO_10;
    public static Pin OC1_PIN = RaspiPin.GPIO_03;
    public static Pin OC2_PIN = RaspiPin.GPIO_02;
    public static Pin OC2_PIN_R1 = RaspiPin.GPIO_29;
    public static Pin OC2_PIN_R2 = RaspiPin.GPIO_02;
    public static Pin TRIGGER_PIN = RaspiPin.GPIO_01;
    public static Pin ECHO_PIN = RaspiPin.GPIO_04;
    
    GpioPinDigitalInput sw1_pin;
    GpioPinDigitalInput sw2_pin;
    GpioPinDigitalInput echo_pin;
    
    GpioPinDigitalOutput led1_pin;
    GpioPinDigitalOutput led2_pin;
    GpioPinDigitalOutput oc1_pin;
    GpioPinDigitalOutput oc2_pin;
    GpioPinDigitalOutput trigger_pin;
    GpioPinDigitalOutput left_dir_pin;
    GpioPinDigitalOutput right_dir_pin;   
    
    //GpioPinPwmOutput left_pwm;
    //GpioPinPwmOutput right_pwm;
    
    final GpioController gpio = GpioFactory.getInstance();
    
    public RRB4J(){
        this(2);
        
    }
    
    public RRB4J(int revision){
        // initialize wiringPi library, this is needed for PWM
        Gpio.wiringPiSetup();

        //left_pwm = gpio.provisionPwmOutputPin(LEFT_GO_PIN, 0);
        //right_pwm = gpio.provisionPwmOutputPin(RIGHT_GO_PIN, 0);
        SoftPwm.softPwmCreate(LEFT_GO_PIN, 0, 100);
        SoftPwm.softPwmCreate(RIGHT_GO_PIN, 0, 100);

        if(revision == 1)
            OC2_PIN = OC2_PIN_R1;
        else
            OC2_PIN = OC2_PIN_R2;
        
        led1_pin = gpio.provisionDigitalOutputPin(LED1_PIN);
        left_dir_pin = gpio.provisionDigitalOutputPin(LEFT_DIR_PIN);
        right_dir_pin = gpio.provisionDigitalOutputPin(RIGHT_DIR_PIN);   
        led2_pin = gpio.provisionDigitalOutputPin(LED2_PIN);
        oc1_pin = gpio.provisionDigitalOutputPin(OC1_PIN);
        oc2_pin = gpio.provisionDigitalOutputPin(OC2_PIN);
        trigger_pin = gpio.provisionDigitalOutputPin(TRIGGER_PIN);
        
        sw1_pin = gpio.provisionDigitalInputPin(SW1_PIN);
        sw2_pin = gpio.provisionDigitalInputPin(SW2_PIN);
        echo_pin = gpio.provisionDigitalInputPin(ECHO_PIN);
    }
    
    public void set_motors(int left, boolean lForward, int right, boolean rForward){
        setLeftMotor(left, lForward);
        setRightMotor(right, rForward);
    }
    
    public void setLeftMotor(int left, boolean forward){
        left_dir_pin.setState(!forward);
        //left_pwm.setPwm(left);
        SoftPwm.softPwmWrite(LEFT_GO_PIN, left);
    }
    
    public void setRightMotor(int right, boolean forward){
        right_dir_pin.setState(!forward);
        //right_pwm.setPwm(right);
        SoftPwm.softPwmWrite(RIGHT_GO_PIN, right);
    }
    
    public void forward(){
        forward(0,50);
    }
    
    public void forward(int speed){
        forward(0,speed);
    }
    
    public void forward(int seconds, int speed){
        set_motors(speed, true, speed, true);
        if(seconds > 0){
            try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            stop();
        }
     }

    public void stop(){
        set_motors(0, true, 0, true);
    }
    
    public void reverse(){
        reverse(0,50);
    }
    
    public void reverse(int speed){
        reverse(0,speed);
    }
 
    public void reverse(int seconds, int speed){
        set_motors(speed, true, speed, true);
        if(seconds > 0){
            try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            stop();
        }
    }
    
    public void left(){
        left(0, 50);
    }
    
    public void left(int seconds, int speed){
        set_motors(0, false, speed, true);
        if(seconds > 0){
            try{
                Thread.sleep(seconds * 1000);
            }catch(InterruptedException e){}
            stop();
        }
    }
    
    public void right(){
        right(0, 50);
    }

    public void right(int seconds, int speed){
        set_motors(speed, true, 0, false);
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