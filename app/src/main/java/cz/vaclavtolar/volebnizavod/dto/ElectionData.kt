package cz.vaclavtolar.volebnizavod.dto;

import java.util.List;

public class ElectionData {

    private List<Kraj> kraj;
    private Cr cr;
    private String datumcasgenerovani;

    public List<Kraj> getKraj() {
        return kraj;
    }

    public void setKraj(List<Kraj> kraj) {
        this.kraj = kraj;
    }

    public Cr getCr() {
        return cr;
    }

    public void setCr(Cr cr) {
        this.cr = cr;
    }

    public String getDatumcasgenerovani() {
        return datumcasgenerovani;
    }

    public void setDatumcasgenerovani(String datumcasgenerovani) {
        this.datumcasgenerovani = datumcasgenerovani;
    }
}
