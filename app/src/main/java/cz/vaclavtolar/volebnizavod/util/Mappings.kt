package cz.vaclavtolar.volebnizavod.util

object Mappings {
    val CISKRAJ_TO_PATH: Map<Int, String> = mapOf(
        1 to "praha",
        2 to "stredocesky",
        3 to "jihocesky",
        4 to "plzensky",
        5 to "karlovarsky",
        6 to "ustecky",
        7 to "liberecky",
        8 to "kralovehradecky",
        9 to "pardubicky",
        10 to "vysocina",
        11 to "jihomoravsky",
        12 to "olomoucky",
        13 to "zlinsky",
        14 to "moravskoslezky"
    )

    val DISTRICT_2006_NUTS_TO_NUTS: Map<String, String> = mapOf(
        // Vysocina
        "CZ0611" to "CZ0631",
        "CZ0612" to "CZ0632",
        "CZ0613" to "CZ0633",
        "CZ0614" to "CZ0634",
        "CZ0615" to "CZ0635",
        // Jihomoravsky kraj
        "CZ0621" to "CZ0641",
        "CZ0622" to "CZ0642",
        "CZ0623" to "CZ0643",
        "CZ0624" to "CZ0644",
        "CZ0625" to "CZ0645",
        "CZ0626" to "CZ0646",
        "CZ0627" to "CZ0647",
    )
}