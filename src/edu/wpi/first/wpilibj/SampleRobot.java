/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj;

import java.io.IOException;

import com.team766.rrb4j.VRConnector;

import edu.wpi.first.wpilibj.RobotBase.RobotState;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * A simple robot base class that knows the standard FRC competition states
 * (disabled, autonomous, or operator controlled).
 *
 * You can build a simple robot program off of this by overriding the
 * robotinit(), disabled(), autonomous() and operatorControl() methods. The
 * startCompetition() method will calls these methods (sometimes repeatedly).
 * depending on the state of the competition.
 *
 * Alternatively you can override the robotMain() method and manage all aspects
 * of the robot yourself.
 */
public class SampleRobot extends RobotBase {

	private boolean m_robotMainOverridden;

	/**
	 * Create a new SampleRobot
	 */
	public SampleRobot() {
		super();
		RobotBase.fileName = this.getClass().getSimpleName();
		m_robotMainOverridden = true;
	}

	/**
	 * Robot-wide initialization code should go here.
	 *
	 * Users should override this method for default Robot-wide initialization
	 * which will be called when the robot is first powered on.
	 *
	 * Called exactly 1 time when the competition starts.
	 */
	protected void robotInit() {
		System.out.println("Default robotInit() method running, consider providing your own");
	}

	/**
	 * Disabled should go here. Users should overload this method to run code
	 * that should run while the field is disabled.
	 *
	 * Called once each time the robot enters the disabled state.
	 */
	protected void disabled() {
		System.out.println("Default disabled() method running, consider providing your own");
	}

	/**
	 * Autonomous should go here. Users should add autonomous code to this
	 * method that should run while the field is in the autonomous period.
	 *
	 * Called once each time the robot enters the autonomous state.
	 */
	public void autonomous() {
		System.out.println("Default autonomous() method running, consider providing your own");
	}

	/**
	 * Operator control (tele-operated) code should go here. Users should add
	 * Operator Control code to this method that should run while the field is
	 * in the Operator Control (tele-operated) period.
	 *
	 * Called once each time the robot enters the operator-controlled state.
	 */
	public void operatorControl() {
		System.out.println("Default operatorControl() method running, consider providing your own");
	}

	/**
	 * Test code should go here. Users should add test code to this method that
	 * should run while the robot is in test mode.
	 */
	public void test() {
		System.out.println("Default test() method running, consider providing your own");
	}

	/**
	 * Robot main program for free-form programs.
	 *
	 * This should be overridden by user subclasses if the intent is to not use
	 * the autonomous() and operatorControl() methods. In that case, the program
	 * is responsible for sensing when to run the autonomous and operator
	 * control functions in their program.
	 *
	 * This method will be called immediately after the constructor is called.
	 * If it has not been overridden by a user subclass (i.e. the default
	 * version runs), then the robotInit(), disabled(), autonomous() and
	 * operatorControl() methods will be called.
	 */
	public void robotMain() {
		m_robotMainOverridden = false;
	}

	private void resetField(){
		if(!VRConnector.SIMULATOR)
			return;
		
		VRConnector.getInstance().putCommandBool(VRConnector.RESET_SIM, true);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}
	/**
	 * Start a competition. This code tracks the order of the field starting to
	 * ensure that everything happens in the right order. Repeatedly run the
	 * correct method, either Autonomous or OperatorControl when the robot is
	 * enabled. After running the correct method, wait for some state to change,
	 * either the other mode starts or the robot is disabled. Then go back and
	 * wait for the robot to be enabled again.
	 */
	public void startCompetition() {
		resetField();

		RobotState _state = RobotState.INIT;
		for(int i = 0; i < RobotState.values().length; i++){
			switch(_state){
				case AUTONOMOUS:
					autonomous();
					_state = RobotState.TELEOP;
					break;
				case DISABLED:
					disabled();
					_state = RobotState.AUTONOMOUS;
					break;
				case INIT:
					robotInit();
					_state = RobotState.DISABLED;
					break;
				case TELEOP:
					operatorControl();
					_state = RobotState.DISABLED;
					break;
				default:
					break;
			}
		}
	}
}
