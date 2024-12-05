package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class Net extends LinearOpMode {
    @Override
    public void runOpMode() {
        final double TILE_SIZE_METERS = 0.6096;
        final double ROBOT_SIZE_METERS = 0.4572;

        DcMotor leftFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorTwo");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "driveMotorOne");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "driveMotorFour");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "driveMotorThree");
        DcMotor arm = hardwareMap.get(DcMotor.class, "arm");

        Servo clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        Servo clawRight = hardwareMap.get(Servo.class, "clawRight");

        Controller controller = new Controller(hardwareMap);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Feild tiles = 24 inches
        // 24in * 3Tiles - 18in (size of robot) = 72in = 1.3716m ~= 1.3 rounded down
        controller.moveForward(TILE_SIZE_METERS * 3 - ROBOT_SIZE_METERS);
        controller.shuffleRight(0.4); // guessed

        // Experimental code
//        controller.moveArm();
//        controller.moveForward(TILE_SIZE_METERS);
//        controller.moveClaw();
//        controller.pivotRight(1);
//        controller.moveArm();
//        controller.moveForward(TILE_SIZE_METERS * 2);
//        controller.shuffleRight(TILE_SIZE_METERS * 2);
    }
}