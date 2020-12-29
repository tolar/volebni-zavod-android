package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Okres {
    var nazokres: String? = null
    var nutsokres: String? = null
    var hlasystrana: List<Hlasystrana>? = null
}