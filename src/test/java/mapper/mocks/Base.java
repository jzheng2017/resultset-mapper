package mapper.mocks;

import nl.jiankai.annotations.Column;

public abstract class Base {
    private int baseAttribute;
    @Column(name = "overridden_name")
    private String overriddenBaseAttribute;

    public int getBaseAttribute() {
        return baseAttribute;
    }

    public void setBaseAttribute(int baseAttribute) {
        this.baseAttribute = baseAttribute;
    }

    public String getOverriddenBaseAttribute() {
        return overriddenBaseAttribute;
    }

    public void setOverriddenBaseAttribute(String overriddenBaseAttribute) {
        this.overriddenBaseAttribute = overriddenBaseAttribute;
    }
}
