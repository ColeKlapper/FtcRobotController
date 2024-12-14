package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class DuoOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        final double DRIVE_SPEED = 1.0;
        final double PRECISION_DRIVE_SPEED = 0.2;
        final double ARM_SPEED = 0.50;
        final double PRECISION_ARM_SPEED = 0.1;

        final double LEFT_CLAW_OPEN_TARGET = 0.9;
        final double RIGHT_CLAW_OPEN_TARGET = 1;
        final double LEFT_CLAW_CLOSED_TARGET = 0.45;
        final double RIGHT_CLAW_CLOSED_TARGET = 0.55;

        final int MAX_ARM_LIMIT = 4955;
        final int MIN_ARM_LIMIT = 5;

        double targetLeftClawPosition = LEFT_CLAW_CLOSED_TARGET;
        double targetRightClawPosition = RIGHT_CLAW_CLOSED_TARGET;

        // Hardware
        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");

        Servo clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        Servo clawRight = hardwareMap.get(Servo.class, "clawRight");

        // Motor settings
        // arm.setPower(ARM_POWER);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm.setTargetPosition(0);
        //arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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
            double playerOneLeftStickY = -gamepad1.left_stick_y;
            double playerOneLeftStickX = gamepad1.left_stick_x * 1.1;
            double rightStickX = gamepad1.right_stick_x;
            double driveSpeed;

            double playerTwoLeftStickY = -gamepad2.left_stick_y * 4;

            double denominator = Math.max(Math.abs(playerOneLeftStickY) + Math.abs(playerOneLeftStickX) + Math.abs(rightStickX), 1);
            double leftFrontMotorPower = (playerOneLeftStickY + playerOneLeftStickX + rightStickX) / denominator;
            double leftBackMotorPower = (playerOneLeftStickY - playerOneLeftStickX + rightStickX) / denominator;
            double rightFrontMotorPower = (playerOneLeftStickY - playerOneLeftStickX - rightStickX) / denominator;
            double rightBackMotorPower = (playerOneLeftStickY + playerOneLeftStickX - rightStickX) / denominator;

            if (gamepad1.left_trigger != 0) {
                driveSpeed = PRECISION_DRIVE_SPEED;
            } else {
                driveSpeed = DRIVE_SPEED;
            }

            // Motor power
            leftFrontDrive.setPower(leftFrontMotorPower * driveSpeed);
            leftBackDrive.setPower(leftBackMotorPower * driveSpeed);
            rightFrontDrive.setPower(rightFrontMotorPower * driveSpeed);
            rightBackDrive.setPower(rightBackMotorPower * driveSpeed);

            // Arm & Claw
            clawLeft.setPosition(targetLeftClawPosition);
            clawRight.setPosition(targetRightClawPosition);

            double armPower = playerTwoLeftStickY / denominator;

            if (gamepad2.left_trigger != 0) {
                armPower *= PRECISION_ARM_SPEED;
            } else {
                armPower *= ARM_SPEED;
            }

            if (leftFrontMotorPower == 0 && leftBackMotorPower == 0 &&
                    rightFrontMotorPower == 0 && rightBackMotorPower == 0) {

                if ((arm.getCurrentPosition() <= MAX_ARM_LIMIT || armPower < 0) &&
                        (arm.getCurrentPosition() >= MIN_ARM_LIMIT || armPower > 0)) {
                    arm.setPower(armPower);
                } else {
                    arm.setPower(0);
                }
            }

            if (gamepad2.b) {
                targetLeftClawPosition = LEFT_CLAW_OPEN_TARGET;
                targetRightClawPosition = RIGHT_CLAW_OPEN_TARGET;
            } else if (gamepad2.x) {
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
            telemetry.addData("Arm Power: ", arm.getPower());
            telemetry.addData("Arm position: ", arm.getCurrentPosition());

            telemetry.update();
        }
    }
}
