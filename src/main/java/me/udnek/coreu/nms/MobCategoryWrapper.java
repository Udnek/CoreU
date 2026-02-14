package me.udnek.coreu.nms;

import net.minecraft.world.entity.MobCategory;

@org.jspecify.annotations.NullMarked
public final class MobCategoryWrapper implements NmsWrapper<MobCategory>{

    public static final MobCategoryWrapper MONSTER = new MobCategoryWrapper(MobCategory.MONSTER);
    public static final MobCategoryWrapper CREATURE = new MobCategoryWrapper(MobCategory.CREATURE);
    public static final MobCategoryWrapper AMBIENT = new MobCategoryWrapper(MobCategory.AMBIENT);
    public static final MobCategoryWrapper AXOLOTLS = new MobCategoryWrapper(MobCategory.AXOLOTLS);
    public static final MobCategoryWrapper UNDERGROUND_WATER_CREATURE = new MobCategoryWrapper(MobCategory.UNDERGROUND_WATER_CREATURE);
    public static final MobCategoryWrapper WATER_CREATURE = new MobCategoryWrapper(MobCategory.WATER_CREATURE);
    public static final MobCategoryWrapper WATER_AMBIENT = new MobCategoryWrapper(MobCategory.WATER_AMBIENT);
    public static final MobCategoryWrapper MISC = new MobCategoryWrapper(MobCategory.MISC);

    public static MobCategoryWrapper fromNms(MobCategory category){
        return switch (category){
            case MONSTER -> MONSTER;
            case CREATURE -> CREATURE;
            case AMBIENT -> AMBIENT;
            case AXOLOTLS -> AXOLOTLS;
            case UNDERGROUND_WATER_CREATURE -> UNDERGROUND_WATER_CREATURE;
            case WATER_CREATURE -> WATER_CREATURE;
            case WATER_AMBIENT -> WATER_AMBIENT;
            case MISC -> MISC;
        };
    }

    private final MobCategory category;

    private MobCategoryWrapper(MobCategory category){
        this.category = category;
    }

    @Override
    public String toString() {
        return "MobCategoryWrapper{" +
                "category=" + category +
                '}';
    }

    @Override
    public MobCategory getNms() {
        return category;
    }
}
