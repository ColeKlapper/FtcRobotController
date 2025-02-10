package org.firstinspires.ftc.teamcode.Controller;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Controller {
    private final double PULSES = 480;
    private final double WHEEL_CIRCUMFERENCE_METERS = 0.31;
    private final double PULSES_PER_METER = PULSES / WHEEL_CIRCUMFERENCE_METERS;
    private final double PULSES_PER_DEGREE = PULSES / 360;

    private final double MOTOR_POWER = 0.35;
    private final double PRECISION_MOTOR_POWER = 0.15;
    private final double ARM_POWER = 1;
    private final double PRECISION_ARM_POWER = 0.1;
    private final double LEFT_CLAW_CLOSED_TARGET = 0.4;
    private final double RIGHT_CLAW_CLOSED_TARGET = 0.55;

    //private final VisionPortal visionPortal;
    private final DcMotor leftFront;
    private final DcMotor leftBack;
    private final DcMotor rightFront;
    private final DcMotor rightBack;
    private final DcMotor primaryArm;
    private final DcMotor secondaryArm;
    private final Servo clawLeft;
    private final Servo clawRight;
    private final Servo secondaryClaw;

    private boolean openClaw = true;

    public Controller(HardwareMap hardwareMap) {
        final double SECONDARY_ARM_SPEED = 0.1;

        leftFront = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        leftBack = hardwareMap.get(DcMotor.class, "driveMotorOne");
        rightFront = hardwareMap.get(DcMotor.class, "driveMotorFour");
        rightBack = hardwareMap.get(DcMotor.class, "driveMotorThree");
        primaryArm = hardwareMap.get(DcMotor.class, "primaryArm");
        secondaryArm = hardwareMap.get(DcMotor.class, "secondaryArm");

        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");
        secondaryClaw = hardwareMap.get(Servo.class, "secondaryClaw");

        primaryArm.setPower(ARM_POWER);
        primaryArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        moveArm(ArmState.DOWN);
        moveSecondaryArm(SecondaryArmState.START);
        secondaryArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        secondaryArm.setPower(SECONDARY_ARM_SPEED);

        clawLeft.setDirection(Servo.Direction.REVERSE);
        clawLeft.setPosition(LEFT_CLAW_CLOSED_TARGET);
        clawRight.setPosition(RIGHT_CLAW_CLOSED_TARGET);

        setDcMotorMode(true);
        moveSecondaryArm(SecondaryArmState.TOP);
    }

    public Controller(DcMotor leftFrontDrive, DcMotor leftBackDrive, DcMotor rightFrontDrive,
                      DcMotor rightBackDrive, DcMotor primaryArm, DcMotor secondaryArm,
                      Servo clawLeft, Servo clawRight, Servo secondaryClaw) {
        this.leftFront = leftFrontDrive;
        this.leftBack = leftBackDrive;
        this.rightFront = rightFrontDrive;
        this.rightBack = rightBackDrive;
        this.primaryArm = primaryArm;
        this.secondaryArm = secondaryArm;
        this.clawLeft = clawLeft;
        this.clawRight = clawRight;
        this.secondaryClaw = secondaryClaw;

    }

    public final void moveForward(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() + distance;
        int rightFrontTarget = rightFront.getCurrentPosition() + distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        moveToPosition(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void moveBackward(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() - distance;
        int rightBackTarget = rightBack.getCurrentPosition() - distance;

        moveToPosition(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void turnLeft(double degrees) {
        int distance = getPulsesFromDegrees(degrees);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        moveToPosition(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void turnRight(double degrees) {
        int distance = getPulsesFromDegrees(degrees);

        int leftFrontTarget = leftFront.getCurrentPosition() + distance;
        int rightFrontTarget = rightFront.getCurrentPosition() + distance;
        int leftBackTarget = leftBack.getCurrentPosition() - distance;
        int rightBackTarget = rightBack.getCurrentPosition() - distance;

        moveToPosition(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void shuffleLeft(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() - distance;
        int rightFrontTarget = rightFront.getCurrentPosition() + distance;
        int leftBackTarget = leftBack.getCurrentPosition() + distance;
        int rightBackTarget = rightBack.getCurrentPosition() - distance;

        moveToPosition(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void shuffleRight(double meters) {
        int distance = getPulsesFromMeters(meters);

        int leftFrontTarget = leftFront.getCurrentPosition() + distance;
        int rightFrontTarget = rightFront.getCurrentPosition() - distance;
        int leftBackTarget = leftBack.getCurrentPosition() - distance;
        int rightBackTarget = rightBack.getCurrentPosition() + distance;

        moveToPosition(leftFrontTarget, rightFrontTarget, leftBackTarget, rightBackTarget);
    }

    public final void moveArm(ArmState armState) {
        final long WAIT_TIME_ARM = 2000;

        final int TARGET_ARM_UP_FULL_POSITION = 3500; // 1230 is 1 rotation, 4920 is 4 rotations
        final int TARGET_ARM_UP_HALF_POSITION = 3150;
        final int TARGET_ARM_UP_LOW_POSITION = 2000;
        final int TARGET_ARM_DOWN_POSITION = 0;

        switch (armState) {
            case LOW:
                primaryArm.setTargetPosition(TARGET_ARM_UP_LOW_POSITION);
                moveSecondaryArm(SecondaryArmState.TOP);
                break;
            case MEDIUM:
                primaryArm.setTargetPosition(TARGET_ARM_UP_HALF_POSITION);
                moveSecondaryArm(SecondaryArmState.TOP);
                break;
            case HIGH:
                primaryArm.setTargetPosition(TARGET_ARM_UP_FULL_POSITION);
                moveSecondaryArm(SecondaryArmState.TOP);
                break;
            case DOWN:
            default:
                primaryArm.setTargetPosition(TARGET_ARM_DOWN_POSITION);
                break;
        }

        sleep(WAIT_TIME_ARM);
    }

    public final void moveSecondaryArm(SecondaryArmState armState) {
        final int REST = 110;
        final int TOP = 120;
        final int PICK_UP = 516;

        switch (armState) {
            case PICK_UP:
                secondaryArm.setTargetPosition(PICK_UP);
                break;
            case REST:
                secondaryArm.setTargetPosition(REST);
                break;
            case TOP:
                secondaryArm.setTargetPosition(TOP);
                break;
            case START:
            default:
                secondaryArm.setTargetPosition(0);
                break;
        }
    }

    public final void moveClaw() {
        final double LEFT_CLAW_OPEN_TARGET = 0.9;
        final double RIGHT_CLAW_OPEN_TARGET = 1;
        final long WAIT_TIME_CLAW = 1000;

        if (openClaw) {
            clawLeft.setPosition(LEFT_CLAW_OPEN_TARGET);
            clawRight.setPosition(RIGHT_CLAW_OPEN_TARGET);
        } else {
            clawLeft.setPosition(LEFT_CLAW_CLOSED_TARGET);
            clawRight.setPosition(RIGHT_CLAW_CLOSED_TARGET);
        }

        openClaw = !openClaw;
        sleep(WAIT_TIME_CLAW);
    }

    public final void setDcMotorMode(boolean autoOn) {
        if (autoOn) {
            rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            rightBack.setTargetPosition(0);
            rightFront.setTargetPosition(0);
            leftBack.setTargetPosition(0);
            leftFront.setTargetPosition(0);

            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftFront.setDirection(DcMotor.Direction.FORWARD);
            leftBack.setDirection(DcMotor.Direction.FORWARD);
            rightFront.setDirection(DcMotor.Direction.REVERSE);
            rightBack.setDirection(DcMotor.Direction.REVERSE);

            leftBack.setPower(MOTOR_POWER);
            rightFront.setPower(MOTOR_POWER);
            leftFront.setPower(MOTOR_POWER);
            rightBack.setPower(MOTOR_POWER);
        } else {
            leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public final void scoreSpecimen() {
        moveForward(0.64);
        moveArm(ArmState.MEDIUM);
        moveForward(0.1);
        moveArm(ArmState.HIGH);
        moveClaw();
        moveArm(ArmState.DOWN);
        sleep(1700);
        moveSecondaryArm(SecondaryArmState.REST);
        sleep(800);
    }

    public final void changePrecisionMode(PrecisionMode precisionMode) {
        switch (precisionMode) {
            case ARM:
                if (primaryArm.getPower() == ARM_POWER) {
                    primaryArm.setPower(PRECISION_ARM_POWER);
                } else {
                    primaryArm.setPower(ARM_POWER);
                }

                break;
            case MOTORS:
            default:
                double motorPower = MOTOR_POWER;

                if (leftFront.getPower() == MOTOR_POWER && rightFront.getPower() == MOTOR_POWER &&
                        leftBack.getPower() == MOTOR_POWER && rightBack.getPower() == MOTOR_POWER) {
                    motorPower = PRECISION_MOTOR_POWER;
                }

                leftFront.setPower(motorPower);
                rightFront.setPower(motorPower);
                leftBack.setPower(motorPower);
                rightBack.setPower(motorPower);

                break;
        }
    }

    private void moveToPosition(int leftFrontTarget, int rightFrontTarget,
                                int leftBackTarget, int rightBackTarget) {
        final long WAIT_TIME_MOTORS = 800;

        leftFront.setTargetPosition(leftFrontTarget);
        rightFront.setTargetPosition(rightFrontTarget);
        leftBack.setTargetPosition(leftBackTarget);
        rightBack.setTargetPosition(rightBackTarget);

        sleep(WAIT_TIME_MOTORS);
    }

    private int getPulsesFromMeters(double meters) {
        return (int) (meters * PULSES_PER_METER);
    }

    public int getPulsesFromDegrees(double degree) {
        return (int) (degree * PULSES_PER_DEGREE);
    }

    private void sleep(long waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
