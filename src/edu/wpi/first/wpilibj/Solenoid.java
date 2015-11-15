/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj;

import com.team766.rrb4j.VRConnector;

/**
 * Solenoid class for running high voltage Digital Output.
 *
 * The Solenoid class is typically used for pneumatics solenoids, but could be used
 * for any device within the current spec of the PCM.
 */
public class Solenoid extends SolenoidBase {

    private boolean isIntake = false;
    private boolean isFire = false;
    private boolean currState;


    /**
     * Constructor using the default PCM ID (0)
     *
     * @param channel The channel on the PCM to control.
     */
    public Solenoid(final int channel) {
    	super(channel);
    	
    	if(VRConnector.SIMULATOR){
	    	if(channel == 0)
	    		isIntake = true;
	    	else if(channel == 1)
	    		isFire = true;
    	}
    }

    /**
     * Destructor.
     */
    public synchronized void free() {
    }

    /**
     * Set the value of a solenoid.
     *
     * @param on Turn the solenoid output off or on.
     */
    public void set(boolean on) {
       currState = on;
       if(isIntake)
    	   VRConnector.getInstance().putCommandBool(VRConnector.INTAKE, on);
       else if(isFire){
    	   VRConnector.getInstance().putCommandBool(VRConnector.LAUNCH, on);
    	   //Reset after firing
    	   currState = false;
       }
    }

    /**
     * Read the current value of the solenoid.
     *
     * @return The current value of the solenoid.
     */
    public boolean get() {
        return currState;
    }
}
