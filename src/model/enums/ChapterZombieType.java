package model.enums;

import java.util.ArrayList;
import java.util.List;

public enum ChapterZombieType {
    // زامبی‌های عمومی (مشترک)
    DEFAULT("ZombieDefault", "ALL"),
    CONE_HEAD("ZombieArmor1", "ALL"),
    BUCKET_HEAD("ZombieArmor2", "ALL"),
    BRICK_HEAD("ZombieArmor4", "ALL"),
    KNIGHT("ZombieDarkArmor3", "ALL"),
    GARGANTUAR("ZombieGargantuar", "ALL"),
    IMP("ZombieImp", "ALL"),
    ALL_STAR("ZombieModernAllStar", "ALL"),
    ARCADE("ZombieArcade", "ALL"),
    NEWSPAPER("ZombieNewspaper", "ALL"),

    // مصر باستان (AncientEgypt)
    RA("ZombieRa", "AncientEgypt"),
    EXPLORER("ZombieExplorer", "AncientEgypt"),
    TOMB_RAISER("ZombieTombRaiser", "AncientEgypt"),

    // غارهای یخی (FrostbiteCaves)
    DODO("ZombieIceAgeDodo", "FrostbiteCaves"),
    HUNTER("ZombieIceAgeHunter", "FrostbiteCaves"),
    TROGLOBITE("ZombieIceAgeTroglobite", "FrostbiteCaves"),

    // ساحل موج بزرگ (BigWaveBeach)
    FISHERMAN("ZombieBeachFisherman", "BigWaveBeach"),
    OCTOPUS("ZombieBeachOctopus", "BigWaveBeach"),
    SNORKEL("ZombieBeachSnorkel", "BigWaveBeach"),

    // قرون وسطی / قرون تاریک (DarkAges)
    JUGGLER("ZombieDarkJuggler", "DarkAges"),
    WIZARD("ZombieWizard", "DarkAges"),
    KING("ZombieDarkKing", "DarkAges"),
    IMP_DRAGON("ZombieDarkImpDragon", "DarkAges"),

    // مپ‌های متفرقه (اختیاری بر اساس جدول)
    UMBRELLA("ZombieLostCityJane", "LostCity"),
    TURQUOISE("ZombieCrystalSkull", "LostCity"),
    PROSPECTOR("ZombieProspector", "LostCity"),
    PIANO("ZombiePiano", "WildWest");

    private final String jsonId;
    private final String chapterRestriction;

    ChapterZombieType(String jsonId, String chapterRestriction) {
        this.jsonId = jsonId;
        this.chapterRestriction = chapterRestriction;
    }

    public String getJsonId() {
        return jsonId;
    }

    public String getChapterRestriction() {
        return chapterRestriction;
    }

    public static List<String> getAvailableZombiesForChapter(String activeChapter) {
        List<String> validZombies = new ArrayList<>();
        String baseChapter = activeChapter != null ? activeChapter.replaceAll("\\d+", "") : "ALL";

        for (ChapterZombieType type : values()) {
            if (type.chapterRestriction.equals("ALL") || type.chapterRestriction.equalsIgnoreCase(baseChapter)) {
                validZombies.add(type.jsonId);
            }
        }
        return validZombies;
    }
}