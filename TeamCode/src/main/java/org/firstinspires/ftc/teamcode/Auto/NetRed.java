package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class NetRed extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");

        Servo clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        Servo clawRight = hardwareMap.get(Servo.class, "clawRight");

        Controller controller = new Controller(leftFrontDrive, rightFrontDrive,
                leftBackDrive, rightBackDrive, arm, clawLeft, clawRight);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Feild tiles = 24 inches
        // 24in * 3Tiles - 18in (size of robot) = 72in = 1.3716m ~= 1.3 rounded down
        controller.moveForward(1.3);
        controller.shuffleLeft(0.4); // guessed
    }
}
