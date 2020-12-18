package mapper.mocks;

import nl.jiankai.annotations.Column;

public class OverrideObject {
    @Column(name = "overridden_name")
    private String overriddenName;

    public String getOverriddenName() {
        return overriddenName;
    }

    public void setOverriddenName(String overriddenName) {
        this.overriddenName = overriddenName;
    }
}
