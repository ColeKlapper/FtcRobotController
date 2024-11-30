package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class SimpleOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        final double DRIVE_SPEED = 1.0;

        final double LEFT_CLAW_OPEN_TARGET = 0.9;
        final double RIGHT_CLAW_OPEN_TARGET = 1;
        final double LEFT_CLAW_CLOSED_TARGET = 0.5;
        final double RIGHT_CLAW_CLOSED_TARGET = 0.6;
        final double ARM_POWER = 1;

        final int TARGET_ARM_UP_POSITION = 4920; // 1230 is 1 rotation, 4920 is 4 rotations
        final int TARGET_ARM_DOWN_POSITION = 0;

        double targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
        double targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;
        int targetArmPosition = TARGET_ARM_DOWN_POSITION;

        // Hardware
        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");

        Servo clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        Servo clawRight = hardwareMap.get(Servo.class, "clawRight");

        // Motor settings
        arm.setTargetPosition(TARGET_ARM_DOWN_POSITION);
        arm.setPower(ARM_POWER);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        clawLeft.setDirection(Servo.Direction.REVERSE);

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Send initialized signal
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Movement code
            double leftStickY = -gamepad1.left_stick_y;
            double leftStickX = gamepad1.left_stick_x * 1.1;
            double rightStickX = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(leftStickY) + Math.abs(leftStickX) + Math.abs(rightStickX), 1);
            double leftFrontMotorPower = (leftStickY + leftStickX + rightStickX) / denominator;
            double leftBackMotorPower = (leftStickY - leftStickX + rightStickX) / denominator;
            double rightFrontMotorPower = (leftStickY - leftStickX - rightStickX) / denominator;
            double rightBackMotorPower = (leftStickY + leftStickX - rightStickX) / denominator;

            // Motor power
            leftFrontDrive.setPower(leftFrontMotorPower * DRIVE_SPEED);
            leftBackDrive.setPower(leftBackMotorPower * DRIVE_SPEED);
            rightFrontDrive.setPower(rightFrontMotorPower * DRIVE_SPEED);
            rightBackDrive.setPower(rightBackMotorPower * DRIVE_SPEED);

            // Arm & Claw
            arm.setTargetPosition(targetArmPosition);
            clawLeft.setPosition(targetLeftClawPosition);
            clawRight.setPosition(targetRightClawPosition);

            if (leftFrontMotorPower == 0 && leftBackMotorPower == 0 &&
                    rightFrontMotorPower == 0 && rightBackMotorPower == 0) {
                if (gamepad1.dpad_up) {
                    targetArmPosition = TARGET_ARM_UP_POSITION;
                } else if (gamepad1.dpad_down) {
                    targetArmPosition = TARGET_ARM_DOWN_POSITION;
                }
            }

            if (gamepad1.b) {
                targetLeftClawPosition = LEFT_CLAW_OPEN_TARGET;
                targetRightClawPosition = RIGHT_CLAW_OPEN_TARGET;
            } else if (gamepad1.x) {
                targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
                targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;
            }

            // Debug
            telemetry.addData("Left Front Motor Power: ", leftFrontDrive.getPower());
            telemetry.addData("Left Back Motor Power: ", leftBackDrive.getPower());
            telemetry.addData("Right Front Motor Power: ", rightFrontDrive.getPower());
            telemetry.addData("Right Back Motor Power: ", rightBackDrive.getPower());
            telemetry.addData("Left claw position: ", clawLeft.getPosition());
            telemetry.addData("Left claw position: ", clawRight.getPosition());
            telemetry.addData("Arm position: ", targetArmPosition);

            telemetry.update();
        }
    }
}
