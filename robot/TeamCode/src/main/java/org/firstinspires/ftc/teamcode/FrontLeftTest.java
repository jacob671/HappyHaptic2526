package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Test: Front Left Only", group="TeleOp")
public class FrontLeftTest extends LinearOpMode {

    private DcMotor frontLeftDrive = null;

    @Override
    public void runOpMode() {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double power = -gamepad1.left_stick_y;
            frontLeftDrive.setPower(power);

            telemetry.addData("Front Left Power", power);
            telemetry.update();
        }
    }
}
