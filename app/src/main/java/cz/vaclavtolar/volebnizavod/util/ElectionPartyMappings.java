package cz.vaclavtolar.volebnizavod.util;

import java.util.HashMap;
import java.util.Map;

import static cz.vaclavtolar.volebnizavod.util.Party.*;

public class ElectionPartyMappings {

    public static Map<Integer, Party> SNEMOVNA_2017 =  new HashMap<Integer, Party>() {{
        put(1, ODS);
        put(2, RN_VU);
        put(3, COS);
        put(4, CSSD);
        put(5, CIBULKA);
        put(6, RC);
        put(7, STAN);
        put(8, KSCM);
        put(9, SZ);
        put(10, ROZUMNI);
        put(11, PROTI_DEVELOPERUM_PU);
        put(12, SSO);
        put(13, BPI_OD);
        put(14, ODA);
        put(15, PIRATI);
        put(16, OBCANE_2011);
        put(17, UNIE_HAVEL_2017);
        put(18, CNF);
        put(19, REFERENDUM_EU);
        put(20, TOP09);
        put(21, ANO);
        put(22, DV_2016);
        put(23, SPR_RSC);
        put(24, KDU_CSL);
        put(25, CSNS);
        put(26, REALISTE);
        put(27, SPORTOVCI);
        put(28, DSSS);
        put(29, SPD);
        put(30, SPO);
        put(31, NAROD_SOBE);
    }};

    private ElectionPartyMappings() {
    }


}
