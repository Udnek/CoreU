package me.udnek.coreu.mgu;

import me.udnek.coreu.mgu.game.MGUGameInstance;

@org.jspecify.annotations.NullMarked public class MGUId{

    private static int CREATED_IDS = 0;

    private final String id;

    protected MGUId(String id){
        this.id = id;
    }

    public String asString(){return id;}

    @Override
    public String toString() {
        return "MGUId{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MGUId mguId && mguId.id.equals(this.id);
    }

    public static MGUId generateNew(MGUGameInstance mguGameInstance){
        return new MGUId(mguGameInstance.getType().getId() + "_" + CREATED_IDS++);
        //return new MGUId(mguGameInstance.getType().getId() + "_" + UUID.randomUUID().toString().substring(0, 5));
    }
}
