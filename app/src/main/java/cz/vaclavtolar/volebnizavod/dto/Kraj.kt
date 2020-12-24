package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Kraj {
    var ciskraj: Int? = null
    var nazkraj: String? = null
    var pocetmandatu: Int? = null
    var strana: List<Strana>? = null
}