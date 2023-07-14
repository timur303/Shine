package kg.kadyrbekov.model.enums.carsenum;

public enum InteriorState {
    VELOUR("Velour"),
    LEATHER("Leather"),
    COMBINED("Combined"),
    WOOD("Wood");

    private final String state;

    InteriorState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}

