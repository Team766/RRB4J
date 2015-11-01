/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj;

import com.team766.rrb4j.RRB4J;
import com.team766.rrb4j.VRConnector;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.util.BoundaryException;

/**
 * Class to read quad encoders. Quadrature encoders are devices that count shaft
 * rotation and can sense direction. The output of the QuadEncoder class is an
 * integer that can count either up or down, and can go negative for reverse
 * direction counting. When creating QuadEncoders, a direction is supplied that
 * changes the sense of the output to make code more readable if the encoder is
 * mounted such that forward movement generates negative values. Quadrature
 * encoders have two digital outputs, an A Channel and a B Channel that are out
 * of phase with each other to allow the FPGA to do direction sensing.
 *
 * All encoders will immediately start counting - reset() them if you need them
 * to be zeroed before use.
 */
public class Encoder extends SensorBase implements PIDSource {

	private double m_distancePerPulse; // distance of travel for each encoder tick
	
	private double stop_threashold = 0.001;
	private final double DIAMETER = 6; //Inches
	private final double CIRCUMFRENCE = DIAMETER * Math.PI;
	
	private boolean isLeft = false;
	private boolean IGNORE = false;
	
	private int counter = 0;
	
	private Counter m_counter; // Counter object for 1x and 2x encoding
	private EncodingType m_encodingType = EncodingType.k4X;
	private int m_encodingScale; // 1x, 2x, or 4x, per the encodingType
	private PIDSourceParameter m_pidSource;

	public Encoder(int aSource, int bSource) {
		if(aSource == 0 && bSource == 0)
			isLeft = true;
		else if(aSource == 1 && bSource == 1)
			isLeft = false;
		else{
			System.err.println("There is no more encoders...Use 0 or 1");
			IGNORE = true;
		}
	}


	/**
	 * @return the encoding scale factor 1x, 2x, or 4x, per the requested
	 *   encodingType. Used to divide raw edge counts down to spec'd counts.
	 */
	public int getEncodingScale() {
		return m_encodingScale;
	}


	/**
	 * Gets the raw value from the encoder. The raw value is the actual count
	 * unscaled by the 1x, 2x, or 4x scale factor.
	 *
	 * @return Current raw count from the encoder
	 */
	public int getRaw() {
		if(IGNORE)
			return 0;
		
		if(VRConnector.SIMULATOR)
			return isLeft ? VRConnector.getInstance().getFeedback(VRConnector.LEFT_ENCODER) : VRConnector.getInstance().getFeedback(VRConnector.RIGHT_ENCODER); 
		else
			return isLeft ? RRB4J.getInstance().getLeftEncoder() : RRB4J.getInstance().getRightEncoder(); 
	}

	/**
	 * Gets the current count. Returns the current count on the Encoder. This
	 * method compensates for the decoding type.
	 *
	 * @return Current count from the Encoder adjusted for the 1x, 2x, or 4x
	 *         scale factor.
	 */
	public int get() {
		return (int) (getRaw() * decodingScaleFactor());
	}

	/**
	 * Reset the Encoder distance to zero. Resets the current count to zero on
	 * the encoder.
	 */
	public void reset() {
		counter = getRaw();
	}


	/**
	 * Sets the maximum period for stopped detection. Sets the value that
	 * represents the maximum period of the Encoder before it will assume that
	 * the attached device is stopped. This timeout allows users to determine if
	 * the wheels or other shaft has stopped rotating. This method compensates
	 * for the decoding type.
	 *
	 *
	 * @param maxPeriod
	 *            The maximum time between rising and falling edges before the
	 *            FPGA will report the device stopped. This is expressed in
	 *            seconds.
	 */
	public void setMaxPeriod(double maxPeriod) {
	}

	/**
	 * Determine if the encoder is stopped. Using the MaxPeriod value, a boolean
	 * is returned that is true if the encoder is considered stopped and false
	 * if it is still moving. A stopped encoder is one where the most recent
	 * pulse width exceeds the MaxPeriod.
	 *
	 * @return True if the encoder is considered stopped.
	 */
	public boolean getStopped() {
		return getRate() <= stop_threashold;
	}

	/**
	 * The last direction the encoder value changed.
	 *
	 * @return The last direction the encoder value changed.
	 */
	public boolean getDirection() {
		return getRate() > 0 ? true : false;
	}

	/**
	 * The scale needed to convert a raw counter value into a number of encoder
	 * pulses.
	 */
	private double decodingScaleFactor() {
		switch (m_encodingType.value) {
		case EncodingType.k1X_val:
			return 1.0;
		case EncodingType.k2X_val:
			return 0.5;
		case EncodingType.k4X_val:
			return 0.25;
		default:
			// This is never reached, EncodingType enum limits values
			return 0.0;
		}
	}

	/**
	 * Get the distance the robot has driven since the last reset.
	 *
	 * @return The distance driven since the last reset as scaled by the value
	 *         from setDistancePerPulse().
	 */
	public double getDistance() {
		if(IGNORE)
			return 0;
		
		if(VRConnector.SIMULATOR)
			return (getRaw() - counter)/360d * CIRCUMFRENCE;
		else
			return getRaw() * decodingScaleFactor() * m_distancePerPulse;
	}

	/**
	 * Get the current rate of the encoder. Units are distance per second as
	 * scaled by the value from setDistancePerPulse().
	 *
	 * @return The current rate of the encoder.
	 */
	public double getRate() {
		return getRaw() - getRaw();
	}

	/**
	 * Set the minimum rate of the device before the hardware reports it
	 * stopped.
	 *
	 * @param minRate
	 *            The minimum rate. The units are in distance per second as
	 *            scaled by the value from setDistancePerPulse().
	 */
	public void setMinRate(double minRate) {
		setMaxPeriod(m_distancePerPulse / minRate);
	}

	/**
	 * Set the distance per pulse for this encoder. This sets the multiplier
	 * used to determine the distance driven based on the count value from the
	 * encoder. Do not include the decoding type in this scale. The library
	 * already compensates for the decoding type. Set this value based on the
	 * encoder's rated Pulses per Revolution and factor in gearing reductions
	 * following the encoder shaft. This distance can be in any units you like,
	 * linear or angular.
	 *
	 * @param distancePerPulse
	 *            The scale factor that will be used to convert pulses to useful
	 *            units.
	 */
	public void setDistancePerPulse(double distancePerPulse) {
		m_distancePerPulse = distancePerPulse;
	}

	/**
	 * Set the direction sensing for this encoder. This sets the direction
	 * sensing on the encoder so that it could count in the correct software
	 * direction regardless of the mounting.
	 *
	 * @param reverseDirection
	 *            true if the encoder direction should be reversed
	 */
	public void setReverseDirection(boolean reverseDirection) {
		if (m_counter != null) {
			m_counter.setReverseDirection(reverseDirection);
		} else {

		}
	}

	/**
	 * Set the Samples to Average which specifies the number of samples of the
	 * timer to average when calculating the period. Perform averaging to
	 * account for mechanical imperfections or as oversampling to increase
	 * resolution.
	 *
	 * TODO: Should this throw a checked exception, so that the user has to deal
	 * with giving an incorrect value?
	 *
	 * @param samplesToAverage
	 *            The number of samples to average from 1 to 127.
	 */
	public void setSamplesToAverage(int samplesToAverage) {
		
	}

	/**
	 * Get the Samples to Average which specifies the number of samples of the
	 * timer to average when calculating the period. Perform averaging to
	 * account for mechanical imperfections or as oversampling to increase
	 * resolution.
	 *
	 * @return SamplesToAverage The number of samples being averaged (from 1 to
	 *         127)
	 */
	public int getSamplesToAverage() {
		return 0;
	}

	/**
	 * Set which parameter of the encoder you are using as a process control
	 * variable. The encoder class supports the rate and distance parameters.
	 *
	 * @param pidSource
	 *            An enum to select the parameter.
	 */
	public void setPIDSourceParameter(PIDSourceParameter pidSource) {
		BoundaryException.assertWithinBounds(pidSource.value, 0, 1);
		m_pidSource = pidSource;
	}

	/**
	 * Implement the PIDSource interface.
	 *
	 * @return The current value of the selected source parameter.
	 */
	public double pidGet() {
		switch (m_pidSource.value) {
		case PIDSourceParameter.kDistance_val:
			return getDistance();
		case PIDSourceParameter.kRate_val:
			return getRate();
		default:
			return 0.0;
		}
	}
}
