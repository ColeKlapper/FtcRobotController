package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class Controller {
    private final double PULSES = 480;
    private final double WHEEL_CIRCUMFERENCE_METERS = 0.31;
    private final double PULSES_PER_METER = PULSES / WHEEL_CIRCUMFERENCE_METERS;
    private final double PULSES_PER_DEGREE = PULSES / 360;

    private final double MOTOR_POWER = 0.35;
    private final double ARM_POWER = 1;
    private final double LEFT_CLAW_OPEN_TARGET = 0.9;
    private final double RIGHT_CLAW_OPEN_TARGET = 1;
    private final double LEFT_CLAW_CLOSED_TARGET = 0.5;
    private final double RIGHT_CLAW_CLOSED_TARGET = 0.6;

    private final long WAIT_TIME = 500;

    private final int TARGET_ARM_UP_FULL_POSITION = 4920; // 1230 is 1 rotation, 4920 is 4 rotations
    private final int TARGET_ARM_UP_HALF_POSITION = 4100;
    private final int TARGET_ARM_UP_LOW_POSITION = 2400;
    private final int TARGET_ARM_DOWN_POSITION = 0;

    //private final VisionPortal visionPortal;
    private final DcMotor leftFront;
    private final DcMotor leftBack;
    private final DcMotor rightFront;
    private final DcMotor rightBack;
    private final DcMotor arm;
    private final Servo clawLeft;
    private final Servo clawRight;

    public Controller(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        leftBack = hardwareMap.get(DcMotor.class, "driveMotorOne");
        rightFront = hardwareMap.get(DcMotor.class, "driveMotorFour");
        rightBack = hardwareMap.get(DcMotor.class, "driveMotorThree");
        arm = hardwareMap.get(DcMotor.class, "arm");

        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

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

        arm.setTargetPosition(TARGET_ARM_DOWN_POSITION);
        arm.setPower(ARM_POWER);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        clawLeft.setDirection(Servo.Direction.REVERSE);
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

    public final void turnLeft(double degrees) {
        int distance = getPulsesFromDegrees(degrees);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        calculateMovement(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void turnRight(double degrees) {
        int distance = getPulsesFromDegrees(degrees);

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

    public final void moveArm(ArmState armState) {
        switch (armState) {
            case LOW:
                arm.setTargetPosition(TARGET_ARM_UP_LOW_POSITION);
                break;
            case MEDIUM:
                arm.setTargetPosition(TARGET_ARM_UP_HALF_POSITION);
                break;
            case HIGH:
                arm.setTargetPosition(TARGET_ARM_UP_FULL_POSITION);
                break;
            case DOWN:
            default:
                arm.setTargetPosition(TARGET_ARM_DOWN_POSITION);
                break;
        }

        sleep();
    }

    public final void moveClaw() {
        if (clawLeft.getPosition() == LEFT_CLAW_CLOSED_TARGET &&
                clawRight.getPosition() == RIGHT_CLAW_CLOSED_TARGET) {
            clawLeft.setPosition(LEFT_CLAW_OPEN_TARGET);
            clawRight.setPosition(RIGHT_CLAW_OPEN_TARGET);
        } else {
            clawLeft.setPosition(LEFT_CLAW_CLOSED_TARGET);
            clawRight.setPosition(RIGHT_CLAW_CLOSED_TARGET);
        }

        sleep();
    }

    private void calculateMovement(int leftFrontTarget, int rightFrontTarget,
                                   int leftBackTarget, int rightBackTarget) {
        leftFront.setTargetPosition(leftFrontTarget);
        rightFront.setTargetPosition(rightFrontTarget);
        leftBack.setTargetPosition(leftBackTarget);
        rightBack.setTargetPosition(rightBackTarget);

        sleep();
    }

    private int getPulsesFromMeters(double meters) {
        return (int) (meters * PULSES_PER_METER);
    }

    private int getPulsesFromDegrees(double degree) {
        return (int) (degree * PULSES_PER_DEGREE);
    }

    private void sleep() {
        try {
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
