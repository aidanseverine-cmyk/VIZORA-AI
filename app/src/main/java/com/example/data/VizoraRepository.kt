package com.example.data

import com.example.network.GeminiVizoraOrchestrator
import kotlinx.coroutines.flow.Flow

class VizoraRepository(
    private val dao: VizoraDao,
    val aiOrchestrator: GeminiVizoraOrchestrator = GeminiVizoraOrchestrator()
) {
    val activeProjects: Flow<List<VideoProjectEntity>> = dao.getActiveProjects()
    val favoriteProjects: Flow<List<VideoProjectEntity>> = dao.getFavoriteProjects()
    val trashedProjects: Flow<List<VideoProjectEntity>> = dao.getTrashedProjects()
    val allProjects: Flow<List<VideoProjectEntity>> = dao.getAllProjects()
    val savedPrompts: Flow<List<SavedPromptEntity>> = dao.getAllSavedPrompts()
    val characterLocks: Flow<List<CharacterLockEntity>> = dao.getCharacterLocks()
    val creditTransactions: Flow<List<CreditTransactionEntity>> = dao.getCreditTransactions()
    val moderationLogs: Flow<List<ModerationLogEntity>> = dao.getModerationLogs()

    suspend fun insertProject(project: VideoProjectEntity): Long = dao.insertProject(project)
    suspend fun updateProject(project: VideoProjectEntity) = dao.updateProject(project)
    suspend fun deleteProjectPermanently(id: Int) = dao.deleteProjectPermanently(id)

    suspend fun insertSavedPrompt(prompt: SavedPromptEntity): Long = dao.insertSavedPrompt(prompt)
    suspend fun updateSavedPrompt(prompt: SavedPromptEntity) = dao.updateSavedPrompt(prompt)
    suspend fun deleteSavedPrompt(id: Int) = dao.deleteSavedPrompt(id)

    suspend fun insertCharacterLock(character: CharacterLockEntity): Long = dao.insertCharacterLock(character)
    suspend fun updateCharacterLock(character: CharacterLockEntity) = dao.updateCharacterLock(character)
    suspend fun deleteCharacterLock(id: Int) = dao.deleteCharacterLock(id)

    suspend fun recordCreditTransaction(tx: CreditTransactionEntity) = dao.insertCreditTransaction(tx)
    suspend fun recordModerationLog(log: ModerationLogEntity) = dao.insertModerationLog(log)

    fun getReadyTemplates(): List<VideoTemplateItem> = listOf(
        VideoTemplateItem(
            id = "tpl_afro_cinema",
            title = "Cinematic Trailer",
            category = "Film & Cinema",
            description = "Anamorphic widescreen trailer with sunset golden hour lighting and sweeping camera motion.",
            defaultPrompt = "Create a cinematic 10-second video of a young African man walking through Dar es Salaam at sunset, realistic lighting, camera slowly moving backward, cinematic atmosphere.",
            durationSec = 10,
            aspectRatio = "21:9",
            style = "African cinematic",
            camera = "Dolly backward",
            lighting = "Sunset",
            thumbnailType = 0
        ),
        VideoTemplateItem(
            id = "tpl_product_ad",
            title = "Product Advertisement",
            category = "Commercial",
            description = "High-contrast studio macro showcase with orbital camera motion and dynamic reflections.",
            defaultPrompt = "Luxury smart chronograph watch levitating above obsidian water ripples, macro studio rim lighting, slow motion water droplets, 360 orbit camera movement, commercial 4K look.",
            durationSec = 10,
            aspectRatio = "16:9",
            style = "Commercial",
            camera = "Orbit",
            lighting = "Studio",
            thumbnailType = 1
        ),
        VideoTemplateItem(
            id = "tpl_real_estate",
            title = "Real Estate Video",
            category = "Architecture",
            description = "Fluid FPV drone fly-through of an oceanfront contemporary villa at golden hour.",
            defaultPrompt = "Architectural drone tour flying smoothly through a glass cliffside villa overlooking the Indian Ocean in Zanzibar at golden hour, warm interior lighting, photorealistic.",
            durationSec = 15,
            aspectRatio = "16:9",
            style = "Photorealistic",
            camera = "Drone shot",
            lighting = "Golden hour",
            thumbnailType = 2
        ),
        VideoTemplateItem(
            id = "tpl_news",
            title = "News Video",
            category = "Broadcast",
            description = "Clean broadcast newsroom intro with holographic world map data visualizations.",
            defaultPrompt = "Modern high-tech broadcast news studio with glowing cyan glass tickers and global satellite map, steady dolly forward camera movement, studio lighting.",
            durationSec = 10,
            aspectRatio = "16:9",
            style = "Realistic",
            camera = "Dolly forward",
            lighting = "Studio",
            thumbnailType = 1
        ),
        VideoTemplateItem(
            id = "tpl_motivational",
            title = "Motivational Video",
            category = "Social Viral",
            description = "Inspiring sunrise ascent with dramatic silhouette lighting and slow-motion intensity.",
            defaultPrompt = "A young Tanzanian boy wearing aviator goggles standing on a lush green hill looking up at a sleek airplane flying across a dramatic sunrise sky, slow motion, inspirational.",
            durationSec = 15,
            aspectRatio = "9:16",
            style = "Cinematic",
            camera = "Crane shot",
            lighting = "Sunrise",
            thumbnailType = 2
        ),
        VideoTemplateItem(
            id = "tpl_tiktok_viral",
            title = "TikTok Viral Video",
            category = "Short Form",
            description = "Fast-paced vertical 9:16 hook with punchy neon aesthetics and kinetic captions.",
            defaultPrompt = "Cyberpunk street dancer performing energetic choreography under glowing holographic rain in Tokyo-Nairobi fusion alley, handheld tracking shot, neon lighting.",
            durationSec = 15,
            aspectRatio = "9:16",
            style = "Cyberpunk",
            camera = "Tracking shot",
            lighting = "Neon",
            thumbnailType = 1
        ),
        VideoTemplateItem(
            id = "tpl_ig_reel",
            title = "Instagram Reel",
            category = "Lifestyle",
            description = "Aesthetic vertical lifestyle reel with dreamy soft cinematic tones.",
            defaultPrompt = "Artisanal coffee being poured in slow motion at a sunlit coastal cafe in Dar es Salaam, steam rising in morning light, shallow depth of field, soft cinematic lighting.",
            durationSec = 10,
            aspectRatio = "9:16",
            style = "Cinematic",
            camera = "Zoom in",
            lighting = "Soft cinematic",
            thumbnailType = 0
        ),
        VideoTemplateItem(
            id = "tpl_documentary",
            title = "Documentary",
            category = "Nature & Culture",
            description = "Authentic handheld natural-light storytelling with rich organic textures.",
            defaultPrompt = "Aerial wildlife documentary shot of elephant herds crossing the Serengeti plains during golden hour dust clouds, sweeping helicopter tracking shot, natural lighting.",
            durationSec = 30,
            aspectRatio = "16:9",
            style = "Documentary",
            camera = "Drone shot",
            lighting = "Golden hour",
            thumbnailType = 0
        ),
        VideoTemplateItem(
            id = "tpl_music_video",
            title = "Music Video",
            category = "Entertainment",
            description = "Vibrant Afrobeat visualiser with anamorphic flares and rhythmic camera cuts.",
            defaultPrompt = "Lead vocalist performing on a rooftop overlooking a glowing city skyline at twilight, warm amber spotlights and violet haze, dynamic crane shot, music video style.",
            durationSec = 30,
            aspectRatio = "16:9",
            style = "Music video",
            camera = "Crane shot",
            lighting = "Dramatic",
            thumbnailType = 1
        ),
        VideoTemplateItem(
            id = "tpl_travel",
            title = "Travel Video",
            category = "Adventure",
            description = "Breathtaking turquoise coastal drone sweep with vibrant tropical grading.",
            defaultPrompt = "Traditional wooden dhow sailboat gliding across crystal-clear turquoise waters off Zanzibar at sunset, aerial drone orbit shot, golden hour reflection.",
            durationSec = 15,
            aspectRatio = "9:16",
            style = "Realistic",
            camera = "Orbit",
            lighting = "Sunset",
            thumbnailType = 0
        ),
        VideoTemplateItem(
            id = "tpl_fashion",
            title = "Fashion Video",
            category = "Editorial",
            description = "High-fashion editorial runway look with crisp studio strobes and slow motion fabric.",
            defaultPrompt = "High-fashion model in avant-garde gold and indigo African couture walking through a minimalist desert pavilion, flowing silk in wind, slow motion dolly backward.",
            durationSec = 10,
            aspectRatio = "4:5",
            style = "Fashion",
            camera = "Dolly backward",
            lighting = "Golden hour",
            thumbnailType = 0
        ),
        VideoTemplateItem(
            id = "tpl_restaurant",
            title = "Restaurant Advertisement",
            category = "Hospitality",
            description = "Mouth-watering culinary macro cinematography with warm flame highlights.",
            defaultPrompt = "Gourmet spiced seafood platter sizzling on an open flame grill with fresh herbs and citrus mist, extreme close-up slow motion pan right, warm dramatic lighting.",
            durationSec = 10,
            aspectRatio = "1:1",
            style = "Commercial",
            camera = "Pan right",
            lighting = "Dramatic",
            thumbnailType = 2
        ),
        VideoTemplateItem(
            id = "tpl_business",
            title = "Business Advertisement",
            category = "Corporate",
            description = "Sleek SaaS & fintech brand story with clean modern glass architecture.",
            defaultPrompt = "Confident startup team collaborating around a transparent holographic strategy table in a sunlit modern skyscraper office, smooth tracking shot, natural lighting.",
            durationSec = 15,
            aspectRatio = "16:9",
            style = "Commercial",
            camera = "Tracking shot",
            lighting = "Natural",
            thumbnailType = 1
        ),
        VideoTemplateItem(
            id = "tpl_educational",
            title = "Educational Video",
            category = "E-Learning",
            description = "Clear 3D animated scientific visualization with crisp labels and smooth zoom.",
            defaultPrompt = "3D educational animation explaining solar energy cells converting sunlight into glowing electrical currents inside a smart microgrid, clean studio lighting, zoom in.",
            durationSec = 30,
            aspectRatio = "16:9",
            style = "3D animation",
            camera = "Zoom in",
            lighting = "Studio",
            thumbnailType = 2
        )
    )
}
