package com.chacha.domain;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class OrderOption {

    private String flavor;
    private String richness;
    private String garlic;
    private String greenOnion;
    private String chashu;
    private String spicySauce;
    private String noodleTexture;

    @Builder
    public OrderOption(String flavor, String richness, String garlic, String greenOnion, String chashu, String spicySauce, String noodleTexture) {
        this.flavor = flavor;
        this.richness = richness;
        this.garlic = garlic;
        this.greenOnion = greenOnion;
        this.chashu = chashu;
        this.spicySauce = spicySauce;
        this.noodleTexture = noodleTexture;
    }
}

