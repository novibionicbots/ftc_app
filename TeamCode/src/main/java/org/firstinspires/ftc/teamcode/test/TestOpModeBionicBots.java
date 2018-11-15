package org.firstinspires.ftc.teamcode.test;

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.systems.Direction;
import org.firstinspires.ftc.teamcode.systems.RRVHardwarePushbot;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldAlignDetector;
import com.disnodeteam.dogecv.detectors.roverrukus.SilverDetector;


@TeleOp(name="TestOpModeBionicBots Meccanum Move")
@Disabled
public class TestOpModeBionicBots extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 4.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 0.8188976 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);

//    private SilverDetector detector;
    private SamplingOrderDetector detector;


    RRVHardwarePushbot robot = new RRVHardwarePushbot();

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status","Initialized");
        telemetry.update();
        robot.init(hardwareMap);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

//        telemetry.addData("Step1","Moving Left....");
//        telemetry.update();
//        robot.meccanumMove(0.5,3, Direction.LEFT);
//
//        Wait(5);
//
//        telemetry.addData("Step1","Moving Left....");
//        telemetry.update();
//        robot.meccanumMove(0.5,3, Direction.RIGHT);

//        encoderDown(1);

        initDetector();

        telemetry.addData("detector.getCurrentOrder()?",detector.getCurrentOrder());
        telemetry.update();

        while(opModeIsActive() && detector.isFound() == false && runtime.seconds() <= 10) {
            if(detector.getCurrentOrder().equals(SamplingOrderDetector.GoldLocation.LEFT)){
                telemetry.addData("detector.getCurrentOrder()?",detector.getCurrentOrder());
                telemetry.update();
                Wait(1);
                break;
            }
            if(detector.getCurrentOrder().equals(SamplingOrderDetector.GoldLocation.CENTER)){
                telemetry.addData("detector.getCurrentOrder()?",detector.getCurrentOrder());
                telemetry.update();
                Wait(1);
                break;
            }
            if(detector.getCurrentOrder().equals(SamplingOrderDetector.GoldLocation.RIGHT)){
                telemetry.addData("detector.getCurrentOrder()?",detector.getCurrentOrder());
                telemetry.update();
                Wait(1);
                break;
            }
            robot.meccanumMove(0.275,-1, Direction.LEFT);
        }
        robot.stop();

        telemetry.addData("detector.getCurrentOrder()?",detector.getCurrentOrder());
        telemetry.addData("Path", "Complete");


        telemetry.addData("Is the cube pushed?",detector.isFound());
        telemetry.addData("Path", "Complete");
        telemetry.update();

        Wait(10);

        detector.disable();


    }

    private void initDetector(){
        telemetry.addData("Status", "DogeCV 2018.0 - Gold Align Example");

//        detector = new SilverDetector();

        detector = new SamplingOrderDetector();

        // detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance(),0,false);
        detector.useDefaults();
        // Optional Tuning
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005;

        detector.ratioScorer.weight = 5;
        detector.ratioScorer.perfectRatio = 1.0;

        detector.enable();
    }

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


}
