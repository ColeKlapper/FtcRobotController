package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

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
        controller.moveForward(1);
        controller.moveArm(ArmState.HIGH);
        controller.moveForward(0.2);
        controller.moveArm(ArmState.MEDIUM);
        controller.moveBackward(0.3);
        controller.moveClaw();
        controller.moveArm(ArmState.DOWN);
        controller.moveBackward(0.8);

        // Move right & grab specimen
        controller.shuffleRight(1.2);
        controller.moveClaw();

        // Move to net & turn
        controller.shuffleLeft(3);
        controller.turnRight(180);

        // Place specimen in net
        controller.moveArm(ArmState.MEDIUM);
        controller.moveForward(0.2);
        controller.moveClaw();
        controller.moveBackward(0.2);

        // Move all the way back into the parking zone
        controller.shuffleRight(3);
    }
}
