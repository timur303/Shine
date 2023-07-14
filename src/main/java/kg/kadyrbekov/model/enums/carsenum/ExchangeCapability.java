package kg.kadyrbekov.model.enums.carsenum;

public enum ExchangeCapability {
    BUYER_PAYS_DIFFERENCE("Buyer pays the difference"),
    SELLER_PAYS_DIFFERENCE("Seller pays the difference"),
    KEY_FOR_KEY("Key for key"),
    NO_EXCHANGE("No exchange");

    private String description;

    ExchangeCapability(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

