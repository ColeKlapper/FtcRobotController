package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class SimpleAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        final double MOTOR_POWER = 0.35;

        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        Movement movement;

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setTargetPosition(0);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setPower(MOTOR_POWER);

        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setTargetPosition(0);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setPower(MOTOR_POWER);

        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setTargetPosition(0);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setPower(MOTOR_POWER);

        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFrontDrive.setTargetPosition(0);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftFrontDrive.setPower(MOTOR_POWER);

        movement = new Movement(leftFrontDrive, rightFrontDrive,
                                leftBackDrive, rightBackDrive);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        movement.moveForward(movement.getTicksFromInches(2.5));
    }
}
