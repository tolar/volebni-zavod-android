package cz.vaclavtolar.volebnizavod.dto

class CachedElectionData {
    var elections: List<Election>? = null
    var electionsData: Map<Int, ElectionData> = mutableMapOf()
    var electionDistrictsData: Map<Int, List<ElectionDistrictData>> = mutableMapOf()
}