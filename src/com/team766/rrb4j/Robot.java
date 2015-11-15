package com.team766.rrb4j;

import java.io.IOException;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class Robot extends SampleRobot {
    Talon left;
    Talon right;
    
    Encoder leftEnc;
    Encoder rightEnc;
    
    Solenoid intake;
    Solenoid shoot;
    
	public Robot(){
		
	}
	
	public void robotInit(){
		System.out.println("Basic Robot: Robot Init!");
		
		left = new Talon(0);
	    right = new Talon(1);
	    
	    leftEnc = new Encoder(0,0);
	    rightEnc = new Encoder(1,1);
	    
	    intake = new Solenoid(0);
	    shoot = new Solenoid(1);
	}
	
	public void disabled(){
		System.out.println("Basic Robot: Disabled!");
	}
	
	public void autonomous() {
		System.out.println("Basic Robot: Auton Controlled!");
		
		intake.set(true);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		
		intake.set(false);
		
		//Drive forward 60 inches
		while((leftEnc.getDistance() + rightEnc.getDistance()) / 2 <= 15){
			left.set(0.5);
			right.set(0.4);
		}
		
		left.set(0);
		right.set(0);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		shoot.set(true);
    }
	
	public void operatorControl() {
        System.out.println("Basic Robot: Operator Controlled!");
        
        //Set Motors Forward
        left.set(0.5);
        right.set(0.5);
        
        //Wait
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("The coffee was not decafe!!!!  Failed to SLEEP!!!!");
		}
      //Set Motors Backwards
        left.set(-0.5);
        right.set(-0.5);
        
        //Wait
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("The coffee was not decafe!!!!  Failed to SLEEP!!!!");
		}
        
        //Stop Motors
        left.set(0);
        right.set(0);        
    }
	
}
