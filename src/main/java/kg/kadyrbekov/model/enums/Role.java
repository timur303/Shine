package kg.kadyrbekov.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN("Administrator"),

    MANAGER("Manager"),

    USER("User");

    private final String displayName;

    private Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String getAuthority() {
        return name();
    }
}
