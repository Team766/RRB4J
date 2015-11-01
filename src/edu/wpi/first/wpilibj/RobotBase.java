/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.				   */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.															   */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj;

import com.team766.rrb4j.RRB4J;
import com.team766.rrb4j.Robot;
import com.team766.rrb4j.VRConnector;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.internal.HardwareHLUsageReporting;
import edu.wpi.first.wpilibj.internal.HardwareTimer;

/**
 * Implement a Robot Program framework.
 * The RobotBase class is intended to be subclassed by a user creating a robot program.
 * Overridden autonomous() and operatorControl() methods are called at the appropriate time
 * as the match proceeds. In the current implementation, the Autonomous code will run to
 * completion before the OperatorControl code could start. In the future the Autonomous code
 * might be spawned as a task, then killed at the end of the Autonomous period.
 */
public abstract class RobotBase {
	/**
	 * The VxWorks priority that robot code should work at (so Java code should run at)
	 */
	public static final int ROBOT_TASK_PRIORITY = 101;
	public static String fileName = "Robot.java";
	
	public enum RobotState {
        DISABLED, AUTONOMOUS, INIT, TELEOP
    }


	/**
	 * Free the resources for a RobotBase class.
	 */
	public void free() {
	}

	/**
	 * @return If the robot is running in simulation.
	 */
	public static boolean isSimulation() {
		return false;
	}

	/**
	 * @return If the robot is running in the real world.
	 */
	public static boolean isReal() {
		return true;
	}

	/**
	 * Determine if the Robot is currently disabled.
	 * @return True if the Robot is currently disabled by the field controls.
	 */
	public boolean isDisabled() {
		return false;
	}

	/**
	 * Determine if the Robot is currently enabled.
	 * @return True if the Robot is currently enabled by the field controls.
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Determine if the robot is currently in Autonomous mode.
	 * @return True if the robot is currently operating Autonomously as determined by the field controls.
	 */
	public boolean isAutonomous() {
		return false;
	}

	/**
	 * Determine if the robot is currently in Test mode
	 * @return True if the robot is currently operating in Test mode as determined by the driver station.
	 */
	public boolean isTest() {
		return false;
	}

	/**
	 * Determine if the robot is currently in Operator Control mode.
	 * @return True if the robot is currently operating in Tele-Op mode as determined by the field controls.
	 */
	public boolean isOperatorControl() {
		return true;
	}

	/**
	 * Indicates if new data is available from the driver station.
	 * @return Has new data arrived over the network since the last time this function was called?
	 */
	public boolean isNewDataAvailable() {
		return true;
	}

	/**
	 * Provide an alternate "main loop" via startCompetition().
	 */
	public abstract void startCompetition();


	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		String propVal = System.getProperty(name);
		if (propVal == null) {
			return defaultValue;
		}
		if (propVal.equalsIgnoreCase("false")) {
			return false;
		} else if (propVal.equalsIgnoreCase("true")) {
			return true;
		} else {
			throw new IllegalStateException(propVal);
		}
	}

	/**
	 * Common initialization for all robot programs.
	 */
	public static void initializeHardwareConfiguration(){
		FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationReserve();

		// Set some implementations so that the static methods work properly
		Timer.SetImplementation(new HardwareTimer());
		HLUsageReporting.SetImplementation(new HardwareHLUsageReporting());
//		RobotState.SetImplementation(DriverStation.getInstance());
	}
    
	/**
	 * Starting point for the applications.
	 */
	public static void main(String args[]){
		if(!VRConnector.SIMULATOR)
			RRB4J.getInstance().set_led1(true);
		else
			startSimulator();
	
		Robot robert = new Robot();
		
		robert.startCompetition();

		if(!VRConnector.SIMULATOR)
			RRB4J.getInstance().set_led1(false);
		
		System.exit(1);
	}
	
	private static void startSimulator(){
		VRConnector.getInstance();
		
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		
		new Thread(VRConnector.getInstance()).start();
	}
}
	
