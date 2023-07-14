package kg.kadyrbekov.model.enums.carsenum;

public enum Currency {
    SOM("som"),
    USD("dollar");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

