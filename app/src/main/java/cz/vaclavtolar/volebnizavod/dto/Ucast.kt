package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Ucast {
    var okrskycelkem: Int? = null
    var okrskyzprac: Int? = null
    var okrskyzpracproc: Double? = null
    var zapsanivolici: Int? = null
    var vydaneobalky: Int? = null
    var ucastproc: Double? = null
    var odevzdaneobalky: Int? = null
    var platnehlasy: Int? = null
    var platnehlasyproc: Double? = null
}