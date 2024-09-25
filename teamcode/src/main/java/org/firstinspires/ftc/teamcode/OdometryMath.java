package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.util.Range;

public class OdometryMath {

    //Distance in inches between the encoder and the center of the robot's roatation.


    // Tracking Math
    @NonNull
    public static double[] locationCheck(double l, double r, double s, double[] last, double[] globalCoord) {


        //last 0 = l, 1 = r, 2 = s, 3 = angle


        final double LeftDist = 6.918;
        final double RightDist = 6.918;
        final double CentDist = 5.9;
        double angle;


        double x;
        double y;

        //constant is the error of (theoretical angle/actual angle), so that the too numbers will match.

        final double Constant = 0.751001;

        //Last covers previous encoder position (l,r,s)
        l -= last[0];
        r -= last[1];
        s -= last[2];

        // Scale the numbers down to fit reality
        l *= Constant;
        r *= Constant;
        s *= Constant;


        //Math Starts
        //If nothing changed, go away
        if (l == 0 && r == 0 && s == 0) {
            return globalCoord;
        }


        //Figure out angle (heading)
        angle = (((r - l) / (LeftDist + RightDist)));


        //Reduce left and right counting to account for the distance traveled to turn (angle). Uses Arc Length Theorum.
        s += (angle * CentDist);
        r -= (angle * RightDist);

        //calculates direction traveling (magnatude)
        double theta = Math.atan2(r, s);

        //Distance Formula :)
        double R = Math.sqrt((s * s) + (r * r));

        //Bs that spins the distance around so that the direction is its own thing. Sucessfully calculates the change in x and y (delta)
        x = R * Math.cos((theta - (Math.PI / 2)) + globalCoord[2] + (angle / 2));
        y = R * Math.sin((theta - (Math.PI / 2)) + globalCoord[2] + (angle / 2));

        // Add Delta x and y into main coordinate system. Update Successful  (Packaged values)
        globalCoord[0] += x;
        globalCoord[1] += y;
        globalCoord[2] += angle;

        //Ship the values back into the main function.
        return globalCoord;

    }

    //MOVING TIME
    public static double[] LinearMovement(double x, double y, double angle, double Di, double[] globalCoord) {
        //Calculate Delta x and y values
        double Dx = x - globalCoord[0];
        double Dy = y - globalCoord[1];


        double Dtheta = Math.atan2(Dy, Dx) - globalCoord[2] + (Math.PI / 2);

        //Dx and Dy reused to figure out where the robot needs to go
        Dx = Math.cos(Dtheta);
        Dy = Math.sin(Dtheta);

        double frontLeftPan, frontRightPan, backLeftPan, backRightPan;


        //Driving Powers without turning.
        frontLeftPan = Range.clip(Dy + Dx, -1.0, 1.0);
        frontRightPan = Range.clip(Dy - Dx, -1.0, 1.0);
        backLeftPan = Range.clip(Dy - Dx, -1.0, 1.0);
        backRightPan = Range.clip(Dy + Dx, -1.0, 1.0);


        //Detects which direction the robot needs to turn
        double turn = globalCoord[2] - angle;


        //total is used for finding the percentage or "weight" of the input
        double total;
        //tempturn is used to hold the variable "turn" so that we can change it to positive to determine weight
        double tempTurn;
        //Percentage calculates the weight, where 1 is the max
        double driveWeight;
        double turnWeight;

        double frontLeftPower;
        double frontRightPower;
        double backLeftPower;
        double backRightPower;

        //Sets power direcrtions based on the direction the robot needs to turn.



        if (turn > 0)
        {
            tempTurn = 0.6;
        }
        else if (turn < 0)
        {
            tempTurn = -0.6;
        }
        else
        {
            tempTurn = 0;
        }

        //If the robot is close enough to
        if (Math.abs(globalCoord[0] - x) < 0.2 && Math.abs(globalCoord[1] - y) < 0.2) {
            frontLeftPan = 0;
            frontRightPan = 0;
            backLeftPan = 0;
            backRightPan = 0;

        }


        //Front Left Wheel
        total = Math.abs(tempTurn) + Math.abs(frontLeftPan);
        if (total > 1) {
            driveWeight = Math.abs(frontLeftPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            frontLeftPower = (frontLeftPan * driveWeight) + (tempTurn * turnWeight);

        } else {
            frontLeftPower = frontLeftPan + tempTurn;
        }


        //Front Right Wheel
        total = Math.abs(tempTurn) + Math.abs(frontRightPan);
        if (total > 1) {
            driveWeight = Math.abs(frontRightPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            frontRightPower = (frontRightPan * driveWeight) - (tempTurn * turnWeight);

        } else {
            frontRightPower = frontRightPan - tempTurn;
        }

        //Back Left Wheel
        total = Math.abs(tempTurn) + Math.abs(backLeftPan);
        if (total > 1) {
            driveWeight = Math.abs(backLeftPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            backLeftPower = (backLeftPan * driveWeight) + (tempTurn * turnWeight);

        } else {
            backLeftPower = backLeftPan + tempTurn;
        }

        //Back Right Wheel
        total = Math.abs(tempTurn) + Math.abs(backRightPan);
        if (total > 1) {
            driveWeight = Math.abs(backRightPan) / total;
            turnWeight = Math.abs(tempTurn) / total;

            backRightPower = (backRightPan * driveWeight) - (tempTurn * turnWeight);

        } else {
            backRightPower = backRightPan - tempTurn;
        }








        //Package the motor powers to send back to the main function
        //FL, FR, BL, BR
        double[] Powers = {frontLeftPower, frontRightPower, backLeftPower, backRightPower};

        return Powers;

    }

    public static double degreePerIn(double x, double y, double finalTheta, double[] globalCoord) {
        double Dx = x - globalCoord[0];
        double Dy = y - globalCoord[1];


        double Dtheta = Math.atan2(Dy, Dx) - globalCoord[2] + (Math.PI / 2);

        Dx = Math.cos(Dtheta);
        Dy = Math.sin(Dtheta);

        double Di = Math.sqrt((Dx * Dx) + (Dy * Dy));
        Dtheta = globalCoord[2] - finalTheta;

        double degreePerIn = Dtheta / Di;

        return degreePerIn;

    }


    public static double motorPowerScaling(double x, double y, double finalTheta, double[] globalCoord, double currentMotorPower, double Distance, boolean traditional,double DeltaTheta) {
        //traditional = true   == it will speed up and then slow down
        //traditional = false == it will only speed up. This will be used if you want to string 2 target positions without stopping


        //These are the limit powers. Change these to your liking
        double minPower = 0.4;
        double maxPower = 1;


        //Determines when the robot will begin to slow down in comparason to it's speed

        x -= globalCoord[0];
        y -= globalCoord[1];

        double CurrentDistance = Math.sqrt((x * x) + (y * y));
        double AccelerationChange = Distance * 0.6;
        double MaxDist = 5;


        //turning on a point variables
        double AngleNeeded = globalCoord[2] - finalTheta; //(currentDist)
        double TurnAccChange = DeltaTheta*0.3;



        //flags for deciding which function to use


        if (Math.abs(globalCoord[0] - x) < 0.3 && Math.abs(globalCoord[1] - y) < 0.3)
        {
            if (Math.abs(AngleNeeded) > Math.abs(TurnAccChange))
            {
                currentMotorPower = 1;
                return currentMotorPower;
            }
            else
            {
                currentMotorPower -= 0.3;
                if(currentMotorPower < minPower)
                {
                    currentMotorPower = minPower;
                }
                return currentMotorPower;
            }

        }

            if (traditional) {
                if (AccelerationChange > MaxDist) {
                    AccelerationChange = MaxDist;
                }


                if (CurrentDistance > AccelerationChange)
                {
                    currentMotorPower = maxPower;

                    return currentMotorPower;
                } else
                {
                    currentMotorPower -= 0.2;

                    if (currentMotorPower < minPower)
                    {
                        currentMotorPower = minPower;
                    }


                    return currentMotorPower;

                }
            } else {
                currentMotorPower += 0.05;

                if (currentMotorPower > maxPower) {
                    currentMotorPower = maxPower;
                }
                return currentMotorPower;
                }



    }
}



