package cz.vaclavtolar.volebnizavod.dto

class CachedElectionData {
    var elections: List<Election>? = null
    var electionsData: Map<String, ElectionData> = mapOf()
    var electionDistrictsData: Map<String, List<ElectionDistrictData>> = mapOf()
}