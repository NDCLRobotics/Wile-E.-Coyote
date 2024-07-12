package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.Range;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Stack;

@TeleOp(name="Center Stage TeleOp TESTING", group="Interactive Opmode")

public class Tele_Op extends OpMode
{
    // Control Hub
    //drive motors
    private DcMotor frontLeftMotor = null;
    private DcMotor frontRightMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor backRightMotor = null;

    //Lift motors
    private DcMotor pullUp = null;
    private CRServo pullReady = null;
    private CRServo BackdropAngle = null;

    private DcMotor pixelLift = null;
    private CRServo pixelAngle = null;

    private CRServo pixelLeft = null;
    private CRServo pixelRight = null;

    private CRServo nuclearLaunch = null;
    private CRServo droneLock = null;
    private CRServo pixelStabber = null;





    // Frames
    private long currentFrame;
    private long startHomeFrame;

    // Variables for power set to the drive and pan functions
    private double frontLeftPower, frontRightPower, backLeftPower, backRightPower , pullPower;
    private double frontLeftPan, frontRightPan, backLeftPan, backRightPan;

    // Scale variable
    private double powerScale = 1;
    private boolean powerSwitching = false;

    private double liftScale = 0.8;
    private boolean liftPower = false;

    private boolean autoHome = false;

    private boolean godMode = false;
    private boolean switching = false;

    private boolean Attenthut = false;
    private boolean PixelAngle = false;



    private boolean switchingsquare = false;
    private boolean switchingcross = false;



    private boolean leftOpen = false;
    private boolean rightOpen = false;

    private boolean switchingPixelRight = false;
    private boolean switchingPixelLeft = false;

    private boolean startUp = false;
    private boolean ifStabbing = true;
    private boolean ifCircle = false;

    private double distance = -6418;

    //pullUp Limits
    private int pullZero;
    private double pixelZero;



    // Pull the date of the file (to update the date of telemetry)
    File curDir = new File("./system");
    private static Stack<File> nest = new Stack<File>();

    public void getAllFiles (File curDir)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        File[] filesList = curDir.listFiles();
        if (filesList != null)
        {
            for (File f : filesList)
            {
                if (f.isDirectory() && !f.getName().equals("bin"))// && nest.size() < 3)
                {
                    nest.push(f);
                    getAllFiles(f);
                    nest.pop();
                }
                else //if (f.isFile())
                {
                    if (sdf.format(f.lastModified()).contains("2022"))
                    {
                        String s = "";
                        for (File ff : nest)
                        {
                            s += ff.getName() + "/";
                        }
                        if (f.isDirectory())
                        {
                            telemetry.addLine(s + f.getName() + "/");
                        }
                        else
                        {
                            telemetry.addLine(s + f.getName() + " " + sdf.format(f.lastModified()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void init ()
    {
        // Initialize connection to motors
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        pullUp = hardwareMap.dcMotor.get("pullUp");
        pullReady = hardwareMap.crservo.get("pullReady");

        pixelLift = hardwareMap.dcMotor.get("pixelLift");
        pixelAngle = hardwareMap.crservo.get("pixelAngle");

        pixelLeft = hardwareMap.crservo.get("pixelLeft");
        pixelRight = hardwareMap.crservo.get("pixelRight");

        nuclearLaunch = hardwareMap.crservo.get("nuclearLaunch");
        droneLock = hardwareMap.crservo.get("droneLock");
        pixelStabber = hardwareMap.crservo.get("pixelStabber");

        // Set direction to the motors (may need to change depending on orientation of robot)
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        pullUp.setDirection(DcMotorSimple.Direction.REVERSE);

        pixelLift.setDirection(DcMotorSimple.Direction.REVERSE);





        //reset encoders

        pullZero = pullUp.getCurrentPosition();
        pixelZero = pixelLift.getCurrentPosition();

        // Send telemetry to the robot
        telemetry.addLine("Working");
        // telemetry.addData("Last updated",sdf.format(file.lastModified()));

        currentFrame = 0;
        startHomeFrame = 0;

        // test stuff
        getAllFiles(curDir);
    }

    @Override
    public void init_loop ()
    {
        // pls do
    }



    @Override
    public void loop ()
    {
        telemetry.addLine("\nMotors:");
        telemetry.addData("Front Left", frontLeftMotor.getCurrentPosition());
        telemetry.addData("Front Right", frontRightMotor.getCurrentPosition());
        telemetry.addData("Back Left", backLeftMotor.getCurrentPosition());
        telemetry.addData("Back Right", backRightMotor.getCurrentPosition());

        telemetry.addData("pullUp ", pullUp.getCurrentPosition() );
        telemetry.addData("pullZero ", pullZero);
        telemetry.addData("pixelZero ", pixelZero);
        telemetry.addData("pixelLift ",pixelLift.getCurrentPosition());

        currentFrame += 1;




        double pullDirection = gamepad2.left_stick_y;
        double pixelUp = gamepad2.right_trigger;
        double pixelDown = gamepad2.left_trigger;

        double pixelPowerUp;
        double pixelPowerDown;


        double drive = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        double pan = -gamepad1.left_stick_x;





        telemetry.addData("drive", drive);
        telemetry.addData("turn", turn);


        //START OF NEW DRIVE FUNCTION
        //total is used for finding the percentage or "weight" of the input
        double total;
        //tempturn is used to hold the variable "turn" so that we can change it to positive to determine weight
        double tempTurn;
        //Percentage calculates the weight, where 1 is the max
        double driveWeight;
        double turnWeight;

        double frontRightPower;
        double frontLeftPower;
        double backRightPower;
        double backLeftPower;

        frontLeftPan = Range.clip(drive - pan, -1.0, 1.0);
        frontRightPan = Range.clip(drive + pan, -1.0, 1.0);
        backLeftPan = Range.clip(drive + pan, -1.0, 1.0);
        backRightPan = Range.clip(drive - pan, -1.0, 1.0);

        tempTurn = Range.clip(turn + 0,-1,1);




        //Front Left Wheel
        total = Math.abs(tempTurn) + Math.abs(frontLeftPan);
        if (total>1)
        {
            driveWeight = Math.abs(frontLeftPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            frontLeftPower = (frontLeftPan * driveWeight) + (tempTurn * turnWeight);

        }
        else
        {
            frontLeftPower = frontLeftPan + tempTurn;
        }


        //Front Right Wheel
        total = Math.abs(tempTurn) + Math.abs(frontRightPan);
        if (total>1)
        {
            driveWeight = Math.abs(frontRightPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            frontRightPower = (frontRightPan * driveWeight) - (tempTurn * turnWeight);

        }
        else
        {
            frontRightPower = frontRightPan - tempTurn;
        }

        //Back Left Wheel
        total = Math.abs(tempTurn) + Math.abs(backLeftPan);
        if (total>1)
        {
            driveWeight = Math.abs(backLeftPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            backLeftPower = (backLeftPan * driveWeight) + (tempTurn * turnWeight);

        }
        else
        {
            backLeftPower = backLeftPan + tempTurn;
        }

        //Back Right Wheel
        total = Math.abs(tempTurn) + Math.abs(backRightPan);
        if (total>1)
        {
            driveWeight = Math.abs(backRightPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            backRightPower = (backRightPan * driveWeight) - (tempTurn * turnWeight);

        }
        else
        {
            backRightPower = backRightPan - tempTurn;
        }

        if(gamepad1.right_trigger != 0)
        {
            frontLeftPower *= 0.3;
            frontRightPower *= 0.3;
            backLeftPower *= 0.3;
            backRightPower *= 0.3;
        }


        //Set Power
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);



        //GAMEPAD-1=DRIVE/DRONE-FIRE
        //GAMEPAD-2=ACCESSORY


        // Incrementing speed for driving motor, up speeds up motors, down slows down motors
        if (gamepad1.dpad_up && !powerSwitching)
        {
            powerSwitching = true;
            powerScale += 0.2;
        }
        if (gamepad1.dpad_down && !powerSwitching)
        {
            powerSwitching = true;
            powerScale -= 0.2;
        }
        if (!gamepad1.dpad_down && !gamepad1.dpad_up && powerSwitching)
        {
            powerSwitching = false;
        }


        //2
        if (gamepad2.dpad_down)
        {
            if (pixelLift.getCurrentPosition()<pullZero+140)
            {
                pixelLift.setPower(1);
            }
            else if (pixelLift.getCurrentPosition()>pullZero + 150)
            {
                pixelLift.setPower(-0.5);
            }
            else
            {
                pixelLift.setPower(0);
            }
        }

        //3
        if(gamepad2.dpad_left)
        {
            if (pixelLift.getCurrentPosition()<pullZero + 225)
            {
                pixelLift.setPower(1);
            }
            else if (pixelLift.getCurrentPosition()>pullZero + 235)
            {
                pixelLift.setPower(-0.5);
            }
            else
            {
                pixelLift.setPower(0);
            }
        }

        //4
        if(gamepad2.dpad_right)
        {
            if (pixelLift.getCurrentPosition() < pullZero + 345)
            {
                pixelLift.setPower(1);
            }
            else if (pixelLift.getCurrentPosition() > pullZero + 355)
            {
                pixelLift.setPower(-0.5);

            }
            else
            {
                pixelLift.setPower(0);

            }
        }

        //5
        if(gamepad2.dpad_up)
        {
            if(pixelLift.getCurrentPosition() < pullZero + 420)
            {
                pixelLift.setPower(1);
            }
            else if (pixelLift.getCurrentPosition() > pullZero + 430)
            {
                pixelLift.setPower(-0.5);


            }
            else
            {
                pixelLift.setPower(0);

            }
        }

        //Pixel-Adjustment
        if (gamepad2.circle && !ifCircle && !ifStabbing)
        {
            //open
            pixelStabber.setPower(-1);
            ifStabbing=true;
            ifCircle = true;

        }
        if (gamepad2.circle && !ifCircle && ifStabbing)
        {
            //close
            pixelStabber.setPower(-0.1);
            ifStabbing = false;
            ifCircle = true;

        }
        if(!gamepad2.circle && ifCircle)
        {
            ifCircle=false;
        }
















        if (gamepad2.right_stick_button && gamepad2.left_stick_button && !switching)
        {
            switching = true;
            godMode = !godMode;
        }
        if (!gamepad2.right_stick_button && !gamepad2.left_stick_button && switching)
        {
            switching = false;
        }






        // Clamp for driving power scale
        powerScale = Range.clip(powerScale, 0.2, 1.0);




        // Output telemetry
        telemetry.addLine("Power Scale:" + powerScale);
        telemetry.addLine("Lift Power Scale:" + liftScale);


        if (godMode)
        {
            telemetry.addLine("I see no God up here... other than me");
        }
        else
        {
            telemetry.addLine("I see no God up here...");
        }

        //robot specific buttons:)


        // startup commands
        if (!startUp)
        {
            startUp = true;
            //send power to servos
            pullReady.setPower(0.67);
            pixelAngle.setPower(0.095);
            pixelLeft.setPower(-0.4);
            pixelRight.setPower(-0.065);


            pixelStabber.setPower(-1);

            droneLock.setPower(0.4);
            nuclearLaunch.setPower(-1);

        }
        telemetry.addData("Left ", frontLeftMotor.getCurrentPosition());
        telemetry.addData("Right ", backLeftMotor.getCurrentPosition() * -1) ;
        telemetry.addData("Center ", frontRightMotor.getCurrentPosition() *-1);
        telemetry.update();


        //PullUp
        pullPower = Range.clip(pullDirection, -1,1);



        if(Math.abs(pullUp.getCurrentPosition() - pullZero )<12850)
        {
            if (pullPower < -0.2 || pullPower > 0.2)
            {
                pullUp.setPower(pullPower);
            }
            else
            {
                pullUp.setPower(0);
            }
        }
        else
        {
            if (pullPower > 0)
            {
                pullUp.setPower(pullPower);
            }
            pullUp.setPower(0);
        }




        if (gamepad2.square && !Attenthut && !switchingsquare)
        {
            pullReady.setPower(0.13);
            Attenthut = true;
            switchingsquare = true;
        }
        if ( gamepad2.square && Attenthut && !switchingsquare)
        {
            pullReady.setPower(0.67);
            Attenthut = false;
            switchingsquare = true;
        }
        if (!gamepad2.square && switchingsquare)
        {
            switchingsquare = false;
        }





        //PixelClaw
        //rotation
        if (gamepad2.cross && !PixelAngle && !switchingcross)
        {
            //was -.25
            pixelAngle.setPower(-0.2);
            PixelAngle = true;
            switchingcross = true;
        }
        if (gamepad2.cross && PixelAngle && !switchingcross)
        {
            pixelAngle.setPower(0.095);

            PixelAngle = false;
            switchingcross = true;
        }
        if (!gamepad2.cross && switchingcross)
        {
            switchingcross = false;
        }

        //claw

        if (gamepad2.left_bumper && !leftOpen && !switchingPixelLeft)
        {
            pixelLeft.setPower(0.02);
            leftOpen = true;
            switchingPixelLeft = true;
        }
        if (gamepad2.left_bumper && leftOpen && !switchingPixelLeft)
        {
            pixelLeft.setPower(-0.4);
            leftOpen = false;
            switchingPixelLeft = true;
        }
        if (!gamepad2.left_bumper && switchingPixelLeft)
        {
            switchingPixelLeft = false;
        }



        if (gamepad2.right_bumper && !rightOpen && !switchingPixelRight)
        {
            pixelRight.setPower(-0.4);
            rightOpen = true;
            switchingPixelRight = true;
        }
        if (gamepad2.right_bumper && rightOpen && !switchingPixelRight)
        {
            pixelRight.setPower(-0.065);
            rightOpen = false;
            switchingPixelRight = true;
        }
        if (!gamepad2.right_bumper && switchingPixelRight)
        {
            switchingPixelRight = false;
        }


        //lift

        pixelPowerUp = Range.clip(pixelUp,0,1);
        pixelPowerDown = Range.clip(pixelDown,0,1);

        if (gamepad2.right_trigger > 0)
        {
            if ( Math.abs(pixelLift.getCurrentPosition() - pixelZero )<4325 )
            {
                pixelLift.setPower(pixelPowerUp);
            }
            else
            {
                pixelLift.setPower(0);
            }

        }
        else if (gamepad2.left_trigger > 0)
        {
            if(pixelLift.getCurrentPosition()>100)
            {
                pixelLift.setPower(-pixelPowerDown);
            }
            else
            {
                pixelLift.setPower(0);
            }
        }
        else
        {
            pixelLift.setPower(0);
        }


        //Note: nuclearLaunch = Drone-launching-crservo
        if ( gamepad1.triangle && gamepad2.triangle)
        {
            droneLock.setPower(-0.5);
            nuclearLaunch.setPower(-0.8);
        }








    }

    @Override
    public void stop ()
    {
        // I don't know how many years in a row I have to reiterate this, pls do... I second this
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backLeftMotor.setPower(0.0);
        backRightMotor.setPower(0.0);

        pullUp.setPower(0.0);

    }
}