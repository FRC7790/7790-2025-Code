package frc.robot.subsystems;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants.TargetClassConstants;

public class TargetClass {

    // Field width based on blue alliance origin. Update this value as required.
    private static final double FIELD_WIDTH = 17.55;
    private static final double FIELD_LENGTH = 8.05;

    private double x;
    private double y;
    private double z;
    private boolean isLeft;
    private int level;
    private int face;
    private boolean isSource;
    private String name;

    public TargetClass(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isSource() {
        return isSource;
    }
    public void setSource(boolean isSource) {
        this.isSource = isSource;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getFace() {
        return face;
    }
    public void setFace(int face) {
        this.face = face;
    }


    public static TargetClass GetTargetByName(String name) {
        TargetClass target = new TargetClass(name);
        // Set properties based on the name.

        //Face 0-5
        //Level 0-3
        //0 left, 1 right

        switch(name) {
            case "SL":
                target.setX(TargetClassConstants.SLPositionX);
                target.setY(TargetClassConstants.SLPositionY);
                target.setZ(TargetClassConstants.SLPositionZ);
                target.setSource(true);
                break;
            case "SR":
                target.setX(TargetClassConstants.SRPositionX);
                target.setY(TargetClassConstants.SRPositionY);
                target.setZ(TargetClassConstants.SRPositionZ);
                target.setSource(true);
                break;
            case "CL":
                target.setX(TargetClassConstants.CLPositionX);
                target.setY(TargetClassConstants.CLPositionY);
                target.setZ(TargetClassConstants.CLPositionZ);
                target.setSource(true);
                break;
            case "CC":
                target.setX(TargetClassConstants.CCPositionX);
                target.setY(TargetClassConstants.CCPositionY);
                target.setZ(TargetClassConstants.CCPositionZ);
                target.setSource(true);
                break;
            case "CR":
                target.setX(TargetClassConstants.CRPositionX);
                target.setY(TargetClassConstants.CRPositionY);
                target.setZ(TargetClassConstants.CRPositionZ);
                target.setSource(true);
                break;
            case "Processor":
                target.setX(TargetClassConstants.ProcessorPositionX);
                target.setY(TargetClassConstants.ProcessorPositionY);
                target.setZ(TargetClassConstants.ProcessorPositionZ);
                target.setSource(true);
                break;
            case "C100":
                target.setX(TargetClassConstants.xValueC1X0);
                target.setY(TargetClassConstants.yValueC1X0);
                target.setZ(TargetClassConstants.zValueC10X);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C101":
                target.setX(TargetClassConstants.xValueC1X1);
                target.setY(TargetClassConstants.yValueC1X1);
                target.setZ(TargetClassConstants.zValueC10X);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C110":
                target.setX(TargetClassConstants.xValueC1X0);
                target.setY(TargetClassConstants.yValueC1X0);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C111":
                target.setX(TargetClassConstants.xValueC1X1);
                target.setY(TargetClassConstants.yValueC1X1);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C120":
                target.setX(TargetClassConstants.xValueC1X0);
                target.setY(TargetClassConstants.yValueC1X0);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C121":
                target.setX(TargetClassConstants.xValueC1X1);
                target.setY(TargetClassConstants.yValueC1X1);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C130":
                target.setX(TargetClassConstants.xValueC130);
                target.setY(TargetClassConstants.yValueC130);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C131":
                target.setX(TargetClassConstants.xValueC131);
                target.setY(TargetClassConstants.yValueC131);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C200":
                target.setX(TargetClassConstants.xValueC2X0);
                target.setY(TargetClassConstants.yValueC2X1);
                target.setZ(TargetClassConstants.zValueC20X);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C201":
                target.setX(TargetClassConstants.xValueC2X0);
                target.setY(TargetClassConstants.yValueC2X1);
                target.setZ(TargetClassConstants.zValueC20X);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C210":
                target.setX(TargetClassConstants.xValueC2X0);
                target.setY(TargetClassConstants.yValueC2X0);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C211":
                target.setX(TargetClassConstants.xValueC2X1);
                target.setY(TargetClassConstants.yValueC2X1);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C220":
                target.setX(TargetClassConstants.xValueC2X0);
                target.setY(TargetClassConstants.yValueC2X0);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C221":
                target.setX(TargetClassConstants.xValueC2X1);
                target.setY(TargetClassConstants.yValueC2X1);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C230":
                target.setX(TargetClassConstants.xValueC230);
                target.setY(TargetClassConstants.yValueC230);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C231":        
                target.setX(TargetClassConstants.xValueC231);
                target.setY(TargetClassConstants.yValueC231);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C300":
                target.setX(TargetClassConstants.xValueC3X0);
                target.setY(TargetClassConstants.yValueC3X0);
                target.setZ(TargetClassConstants.zValueC30X);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C301":
                target.setX(TargetClassConstants.xValueC3X1);
                target.setY(TargetClassConstants.yValueC3X1);
                target.setZ(TargetClassConstants.zValueC30X);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C310":
                target.setX(TargetClassConstants.xValueC3X0);
                target.setY(TargetClassConstants.yValueC3X0);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C311":
                target.setX(TargetClassConstants.xValueC3X1);
                target.setY(TargetClassConstants.yValueC3X1);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C320":
                target.setX(TargetClassConstants.xValueC3X0);
                target.setY(TargetClassConstants.yValueC3X0);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C321":
                target.setX(TargetClassConstants.xValueC3X1);
                target.setY(TargetClassConstants.yValueC3X1);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C330":
                target.setX(TargetClassConstants.xValueC330);
                target.setY(TargetClassConstants.yValueC330);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C331":
                target.setX(TargetClassConstants.xValueC331);
                target.setY(TargetClassConstants.yValueC331);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "S330":
                target.setX(TargetClassConstants.xValueS330);
                target.setY(TargetClassConstants.yValueS330);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "S331":
                target.setX(TargetClassConstants.xValueS331);
                target.setY(TargetClassConstants.yValueS331);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C400":
                target.setX(TargetClassConstants.xValueC4X0);
                target.setY(TargetClassConstants.yValueC4X0);
                target.setZ(TargetClassConstants.zValueC40X);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C401":
                target.setX(TargetClassConstants.xValueC4X1);
                target.setY(TargetClassConstants.yValueC4X1);
                target.setZ(TargetClassConstants.zValueC40X);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C410":
                target.setX(TargetClassConstants.xValueC4X0);
                target.setY(TargetClassConstants.yValueC4X0);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C411":
                target.setX(TargetClassConstants.xValueC4X1);
                target.setY(TargetClassConstants.yValueC4X1);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C420":
                target.setX(TargetClassConstants.xValueC4X0);
                target.setY(TargetClassConstants.yValueC4X0);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C421":
                target.setX(TargetClassConstants.xValueC4X1);
                target.setY(TargetClassConstants.yValueC4X1);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C430":
                target.setX(TargetClassConstants.xValueC430);
                target.setY(TargetClassConstants.yValueC430);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C431":
                target.setX(TargetClassConstants.xValueC431);
                target.setY(TargetClassConstants.yValueC431);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "S430":
                target.setX(TargetClassConstants.xValueS430);
                target.setY(TargetClassConstants.yValueS430);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "S431":
                target.setX(TargetClassConstants.xValueS431);
                target.setY(TargetClassConstants.yValueS431);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C500":
                target.setX(TargetClassConstants.xValueC5X0);
                target.setY(TargetClassConstants.yValueC5X0);
                target.setZ(TargetClassConstants.zValueC50X);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C501":
                target.setX(TargetClassConstants.xValueC5X1);
                target.setY(TargetClassConstants.yValueC5X1);
                target.setZ(TargetClassConstants.zValueC50X);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C510":
                target.setX(TargetClassConstants.xValueC5X0);
                target.setY(TargetClassConstants.yValueC5X0);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C511":
                target.setX(TargetClassConstants.xValueC5X1);
                target.setY(TargetClassConstants.yValueC5X1);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C520":
                target.setX(TargetClassConstants.xValueC5X0);
                target.setY(TargetClassConstants.yValueC5X0);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C521":
                target.setX(TargetClassConstants.xValueC5X1);
                target.setY(TargetClassConstants.yValueC5X1);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C530":
                target.setX(TargetClassConstants.xValueC530);
                target.setY(TargetClassConstants.yValueC530);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C531":
                target.setX(TargetClassConstants.xValueC531);
                target.setY(TargetClassConstants.yValueC531);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "S530":
                target.setX(TargetClassConstants.xValueS530);
                target.setY(TargetClassConstants.yValueS530);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "S531":
                target.setX(TargetClassConstants.xValueS531);
                target.setY(TargetClassConstants.yValueS531);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C600":
                target.setX(TargetClassConstants.xValueC6X0);
                target.setY(TargetClassConstants.yValueC6X0);
                target.setZ(TargetClassConstants.zValueC60X);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C601":
                target.setX(TargetClassConstants.xValueC6X1);
                target.setY(TargetClassConstants.yValueC6X1);
                target.setZ(TargetClassConstants.zValueC60X);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX0X);
                target.setSource(false);
                break;
            case "C610":
                target.setX(TargetClassConstants.xValueC6X0);
                target.setY(TargetClassConstants.yValueC6X0);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C611":
                target.setX(TargetClassConstants.xValueC6X1);
                target.setY(TargetClassConstants.yValueC6X1);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX1X);
                target.setSource(false);
                break;
            case "C620":
                target.setX(TargetClassConstants.xValueC6X0);
                target.setY(TargetClassConstants.yValueC6X0);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C621":
                target.setX(TargetClassConstants.xValueC6X1);
                target.setY(TargetClassConstants.yValueC6X1);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX2X);
                target.setSource(false);
                break;
            case "C630":
                target.setX(TargetClassConstants.xValueC630);
                target.setY(TargetClassConstants.yValueC630);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX0);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;
            case "C631":
                target.setX(TargetClassConstants.xValueC631);
                target.setY(TargetClassConstants.yValueC631);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setLeft(TargetClassConstants.setLeftCXX1);
                target.setLevel(TargetClassConstants.heightCX3X);
                target.setSource(false);
                break;


                
            // Algae targets for Face 1
            case "A100": // Face 1, center, no backup
                target.setX(TargetClassConstants.xValueA1X0);
                target.setY(TargetClassConstants.yValueA1X0);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setSource(false);
                break;
            case "A110": // Face 1, offset, no backup
                target.setX(TargetClassConstants.xValueA1X1);
                target.setY(TargetClassConstants.yValueA1X1);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setSource(false);
                break;
            case "A111": // Face 1, offset, with backup
                target.setX(TargetClassConstants.xValueA1X1Backup);
                target.setY(TargetClassConstants.yValueA1X1Backup);
                target.setZ(TargetClassConstants.zValueC1XX);
                target.setFace(TargetClassConstants.faceValueC1XX);
                target.setSource(false);
                break;
                
            // Algae targets for Face 2
            case "A200": // Face 2, center, no backup
                target.setX(TargetClassConstants.xValueA2X0);
                target.setY(TargetClassConstants.yValueA2X0);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setSource(false);
                break;
            case "A210": // Face 2, offset, no backup
                target.setX(TargetClassConstants.xValueA2X1);
                target.setY(TargetClassConstants.yValueA2X1);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setSource(false);
                break;
            case "A211": // Face 2, offset, with backup
                target.setX(TargetClassConstants.xValueA2X1Backup);
                target.setY(TargetClassConstants.yValueA2X1Backup);
                target.setZ(TargetClassConstants.zValueC2XX);
                target.setFace(TargetClassConstants.faceValueC2XX);
                target.setSource(false);
                break;
                
            // Algae targets for Face 3
            case "A300": // Face 3, center, no backup
                target.setX(TargetClassConstants.xValueA3X0);
                target.setY(TargetClassConstants.yValueA3X0);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setSource(false);
                break;
            case "A310": // Face 3, offset, no backup
                target.setX(TargetClassConstants.xValueA3X1);
                target.setY(TargetClassConstants.yValueA3X1);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setSource(false);
                break;
            case "A311": // Face 3, offset, with backup
                target.setX(TargetClassConstants.xValueA3X1Backup);
                target.setY(TargetClassConstants.yValueA3X1Backup);
                target.setZ(TargetClassConstants.zValueC3XX);
                target.setFace(TargetClassConstants.faceValueC3XX);
                target.setSource(false);
                break;
                
            // Algae targets for Face 4
            case "A400": // Face 4, center, no backup
                target.setX(TargetClassConstants.xValueA4X0);
                target.setY(TargetClassConstants.yValueA4X0);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setSource(false);
                break;
            case "A410": // Face 4, offset, no backup
                target.setX(TargetClassConstants.xValueA4X1);
                target.setY(TargetClassConstants.yValueA4X1);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setSource(false);
                break;
            case "A411": // Face 4, offset, with backup
                target.setX(TargetClassConstants.xValueA4X1Backup);
                target.setY(TargetClassConstants.yValueA4X1Backup);
                target.setZ(TargetClassConstants.zValueC4XX);
                target.setFace(TargetClassConstants.faceValueC4XX);
                target.setSource(false);
                break;
                
            // Algae targets for Face 5
            case "A500": // Face 5, center, no backup
                target.setX(TargetClassConstants.xValueA5X0);
                target.setY(TargetClassConstants.yValueA5X0);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setSource(false);
                break;
            case "A510": // Face 5, offset, no backup
                target.setX(TargetClassConstants.xValueA5X1);
                target.setY(TargetClassConstants.yValueA5X1);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setSource(false);
                break;
            case "A511": // Face 5, offset, with backup
                target.setX(TargetClassConstants.xValueA5X1Backup);
                target.setY(TargetClassConstants.yValueA5X1Backup);
                target.setZ(TargetClassConstants.zValueC5XX);
                target.setFace(TargetClassConstants.faceValueC5XX);
                target.setSource(false);
                break;
                
            // Algae targets for Face 6
            case "A600": // Face 6, center, no backup
                target.setX(TargetClassConstants.xValueA6X0);
                target.setY(TargetClassConstants.yValueA6X0);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setSource(false);
                break;
            case "A610": // Face 6, offset, no backup
                target.setX(TargetClassConstants.xValueA6X1);
                target.setY(TargetClassConstants.yValueA6X1);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setSource(false);
                break;
            case "A611": // Face 6, offset, with backup
                target.setX(TargetClassConstants.xValueA6X1Backup);
                target.setY(TargetClassConstants.yValueA6X1Backup);
                target.setZ(TargetClassConstants.zValueC6XX);
                target.setFace(TargetClassConstants.faceValueC6XX);
                target.setSource(false);
                break;
            // Add more cases as needed.
            default:
                target = null;
                break;
        }
        return target;
    }

    /**
     * Converts this target data into a Pose2d.
     * If on the red alliance, mirror the pose relative to the blue-origin field coordinate system.
     */
    public static Pose2d toPose2d(Pose2d currentPose) {

        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            double mirroredX = FIELD_WIDTH - currentPose.getX();
            double mirroredY = FIELD_LENGTH - currentPose.getY();
            double mirroredAngle = currentPose.getRotation().getRadians() - Math.PI;
            return new Pose2d(mirroredX, mirroredY, new Rotation2d(mirroredAngle));
        }
        return currentPose;
    }

    public static Supplier<Pose2d> toPose2dSupplier(ButtonBox buttonBox) {
        TargetClass currentTarget = buttonBox.peekNextTarget();
        
        
        if (currentTarget == null) {
            
            return () -> new Pose2d();
        }
        
        Pose2d currentPose = new Pose2d(currentTarget.getX(), currentTarget.getY(), new Rotation2d(currentTarget.getZ()));
        return () -> toPose2d(currentPose);
    }
    
    @Override
    public String toString() {
        return name;
    }
}