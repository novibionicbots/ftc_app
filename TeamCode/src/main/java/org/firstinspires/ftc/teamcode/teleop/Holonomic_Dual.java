package org.firstinspires.ftc.teamcode;
/*
This code tells the program that this is the location of the program.
*/



import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.Servo;
/*
Whenever anyone programs on android studio or on OnBotJava, you most likely use a
motor/servo/sensor, and regular java programming has not defined the term DcMotor,Gyroscope,
CRServo,DistanceSensor, etc. FIRST knew this, so they created a package, or a repository,
that keeps the code defining what each term means. This code is telling the program to
import these terms so that the program will not be confused on any term
*/
@TeleOp
/*
This code is telling the program that this is a TeleOp program, or a driver controlled
program.
*/
public class Holonomic_Dual extends LinearOpMode {
    /*
    This code tells the name of the program, and tells it that it gets a bit of code from
    a seperate program called LinearOpMode. More of LinearOpMode will be shown later.
    */
    private DcMotor left_front;
    private DcMotor right_front;
    private DcMotor left_back;
    private DcMotor right_back;
    private DcMotor Rack_and_Pinion_Motor;
    private DcMotor lift_Base;
    private DcMotor lift_Extn;
    private CRServo servo0;
    /*
    This code says the name of each motor and what term it is(DcMotor, CRServo, etc.)
    */
    double leftFPower;
    double rightFPower;
    double leftRPower;
    double rightRPower;
    double speed = 1.5;
    double scale;
    int direction = 1;
    /*
    This code initalizes what variables are going to be used. These variable support the
    driver and makes the driving easier. The code for these variables will be shown later
    */


    @Override
    /*
    As mentioned before, this program gets code from another program called LinearOpMode.
    To make this code different from LinearOpMode, it overrides the code that in inherited
    from LinearOpMode, and makes this code unique
    */
    public void runOpMode() {
        /*
        This code tells the program to wait until the program is run(Between this code
        and the waitForStart() code is the initalization code)
        */
        left_front = hardwareMap.get(DcMotor.class, "left_Front");
        right_front = hardwareMap.get(DcMotor.class, "right_Front");
        left_back = hardwareMap.get(DcMotor.class, "left_Rear");
        right_back = hardwareMap.get(DcMotor.class, "right_Rear");
        lift_Base = hardwareMap.get(DcMotor.class, "lift_Base");
        lift_Extn = hardwareMap.get(DcMotor.class, "lift_Extn");
        servo0 = hardwareMap.get(CRServo.class, "servo0");
        Rack_and_Pinion_Motor = hardwareMap.get(DcMotor.class, "rack_pinion");
        /*
        This code tells the program to look to a certain place in the coniguration file
        on the robot to learn which motor is which.
        */
        right_front.setDirection(DcMotor.Direction.REVERSE);
        right_back.setDirection(DcMotor.Direction.REVERSE);
    /*
    This code says that the right motors must be reversed, so that it is easier to program
    */

        telemetry.addData("Status", "Intialized");
        telemetry.update();
        /*
        This code displays a message on the robot stating that the robot is intialized
        */
//        waitForStart();

        while (!opModeIsActive() && !isStopRequested()) {
            telemetry.addData("status", "waiting for start command...");
            telemetry.update();
        }

        /*
        This code tells the robot wait for the game to start (driver presses PLAY)
        */
        while (opModeIsActive()){
            /*
            This code tells the the code to run until the end of the match (driver presses STOP)
            */
            lift_Base.setPower(0);
            servo0.setPower(0);
            lift_Extn.setPower(0);
            Rack_and_Pinion_Motor.setPower(0);
           /*
           This code tells the robot to set the power of these motors to 0, and
           whenever the driver is not pressing any buttons,the power of the motors are
           set to 0
           */

            //leftPower = Math.cbrt(0.25 *(-gamepad1.left_stick_y - Math.signum(-gamepad1.left_stick_y)*0.5))+ Math.signum(-gamepad1.left_stick_y) * 0.5;
            //rightPower = Math.cbrt(0.25 *(-gamepad1.right_stick_y - Math.signum(-gamepad1.right_stick_y)*0.5))+ Math.signum(-gamepad1.right_stick_y) * 0.5;
           /*
           This code sets the power of these variable to a math function that essentialy ensures
           that the robot is topple-proof
           */
            //   if(direction== 1){
            //     right_front.setPower(leftPower/speed);
            //     right_back.setPower(leftPower/speed);
            //     left_back.setPower(rightPower/speed);
            //     left_front.setPower(rightPower/speed);
            //   } else {
            //     right_front.setPower(rightPower/speed);
            //     right_back.setPower(rightPower/speed);
            //     left_back.setPower(leftPower/speed);
            //     left_front.setPower(leftPower/speed);
            //   }
            leftFPower = -gamepad1.right_stick_y-gamepad1.right_stick_x-gamepad1.left_stick_x;
            leftRPower = -gamepad1.right_stick_y+gamepad1.right_stick_x-gamepad1.left_stick_x;
            rightFPower = -gamepad1.right_stick_y+gamepad1.right_stick_x+gamepad1.left_stick_x;
            rightRPower = -gamepad1.right_stick_y-gamepad1.right_stick_x+gamepad1.left_stick_x;

            scale = Math.max(Math.max(leftFPower, leftRPower), Math.max(rightFPower, rightRPower));
            if (scale>1) {
                leftFPower/=scale;
                leftRPower/=scale;
                rightFPower/=scale;
                rightRPower/=scale;
            }

            right_front.setPower(rightFPower);
            right_back.setPower(rightRPower);
            left_back.setPower(leftRPower);
            left_front.setPower(leftFPower);

            /*
            This code sets the motors to the power variable divided by the speed variable
            */
            while(gamepad1.right_bumper){
                Rack_and_Pinion_Motor.setPower(1);
                telemetry.addData("Rack and Pinion motor","Heading Up");
                telemetry.update();
            }
           /*
           This code says that, while the right bumper is pressed, the lander motor will
           head up, and will also display a message saying that it is heading up
           */

            while(gamepad1.left_bumper){
                Rack_and_Pinion_Motor.setPower(-1);
                telemetry.addData("Rack and Pinion motor","Heading Down");
                telemetry.update();
            }
            /*
           This code says that, while the left bumper is pressed, the lander motor will
           head down, and will also display a message saying that it is heading down
           */
            while(gamepad2.right_trigger > 0){
                lift_Base.setPower(gamepad2.right_trigger/4);
                telemetry.addData("lift_Base Motor",lift_Base.getPower());
                telemetry.update();
            }
            /*
            This code says that while the right trigger is pressed, than make the lift_Base
            motor head down, and displays a message saying the power of the lift_Base Motor.
            */
            while(gamepad2.left_trigger > 0){
                lift_Base.setPower(-(gamepad2.left_trigger)/2);
                telemetry.addData("lift_Base Motor",lift_Base.getPower());
                telemetry.update();
            }
            /*
            This code says that while the left trigger is pressed, than make the lift_Base
            motor head up, and displays a message saying the power of the lift_Base Motor.
            */
            while(gamepad2.y){
                lift_Extn.setPower(1.0);
                telemetry.addData("lift_Extn Motor",lift_Extn.getPower());
            }
            /*
            This code makes the lift_Extn Motor head down, and displays a message giving the power
            of the lift_Extn motor
            */
            while(gamepad2.a){
                lift_Extn.setPower(-1.0);
                telemetry.addData("lift_Extn Motor",lift_Extn.getPower());
            }
            /*
            This code makes the lift_Extn Motor head up, and displays a message giving the power
            of the lift_Extn motor
            */
            while(gamepad2.x){
                servo0.setPower(2);

            }
            /*
            This code sets the power of the servo, servo0, to 2, and makes it pick up blocks
            and balls
            */
            while(gamepad2.b){
                servo0.setPower(-2);
            }
            /*
            This code sets the power of the servo, servo0, to -2, and makes it drop off blocks
            and balls
            */
            while(gamepad1.dpad_up){
                left_back.setPower(0.5);

                left_front.setPower(0.5);

                right_back.setPower(0.5);

                right_front.setPower(0.5);

            }
            /*
            This code says that when the down button on the dpad is pressed, then the power
            of the wheels are half of what is was. When the down button on the dpad is pressed
            again, then it sets it power back to its original power
            */
            while(gamepad1.dpad_down){
                left_back.setPower(-0.5);

                left_front.setPower(-0.5);

                right_back.setPower(-0.5);

                right_front.setPower(-0.5);

            }
            while(gamepad1.dpad_right){
                if(direction == 1){
                    left_back.setPower(0.5);
                    right_back.setPower(-0.5);
                    left_front.setPower(-0.5);
                    right_front.setPower(0.5);
                } else {
                    left_back.setPower(-0.5);
                    right_back.setPower(0.5);
                    left_front.setPower(0.5);
                    right_front.setPower(-0.5);
                }

            }
            /*
            This code says, when the right button on the dpad is pressed, then the robot shifts right
            */
            while(gamepad1.dpad_left){
                if(direction == 1){
                    left_back.setPower(-0.5);
                    right_back.setPower(0.5);
                    left_front.setPower(0.5);
                    right_front.setPower(-0.5);
                } else {
                    left_back.setPower(0.5);
                    right_back.setPower(-0.5);
                    left_front.setPower(-0.5);
                    right_front.setPower(0.5);
                }
            }
            /*
            This code says, when the left button on the dpad is pressed, then the robot shifts left
            */
            telemetry.addData("Left Stick is",gamepad1.left_stick_y );
            //telemetry.addData("leftPower is",leftPower);
            telemetry.addData("Direction is",direction );
            telemetry.addData("Speed",speed);
            telemetry.addData("Power of left front",left_front.getPower());
            telemetry.addData("Power of left back", left_back.getPower());
            telemetry.addData("Power of right front",right_front.getPower());
            telemetry.addData("Power of right back",right_back.getPower());
            telemetry.addData("Status", "Running");
            telemetry.addData("lift_Base Motor",lift_Base.getPower());
            telemetry.update();
            /*
            This code displays a message saying what the power of the driving motors, the arm motor
            and what the direction and the speed is.
            */

        }
    }
}
