package kg.kadyrbekov.model.enums.carsenum;

public enum Color {

    RED("#FF0000"),
    BLUE("#0000FF"),
    GREEN("#00FF00"),
    YELLOW("#FFFF00"),
    ORANGE("#FFA500"),
    PURPLE("#800080"),
    PINK("#FFC0CB"),
    BLACK("#000000"),
    WHITE("#FFFFFF"),
    GRAY("#808080"),
    SILVER("#C0C0C0"),
    BROWN("#A52A2A"),
    GOLD("#FFD700"),
    BEIGE("#F5F5DC"),
    MAROON("#800000");

    private String code;

    Color(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
