package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class GenerationMode(val label: String) {
    TEXT_TO_VIDEO("Text to Video"),
    IMAGE_TO_VIDEO("Image to Video"),
    VIDEO_TO_VIDEO("Video to Video"),
    STORY_TO_VIDEO("Story → Video")
}

enum class JobStatus(val label: String) {
    QUEUED("queued"),
    PROCESSING("processing"),
    RENDERING("rendering"),
    COMPLETED("completed"),
    FAILED("failed")
}

enum class VideoStyle(val label: String, val tag: String) {
    CINEMATIC("Cinematic", "cinematic"),
    AFRICAN_CINEMATIC("African cinematic", "afro_cinema"),
    REALISTIC("Realistic", "realistic"),
    PHOTOREALISTIC("Photorealistic", "photorealistic"),
    CYBERPUNK("Cyberpunk", "cyberpunk"),
    ANIME("Anime", "anime"),
    CARTOON("Cartoon", "cartoon"),
    ANIMATION_3D("3D animation", "3d"),
    DOCUMENTARY("Documentary", "documentary"),
    COMMERCIAL("Commercial", "commercial"),
    MUSIC_VIDEO("Music video", "music_video"),
    FASHION("Fashion", "fashion"),
    HORROR("Horror", "horror"),
    SCI_FI("Sci-fi", "scifi"),
    FANTASY("Fantasy", "fantasy"),
    VINTAGE("Vintage", "vintage")
}

enum class CameraMovement(val label: String) {
    STATIC("Static"),
    PAN_LEFT("Pan left"),
    PAN_RIGHT("Pan right"),
    ZOOM_IN("Zoom in"),
    ZOOM_OUT("Zoom out"),
    DOLLY_FORWARD("Dolly forward"),
    DOLLY_BACKWARD("Dolly backward"),
    TRACKING_SHOT("Tracking shot"),
    CRANE_SHOT("Crane shot"),
    DRONE_SHOT("Drone shot"),
    HANDHELD("Handheld"),
    ORBIT("Orbit"),
    SLOW_MOTION("Slow motion")
}

enum class LightingMode(val label: String) {
    NATURAL("Natural"),
    GOLDEN_HOUR("Golden hour"),
    SUNSET("Sunset"),
    SUNRISE("Sunrise"),
    STUDIO("Studio"),
    NEON("Neon"),
    DRAMATIC("Dramatic"),
    LOW_LIGHT("Low light"),
    SOFT_CINEMATIC("Soft cinematic")
}

enum class AspectRatioOption(val ratio: String, val platform: String, val widthFactor: Float, val heightFactor: Float) {
    RATIO_16_9("16:9", "YouTube", 16f, 9f),
    RATIO_9_16("9:16", "TikTok/Reels/Shorts", 9f, 16f),
    RATIO_1_1("1:1", "Instagram", 1f, 1f),
    RATIO_4_5("4:5", "Instagram/Facebook", 4f, 5f),
    RATIO_21_9("21:9", "Cinematic", 21f, 9f)
}

enum class VideoQuality(val label: String, val creditMultiplier: Int) {
    STANDARD("Standard", 1),
    HD("HD", 2),
    FULL_HD("Full HD", 3),
    UHD_4K("4K", 5)
}

enum class SocialPreset(
    val title: String,
    val ratio: String,
    val resolution: String,
    val durationSec: Int,
    val safeAreaNote: String
) {
    TIKTOK("TikTok Video", "9:16", "Full HD", 15, "Bottom 20% & Right 15% reserved for UI"),
    INSTAGRAM_REEL("Instagram Reel", "9:16", "Full HD", 15, "Top/Bottom 15% safe zone active"),
    YOUTUBE_SHORT("YouTube Short", "9:16", "Full HD", 30, "Right rail & bottom caption safe zone"),
    YOUTUBE_VIDEO("YouTube Video", "16:9", "4K", 60, "Full 16:9 widescreen safe frame"),
    FACEBOOK_REEL("Facebook Reel", "9:16", "HD", 15, "Bottom 18% overlay safe zone"),
    INSTAGRAM_POST("Instagram Post", "1:1", "Full HD", 10, "Centered 1:1 square safe zone"),
    FACEBOOK_VIDEO("Facebook Video", "4:5", "Full HD", 30, "4:5 mobile feed optimized"),
    X_VIDEO("X Video", "16:9", "Full HD", 15, "High-bitrate timeline playback")
}

@Serializable
data class StoryScene(
    val sceneNumber: Int,
    val title: String,
    val visualPrompt: String,
    val cameraMovement: String,
    val narration: String,
    val durationSec: Int = 5,
    val transition: String = "Cross Dissolve",
    val characterLockApplied: Boolean = true
)

@Serializable
data class EnhancedPromptBreakdown(
    val fullEnhancedPrompt: String,
    val subject: String,
    val environment: String,
    val action: String,
    val cameraMovement: String,
    val lens: String,
    val lighting: String,
    val atmosphere: String,
    val composition: String,
    val visualStyle: String,
    val motion: String,
    val duration: String,
    val aspectRatio: String
)

@Serializable
data class CaptionSegment(
    val id: Int,
    val startSec: Float,
    val endSec: Float,
    val text: String,
    val highlightWord: String = ""
)

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val prompt: String,
    val enhancedPrompt: String = "",
    val generationMode: String = GenerationMode.TEXT_TO_VIDEO.name,
    val durationSec: Int = 10,
    val aspectRatio: String = "16:9",
    val quality: String = "Full HD",
    val style: String = "African cinematic",
    val cameraMovement: String = "Dolly backward",
    val lighting: String = "Sunset",
    val motionIntensity: Float = 0.75f,
    val imagePreservation: Float = 0.85f,
    val characterPreservation: Float = 0.92f,
    val referenceImageUri: String = "",
    val characterLockName: String = "",
    val jobStatus: String = JobStatus.COMPLETED.name,
    val progressStep: String = "Finalizing your video...",
    val progressPercent: Int = 100,
    val creditCost: Int = 15,
    val isFavorite: Boolean = false,
    val isTrashed: Boolean = false,
    val sceneCount: Int = 1,
    val scenesJson: String = "",
    val voiceoverScript: String = "",
    val voiceLanguage: String = "English",
    val voiceName: String = "Kore (Female • Warm)",
    val musicPrompt: String = "Emotional Afrobeat instrumental with warm Rhodes and soft percussion",
    val sfxList: String = "Ocean, Footsteps",
    val captionsJson: String = "",
    val trimStartSec: Float = 0f,
    val trimEndSec: Float = 10f,
    val playbackSpeed: Float = 1.0f,
    val activeFilter: String = "Anamorphic Gold",
    val colorHueShift: Float = 0f,
    val previewSeed: Int = 101,
    val providerUsed: String = "Veo 3.1 (veo-3.1-fast-generate-preview)",
    val operationId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_prompts")
data class SavedPromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val promptText: String,
    val category: String,
    val style: String,
    val aspectRatio: String,
    val durationSec: Int,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "character_locks")
data class CharacterLockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val faceDescription: String,
    val hairDescription: String,
    val clothingDescription: String,
    val bodyAppearance: String,
    val ageRange: String,
    val skinAppearance: String,
    val identityToken: String,
    val isLocked: Boolean = true
)

@Entity(tableName = "credit_transactions")
data class CreditTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val deltaCredits: Int,
    val planTier: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "moderation_logs")
data class ModerationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inputContent: String,
    val flagReason: String,
    val actionTaken: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class VideoTemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val defaultPrompt: String,
    val durationSec: Int,
    val aspectRatio: String,
    val style: String,
    val camera: String,
    val lighting: String,
    val thumbnailType: Int
)
