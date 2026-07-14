package model.entities.zombie;

public enum ZombieEffect {
    NONE,
    CHILLED,        // Slowed movement
    FROZEN,         // Completely frozen
    POISONED,       // Damage over time
    HYPNOTIZED,     // Fighting for player
    ON_FIRE,        // Burning
    GLOWING,        // Drops plant food on death
    ARMORED,        // Has armor
    STUNNED,        // Temporarily stunned
    SPINNING,       // Jester spinning
    DIVING,         // Underwater
    FLYING,         // Dodo riding
    ENRAGED,        // Newspaper zombie enraged
    BLESSED,        // Knight blessed by King
    ROOTED,         // Stuck in place
    SLOWED,         // Movement speed reduced
    FAST,           // Movement speed increased
    HASTE           // Attack speed increased
}
