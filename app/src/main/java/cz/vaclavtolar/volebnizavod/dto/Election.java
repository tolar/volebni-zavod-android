package cz.vaclavtolar.volebnizavod.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;

import org.joda.time.LocalDate;

public class Election {

    private String id;

    private String name;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    private LocalDate date;

    private ElectionData data;

    private String updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ElectionData getData() {
        return data;
    }

    public void setData(ElectionData data) {
        this.data = data;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
