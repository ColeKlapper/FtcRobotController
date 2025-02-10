package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Controller.*;

abstract class OpModeTools extends LinearOpMode {
    protected final double LEFT_CLAW_OPEN_TARGET = 0.9;
    protected final double LEFT_CLAW_CLOSED_TARGET = 0.41;

    protected final double RIGHT_CLAW_OPEN_TARGET = 1;
    protected final double RIGHT_CLAW_CLOSED_TARGET = 0.53;

    protected final double SECONDARY_CLAW_OPEN = 0.0;
    protected final double SECONDARY_CLAW_HALF_OPEN = 0.18;
    protected final double SECONDARY_CLAW_CLOSED = 0.23;

    protected final double DRIVE_SPEED = 1.0;
    protected final double PRECISION_DRIVE_SPEED = 0.3;

    protected final double PRIMARY_ARM_SPEED = 0.50;
    protected final double PRECISION_PRIMARY_ARM_SPEED = 0.1;

    protected final int TARGET_ARM_HIGH_POSITION = 4930;
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
    protected ArmTradeState armTradeState = ArmTradeState.HOLDING;
    protected Gamepad secondaryArmGamepad;

    protected double targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
    protected double targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;

    protected int targetArmPosition = TARGET_ARM_DOWN_POSITION;

    private final int SECONDARY_ARM_HOLDING = 120;

    private boolean interruptHeldDown = false;
    private boolean abHeldDown = false;
    private boolean stopArmHeldDown = false;
    private boolean mainClawHeldDown = false;
    private boolean primaryClawOpen = false;

    abstract void customOpMode();

    @Override
    public void runOpMode() {
        final double SECONDARY_ARM_SPEED = 0.8;

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
        secondaryClaw.setPosition(SECONDARY_CLAW_CLOSED);

        waitForStart();
        secondaryArm.setTargetPosition(SECONDARY_ARM_HOLDING);
        customOpMode();
    }

    protected final void checkScoreSpecimen() {
        if (gamepad1.right_trigger != 0) {
            if (!interruptHeldDown) {
                interruptHeldDown = true;

                controller.setDcMotorMode(false);

                autoThread.interrupt();
                secondaryArmThread.interrupt();
                secondaryArmThread.rest();
            }
        } else {
            interruptHeldDown = false;
        }

        if (gamepad1.a && gamepad1.y) {
            if (!abHeldDown && armTradeState == ArmTradeState.HOLDING) {
                abHeldDown = true;

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
            abHeldDown = false;
        }
    }

    protected final void checkMoveClaw() {
        if (secondaryArmGamepad.x) {
            if (!mainClawHeldDown) {
                mainClawHeldDown = true;

                if (primaryClawOpen) {
                    targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
                    targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;
                } else {
                    targetLeftClawPosition = LEFT_CLAW_OPEN_TARGET;
                    targetRightClawPosition = RIGHT_CLAW_OPEN_TARGET;
                }

                primaryClawOpen = !primaryClawOpen;
            }
        } else {
            mainClawHeldDown = false;
        }
    }

    protected final class SecondaryArmThread extends Thread {
        public DcMotor.RunMode previousMode = primaryArm.getMode();

        private boolean moveArmHeldDown = false;
        private boolean clawHeldDown = false;
        private boolean clawOpen = false;

        @Override
        public void run() {
            final int SECONDARY_ARM_SUBMERSIBLE = 280;
            final int SECONDARY_ARM_PICK_UP = 390;
            final int SECONDARY_ARM_ALIGNMENT = 355;

            if (secondaryArmGamepad == null) {
                throw new RuntimeException("No secondary arm game pad defined.");
            }

            while (opModeIsActive()) {
                if (secondaryArmGamepad.y) {
                    if (!stopArmHeldDown) {
                        stopArmHeldDown = true;

                        switch (armTradeState) {
                            case PASS:
                                break;
                            case STOP:
                                armTradeState = ArmTradeState.HOLDING;
                                secondaryArm.setTargetPosition(SECONDARY_ARM_HOLDING);
                                break;
                            default:
                                if (primaryArm.getCurrentPosition() <= TARGET_ARM_DOWN_POSITION) {
                                    armTradeState = ArmTradeState.STOP;
                                    previousMode = primaryArm.getMode();

                                    secondaryArm.setTargetPosition(0);
                                }

                                break;
                        }
                    }
                } else {
                    stopArmHeldDown = false;
                }

                if (armTradeState != ArmTradeState.STOP) {
                    if (secondaryArmGamepad.b && armTradeState != ArmTradeState.HOLDING) {
                        if (!clawHeldDown) {
                            clawHeldDown = true;

                            if (clawOpen) {
                                secondaryClaw.setPosition(SECONDARY_CLAW_CLOSED);
                            } else {
                                secondaryClaw.setPosition(SECONDARY_CLAW_OPEN);
                            }

                            clawOpen = !clawOpen;
                        }
                    } else {
                        clawHeldDown = false;
                    }

                    if (secondaryArmGamepad.right_bumper) {
                        if (!moveArmHeldDown) {
                            ArmTradeState previousArmTradeState = armTradeState;

                            moveArmHeldDown = true;
                            armTradeState = ArmTradeState.PASS;
                            previousMode = primaryArm.getMode();

                            primaryArm.setPower(1);
                            primaryArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            clawLeft.setPosition(LEFT_CLAW_OPEN_TARGET);
                            clawRight.setPosition(RIGHT_CLAW_OPEN_TARGET);

                            switch (previousArmTradeState) {
                                case ENTER_SUBMERSIBLE:
                                    moveSecondaryArm(SECONDARY_ARM_SUBMERSIBLE);
                                case ALIGNMENT:
                                    if (!clawOpen) {
                                        moveClaw(SECONDARY_CLAW_OPEN);
                                        clawOpen = true;
                                    }

                                    moveSecondaryArm(SECONDARY_ARM_PICK_UP);
                                case PICK_UP:
                                    if (clawOpen) {
                                        moveClaw(SECONDARY_CLAW_CLOSED);
                                        clawOpen = false;
                                    }
                                case EXIT_SUBMERSIBLE:
                                    moveSecondaryArm(SECONDARY_ARM_HOLDING);
                                case HOLDING:
                                    if (primaryArm.getCurrentPosition() != TARGET_ARM_HIGH_POSITION) {
                                        movePrimaryArm(TARGET_ARM_HIGH_POSITION);
                                    }
                                    tradSample();
                                    break;

                            }
                        }
                    } else if (secondaryArmGamepad.left_bumper) {
                        if (!moveArmHeldDown) {
                            moveArmHeldDown = true;

                            switch (armTradeState) {
                                case HOLDING:
                                    armTradeState = ArmTradeState.ENTER_SUBMERSIBLE;
                                    secondaryArm.setTargetPosition(SECONDARY_ARM_SUBMERSIBLE);
                                    break;
                                case ENTER_SUBMERSIBLE:
                                    armTradeState = ArmTradeState.ALIGNMENT;
                                    secondaryArm.setTargetPosition(SECONDARY_ARM_ALIGNMENT);
                                    break;
                                case ALIGNMENT:
                                    armTradeState = ArmTradeState.PICK_UP;
                                    secondaryArm.setTargetPosition(SECONDARY_ARM_PICK_UP);
                                    break;
                                case PICK_UP:
                                    armTradeState = ArmTradeState.EXIT_SUBMERSIBLE;
                                    moveSecondaryArm(SECONDARY_ARM_SUBMERSIBLE);
                                    moveClaw(SECONDARY_CLAW_HALF_OPEN);
                                    secondaryClaw.setPosition(SECONDARY_CLAW_CLOSED);
                                    break;
                                case EXIT_SUBMERSIBLE:
                                    armTradeState = ArmTradeState.HOLDING;
                                    secondaryArm.setTargetPosition(SECONDARY_ARM_HOLDING);
                                    break;
                            }
                        }
                    } else {
                        moveArmHeldDown = false;
                    }
                }
            }
        }

        public void rest() {
            moveArmHeldDown = false;
            clawHeldDown = false;
            clawOpen = false;

            armTradeState = ArmTradeState.HOLDING;
            primaryArm.setMode(previousMode);
            secondaryClaw.setPosition(SECONDARY_CLAW_CLOSED);

            if (primaryArm.getCurrentPosition() != TARGET_ARM_DOWN_POSITION) {
                movePrimaryArm(TARGET_ARM_HIGH_POSITION);
                secondaryArm.setTargetPosition(SECONDARY_ARM_HOLDING);
            } else {
                secondaryArm.setTargetPosition(SECONDARY_ARM_HOLDING);
            }

            start();
        }

        private void moveClaw(double position) {
            secondaryClaw.setPosition(position);
            pause(300);
        }

        private void movePrimaryArm(int position) {
            primaryArm.setTargetPosition(position);
            pause(2500);
        }

        private void moveSecondaryArm(int position) {
            secondaryArm.setTargetPosition(position);
            pause(700);
        }

        private void tradSample() {
            final int SECONDARY_ARM_PASS = -120;

            moveSecondaryArm(SECONDARY_ARM_PASS);
            movePrimaryArm(TARGET_ARM_DOWN_POSITION);

            clawLeft.setPosition(LEFT_CLAW_CLOSED_TARGET);
            clawRight.setPosition(RIGHT_CLAW_CLOSED_TARGET);
            pause(300);

            moveClaw(SECONDARY_CLAW_OPEN);

            movePrimaryArm(TARGET_ARM_HIGH_POSITION);
            moveSecondaryArm(SECONDARY_ARM_HOLDING);

            moveClaw(SECONDARY_CLAW_CLOSED);

            primaryArm.setMode(previousMode);
            targetArmPosition = TARGET_ARM_HIGH_POSITION;
            armTradeState = ArmTradeState.HOLDING;
        }

        private void pause(long millis) {
            try {
                sleep(millis);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }

    protected final class AutoThread extends Thread {
        @Override
        public void run() {
            controller.scoreSpecimen();
            controller.setDcMotorMode(false);
        }
    }

    protected enum ArmTradeState {
        HOLDING,
        ENTER_SUBMERSIBLE,
        ALIGNMENT,
        PICK_UP,
        EXIT_SUBMERSIBLE,
        PASS,
        STOP,
    }
}
