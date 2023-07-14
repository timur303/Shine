package kg.kadyrbekov.model.enums.carsenum;

public enum State {

    GOOD("Good"),
    PERFECT("Perfect"),
    ACCIDENT("Accident"),
    NON_OPERATIONAL("Non-Operational"),
    NEW("New");

    private String state;

    State(String state) {
        this.state = state;
    }


    public String getState() {
        return state;
    }
}


