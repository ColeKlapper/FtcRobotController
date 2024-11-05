package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class Controller {
    private final double PULSES = 480;
    private final double WHEEL_CIRCUMFERENCE_METERS = 0.31;
    private final double PULSES_PER_METER = PULSES / WHEEL_CIRCUMFERENCE_METERS;
    private final double MOTOR_POWER = 0.35;

    //private final VisionPortal visionPortal;
    private final DcMotor leftFront;
    private final DcMotor rightFront;
    private final DcMotor leftBack;
    private final DcMotor rightBack;

    public Controller(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack) {
        //this.visionPortal = visionPortal;
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.leftBack = leftBack;
        this.rightBack = rightBack;

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setTargetPosition(0);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setPower(MOTOR_POWER);

        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setTargetPosition(0);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setPower(MOTOR_POWER);

        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setTargetPosition(0);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setPower(MOTOR_POWER);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setTargetPosition(0);
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFront.setPower(MOTOR_POWER);
    }

    public final void moveForward(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() + distance;
        int rightFrontTarget = rightFront.getCurrentPosition() + distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void moveBackward(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() - distance;
        int rightBackTarget = rightBack.getCurrentPosition() - distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void pivotLeft(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void pivotRight(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() + distance;
        int rightFrontTarget = rightFront.getCurrentPosition() + distance;
        int leftBackTarget = leftBack.getCurrentPosition() - distance;
        int rightBackTarget = rightBack.getCurrentPosition() - distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void shuffleLeft(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() + distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() - distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void shuffleRight(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() + distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() - distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    private void calculateMovement(int leftFrontTarget, int rightFrontTarget,
                                   int leftBackTarget, int rightBackTarget) {
        leftFront.setTargetPosition(leftFrontTarget);
        rightFront.setTargetPosition(rightFrontTarget);
        leftBack.setTargetPosition(leftBackTarget);
        rightBack.setTargetPosition(rightBackTarget);

        while(Math.abs(leftBack.getCurrentPosition() - leftBackTarget) > 10 ||
                Math.abs(leftFront.getCurrentPosition() - leftFrontTarget) > 5 ||
                Math.abs(rightBack.getCurrentPosition() - rightBackTarget) > 10 ||
                Math.abs(rightFront.getCurrentPosition() - rightFrontTarget) > 10) {
            sleep(10);
        }
        sleep(100);
    }

    private int getPulsesFromMeters(double meters) {
        return (int) (meters * PULSES_PER_METER);
    }

    private int getPulsesFromDegrees(double degree) {
        return (int) (degree / 90 * PULSES);
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
