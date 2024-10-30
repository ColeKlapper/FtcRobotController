package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class SimpleOpMode extends LinearOpMode {

    @Override
    public void runOpMode() {
        final double DRIVE_SPEED = 1.0;

        // Hardware
        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");

        // Motor settings
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

            // Debug
            // telemetry.update();
        }
    }
}
