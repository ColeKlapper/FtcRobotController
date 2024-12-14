package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class Observation extends LinearOpMode {
    @Override
    public void runOpMode() {
       Controller controller = new Controller(hardwareMap);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Get to high chamber & score
        controller.moveForward(0.64);
        controller.moveArm(ArmState.MEDIUM);
        controller.moveForward(0.1);
        controller.moveArm(ArmState.HIGH);
        controller.changePrecisionMode(PrecisionMode.MOTORS);
        controller.moveBackward(0.6);
        controller.changePrecisionMode(PrecisionMode.MOTORS);
        controller.moveClaw();
        controller.moveArm(ArmState.DOWN);

        // Move all the way back into the parking zone
        controller.shuffleRight(10);
        controller.moveBackward(0.4);
    }
}
