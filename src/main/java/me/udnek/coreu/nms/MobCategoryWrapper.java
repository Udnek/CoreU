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

    private final MobCategory category;

    public MobCategoryWrapper(MobCategory category){
        this.category = category;
    }

    @Override
    public MobCategory getNms() {
        return category;
    }
}
