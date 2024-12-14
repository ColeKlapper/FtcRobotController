package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class Net extends LinearOpMode {
    @Override
    public void runOpMode() {
        Controller controller = new Controller(hardwareMap);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Feild tiles = 24 inches
        // 24in * 3Tiles - 18in (size of robot) = 72in = 1.3716m ~= 1.3 rounded down
        controller.moveForward(0.64);
        controller.moveArm(ArmState.MEDIUM);
        controller.moveForward(0.1);
        controller.moveArm(ArmState.HIGH);
        controller.changePrecisionMode(PrecisionMode.MOTORS);
        controller.moveBackward(0.6);
        controller.changePrecisionMode(PrecisionMode.MOTORS);
        controller.moveClaw();
        controller.moveArm(ArmState.DOWN);
    }
}