package cz.vaclavtolar.volebnizavod.dto

class CachedElectionsData(electionsData: ElectionData?) {
    var electionsData: Map<String, ElectionData> = mapOf()
}