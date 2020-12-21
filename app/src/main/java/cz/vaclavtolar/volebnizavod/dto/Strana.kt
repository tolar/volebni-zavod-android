package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Strana {
    var kstrana: Int? = null
    var nazstr: String? = null
    var hodnotystrana: Hodnotystrana? = null

}

