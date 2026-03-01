package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="First Auto", group="Linear OpMode")
public class FirstAuto extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor frontLeftDrive  = null;
    private DcMotor backLeftDrive   = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive  = null;

    // GoBILDA chassis constants (312 RPM motors, 104mm mecanum wheels)
    static final double TICKS_PER_REV     = 537.7;
    static final double WHEEL_DIAMETER_IN = 4.0945;  // 104mm
    static final double TICKS_PER_INCH    = TICKS_PER_REV / (WHEEL_DIAMETER_IN * Math.PI);

    // Mecanum wheels slip sideways, so strafing needs more ticks than driving.
    // Tune this on your actual robot — start at 1.1 and adjust.
    static final double STRAFE_CORRECTION = 1.1;

    static final double DRIVE_SPEED       = 0.1;
    static final double TIMEOUT_SECONDS   = 2.0; // give up on a move after this long

    @Override
    public void runOpMode() {

        frontLeftDrive  = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive   = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive  = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Hold position when power is 0 instead of coasting
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        resetEncoders();

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();
        runtime.reset();

        drive(10);

        telemetry.addData("Status", "Done");
        telemetry.update();
    }

    /** Drive forward (positive) or backward (negative) a number of inches. */
    private void drive(double inches) {
        int ticks = (int)(inches * TICKS_PER_INCH);
        moveMotors(-ticks, ticks, -ticks, ticks);
    }

    /** Strafe right (positive) or left (negative) a number of inches. */
    private void strafe(double inches) {
        int ticks = (int)(inches * TICKS_PER_INCH * STRAFE_CORRECTION);
        moveMotors(ticks, -ticks, ticks, -ticks);
    }

    /**
     * Turn clockwise (positive) or counter-clockwise (negative) by degrees.
     * TURN_TICKS_PER_DEGREE is an approximation — tune it on your robot.
     */
    private void turn(double degrees) {
        final double TURN_TICKS_PER_DEGREE = 8.0;
        int ticks = (int)(degrees * TURN_TICKS_PER_DEGREE);
        moveMotors(-ticks, ticks, ticks, -ticks);
    }

    /**
     * Core move: sets each motor's target relative to its current position,
     * then waits until all motors reach their targets or the timeout expires.
     *
     * Wheel signs by direction:
     *   Forward:      FL+  FR+  BL+  BR+
     *   Strafe right: FL+  FR-  BL+  BR-
     *   Turn CW:      FL-  FR+  BL+  BR-
     */
    private void moveMotors(int fl, int fr, int bl, int br) {
        resetEncoders();

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeftDrive.setTargetPosition(fl);
        frontRightDrive.setTargetPosition(fr);
        backLeftDrive.setTargetPosition(bl);
        backRightDrive.setTargetPosition(br);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(DRIVE_SPEED);
        backLeftDrive.setPower(DRIVE_SPEED);
        backRightDrive.setPower(DRIVE_SPEED);

        while (opModeIsActive() && runtime.seconds() < TIMEOUT_SECONDS &&
               (frontLeftDrive.isBusy() || frontRightDrive.isBusy() ||
                backLeftDrive.isBusy()  || backRightDrive.isBusy())) {
            telemetry.addData("FL", frontLeftDrive.getCurrentPosition());
            telemetry.addData("FR", frontRightDrive.getCurrentPosition());
            telemetry.addData("BL", backLeftDrive.getCurrentPosition());
            telemetry.addData("BR", backRightDrive.getCurrentPosition());
            telemetry.update();
        }

        stopMotors();
    }

    private void resetEncoders() {
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void stopMotors() {
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }
}
