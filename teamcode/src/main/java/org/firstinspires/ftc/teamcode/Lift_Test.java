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

@TeleOp(name="Lift Testing", group="Interactive Opmode")

public class Lift_Test extends OpMode
{
    // Control Hub
    //drive motors
    private DcMotor liftMotor = null;

    private int liftZero;




    @Override
    public void init ()
    {
        // Initialize connection to motors
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        // Set direction to the motors (may need to change depending on orientation of robot)
        liftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        //reset encoders
        liftZero = liftMotor.getCurrentPosition();

        // Send telemetry to the robot
        telemetry.addLine("Working");
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
        telemetry.addData("Lift", liftMotor.getCurrentPosition());
        telemetry.update();


        //liftMotor.setPower(1);
        //liftMotor.getCurrentPosition()

        //True range
        //int liftMotorEncoderRange = 4100;
        //Limited range to prevent top heaviness
        int liftMotorEncoderRange = 3000;

        if (gamepad2.dpad_up)
        {
            if(liftMotor.getCurrentPosition()<4100) {
                liftMotor.setPower(0.75);
            }
            else {
                liftMotor.setPower(0);
            }

        }else if (gamepad2.dpad_down)
        {
            if(liftMotor.getCurrentPosition()>100) {
                liftMotor.setPower(-0.5);
            }
            else {
                liftMotor.setPower(0);
            }

        }
        else
        {
            liftMotor.setPower(0);
        }





    }

    @Override
    public void stop ()
    {
        // I don't know how many years in a row I have to reiterate this, pls do... I second this
        liftMotor.setPower(0.0);

    }
}