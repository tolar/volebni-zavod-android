package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Hlasystrana {
    var kstrana: Int? = null
    var prochlasu: Double? = null
}