package cz.vaclavtolar.volebnizavod.util;

import android.graphics.Color;


public enum Party {
    ANO(Color.parseColor("#261060")),
    ODS(Color.parseColor("#004494")),
    PIRATI(Color.parseColor("#000000")),
    SPD(Color.parseColor("#2C86C4")),
    KSCM(Color.parseColor("#D8261C")),
    CSSD(Color.parseColor("#f29400")),
    KDU_CSL(Color.parseColor("#F9DA23")),
    TOP09(Color.parseColor("#993399")),
    STAN(Color.parseColor("#b6c932")),
    SSO(Color.parseColor("#cccccc")),
    SZ(Color.parseColor("#cccccc")),
    ROZUMNI(Color.parseColor("#cccccc")),
    REALISTE(Color.parseColor("#cccccc")),
    SPO(Color.parseColor("#cccccc")),
    SPORTOVCI(Color.parseColor("#cccccc")),
    DSSS(Color.parseColor("#cccccc")),
    SPR_RSC(Color.parseColor("#cccccc")),
    RN_VU(Color.parseColor("#cccccc")),
    ODA(Color.parseColor("#cccccc")),
    BPI_OD(Color.parseColor("#cccccc")),
    REFERENDUM_EU(Color.parseColor("#cccccc")),
    RC(Color.parseColor("#cccccc")),
    COS(Color.parseColor("#cccccc")),
    DV_2016(Color.parseColor("#cccccc")),
    CSNS(Color.parseColor("#cccccc")),
    CIBULKA(Color.parseColor("#cccccc")),
    PROTI_DEVELOPERUM_PU(Color.parseColor("#cccccc")),
    UNIE_HAVEL_2017(Color.parseColor("#cccccc")),
    OBCANE_2011(Color.parseColor("#cccccc")),
    NAROD_SOBE(Color.parseColor("#cccccc")),
    CNF(Color.parseColor("#cccccc"));

    private final Integer color;

    public Integer getColor() {
        return color;
    }

    Party(Integer color) {
        this.color = color;
    }

}
