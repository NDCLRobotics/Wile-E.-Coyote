package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;


import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import com.qualcomm.robotcore.util.Range;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Stack;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import com.qualcomm.hardware.bosch.BNO055IMU;

@Autonomous(name = "OdometryTesting", group = "Concept")
public class OdometryTestingMain extends LinearOpMode {

    /// IMU
    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    private float currentAngleX, currentAngleY, currentAngleZ;
    private float zeroAngleX, zeroAngleY, zeroAngleZ;

    private double zeroLift = 0;

    //encoder
    private double zeroL, zeroR,zeroC;
    private double Cencoder = 0;
    private double Lencoder = 0;
    private double Rencoder = 0;

    //lift encoders :D
    private double LiftCoder = 0;

// LocationCheck variables.
    private double[] currentCoord = {0,0,Math.toRadians(90),0}; //aka "globalCoord", is the current x,y,and angle values.
    private double[] lastEncoderCoord = {0,0,0,0}; //l,r,s
    //tick to inch ratio
    private final double ticksToInch = 320.877;

    //LiftMotor Flag
    boolean liftDone = false;

    double Di = 0;

    /// Wheel Motors
    private DcMotor frontLeftMotor = null;
    private DcMotor frontRightMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor backRightMotor = null;

    //Extremities
    private DcMotor liftMotor = null;

    // Waits one loop to engage LastEncoder system
    private boolean LastEncoderFlag = false; //so that last encoder only comes in the second time around.
    boolean StartTurn = true;
    boolean Deacceleration = true;
    int step = 0;
    double motorScalingFactor = 0;
    double previousMotorScalingFactor = 0;
    double Distance = 0;
    double DeltaTheta = 0;
    // Driving Powers Container
    double [] Powers = {0,0,0,0};
    @Override
    public void waitForStart() {
        super.waitForStart();
    }

    public void runOpMode() {





        // Initialize connection to motors
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        liftMotor = hardwareMap.dcMotor.get("pixelLift");


        // Set direction to the motors (may need to change depending on orientation of robot)
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        //set up encoders
        //X = Right, Y = Left.    Change as needed
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);



        //Set to run without encoders
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        //Zeros are defined. They are public and need to remain there so that files have something to talk to.
        if (opModeIsActive())
        {
            zeroL = frontLeftMotor.getCurrentPosition()/ticksToInch;
            zeroR = backLeftMotor.getCurrentPosition()/ticksToInch;
            zeroC = frontRightMotor.getCurrentPosition()/ticksToInch;

            zeroLift = liftMotor.getCurrentPosition();


            while (opModeIsActive()) {

                //Tracking current position
                Cencoder = (frontRightMotor.getCurrentPosition() * -1 /ticksToInch) - zeroC;
                Lencoder = (frontLeftMotor.getCurrentPosition() / ticksToInch) - zeroL;
                Rencoder = (backLeftMotor.getCurrentPosition() / ticksToInch * -1) - zeroR;



                //FUNCTION TIME
                currentCoord = OdometryMath.locationCheck(Lencoder, Rencoder, Cencoder, lastEncoderCoord, currentCoord);

                //Tracking Previous Position (go through once, set equal to true. After that, update Last Encoder)
                if (LastEncoderFlag) {
                    lastEncoderCoord[0] = Lencoder;
                    lastEncoderCoord[1] = Rencoder;
                    lastEncoderCoord[2] = Cencoder;

                }
                else
                {
                    LastEncoderFlag = true;
                }



                double x;
                double y;
                double Angle;

                //target lift height
                double tlh ;

                //Telemetry to see what the hell is going on in locationCheck These numbers should be similar to reality
                telemetry.addData("x", currentCoord[0]);
                telemetry.addData("y", currentCoord[1]);
                telemetry.addData("angle", Math.toDegrees(currentCoord[2]));

                telemetry.addData("left",Lencoder);
                telemetry.addData("right",Rencoder);
                telemetry.addData("center",Cencoder);







                //Alright, paperwork done! ITS MOVEMENT TIME


                //Motor Power Format: {frontLeft, frontRight, backLeft, backRight}
                double [] motorPowers = {0,0,0,0};




               if (step == 0)
                {
                    //Mess with this stuff
                    x = 0;
                    y = 40;
                    Angle = Math.toRadians(0);
                    Deacceleration  = true;
                    //End
                    //Now leave the rest alone!



                    if (StartTurn)
                    {
                        Di = OdometryMath.degreePerIn(x,y,Angle,currentCoord);
                        Distance = Math.sqrt((x*x) + (y*y));
                        DeltaTheta = currentCoord[2] - Angle;
                        StartTurn = false;
                    }
                    previousMotorScalingFactor = motorScalingFactor;
                    motorScalingFactor = OdometryMath.motorPowerScaling(x,y,Angle,currentCoord,previousMotorScalingFactor,Distance, Deacceleration ,DeltaTheta );
                    motorPowers = OdometryMath.LinearMovement(x,y,Angle, Di, currentCoord);

                    //moving whith extremities
                    /*
                    //Lift Motor Time
                    tlh = 600;

                    if (liftMotor.getCurrentPosition() - zeroLift < tlh)
                    {
                        liftMotor.setPower(0.5);
                    }
                    else if (liftMotor.getCurrentPosition() - zeroLift > tlh)
                    {
                        liftMotor.setPower(-0.5);
                    }
                    else
                    {
                        liftMotor.setPower(0);
                        liftDone = true;
                    }

                     */



                    if (Math.abs(currentCoord[0] - x) <0.3 && Math.abs(currentCoord[1] - y) < 0.3 && Math.abs(currentCoord[2] - Angle) < Math.toRadians(1.5) /*&& liftDone*/)
                    {
                        StartTurn = true;
                        liftDone = false;
                        frontLeftMotor.setPower(0);
                        frontRightMotor.setPower(0);
                        backLeftMotor.setPower(0);
                        backRightMotor.setPower(0);

                        //step ++;
                    }
                    else
                    {
                        frontLeftMotor.setPower(motorPowers[0] * motorScalingFactor);
                        frontRightMotor.setPower(motorPowers[1] * motorScalingFactor);
                        backLeftMotor.setPower(motorPowers[2] * motorScalingFactor);
                        backRightMotor.setPower(motorPowers[3] * motorScalingFactor);



                    }

                }
               else
               {
                   frontLeftMotor.setPower(0);
                   frontRightMotor.setPower(0);
                   backLeftMotor.setPower(0);
                   backRightMotor.setPower(0);

                   liftMotor.setPower(0);
               }



                telemetry.addData("Step " , step);


                telemetry.update();






            }

        }
    }
}
