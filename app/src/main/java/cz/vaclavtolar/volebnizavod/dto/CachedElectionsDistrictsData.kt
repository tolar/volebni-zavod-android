package cz.vaclavtolar.volebnizavod.dto

class CachedElectionsDistrictsData(electionDistrictsData: List<ElectionDistrictData>?) {
    var electionDistrictsData: MutableMap<Int, List<ElectionDistrictData>> = mutableMapOf()
}