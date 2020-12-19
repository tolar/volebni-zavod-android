package cz.vaclavtolar.volebnizavod.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer
import org.joda.time.LocalDate

class Election {
    var id: String? = null
    var name: String? = null

    @JsonDeserialize(using = LocalDateDeserializer::class)
    var date: LocalDate? = null
    var updated: String? = null
}