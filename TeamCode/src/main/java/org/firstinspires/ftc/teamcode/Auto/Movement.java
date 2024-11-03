package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Movement {
    private final double PULSES = 480;
    private final double WHEEL_CIRCUMFERENCE_METERS = 0.31;
    private final double PULSES_PER_METER = PULSES / WHEEL_CIRCUMFERENCE_METERS;
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    public Movement(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack) {
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.leftBack = leftBack;
        this.rightBack = rightBack;
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
        return (int) (degree / 90 * 815);
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
