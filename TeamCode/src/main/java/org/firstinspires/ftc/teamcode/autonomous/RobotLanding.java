/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.autonomous;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldAlignDetector;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.systems.Direction;
import org.firstinspires.ftc.teamcode.systems.RRVHardwarePushbot;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Autonomous Landing - TeamMarker", group="Pushbot")
//@Disabled

public class RobotLanding extends LinearOpMode {

    /* Declare OpMode members. */
    RRVHardwarePushbot robot = new RRVHardwarePushbot();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();
    private GoldAlignDetector detector;

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 4.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 0.8188976 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;


    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Step0",  "Starting at %7d",
                robot.rack_pinion.getCurrentPosition());
        telemetry.update();

        initDetector();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        encoderDown(9.5);

        runtime.reset();

        //Move LEFT to Unlatch
        telemetry.addData("Step1","Unlatching....");
        telemetry.update();
        robot.meccanumMove(0.2,1,Direction.RIGHT);
        //Move FORWARD
        telemetry.addData("Step2","Move Backward");
        telemetry.update();
        robot.meccanumMove(0.3,0.8,Direction.BACK);
        //Move RIGHT
        robot.meccanumMove(0.4,0.9,Direction.RIGHT);
        //Start scanning
        telemetry.addData("Step 3","Looking for the gold");
        telemetry.update();

        while(opModeIsActive() && detector.getAligned() == false && runtime.seconds() <= 10) {
            robot.meccanumMove(0.275,-1, Direction.LEFT);
        }
        //End Scanning - GOLD FOUND
        double initialPosition = detector.getXPosition();
        robot.stop();

        //Position for Knocking
        robot.meccanumMove(0.375, 0.7, Direction.RIGHT);

        //Move to KNOCK
        robot.meccanumMove(0.3,1, Direction.BACK);

        telemetry.addData("Is the cube pushed?",detector.isFound());
        telemetry.addData("Detecting Gold",detector.getAligned());
        telemetry.addData("Initial position of Gold",initialPosition);
        telemetry.addData("Path", "Complete");
        telemetry.update();

        Wait(2);

        //Move to drop Team Marker
//        robot.meccanumMove(0.2,2, Direction.FORWARD);

//        robot.servo0.setPower(-2);
        Wait(10);

        detector.disable();

    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDown(double inches) {
        runtime.reset();
        int currentPos = robot.rack_pinion.getCurrentPosition();
        int finalPos = (int) (currentPos+COUNTS_PER_INCH*inches);
        telemetry.addData("Current position:",currentPos);
        telemetry.addData("Final position:",finalPos);
        telemetry.update();
        Wait(1);
        while (opModeIsActive()&& (currentPos < finalPos)) {
            currentPos = robot.rack_pinion.getCurrentPosition();
            telemetry.addData("Current position while moving:",currentPos);
            telemetry.update();
            robot.rack_pinion.setPower(1.0);
        }

        robot.rack_pinion.setPower(0);
    }

    public void Wait(double seconds){
        runtime.reset();
        while(opModeIsActive() && runtime.seconds()< seconds){}
    }

    private void initDetector(){
        telemetry.addData("Status", "DogeCV 2018.0 - Gold Align Example");

        detector = new GoldAlignDetector();

        // detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(),0,false);
        detector.useDefaults();
        // Optional Tuning
        detector.alignSize = 100; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        detector.alignPosOffset = 0; // How far from center frame to offset this alignment zone.
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005;

        detector.ratioScorer.weight = 5;
        detector.ratioScorer.perfectRatio = 1.0;

        detector.enable();
    }
}
