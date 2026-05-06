package world;

class EmptyEntity implements WorldEntity {
    public static final EmptyEntity INSTANCE = new EmptyEntity();

    private EmptyEntity() {}
}