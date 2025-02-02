package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Controller.*;

abstract class OpModeTools extends LinearOpMode {
    protected final double LEFT_CLAW_OPEN_TARGET = 0.9;
    protected final double LEFT_CLAW_CLOSED_TARGET = 0.43;

    protected final double RIGHT_CLAW_OPEN_TARGET = 1;
    protected final double RIGHT_CLAW_CLOSED_TARGET = 0.55;

    protected final double SECONDARY_CLAW_OPEN = 0.0;
    protected final double SECONDARY_CLAW_HALF_OPEN = 0.1;
    protected final double SECONDARY_CLAW_CLOSED = 0.23;

    protected final double DRIVE_SPEED = 1.0;
    protected final double PRECISION_DRIVE_SPEED = 0.3;

    protected final double PRIMARY_ARM_SPEED = 0.50;
    protected final double PRECISION_PRIMARY_ARM_SPEED = 0.1;

    protected final int TARGET_ARM_HIGH_POSITION = 4930;

    protected final int TARGET_SECONDARY_ARM_REST = 150;
    protected final int TARGET_SECONDARY_ARM_TOP = 290;
    protected final int TARGET_SECONDARY_ARM_PICK_UP = 550;

    protected final int TARGET_ARM_DOWN_POSITION = 0;

    protected DcMotor leftFront;
    protected DcMotor leftBack;
    protected DcMotor rightFront;
    protected DcMotor rightBack;
    protected DcMotor primaryArm;
    protected DcMotor secondaryArm;

    protected Servo clawLeft;
    protected Servo clawRight;
    protected Servo secondaryClaw;

    protected Controller controller;
    protected AutoThread autoThread;
    protected SecondaryArmThread secondaryArmThread;

    protected double targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
    protected double targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;

    protected boolean secondaryArmLongSequenceActivated = false;
    protected boolean secondaryArmShortSequenceActivated = false;
    private boolean rightTriggerHeldDown = false;

    abstract void customOpMode();

    @Override
    public void runOpMode() {
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

        controller = new Controller(leftFront, leftBack, rightFront, rightBack, primaryArm,
                secondaryArm, clawLeft, clawRight, secondaryClaw);
        autoThread = new AutoThread();
        secondaryArmThread = new SecondaryArmThread();

        // Motor settings
        primaryArm.setTargetPosition(TARGET_ARM_DOWN_POSITION);
        secondaryArm.setTargetPosition(TARGET_ARM_DOWN_POSITION);
        secondaryArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        secondaryArm.setPower(SECONDARY_ARM_SPEED);

        clawLeft.setDirection(Servo.Direction.REVERSE);

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Secondary Claw position: ", secondaryClaw.getPosition());
        telemetry.update();
        secondaryArm.setTargetPosition(controller.REST);
        secondaryClaw.setPosition(SECONDARY_CLAW_CLOSED);

        waitForStart();
        customOpMode();
    }

    protected final void checkScoreSpecimen() {
        if (gamepad1.right_trigger != 0) {
            if (!rightTriggerHeldDown && !secondaryArmShortSequenceActivated &&
                    !secondaryArmLongSequenceActivated) {
                rightTriggerHeldDown = true;

                if (!autoThread.isAlive()) {
                    controller.setDcMotorMode(true);
                    autoThread.start();
                } else {
                    controller.setDcMotorMode(false);
                    autoThread.interrupt();
                }

                sleep(3);
            }
        } else {
            rightTriggerHeldDown = false;
        }
    }

    protected final void checkMoveClaw(Gamepad gamepad) {
        if (!secondaryArmShortSequenceActivated) {
            if (primaryArm.getCurrentPosition() <= 800 &&
                    secondaryArm.getCurrentPosition() > controller.REST) {
                controller.moveSecondaryArm(SecondaryArmState.REST);
            } else if (secondaryArm.getCurrentPosition() < controller.TOP) {
                controller.moveSecondaryArm(SecondaryArmState.TOP);
            }
        }

        if (gamepad.x) {
            targetLeftClawPosition = LEFT_CLAW_OPEN_TARGET;
            targetRightClawPosition = RIGHT_CLAW_OPEN_TARGET;
        } else if (gamepad.b) {
            targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
            targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;
        }
    }

    protected final class SecondaryArmThread extends Thread {
        private DcMotor.RunMode previousMode = primaryArm.getMode();
        private Gamepad gamepad;

        private boolean buttonHeldDown = false;

        @Override
        public void run() {

            while (opModeIsActive()) {
                if (gamepad.right_bumper) {
                    if (!buttonHeldDown) {
                        buttonHeldDown = true;

                        if (!secondaryArmLongSequenceActivated) {
                            previousMode = primaryArm.getMode();
                            secondaryArmLongSequenceActivated = true;

                            primaryArm.setPower(1);
                            primaryArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            if (!secondaryArmShortSequenceActivated) {
                                secondaryClaw.setPosition(SECONDARY_CLAW_OPEN);
                                secondaryArm.setTargetPosition(TARGET_SECONDARY_ARM_PICK_UP);

                                clawLeft.setPosition(LEFT_CLAW_OPEN_TARGET);
                                clawRight.setPosition(RIGHT_CLAW_OPEN_TARGET);

                                movePrimaryArm(TARGET_ARM_HIGH_POSITION);
                            } else {
                                clawLeft.setPosition(LEFT_CLAW_OPEN_TARGET);
                                clawRight.setPosition(RIGHT_CLAW_OPEN_TARGET);
                                moveClaw(SECONDARY_CLAW_CLOSED);

                                secondaryArm.setTargetPosition(TARGET_SECONDARY_ARM_TOP);

                                movePrimaryArm(TARGET_ARM_HIGH_POSITION);

                                secondaryArmShortSequenceActivated = false;
                                tradSample();
                            }
                        } else {
                            moveClaw(SECONDARY_CLAW_CLOSED);
                            moveSecondaryArm(TARGET_SECONDARY_ARM_TOP);

                            tradSample();
                        }
                    }
                } else if (gamepad.left_bumper) {
                    if (!buttonHeldDown && !secondaryArmLongSequenceActivated) {
                        if (secondaryArm.getCurrentPosition() < TARGET_SECONDARY_ARM_PICK_UP - 50) {
                            secondaryArmShortSequenceActivated = true;

                            moveSecondaryArm(TARGET_SECONDARY_ARM_PICK_UP);
                            moveClaw(SECONDARY_CLAW_OPEN);
                        } else {
                            moveClaw(SECONDARY_CLAW_CLOSED);
                            moveSecondaryArm(TARGET_SECONDARY_ARM_TOP);
                            secondaryArmShortSequenceActivated = false;
                        }
                    }
                } else {
                    buttonHeldDown = false;
                }

                try {
                    sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void setGamePad(Gamepad gamepad) {
            this.gamepad = gamepad;
            start();
        }

        private void moveClaw(double position) {
            secondaryClaw.setPosition(position);

            try {
                sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void movePrimaryArm(int position) {
            primaryArm.setTargetPosition(position);

            try {
                sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void moveSecondaryArm(int position) {
            secondaryArm.setTargetPosition(position);

            try {
                sleep(1100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void tradSample() {
            final int TARGET_GRAB_SAMPLE_POSITION = 1180;
            moveClaw(SECONDARY_CLAW_HALF_OPEN);
            moveClaw(SECONDARY_CLAW_CLOSED);

            moveSecondaryArm(TARGET_ARM_DOWN_POSITION);
            movePrimaryArm(TARGET_GRAB_SAMPLE_POSITION);

            clawLeft.setPosition(LEFT_CLAW_CLOSED_TARGET);
            clawRight.setPosition(RIGHT_CLAW_CLOSED_TARGET);
            try {
                sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            moveClaw(SECONDARY_CLAW_OPEN);

            movePrimaryArm(TARGET_ARM_HIGH_POSITION);
            moveSecondaryArm(TARGET_SECONDARY_ARM_TOP);

            movePrimaryArm(TARGET_ARM_DOWN_POSITION);
            secondaryClaw.setPosition(SECONDARY_CLAW_CLOSED);
            moveSecondaryArm(TARGET_SECONDARY_ARM_REST);

            primaryArm.setMode(previousMode);
            secondaryArmLongSequenceActivated = false;
        }
    }

    protected final class AutoThread extends Thread {
        @Override
        public void run() {
            controller.scoreSpecimen();
            controller.setDcMotorMode(false);
        }
    }
}
