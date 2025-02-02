package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Controller.SecondaryArmState;

@TeleOp
public class SingleOpMode extends OpModeTools {
    @Override
    protected void customOpMode() {
        final double ARM_POWER = 1;

        final int TARGET_ARM_MEDIUM_POSITION = 3500; // 1230 is 1 rotation, 4920 is 4 rotations
        final int TARGET_ARM_LOW_POSITION = 3150;

        int targetArmPosition = TARGET_ARM_DOWN_POSITION;

        primaryArm.setPower(ARM_POWER);
        primaryArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        secondaryArmThread.setGamePad(gamepad1);

        while (opModeIsActive()) {
            checkScoreSpecimen();

            if (!autoThread.isAlive()) {
                // Movement code
                double leftStickY = -gamepad1.left_stick_y;
                double leftStickX = gamepad1.left_stick_x * 1.1;
                double rightStickX = gamepad1.right_stick_x;
                double driveSpeed;

                double denominator = Math.max(Math.abs(leftStickY) + Math.abs(leftStickX) + Math.abs(rightStickX), 1);
                double leftFrontMotorPower = (leftStickY + leftStickX + rightStickX) / denominator;
                double leftBackMotorPower = (leftStickY - leftStickX + rightStickX) / denominator;
                double rightFrontMotorPower = (leftStickY - leftStickX - rightStickX) / denominator;
                double rightBackMotorPower = (leftStickY + leftStickX - rightStickX) / denominator;

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
                    if (leftFrontMotorPower == 0 && leftBackMotorPower == 0 &&
                            rightFrontMotorPower == 0 && rightBackMotorPower == 0) {
                        if (gamepad1.dpad_up) {
                            targetArmPosition = TARGET_ARM_MEDIUM_POSITION;
                            controller.moveSecondaryArm(SecondaryArmState.TOP);
                        } else if (gamepad1.dpad_down) {
                            targetArmPosition = TARGET_ARM_DOWN_POSITION;
                        } else if (gamepad1.dpad_left) {
                            targetArmPosition = TARGET_ARM_HIGH_POSITION;
                            controller.moveSecondaryArm(SecondaryArmState.TOP);
                        } else if (gamepad1.dpad_right) {
                            targetArmPosition = TARGET_ARM_LOW_POSITION;
                            controller.moveSecondaryArm(SecondaryArmState.TOP);
                        }
                    }

                    // Arm & Claw
                    primaryArm.setTargetPosition(targetArmPosition);
                    clawLeft.setPosition(targetLeftClawPosition);
                    clawRight.setPosition(targetRightClawPosition);

                    checkMoveClaw(gamepad1);
                }
            }
        }
    }
}
