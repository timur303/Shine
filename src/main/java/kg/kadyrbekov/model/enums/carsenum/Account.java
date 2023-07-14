package kg.kadyrbekov.model.enums.carsenum;
public enum Account {

    KYRGYZSTAN("Kyrgyzstan"),
    KAZAKHSTAN("Kazakhstan"),
    UZBEKISTAN("Uzbekistan"),
    RUSSIA("Russia"),
    BELARUS("Belarus"),
    ABKHAZIA("Abkhazia"),
    ARMENIA("Armenia");

    private String country;

    Account(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}

