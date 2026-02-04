package me.udnek.coreu.custom.component;

@org.jspecify.annotations.NullMarked public  interface CustomComponent<HolderType>{

    default void throwCanNotChangeDefault(){
        throw new RuntimeException("Can not change default component: " + this + ", create default or apply new firstly");
    }

    CustomComponentType<? super HolderType, ? extends CustomComponent<? super HolderType>> getType();
}
