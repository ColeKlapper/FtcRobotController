package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Controller.SecondaryArmState;

@TeleOp
public class DuoOpMode extends OpModeTools {
    @Override
    protected void customOpMode() {
        final int MAX_ARM_LIMIT = 4955;
        final int MIN_ARM_LIMIT = 5;

        // Motor settings
        primaryArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        primaryArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        secondaryArmThread.setGamePad(gamepad2);

        while (opModeIsActive()) {
            checkScoreSpecimen();

            if (!autoThread.isAlive()) {
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
                leftFront.setPower(leftFrontMotorPower * driveSpeed);
                leftBack.setPower(leftBackMotorPower * driveSpeed);
                rightFront.setPower(rightFrontMotorPower * driveSpeed);
                rightBack.setPower(rightBackMotorPower * driveSpeed);

                if (!secondaryArmLongSequenceActivated) {
                    double armPower = playerTwoLeftStickY / denominator;

                    // Arm & Claw
                    clawLeft.setPosition(targetLeftClawPosition);
                    clawRight.setPosition(targetRightClawPosition);

                    if (gamepad2.left_trigger != 0) {
                        armPower *= PRECISION_PRIMARY_ARM_SPEED;
                    } else {
                        armPower *= PRIMARY_ARM_SPEED;
                    }

                    if ((primaryArm.getCurrentPosition() <= MAX_ARM_LIMIT || armPower < 0) &&
                            (primaryArm.getCurrentPosition() >= MIN_ARM_LIMIT || armPower > 0)) {
                        primaryArm.setPower(armPower);
                    } else {
                        primaryArm.setPower(0);
                    }

                    checkMoveClaw(gamepad2);
                }
            }

            // Debug
//            telemetry.addData("Left Front Motor Power: ", leftFront.getPower());
//            telemetry.addData("Left Back Motor Power: ", leftBack.getPower());
//            telemetry.addData("Right Front Motor Power: ", rightFront.getPower());
//            telemetry.addData("Right Back Motor Power: ", rightBack.getPower());
//            telemetry.addData("Left claw position: ", clawLeft.getPosition());
//            telemetry.addData("Left claw position: ", clawRight.getPosition());
//            telemetry.addData("Arm Power: ", primaryArm.getPower());
//            telemetry.addData("Arm position: ", primaryArm.getCurrentPosition());
//            telemetry.addData("Secondary Arm Position: ", secondaryArm.getCurrentPosition());
//            telemetry.update();
        }
    }
}