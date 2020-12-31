package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Hodnotystrana {
    var hlasy: Int? = null
    var prochlasu: Double? = null
    var mandaty: Int? = null
    var procmandatu: Double? = null
}