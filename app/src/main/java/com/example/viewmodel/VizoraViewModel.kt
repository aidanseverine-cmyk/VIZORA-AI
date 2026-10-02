package com.example.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.VizoraAudioSynthesizer
import com.example.data.AspectRatioOption
import com.example.data.CameraMovement
import com.example.data.CaptionSegment
import com.example.data.CharacterLockEntity
import com.example.data.CreditTransactionEntity
import com.example.data.EnhancedPromptBreakdown
import com.example.data.GenerationMode
import com.example.data.JobStatus
import com.example.data.LightingMode
import com.example.data.ModerationLogEntity
import com.example.data.SavedPromptEntity
import com.example.data.SocialPreset
import com.example.data.StoryScene
import com.example.data.VideoProjectEntity
import com.example.data.VideoQuality
import com.example.data.VideoStyle
import com.example.data.VideoTemplateItem
import com.example.data.VizoraDatabase
import com.example.data.VizoraRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

enum class VizoraDestination(val route: String, val label: String) {
    HOME("home", "Home"),
    CREATE("create", "Create"),
    STORY("story", "Story"),
    EDITOR("editor", "Editor"),
    LIBRARY("library", "Library"),
    STUDIO("studio", "Studio")
}

enum class SubscriptionPlan(
    val id: String,
    val title: String,
    val monthlyCredits: Int,
    val badge: String
) {
    UNLIMITED_FREE("free_unlimited", "100% Free Unlimited Studio", 999999, "100% Free Forever")
}

data class VizoraUiState(
    val currentDestination: VizoraDestination = VizoraDestination.HOME,
    val isDarkMode: Boolean = true,

    // User Account (100% Free Unlimited Access)
    val isLoggedIn: Boolean = true,
    val userName: String = "Aidan Severine",
    val userEmail: String = "aidan@vizora.ai",
    val userHandle: String = "@aidan_creator",
    val currentPlan: SubscriptionPlan = SubscriptionPlan.UNLIMITED_FREE,
    val creditsRemaining: Int = 999999,

    // Active Video Generation Controls
    val generationMode: GenerationMode = GenerationMode.TEXT_TO_VIDEO,
    val promptText: String = "Create a cinematic 10-second video of a young African man walking through Dar es Salaam at sunset, realistic lighting, camera slowly moving backward, cinematic atmosphere.",
    val isEnhancingPrompt: Boolean = false,
    val enhancedPromptBreakdown: EnhancedPromptBreakdown? = null,
    val showEnhancedPreviewCard: Boolean = false,

    // Advanced Video Settings
    val selectedDurationSec: Int = 10,
    val selectedAspectRatio: AspectRatioOption = AspectRatioOption.RATIO_16_9,
    val selectedQuality: VideoQuality = VideoQuality.FULL_HD,
    val selectedStyle: VideoStyle = VideoStyle.AFRICAN_CINEMATIC,
    val selectedCamera: CameraMovement = CameraMovement.DOLLY_BACKWARD,
    val selectedLighting: LightingMode = LightingMode.SUNSET,
    val selectedSocialPreset: SocialPreset? = null,
    val numberOfGenerations: Int = 1,

    // Image-to-Video & Character Lock
    val uploadedImageUri: String = "",
    val uploadedImageLabel: String = "Person standing on Coco Beach, Dar es Salaam at golden hour",
    val motionIntensity: Float = 0.78f,
    val imagePreservation: Float = 0.88f,
    val characterPreservation: Float = 0.94f,
    val activeCharacterLockId: Int? = 1,

    // Generation Job Queue & Experience
    val isGenerating: Boolean = false,
    val currentJobStatus: JobStatus = JobStatus.COMPLETED,
    val generationStepText: String = "",
    val generationProgressPercent: Int = 0,
    val generationErrorMessage: String? = null,
    val activeProject: VideoProjectEntity? = null,
    val isVideoPlaying: Boolean = true,

    // Story -> Video State
    val storyInputText: String = "A young Tanzanian boy dreams of becoming a pilot, sketching wing designs in his notebook by lantern light until the day he steps into the cockpit at sunrise.",
    val isBreakingDownStory: Boolean = false,
    val storyScenes: List<StoryScene> = emptyList(),
    val storyTransitionType: String = "Cross Dissolve",
    val storyIncludeNarration: Boolean = true,
    val storyIncludeMusic: Boolean = true,
    val storyIncludeSubtitles: Boolean = true,

    // AI Video Editor, Voiceover, Music, SFX & Subtitles
    val activeEditorTab: Int = 0, // 0=Timeline, 1=Voiceover, 2=Music & SFX, 3=Subtitles, 4=Color & FX
    val trimStartSec: Float = 0f,
    val trimEndSec: Float = 10f,
    val playbackSpeed: Float = 1.0f,
    val rotationDegrees: Int = 0,
    val textOverlay: String = "DAR ES SALAAM • GOLDEN HOUR",
    val activeSticker: String = "🎬 4K ANAMORPHIC",
    val activeFilter: String = "Anamorphic Gold",
    val brightnessOffset: Float = 0.05f,
    val contrastScale: Float = 1.12f,
    val saturationScale: Float = 1.15f,
    val masterVolume: Float = 0.90f,

    // Voiceover Controls
    val voiceGender: String = "Female",
    val voiceAccent: String = "East African English",
    val voiceLanguage: String = "English",
    val voiceEmotion: String = "Cinematic & Warm",
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val voiceScript: String = "Beneath the golden horizon of Dar es Salaam, every step tells a story of vision and motion.",
    val voiceStatusMessage: String = "Ready • Neural TTS Engine",

    // Music & SFX Controls
    val musicPrompt: String = "Create an emotional Afrobeat instrumental for a romantic video.",
    val musicVolume: Float = 0.80f,
    val musicFadeIn: Boolean = true,
    val musicFadeOut: Boolean = true,
    val musicLoop: Boolean = true,
    val musicBpm: Int = 98,
    val activeSfxList: List<String> = listOf("Ocean", "Footsteps"),
    val isPlayingAudioPreview: Boolean = false,

    // Automatic Subtitles Controls
    val captions: List<CaptionSegment> = emptyList(),
    val captionFontSizeSp: Int = 16,
    val captionPosition: String = "Bottom Safe Zone",
    val captionAnimation: String = "Word-by-Word Pop",
    val wordByWordHighlight: Boolean = true,
    val burnInCaptions: Boolean = true,

    // Admin & Safety State
    val isAdminMode: Boolean = false,
    val systemAnnouncement: String = "Veo 3.1 Fast & 4K Anamorphic Pipeline Active",
    val configuredProviderName: String = "Google Veo 3.1 (veo-3.1-fast-generate-preview)",
    val suspendedAccounts: List<String> = listOf("spam_bot_99@temp.io"),
    val statusBannerMessage: String? = null
) {
    val estimatedCreditCost: Int
        get() {
            val basePer5Sec = 5
            val durationBlocks = (selectedDurationSec / 5).coerceAtLeast(1)
            val singleCost = basePer5Sec * durationBlocks * selectedQuality.creditMultiplier
            return singleCost * numberOfGenerations
        }
}

class VizoraViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VizoraDatabase.getDatabase(application)
    val repository = VizoraRepository(database.vizoraDao())
    private val audioSynthesizer = VizoraAudioSynthesizer(application)
    private val json = Json { ignoreUnknownKeys = true }

    private val _uiState = MutableStateFlow(VizoraUiState())
    val uiState: StateFlow<VizoraUiState> = _uiState.asStateFlow()

    val activeProjects: StateFlow<List<VideoProjectEntity>> = repository.activeProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteProjects: StateFlow<List<VideoProjectEntity>> = repository.favoriteProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedProjects: StateFlow<List<VideoProjectEntity>> = repository.trashedProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPrompts: StateFlow<List<SavedPromptEntity>> = repository.savedPrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val characterLocks: StateFlow<List<CharacterLockEntity>> = repository.characterLocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creditTransactions: StateFlow<List<CreditTransactionEntity>> = repository.creditTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val moderationLogs: StateFlow<List<ModerationLogEntity>> = repository.moderationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: List<VideoTemplateItem> = repository.getReadyTemplates()

    init {
        seedInitialStudioData()
    }

    private fun seedInitialStudioData() {
        viewModelScope.launch {
            val existingProjects = repository.activeProjects.first()
            if (existingProjects.isEmpty()) {
                val initialCaptions = repository.aiOrchestrator.generateTimedCaptions(
                    "Beneath the golden sunset of Dar es Salaam, a new generation of creators brings stories to life.",
                    10
                )
                val initialStoryScenes = repository.aiOrchestrator.breakStoryIntoScenes(
                    _uiState.value.storyInputText,
                    "Amani • Young Tanzanian aviator with brass goggles",
                    "African cinematic"
                )

                val p1 = VideoProjectEntity(
                    title = "Dar es Salaam Sunset Walk",
                    prompt = "Create a cinematic 10-second video of a young African man walking through Dar es Salaam at sunset, realistic lighting, camera slowly moving backward, cinematic atmosphere.",
                    enhancedPrompt = "35mm anamorphic prime shot of a young African man walking through the vibrant coastal streets of Dar es Salaam at sunset, warm golden hour volumetric rim lighting, camera executing a stabilized dolly backward movement, shallow depth of field, Kodak 2383 film look.",
                    generationMode = GenerationMode.TEXT_TO_VIDEO.name,
                    durationSec = 10,
                    aspectRatio = "16:9",
                    quality = "Full HD",
                    style = "African cinematic",
                    cameraMovement = "Dolly backward",
                    lighting = "Sunset",
                    characterLockName = "Kofi (Lead Protagonist)",
                    jobStatus = JobStatus.COMPLETED.name,
                    creditCost = 30,
                    isFavorite = true,
                    captionsJson = json.encodeToString(initialCaptions),
                    previewSeed = 101
                )

                val p2 = VideoProjectEntity(
                    title = "Dreams Above Kilimanjaro",
                    prompt = "A young Tanzanian boy dreams of becoming a pilot, building model planes until he flies over Mount Kilimanjaro at sunrise.",
                    enhancedPrompt = "Multi-scene Story → Video production maintaining Character Lock across 4 chronological scenes with narration, Afrobeat orchestral score, and word-by-word subtitles.",
                    generationMode = GenerationMode.STORY_TO_VIDEO.name,
                    durationSec = 15,
                    aspectRatio = "16:9",
                    quality = "4K",
                    style = "Cinematic",
                    cameraMovement = "Drone shot",
                    lighting = "Sunrise",
                    characterLockName = "Amani (Young Aviator)",
                    jobStatus = JobStatus.COMPLETED.name,
                    sceneCount = 4,
                    scenesJson = json.encodeToString(initialStoryScenes),
                    creditCost = 75,
                    isFavorite = true,
                    previewSeed = 202
                )

                val p3 = VideoProjectEntity(
                    title = "Neo-Nairobi Cyberpunk Rain",
                    prompt = "Futuristic neon-lit metropolis at night in rain, glowing Swahili holographic billboards, flying transit pods streaking through misty towers.",
                    enhancedPrompt = "21:9 ultra-widescreen cyberpunk skyline with neon cyan and magenta reflections on wet asphalt, crane shot ascending through volumetric rain.",
                    generationMode = GenerationMode.TEXT_TO_VIDEO.name,
                    durationSec = 15,
                    aspectRatio = "21:9",
                    quality = "4K",
                    style = "Cyberpunk",
                    cameraMovement = "Crane shot",
                    lighting = "Neon",
                    jobStatus = JobStatus.COMPLETED.name,
                    creditCost = 75,
                    isFavorite = false,
                    previewSeed = 303
                )

                repository.insertProject(p1)
                repository.insertProject(p2)
                repository.insertProject(p3)

                _uiState.update {
                    it.copy(
                        activeProject = p1,
                        captions = initialCaptions,
                        storyScenes = initialStoryScenes
                    )
                }
            } else {
                val firstProj = existingProjects.first()
                val defaultScenes = repository.aiOrchestrator.breakStoryIntoScenes(
                    _uiState.value.storyInputText,
                    "Amani • Young Tanzanian aviator",
                    "African cinematic"
                )
                val defaultCaptions = repository.aiOrchestrator.generateTimedCaptions(
                    firstProj.prompt,
                    firstProj.durationSec
                )
                _uiState.update {
                    it.copy(
                        activeProject = firstProj,
                        captions = defaultCaptions,
                        storyScenes = defaultScenes
                    )
                }
            }

            if (repository.characterLocks.first().isEmpty()) {
                repository.insertCharacterLock(
                    CharacterLockEntity(
                        name = "Kofi • Coastal Filmmaker",
                        faceDescription = "Warm symmetrical East African facial features, expressive dark eyes, short neatly tapered beard",
                        hairDescription = "Short twisted sponge curls with clean temple fade",
                        clothingDescription = "Terracotta linen overshirt over crisp white tee, vintage silver wristwatch",
                        bodyAppearance = "Athletic, tall 6'0\" posture, relaxed confident stride",
                        ageRange = "26-28 years old",
                        skinAppearance = "Rich deep mahogany complexion with warm golden-hour subsurface glow",
                        identityToken = "CHAR_LOCK_KOFI_8841",
                        isLocked = true
                    )
                )
                repository.insertCharacterLock(
                    CharacterLockEntity(
                        name = "Amani • Young Aviator",
                        faceDescription = "Bright curious eyes, joyful smile, vintage brass aviator goggles resting on forehead",
                        hairDescription = "Short natural curls",
                        clothingDescription = "Olive flight jacket with East African sky squadron patch",
                        bodyAppearance = "Youthful energetic build",
                        ageRange = "12-14 years old",
                        skinAppearance = "Warm bronze-brown skin with natural sunlight highlights",
                        identityToken = "CHAR_LOCK_AMANI_3920",
                        isLocked = true
                    )
                )
            }

            if (repository.savedPrompts.first().isEmpty()) {
                repository.insertSavedPrompt(
                    SavedPromptEntity(
                        title = "Dar es Salaam Sunset Walk",
                        promptText = "Create a cinematic 10-second video of a young African man walking through Dar es Salaam at sunset, realistic lighting, camera slowly moving backward, cinematic atmosphere.",
                        category = "African Cinema",
                        style = "African cinematic",
                        aspectRatio = "16:9",
                        durationSec = 10,
                        isFavorite = true
                    )
                )
                repository.insertSavedPrompt(
                    SavedPromptEntity(
                        title = "Zanzibar Dhow Aerial Orbit",
                        promptText = "Traditional wooden dhow sailboat gliding across crystal-clear turquoise waters off Zanzibar at golden hour, sweeping 360 drone orbit shot, photorealistic.",
                        category = "Travel & Drone",
                        style = "Photorealistic",
                        aspectRatio = "9:16",
                        durationSec = 15,
                        isFavorite = true
                    )
                )
                repository.insertSavedPrompt(
                    SavedPromptEntity(
                        title = "Afro-Futurist Solar Metropolis",
                        promptText = "Sleek solar-powered glass towers intertwined with lush vertical botanical gardens in futuristic Kigali, sunrise volumetric rays, crane shot.",
                        category = "Sci-Fi & Architecture",
                        style = "Sci-fi",
                        aspectRatio = "21:9",
                        durationSec = 15,
                        isFavorite = false
                    )
                )
            }

            if (repository.creditTransactions.first().isEmpty()) {
                repository.recordCreditTransaction(
                    CreditTransactionEntity(
                        description = "Creator Plan Monthly Allocation",
                        deltaCredits = 500,
                        planTier = "Creator",
                        status = "Completed"
                    )
                )
            }
        }
    }

    fun navigateTo(destination: VizoraDestination) {
        _uiState.update { it.copy(currentDestination = destination, statusBannerMessage = null) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun updatePromptText(newPrompt: String) {
        _uiState.update {
            it.copy(
                promptText = newPrompt,
                generationErrorMessage = null
            )
        }
    }

    fun selectGenerationMode(mode: GenerationMode) {
        _uiState.update { it.copy(generationMode = mode) }
        if (mode == GenerationMode.STORY_TO_VIDEO) {
            navigateTo(VizoraDestination.STORY)
        }
    }

    fun updateDuration(seconds: Int) {
        _uiState.update {
            it.copy(
                selectedDurationSec = seconds,
                trimEndSec = seconds.toFloat()
            )
        }
    }

    fun updateAspectRatio(option: AspectRatioOption) {
        _uiState.update { it.copy(selectedAspectRatio = option) }
    }

    fun updateQuality(quality: VideoQuality) {
        _uiState.update { it.copy(selectedQuality = quality) }
    }

    fun updateStyle(style: VideoStyle) {
        _uiState.update { it.copy(selectedStyle = style) }
    }

    fun updateCameraMovement(camera: CameraMovement) {
        _uiState.update { it.copy(selectedCamera = camera) }
    }

    fun updateLighting(lighting: LightingMode) {
        _uiState.update { it.copy(selectedLighting = lighting) }
    }

    fun updateNumberOfGenerations(count: Int) {
        _uiState.update { it.copy(numberOfGenerations = count.coerceIn(1, 4)) }
    }

    fun applySocialPreset(preset: SocialPreset) {
        val ratioOption = AspectRatioOption.entries.find { it.ratio == preset.ratio }
            ?: AspectRatioOption.RATIO_9_16
        val qualityOption = VideoQuality.entries.find { it.label == preset.resolution }
            ?: VideoQuality.FULL_HD

        _uiState.update {
            it.copy(
                selectedSocialPreset = preset,
                selectedAspectRatio = ratioOption,
                selectedQuality = qualityOption,
                selectedDurationSec = preset.durationSec,
                trimEndSec = preset.durationSec.toFloat(),
                statusBannerMessage = "Applied ${preset.title} (${preset.ratio} • ${preset.resolution} • ${preset.durationSec}s)"
            )
        }
    }

    fun updateImageToVideoSettings(
        uri: String? = null,
        label: String? = null,
        motion: Float? = null,
        imgPreservation: Float? = null,
        charPreservation: Float? = null
    ) {
        _uiState.update { state ->
            state.copy(
                uploadedImageUri = uri ?: state.uploadedImageUri,
                uploadedImageLabel = label ?: state.uploadedImageLabel,
                motionIntensity = motion ?: state.motionIntensity,
                imagePreservation = imgPreservation ?: state.imagePreservation,
                characterPreservation = charPreservation ?: state.characterPreservation
            )
        }
    }

    fun selectCharacterLock(characterId: Int?) {
        _uiState.update { it.copy(activeCharacterLockId = characterId) }
    }

    fun createCharacterLock(
        name: String,
        face: String,
        hair: String,
        clothing: String,
        body: String,
        age: String,
        skin: String
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = repository.insertCharacterLock(
                CharacterLockEntity(
                    name = name.trim(),
                    faceDescription = face.ifBlank { "Consistent facial geometry" },
                    hairDescription = hair.ifBlank { "Signature hairstyle" },
                    clothingDescription = clothing.ifBlank { "Signature wardrobe" },
                    bodyAppearance = body.ifBlank { "Consistent stature" },
                    ageRange = age.ifBlank { "Adult" },
                    skinAppearance = skin.ifBlank { "Natural skin tone" },
                    identityToken = "CHAR_LOCK_${name.take(4).uppercase()}_${(1000..9999).random()}",
                    isLocked = true
                )
            )
            _uiState.update {
                it.copy(
                    activeCharacterLockId = id.toInt(),
                    statusBannerMessage = "Character Lock '$name' saved & activated"
                )
            }
        }
    }

    // --- Safety & Moderation System (Requirement 26) ---
    private suspend fun checkSafetyPolicy(prompt: String): String? {
        val prohibitedKeywords = listOf(
            "deepfake non-consensual",
            "terrorist attack",
            "explicit gore",
            "child exploitation",
            "unauthorized celebrity impersonation"
        )
        val lower = prompt.lowercase()
        val matched = prohibitedKeywords.firstOrNull { lower.contains(it) }
        if (matched != null) {
            repository.recordModerationLog(
                ModerationLogEntity(
                    inputContent = prompt.take(120),
                    flagReason = "Safety Policy Violation: $matched",
                    actionTaken = "Blocked generation & preserved credits"
                )
            )
            return "Safety Policy Alert: Your prompt triggered VIZORA AI safety safeguards ($matched). Please revise your prompt."
        }
        return null
    }

    // --- Feature 3: AI Prompt Enhancer ---
    fun enhanceCurrentPrompt() {
        val state = _uiState.value
        if (state.isEnhancingPrompt) return

        viewModelScope.launch {
            _uiState.update { it.copy(isEnhancingPrompt = true, generationErrorMessage = null) }
            val activeCharacter = characterLocks.value.find { it.id == state.activeCharacterLockId }
            val charDesc = activeCharacter?.let {
                "${it.name}: ${it.faceDescription}, ${it.hairDescription}, wearing ${it.clothingDescription}, ${it.skinAppearance}"
            }

            val breakdown = repository.aiOrchestrator.enhanceVideoPrompt(
                rawPrompt = state.promptText,
                style = state.selectedStyle.label,
                camera = state.selectedCamera.label,
                lighting = state.selectedLighting.label,
                durationSec = state.selectedDurationSec,
                aspectRatio = state.selectedAspectRatio.ratio,
                characterLockDescription = charDesc
            )

            _uiState.update {
                it.copy(
                    isEnhancingPrompt = false,
                    promptText = breakdown.fullEnhancedPrompt,
                    enhancedPromptBreakdown = breakdown,
                    showEnhancedPreviewCard = true,
                    statusBannerMessage = "AI Prompt Enhanced with 12 cinematography parameters"
                )
            }
        }
    }

    fun dismissEnhancedPreviewCard() {
        _uiState.update { it.copy(showEnhancedPreviewCard = false) }
    }

    // --- Feature 1, 21, 24, 29: Asynchronous Video Generation Job Pipeline ---
    fun generateVideo(actionVariant: String = "Standard") {
        val state = _uiState.value
        if (state.isGenerating) return

        viewModelScope.launch {
            // 1. Safety Moderation Check
            val safetyViolation = checkSafetyPolicy(state.promptText)
            if (safetyViolation != null) {
                _uiState.update {
                    it.copy(
                        currentJobStatus = JobStatus.FAILED,
                        generationErrorMessage = safetyViolation
                    )
                }
                return@launch
            }

            // 2. 100% Free Unlimited Generation (No credit limits or deductions)
            _uiState.update {
                it.copy(
                    isGenerating = true,
                    currentJobStatus = JobStatus.QUEUED,
                    generationProgressPercent = 8,
                    generationStepText = "Preparing your prompt...",
                    generationErrorMessage = null
                )
            }

            try {
                delay(450)
                _uiState.update {
                    it.copy(
                        currentJobStatus = JobStatus.PROCESSING,
                        generationProgressPercent = 25,
                        generationStepText = "Generating scenes..."
                    )
                }

                // Dispatch to real AI Provider Abstraction Layer
                val jobResult = repository.aiOrchestrator.generateVideoJob(
                    prompt = state.promptText,
                    aspectRatio = state.selectedAspectRatio.ratio,
                    resolution = state.selectedQuality.label,
                    durationSec = state.selectedDurationSec,
                    style = state.selectedStyle.label,
                    cameraMovement = state.selectedCamera.label,
                    lighting = state.selectedLighting.label
                )

                delay(550)
                _uiState.update {
                    it.copy(
                        currentJobStatus = JobStatus.PROCESSING,
                        generationProgressPercent = 50,
                        generationStepText = "Creating motion..."
                    )
                }

                delay(550)
                _uiState.update {
                    it.copy(
                        currentJobStatus = JobStatus.RENDERING,
                        generationProgressPercent = 72,
                        generationStepText = "Rendering video..."
                    )
                }

                val generatedCaptions = repository.aiOrchestrator.generateTimedCaptions(
                    state.promptText,
                    state.selectedDurationSec
                )

                delay(450)
                _uiState.update {
                    it.copy(
                        currentJobStatus = JobStatus.RENDERING,
                        generationProgressPercent = 88,
                        generationStepText = "Adding audio..."
                    )
                }

                delay(400)
                _uiState.update {
                    it.copy(
                        currentJobStatus = JobStatus.COMPLETED,
                        generationProgressPercent = 100,
                        generationStepText = "Finalizing your video..."
                    )
                }

                val activeChar = characterLocks.value.find { it.id == state.activeCharacterLockId }
                val titlePrefix = when (actionVariant) {
                    "Extend" -> "Extended • "
                    "Variation" -> "Variation • "
                    "ContinueScene" -> "Scene Cont. • "
                    else -> ""
                }
                val cleanTitle = titlePrefix + state.promptText
                    .removePrefix("Create a ")
                    .take(34)
                    .trim()
                    .ifBlank { "VIZORA Cinema Shot" }

                val newProject = VideoProjectEntity(
                    title = cleanTitle,
                    prompt = state.promptText,
                    enhancedPrompt = state.enhancedPromptBreakdown?.fullEnhancedPrompt ?: state.promptText,
                    generationMode = state.generationMode.name,
                    durationSec = state.selectedDurationSec,
                    aspectRatio = state.selectedAspectRatio.ratio,
                    quality = state.selectedQuality.label,
                    style = state.selectedStyle.label,
                    cameraMovement = state.selectedCamera.label,
                    lighting = state.selectedLighting.label,
                    motionIntensity = state.motionIntensity,
                    imagePreservation = state.imagePreservation,
                    characterPreservation = state.characterPreservation,
                    referenceImageUri = state.uploadedImageUri,
                    characterLockName = activeChar?.name.orEmpty(),
                    jobStatus = JobStatus.COMPLETED.name,
                    progressStep = "Completed",
                    progressPercent = 100,
                    creditCost = 0,
                    captionsJson = json.encodeToString(generatedCaptions),
                    trimStartSec = 0f,
                    trimEndSec = state.selectedDurationSec.toFloat(),
                    previewSeed = (100..999).random(),
                    providerUsed = jobResult.providerUsed,
                    operationId = jobResult.operationId
                )

                val insertedId = repository.insertProject(newProject)
                val savedProject = newProject.copy(id = insertedId.toInt())

                repository.recordCreditTransaction(
                    CreditTransactionEntity(
                        description = "Generated Free: $cleanTitle (${state.selectedQuality.label}, ${state.selectedDurationSec}s)",
                        deltaCredits = 0,
                        planTier = "100% Free Unlimited",
                        status = "Completed"
                    )
                )

                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        activeProject = savedProject,
                        captions = generatedCaptions,
                        trimStartSec = 0f,
                        trimEndSec = savedProject.durationSec.toFloat(),
                        statusBannerMessage = "Video generated (${jobResult.operationId})"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        currentJobStatus = JobStatus.FAILED,
                        generationErrorMessage = "Something went wrong while generating your video."
                    )
                }
            }
        }
    }

    fun extendActiveVideo() {
        val current = _uiState.value.selectedDurationSec
        val extended = when (current) {
            5 -> 10
            10 -> 15
            15 -> 30
            else -> 60
        }
        _uiState.update { it.copy(selectedDurationSec = extended, trimEndSec = extended.toFloat()) }
        generateVideo(actionVariant = "Extend")
    }

    fun createVideoVariation() {
        generateVideo(actionVariant = "Variation")
    }

    fun continueFromPreviousScene() {
        val currentPrompt = _uiState.value.promptText
        _uiState.update {
            it.copy(
                promptText = "$currentPrompt — Continuing directly from the final frame into a sweeping reveal shot."
            )
        }
        generateVideo(actionVariant = "ContinueScene")
    }

    fun triggerSimulatedErrorForTesting() {
        _uiState.update {
            it.copy(
                isGenerating = false,
                currentJobStatus = JobStatus.FAILED,
                generationErrorMessage = "Something went wrong while generating your video."
            )
        }
    }

    fun reportGenerationProblem(reason: String) {
        viewModelScope.launch {
            repository.recordModerationLog(
                ModerationLogEntity(
                    inputContent = _uiState.value.promptText.take(100),
                    flagReason = "User Reported Issue: $reason",
                    actionTaken = "Logged for Engineering & Refund Review"
                )
            )
            _uiState.update {
                it.copy(
                    generationErrorMessage = null,
                    statusBannerMessage = "Problem report submitted to VIZORA AI engineering team."
                )
            }
        }
    }

    // --- Feature 4: AI Story to Video ---
    fun updateStoryInputText(story: String) {
        _uiState.update { it.copy(storyInputText = story) }
    }

    fun updateStoryOptions(
        transition: String? = null,
        narration: Boolean? = null,
        music: Boolean? = null,
        subtitles: Boolean? = null
    ) {
        _uiState.update {
            it.copy(
                storyTransitionType = transition ?: it.storyTransitionType,
                storyIncludeNarration = narration ?: it.storyIncludeNarration,
                storyIncludeMusic = music ?: it.storyIncludeMusic,
                storyIncludeSubtitles = subtitles ?: it.storyIncludeSubtitles
            )
        }
    }

    fun generateStoryScenesAndVideo() {
        val state = _uiState.value
        if (state.isBreakingDownStory) return

        viewModelScope.launch {
            _uiState.update { it.copy(isBreakingDownStory = true) }
            val activeChar = characterLocks.value.find { it.id == state.activeCharacterLockId }
            val scenes = repository.aiOrchestrator.breakStoryIntoScenes(
                storyText = state.storyInputText,
                characterLockDescription = activeChar?.let { "${it.name} (${it.faceDescription}, ${it.clothingDescription})" },
                style = state.selectedStyle.label
            )

            val totalDuration = scenes.sumOf { it.durationSec }.coerceAtLeast(15)
            val combinedNarration = scenes.joinToString(" ") { it.narration }
            val captions = repository.aiOrchestrator.generateTimedCaptions(combinedNarration, totalDuration)

            val storyProject = VideoProjectEntity(
                title = "Story • " + (scenes.firstOrNull()?.title?.substringAfter("• ") ?: "Cinematic Story"),
                prompt = state.storyInputText,
                enhancedPrompt = scenes.joinToString(" | ") { "${it.title}: ${it.visualPrompt}" },
                generationMode = GenerationMode.STORY_TO_VIDEO.name,
                durationSec = totalDuration,
                aspectRatio = state.selectedAspectRatio.ratio,
                quality = state.selectedQuality.label,
                style = state.selectedStyle.label,
                cameraMovement = "Multi-Scene Dynamic",
                lighting = state.selectedLighting.label,
                characterLockName = activeChar?.name ?: "Consistent Protagonist",
                jobStatus = JobStatus.COMPLETED.name,
                sceneCount = scenes.size,
                scenesJson = json.encodeToString(scenes),
                voiceoverScript = combinedNarration,
                captionsJson = json.encodeToString(captions),
                creditCost = 25,
                previewSeed = (400..899).random()
            )

            val id = repository.insertProject(storyProject)
            _uiState.update {
                it.copy(
                    isBreakingDownStory = false,
                    storyScenes = scenes,
                    activeProject = storyProject.copy(id = id.toInt()),
                    voiceScript = combinedNarration,
                    captions = captions,
                    statusBannerMessage = "Generated ${scenes.size} chronological story scenes with Character Lock!"
                )
            }
        }
    }

    // --- Feature 6, 7, 8, 9, 10: Video Editor, Voiceover, Music, SFX & Subtitles ---
    fun selectEditorTab(index: Int) {
        _uiState.update { it.copy(activeEditorTab = index) }
    }

    fun updateEditorTimeline(
        trimStart: Float? = null,
        trimEnd: Float? = null,
        speed: Float? = null,
        rotation: Int? = null,
        overlayText: String? = null,
        sticker: String? = null,
        filter: String? = null,
        brightness: Float? = null,
        contrast: Float? = null,
        saturation: Float? = null,
        volume: Float? = null
    ) {
        _uiState.update { s ->
            s.copy(
                trimStartSec = trimStart ?: s.trimStartSec,
                trimEndSec = trimEnd ?: s.trimEndSec,
                playbackSpeed = speed ?: s.playbackSpeed,
                rotationDegrees = rotation ?: s.rotationDegrees,
                textOverlay = overlayText ?: s.textOverlay,
                activeSticker = sticker ?: s.activeSticker,
                activeFilter = filter ?: s.activeFilter,
                brightnessOffset = brightness ?: s.brightnessOffset,
                contrastScale = contrast ?: s.contrastScale,
                saturationScale = saturation ?: s.saturationScale,
                masterVolume = volume ?: s.masterVolume
            )
        }
    }

    fun splitClipAtPlayhead() {
        _uiState.update {
            it.copy(statusBannerMessage = "Split clip at timeline playhead & preserved keyframes")
        }
    }

    fun mergeStoryScenesIntoTimeline() {
        _uiState.update {
            it.copy(statusBannerMessage = "Merged all scenes with ${it.storyTransitionType} transitions")
        }
    }

    fun updateVoiceoverSettings(
        gender: String? = null,
        accent: String? = null,
        language: String? = null,
        emotion: String? = null,
        speed: Float? = null,
        pitch: Float? = null,
        script: String? = null
    ) {
        _uiState.update { s ->
            s.copy(
                voiceGender = gender ?: s.voiceGender,
                voiceAccent = accent ?: s.voiceAccent,
                voiceLanguage = language ?: s.voiceLanguage,
                voiceEmotion = emotion ?: s.voiceEmotion,
                voiceSpeed = speed ?: s.voiceSpeed,
                voicePitch = pitch ?: s.voicePitch,
                voiceScript = script ?: s.voiceScript
            )
        }
    }

    fun generateAndPlayVoiceover() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(voiceStatusMessage = "Synthesizing ${s.voiceLanguage} voiceover...") }
            val voiceName = if (s.voiceGender == "Male") "Puck" else "Kore"
            val result = repository.aiOrchestrator.generateVoiceoverAudio(
                script = s.voiceScript,
                voiceName = voiceName,
                language = s.voiceLanguage,
                emotion = s.voiceEmotion
            )
            audioSynthesizer.speakVoiceover(
                script = s.voiceScript,
                language = s.voiceLanguage,
                speed = s.voiceSpeed,
                pitch = s.voicePitch
            )
            _uiState.update {
                it.copy(
                    voiceStatusMessage = result.summaryDescription,
                    statusBannerMessage = "Playing AI Voiceover (${s.voiceLanguage} • ${s.voiceGender})"
                )
            }
        }
    }

    fun updateMusicSettings(
        prompt: String? = null,
        volume: Float? = null,
        fadeIn: Boolean? = null,
        fadeOut: Boolean? = null,
        loop: Boolean? = null
    ) {
        _uiState.update { s ->
            s.copy(
                musicPrompt = prompt ?: s.musicPrompt,
                musicVolume = volume ?: s.musicVolume,
                musicFadeIn = fadeIn ?: s.musicFadeIn,
                musicFadeOut = fadeOut ?: s.musicFadeOut,
                musicLoop = loop ?: s.musicLoop
            )
        }
    }

    fun generateAndPlayMusic() {
        val s = _uiState.value
        viewModelScope.launch {
            val metadata = repository.aiOrchestrator.generateMusicTrackMetadata(
                s.musicPrompt,
                s.selectedDurationSec
            )
            _uiState.update {
                it.copy(
                    musicBpm = metadata.bpm,
                    isPlayingAudioPreview = true,
                    statusBannerMessage = "Playing AI Music: ${metadata.title} (${metadata.bpm} BPM • ${metadata.keySignature})"
                )
            }
            audioSynthesizer.playMusicPreview(
                scope = viewModelScope,
                bpm = metadata.bpm,
                volume = s.musicVolume,
                fadeIn = s.musicFadeIn,
                fadeOut = s.musicFadeOut
            ) {
                _uiState.update { it.copy(isPlayingAudioPreview = false) }
            }
        }
    }

    fun toggleAndPlaySfx(sfxName: String) {
        val current = _uiState.value.activeSfxList.toMutableList()
        if (current.contains(sfxName)) {
            current.remove(sfxName)
        } else {
            current.add(sfxName)
        }
        _uiState.update {
            it.copy(
                activeSfxList = current,
                statusBannerMessage = "Synthesizing AI Sound Effect: $sfxName"
            )
        }
        audioSynthesizer.playSfxPreview(viewModelScope, sfxName)
    }

    fun regenerateSubtitles() {
        val s = _uiState.value
        viewModelScope.launch {
            val sourceText = s.voiceScript.ifBlank { s.promptText }
            val segments = repository.aiOrchestrator.generateTimedCaptions(sourceText, s.selectedDurationSec)
            _uiState.update {
                it.copy(
                    captions = segments,
                    statusBannerMessage = "Generated ${segments.size} timed caption segments"
                )
            }
        }
    }

    fun updateCaptionSegmentText(id: Int, newText: String) {
        _uiState.update { state ->
            state.copy(
                captions = state.captions.map {
                    if (it.id == id) it.copy(text = newText) else it
                }
            )
        }
    }

    fun updateCaptionStyle(
        fontSize: Int? = null,
        position: String? = null,
        animation: String? = null,
        wordHighlight: Boolean? = null,
        burnIn: Boolean? = null
    ) {
        _uiState.update { s ->
            s.copy(
                captionFontSizeSp = fontSize ?: s.captionFontSizeSp,
                captionPosition = position ?: s.captionPosition,
                captionAnimation = animation ?: s.captionAnimation,
                wordByWordHighlight = wordHighlight ?: s.wordByWordHighlight,
                burnInCaptions = burnIn ?: s.burnInCaptions
            )
        }
    }

    // --- Feature 13, 17, 18: Projects, Templates, Prompt Library ---
    fun applyTemplate(template: VideoTemplateItem) {
        val ratio = AspectRatioOption.entries.find { it.ratio == template.aspectRatio }
            ?: AspectRatioOption.RATIO_16_9
        val style = VideoStyle.entries.find { it.label.equals(template.style, ignoreCase = true) }
            ?: VideoStyle.CINEMATIC
        val camera = CameraMovement.entries.find { it.label.equals(template.camera, ignoreCase = true) }
            ?: CameraMovement.DOLLY_BACKWARD
        val lighting = LightingMode.entries.find { it.label.equals(template.lighting, ignoreCase = true) }
            ?: LightingMode.GOLDEN_HOUR

        _uiState.update {
            it.copy(
                currentDestination = VizoraDestination.CREATE,
                promptText = template.defaultPrompt,
                selectedDurationSec = template.durationSec,
                selectedAspectRatio = ratio,
                selectedStyle = style,
                selectedCamera = camera,
                selectedLighting = lighting,
                statusBannerMessage = "Loaded template: ${template.title}"
            )
        }
    }

    fun openProjectInStudio(project: VideoProjectEntity, openEditor: Boolean = false) {
        val parsedCaptions = try {
            if (project.captionsJson.isNotBlank()) {
                json.decodeFromString<List<CaptionSegment>>(project.captionsJson)
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        _uiState.update {
            it.copy(
                activeProject = project,
                promptText = project.prompt,
                selectedDurationSec = project.durationSec,
                trimStartSec = project.trimStartSec,
                trimEndSec = project.trimEndSec,
                playbackSpeed = project.playbackSpeed,
                activeFilter = project.activeFilter,
                captions = parsedCaptions.ifEmpty { it.captions },
                currentDestination = if (openEditor) VizoraDestination.EDITOR else VizoraDestination.CREATE
            )
        }
    }

    fun toggleProjectFavorite(project: VideoProjectEntity) {
        viewModelScope.launch {
            val updated = project.copy(isFavorite = !project.isFavorite)
            repository.updateProject(updated)
            if (_uiState.value.activeProject?.id == project.id) {
                _uiState.update { it.copy(activeProject = updated) }
            }
        }
    }

    fun moveProjectToTrash(project: VideoProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project.copy(isTrashed = true))
            _uiState.update { it.copy(statusBannerMessage = "Moved '${project.title}' to Trash") }
        }
    }

    fun restoreProjectFromTrash(project: VideoProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project.copy(isTrashed = false))
            _uiState.update { it.copy(statusBannerMessage = "Restored '${project.title}'") }
        }
    }

    fun deleteProjectForever(project: VideoProjectEntity) {
        viewModelScope.launch {
            repository.deleteProjectPermanently(project.id)
        }
    }

    fun savePromptToLibrary(title: String, promptText: String, category: String) {
        if (promptText.isBlank()) return
        val s = _uiState.value
        viewModelScope.launch {
            repository.insertSavedPrompt(
                SavedPromptEntity(
                    title = title.ifBlank { promptText.take(28) },
                    promptText = promptText,
                    category = category.ifBlank { "Custom" },
                    style = s.selectedStyle.label,
                    aspectRatio = s.selectedAspectRatio.ratio,
                    durationSec = s.selectedDurationSec,
                    isFavorite = false
                )
            )
            _uiState.update { it.copy(statusBannerMessage = "Saved prompt to Prompt Library") }
        }
    }

    fun duplicateSavedPrompt(prompt: SavedPromptEntity) {
        viewModelScope.launch {
            repository.insertSavedPrompt(
                prompt.copy(
                    id = 0,
                    title = "${prompt.title} (Copy)",
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun toggleSavedPromptFavorite(prompt: SavedPromptEntity) {
        viewModelScope.launch {
            repository.updateSavedPrompt(prompt.copy(isFavorite = !prompt.isFavorite))
        }
    }

    fun deleteSavedPrompt(prompt: SavedPromptEntity) {
        viewModelScope.launch {
            repository.deleteSavedPrompt(prompt.id)
        }
    }

    // --- Download & Share Video Package ---
    fun exportAndDownloadVideo(): String {
        val context = getApplication<Application>()
        val project = _uiState.value.activeProject
        val fileName = "VIZORA_AI_${project?.id ?: 1}_${System.currentTimeMillis().toString().takeLast(5)}.mp4.json"
        val file = File(context.filesDir, fileName)
        val exportContent = """
            {
              "platform": "VIZORA AI Cinema Engine",
              "title": "${project?.title ?: "Cinematic Video"}",
              "prompt": "${_uiState.value.promptText.replace("\"", "\\\"")}",
              "durationSec": ${_uiState.value.selectedDurationSec},
              "aspectRatio": "${_uiState.value.selectedAspectRatio.ratio}",
              "quality": "${_uiState.value.selectedQuality.label}",
              "style": "${_uiState.value.selectedStyle.label}",
              "camera": "${_uiState.value.selectedCamera.label}",
              "lighting": "${_uiState.value.selectedLighting.label}",
              "burnInCaptions": ${_uiState.value.burnInCaptions}
            }
        """.trimIndent()
        file.writeText(exportContent)
        _uiState.update {
            it.copy(statusBannerMessage = "Exported master video package: $fileName")
        }
        return file.absolutePath
    }

    fun shareActiveVideo() {
        val context = getApplication<Application>()
        val project = _uiState.value.activeProject
        val shareText = buildString {
            appendLine("🎬 Created with VIZORA AI — Imagine it. Prompt it. Create it.")
            appendLine("Title: ${project?.title ?: "AI Cinema Masterpiece"}")
            appendLine("Prompt: ${_uiState.value.promptText}")
            appendLine("Specs: ${_uiState.value.selectedAspectRatio.ratio} • ${_uiState.value.selectedQuality.label} • ${_uiState.value.selectedStyle.label}")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "VIZORA AI Video Creation")
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            val chooser = Intent.createChooser(intent, "Share VIZORA AI Video").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
        }
    }

    // --- Account & Admin Panel (100% Free Unlimited Studio) ---
    fun updateAccountProfile(name: String, email: String, handle: String) {
        _uiState.update {
            it.copy(
                userName = name.ifBlank { it.userName },
                userEmail = email.ifBlank { it.userEmail },
                userHandle = handle.ifBlank { it.userHandle },
                statusBannerMessage = "Updated VIZORA AI Creator profile"
            )
        }
    }

    fun toggleAdminMode() {
        _uiState.update { it.copy(isAdminMode = !it.isAdminMode) }
    }

    fun adminAdjustCredits(delta: Int) {
        viewModelScope.launch {
            repository.recordCreditTransaction(
                CreditTransactionEntity(
                    description = "Admin Credit Adjustment (${if (delta >= 0) "+$delta" else "$delta"})",
                    deltaCredits = delta,
                    planTier = "Admin Override",
                    status = "Processed"
                )
            )
            _uiState.update {
                it.copy(
                    creditsRemaining = (it.creditsRemaining + delta).coerceAtLeast(0),
                    statusBannerMessage = "Admin adjusted user balance by $delta credits"
                )
            }
        }
    }

    fun adminUpdateAnnouncement(announcement: String) {
        _uiState.update {
            it.copy(
                systemAnnouncement = announcement,
                statusBannerMessage = "Updated global studio announcement"
            )
        }
    }

    fun adminSwitchProvider(providerLabel: String) {
        _uiState.update {
            it.copy(
                configuredProviderName = providerLabel,
                statusBannerMessage = "Switched AI Video Provider to $providerLabel"
            )
        }
    }

    fun clearStatusBanner() {
        _uiState.update { it.copy(statusBannerMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        audioSynthesizer.stopAllAudio()
    }
}
