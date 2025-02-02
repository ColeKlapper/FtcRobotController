package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Controller.*;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class Net extends LinearOpMode {
    @Override
    public void runOpMode() {
        Controller controller = new Controller(hardwareMap);

        // Telemetry Initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Get to high chamber & score
        controller.scoreSpecimen();
    }
}