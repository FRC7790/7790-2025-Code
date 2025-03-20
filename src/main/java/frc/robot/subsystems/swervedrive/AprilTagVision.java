package frc.robot.subsystems.swervedrive;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.AprilTagVisionConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AprilTagVision extends SubsystemBase {
    
    private static final PhotonCamera rightCam = new PhotonCamera("rightcam");
    private static final PhotonCamera leftCam = new PhotonCamera("leftcam");
    //private static final PhotonCamera limelight = new PhotonCamera("limelight");

    private static final Transform3d rightCamToRobot = new Transform3d(
            new Translation3d(AprilTagVisionConstants.rightCamXOffset, AprilTagVisionConstants.rightCamYOffset, AprilTagVisionConstants.rightCamZOffset),
            new Rotation3d(AprilTagVisionConstants.rightCamRoll, AprilTagVisionConstants.rightCamPitch, AprilTagVisionConstants.rightCamYaw));

    private static final Transform3d leftCamToRobot = new Transform3d(
            new Translation3d(AprilTagVisionConstants.leftCamXOffset, AprilTagVisionConstants.leftCamYOffset, AprilTagVisionConstants.leftCamZOffset),
            new Rotation3d(AprilTagVisionConstants.leftCamRoll, AprilTagVisionConstants.leftCamPitch, AprilTagVisionConstants.leftCamYaw));

        /*
    private static final Transform3d limelightToRobot = new Transform3d(
            new Translation3d(AprilTagVisionConstants.limelightXOffset, AprilTagVisionConstants.limelightYOffset, AprilTagVisionConstants.limelightZOffset),
            new Rotation3d(AprilTagVisionConstants.limelightRoll, AprilTagVisionConstants.limelightPitch, AprilTagVisionConstants.limelightYaw));
    */
    private static final AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeWelded);

    private static PhotonPoseEstimator rightPoseEstimator;
    private static PhotonPoseEstimator leftPoseEstimator;
    //private static PhotonPoseEstimator limelightPoseEstimator;

    // Store last camera results
    private static Optional<EstimatedRobotPose> lastRightCamResult = Optional.empty();
    private static Optional<EstimatedRobotPose> lastLeftCamResult = Optional.empty();

    // Set of valid tag IDs (for O(1) lookup)
    private static Set<Integer> validTagIds;

    static {
        rightPoseEstimator = new PhotonPoseEstimator(fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, rightCamToRobot);
        leftPoseEstimator = new PhotonPoseEstimator(fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, leftCamToRobot);
        //limelightPoseEstimator = new PhotonPoseEstimator(fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, limelightToRobot);

        rightPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
        leftPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
        //limelightPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
        
        // Create set of valid tag IDs for faster lookup
        validTagIds = new HashSet<>(Arrays.asList(AprilTagVisionConstants.VALID_TAG_IDS));
    }

    /**
     * Filters the estimated robot pose based on tag distance and valid tag IDs.
     * Rejects any tags that are too far away or have IDs in the disallowed range.
     * 
     * @param estimate The original pose estimate
     * @param currentPose Current robot pose for distance calculation
     * @return Filtered pose estimate or empty if all tags are invalid
     */
    private static Optional<EstimatedRobotPose> filterByDistanceAndId(
            Optional<EstimatedRobotPose> estimate, 
            Pose2d currentPose) {
        
        if (estimate.isEmpty()) {
            return estimate;
        }
        
        EstimatedRobotPose pose = estimate.get();
        
        // If no targets, return empty
        if (pose.targetsUsed.isEmpty()) {
            return Optional.empty();
        }
        
        // For multi-tag detections, filter out invalid IDs
        List<org.photonvision.targeting.PhotonTrackedTarget> validTargets = new ArrayList<>();
        
        for (var target : pose.targetsUsed) {
            int tagId = target.getFiducialId();
            
            // Check if tag ID is in valid list
            if (!validTagIds.contains(tagId)) {
                continue; // Skip invalid tag IDs
            }
            
            // Get the tag position from the field layout
            Optional<Pose3d> tagPose = fieldLayout.getTagPose(tagId);
            
            if (tagPose.isPresent()) {
                // Calculate distance from robot to tag
                double distance = tagPose.get().toPose2d().getTranslation()
                    .getDistance(currentPose.getTranslation());
                
                // If the tag is within maximum distance, add it to valid targets
                if (distance <= AprilTagVisionConstants.MAX_TAG_DISTANCE) {
                    validTargets.add(target);
                }
            }
        }
        
        // If no valid targets remain, return empty
        if (validTargets.isEmpty()) {
            return Optional.empty();
        }
        
        // If this is a single-tag detection, or our original detection only had invalid tags,
        // we need to return the original estimate if it's valid, or empty if not
        if (validTargets.size() != pose.targetsUsed.size()) {
            // Special handling for multi-tag cases would go here if needed
            // For now, we'll just keep the original estimate if any valid tags remain
            // This preserves the multi-tag calculation from PhotonVision
        }
        
        return Optional.of(pose);
    }

    public static Optional<EstimatedRobotPose> getRightCamPose(Pose2d prevEstimatedPose) {
        rightPoseEstimator.setReferencePose(prevEstimatedPose);
        lastRightCamResult = filterByDistanceAndId(
            rightPoseEstimator.update(rightCam.getLatestResult()),
            prevEstimatedPose
        );
        return lastRightCamResult;
    }
    
    public static Optional<EstimatedRobotPose> getLeftCamPose(Pose2d prevEstimatedPose) {
        leftPoseEstimator.setReferencePose(prevEstimatedPose);
        lastLeftCamResult = filterByDistanceAndId(
            leftPoseEstimator.update(leftCam.getLatestResult()),
            prevEstimatedPose
        );
        return lastLeftCamResult;
    }
    
    // Methods to access the last camera results
    public static Optional<EstimatedRobotPose> getLastRightCamResult() {
        return lastRightCamResult;
    }
    
    public static Optional<EstimatedRobotPose> getLastLeftCamResult() {
        return lastLeftCamResult;
    }

    public static Optional<Pose3d> getBestPoseEstimate(Pose2d prevEstimatedPose) {
        // First try to get multi-tag results (more accurate)
        List<Pose3d> multiTagPoses = getMultiTagPoses(prevEstimatedPose);
        
        if (!multiTagPoses.isEmpty()) {
            // If we have multi-tag poses, use those (they're more reliable)
            return Optional.of(averagePoses(multiTagPoses));
        }
        
        // Fall back to single-tag results with ambiguity check
        List<Pose3d> singleTagPoses = getSingleTagPoses(prevEstimatedPose);
        
        if (!singleTagPoses.isEmpty()) {
            return Optional.of(averagePoses(singleTagPoses));
        }
        
        return Optional.empty(); // No valid poses found
    }

    private static List<Pose3d> getMultiTagPoses(Pose2d prevEstimatedPose) {
        List<Pose3d> multiTagPoses = new ArrayList<>();
        
        Optional<EstimatedRobotPose> rightCamEstimate = getRightCamPose(prevEstimatedPose);
        if (rightCamEstimate.isPresent() && rightCamEstimate.get().targetsUsed.size() > 1) {
            multiTagPoses.add(rightCamEstimate.get().estimatedPose);
        }
        
        Optional<EstimatedRobotPose> leftCamEstimate = getLeftCamPose(prevEstimatedPose);
        if (leftCamEstimate.isPresent() && leftCamEstimate.get().targetsUsed.size() > 1) {
            multiTagPoses.add(leftCamEstimate.get().estimatedPose);
        }
        
        /*
        Optional<EstimatedRobotPose> limelightEstimate = getLimelightPose(prevEstimatedPose);
        if (limelightEstimate.isPresent() && limelightEstimate.get().targetsUsed.size() > 1) {
            multiTagPoses.add(limelightEstimate.get().estimatedPose);
        }
        */
        
        return multiTagPoses;
    }

    private static List<Pose3d> getSingleTagPoses(Pose2d prevEstimatedPose) {
        List<Pose3d> singleTagPoses = new ArrayList<>();
        
        Optional<EstimatedRobotPose> rightCamEstimate = getRightCamPose(prevEstimatedPose);
        if (rightCamEstimate.isPresent() && rightCamEstimate.get().targetsUsed.size() == 1) {
            EstimatedRobotPose estimate = rightCamEstimate.get();
            if (estimate.targetsUsed.get(0).getPoseAmbiguity() <= AprilTagVisionConstants.ambiguityThreshold) {
                singleTagPoses.add(estimate.estimatedPose);
            }
        }
        
        Optional<EstimatedRobotPose> leftCamEstimate = getLeftCamPose(prevEstimatedPose);
        if (leftCamEstimate.isPresent() && leftCamEstimate.get().targetsUsed.size() == 1) {
            EstimatedRobotPose estimate = leftCamEstimate.get();
            if (estimate.targetsUsed.get(0).getPoseAmbiguity() <= AprilTagVisionConstants.ambiguityThreshold) {
                singleTagPoses.add(estimate.estimatedPose);
            }
        }
        
        /*
        Optional<EstimatedRobotPose> limelightEstimate = getLimelightPose(prevEstimatedPose);
        if (limelightEstimate.isPresent() && limelightEstimate.get().targetsUsed.size() == 1) {
            EstimatedRobotPose estimate = limelightEstimate.get();
            if (estimate.targetsUsed.get(0).getPoseAmbiguity() <= AprilTagVisionConstants.ambiguityThreshold) {
                singleTagPoses.add(estimate.estimatedPose);
            }
        }
        */
        
        return singleTagPoses;
    }

    private static Pose3d averagePoses(List<Pose3d> poses) {
        // If only one pose, return it directly
        if (poses.size() == 1) {
            return poses.get(0);
        }
        
        // Average the positions
        double x = 0, y = 0, z = 0;
        
        // We'll use quaternions to average rotations correctly
        double qw = 0, qx = 0, qy = 0, qz = 0;
        
        for (Pose3d pose : poses) {
            // Add position components
            x += pose.getX();
            y += pose.getY();
            z += pose.getZ();
            
            // Convert rotation to quaternion and add components
            // Note: WPILib Rotation3d stores quaternions internally
            qw += pose.getRotation().getQuaternion().getW();
            qx += pose.getRotation().getQuaternion().getX();
            qy += pose.getRotation().getQuaternion().getY();
            qz += pose.getRotation().getQuaternion().getZ();
        }
        
        int count = poses.size();
        
        // Average the position
        Translation3d avgTranslation = new Translation3d(x / count, y / count, z / count);
        
        // Average and normalize the quaternion
        double length = Math.sqrt(qw*qw + qx*qx + qy*qy + qz*qz);
        qw /= length;
        qx /= length;
        qy /= length;
        qz /= length;
        
        // Create the rotation from the averaged quaternion
        Rotation3d avgRotation = new Rotation3d(
            new edu.wpi.first.math.geometry.Quaternion(qw, qx, qy, qz));
        
        return new Pose3d(avgTranslation, avgRotation);
    }
}