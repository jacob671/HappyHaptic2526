# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FTC (First Tech Challenge) team 26532 — **Happy Haptic Doctors**, season 2025-2026.

This repo has two top-level directories:
- `robot/` — Android Gradle project containing all robot control code, built on the FTC SDK 11.1.0
- `controller/` — Currently empty; reserved for future use (scouting/analysis, possibly Python)

## Building

All build commands run from inside `robot/`:

```bash
cd robot
./gradlew assembleDebug      # build debug APK
./gradlew assembleRelease    # build release APK
./gradlew :TeamCode:build    # build only the TeamCode module
```

Deployment to the REV Control Hub (or Android robot controller phone) is done via Android Studio's "Run" button (green arrow), which also builds. There is no automated test runner — testing is physical, on the robot.

## Architecture

### Module layout

```
robot/
  FtcRobotController/   # FTC SDK — do not modify
  TeamCode/             # Team code lives here
    src/main/java/org/firstinspires/ftc/teamcode/
```

All team-written Java goes in `robot/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.

`FtcRobotController/external/samples/` contains reference sample OpModes that can be copied into TeamCode as starting points. Do not edit files inside `FtcRobotController/`.

### OpMode types

FTC programs are called **OpModes**. There are two styles:

**Linear** (preferred for most uses):
```java
@TeleOp(name="My OpMode", group="TeleOp")
public class MyOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        // hardware init here
        waitForStart();
        while (opModeIsActive()) {
            // loop logic here
            telemetry.update();
        }
    }
}
```

**Iterative**:
```java
@TeleOp(name="My OpMode", group="TeleOp")
public class MyOpMode extends OpMode {
    @Override public void init() { /* setup */ }
    @Override public void loop() { /* runs repeatedly */ }
}
```

Use `@Autonomous` instead of `@TeleOp` for autonomous period OpModes.

Remove `@Disabled` (or don't include it) to make an OpMode appear on the Driver Station menu.

### Hardware access

Hardware devices are accessed via `hardwareMap` using the string names assigned in the robot configuration file (set up in the FTC Robot Controller app):

```java
DcMotor motor = hardwareMap.get(DcMotor.class, "motor_name");
Servo servo   = hardwareMap.get(Servo.class, "servo_name");
```

The string names must match exactly what is configured on the robot. Mismatches cause runtime exceptions.

### Hardware abstraction pattern

The recommended pattern (shown in `FtcRobotController/external/samples/externalhardware/`) is to create a separate `RobotHardware` class that:
- Takes a `LinearOpMode` reference in its constructor
- Initializes all hardware in an `init()` method
- Exposes higher-level methods (e.g., `driveRobot()`, `setHandPositions()`) rather than raw hardware objects

This lets multiple OpModes share hardware setup without duplicating code.

### Telemetry

Use `telemetry` to display values on the Driver Station:
```java
telemetry.addData("Label", value);
telemetry.update();  // must call update() to push data to DS
```

### Key SDK dependencies (FTC SDK 11.1.0)

- `com.qualcomm.robotcore.hardware.*` — motors, servos, sensors
- `com.qualcomm.robotcore.eventloop.opmode.*` — `LinearOpMode`, `OpMode`, annotations
- `com.qualcomm.robotcore.util.ElapsedTime`, `Range` — timing and clamping utilities
- `org.firstinspires.ftc.vision.*` — camera/AprilTag vision
