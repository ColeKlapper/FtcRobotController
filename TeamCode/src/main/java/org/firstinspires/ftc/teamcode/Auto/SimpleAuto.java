package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class SimpleAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        Controller controller = new Controller(leftFrontDrive, rightFrontDrive,
                leftBackDrive, rightBackDrive);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        controller.moveForward(2.5);
    }
}
