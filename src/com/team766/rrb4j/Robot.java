package com.team766.rrb4j;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;

public class Robot extends SampleRobot {
    Talon left;
    Talon right;
    
	public Robot(){
		
	}
	
	public void robotInit(){
		System.out.println("Basic Robot: Robot Init!");
		
		left = new Talon(0);
	    right = new Talon(1);
	}
	
	public void disabled(){
		System.out.println("Basic Robot: Disabled!");
	}
	
	public void autonomous() {
		System.out.println("Basic Robot: Auton Controlled!");
    }
	
	public void operatorControl() {
        System.out.println("Basic Robot: Operator Controlled!");
        
        //Set Motors Forward
        left.set(1);
        right.set(-1);
        
        //Wait
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("The coffee was not decafe!!!!  Failed to SLEEP!!!!");
		}
        
      //Set Motors Backwards
        left.set(-1);
        right.set(1);
        
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