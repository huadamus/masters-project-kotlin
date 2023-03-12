package data

import log
import model.ShareData
import model.Date
import java.io.File
import java.util.*

object DataConverter {
    private const val DEVELOPED_DATA_FILENAME = "data/msci_world.csv"
    private const val DEVELOPED_DAILY_DATA_FILENAME = "data/msci_world_daily.csv"
    private const val EMERGING_DATA_FILENAME = "data/msci_emerging.csv"
    private const val EMERGING_DAILY_DATA_FILENAME = "data/msci_emerging_daily.csv"
    private const val CRB_DATA_FILENAME = "data/crb.csv"
    private const val OIL_DATA_FILENAME = "data/crude_oil.csv"
    private const val GOLD_USD_DATA_FILENAME = "data/gold_usd.csv"
    private const val SHILLER_PE_SP500_DATA_FILENAME = "data/shiller_sp_500_pe_ratio.csv"
    private const val DOW_TO_GOLD_RATIO_DATA_FILENAME = "data/dow_to_gold_ratio.csv"
    private const val DOW_JONES_DATA_FILENAME = "data/dow_jones.csv"
    private const val CPI_DATA_FILENAME = "data/cpi.csv"

    private var developedData: Map<Date, Double>? = null
    private var developedDataDaily: Map<Date, Double>? = null
    private var emergingData: Map<Date, Double>? = null
    private var crbData: Map<Date, Double>? = null
    private var goldUsdData: Map<Date, Double>? = null
    private var shillerPESP500RatioData: Map<Date, Double>? = null
    private var dowToGoldRatioData: Map<Date, Double>? = null
    private var dowJonesData: Map<Date, Double>? = null
    private var cpiData: Map<Date, Double>? = null

    fun convert() {
        var lastDeveloped = -1.0
        var lastEmerging = -1.0
        var lastCommodity = -1.0
        var lastGold = -1.0
        var lastdowToGold = -1.0
        var lastShillerPe = -1.0
        var lastCpi = -1.0

        loadDevelopedData()
        loadEmergingData()
        loadCrbAndOilData()
        loadGoldUsdData()
        loadDowToGoldData()
        loadShillerPESP500Ratio()
        loadCpiData()

        log("Date,MSCIDeveloped,MSCIEmerging,Commodity,Gold,DowToGold,ShillerPE,Cpi")
        var date = Date(1, 1, 1987)
        while (date != Date(1, 1, 2022)) {
            if (developedDataDaily!!.containsKey(date)) {
                lastDeveloped = developedDataDaily!![date]!!
            } else {
                if (developedData!!.containsKey(date))
                    lastDeveloped = developedData!![date]!!
            }
            if (emergingData!!.containsKey(date)) {
                lastEmerging = emergingData!![date]!!
            }
            if (crbData!!.containsKey(date)) {
                lastCommodity = crbData!![date]!!
            }
            if (goldUsdData!!.containsKey(date)) {
                lastGold = goldUsdData!![date]!!
            }
            if (dowToGoldRatioData!!.containsKey(date)) {
                lastdowToGold = dowToGoldRatioData!![date]!!
            }
            if (shillerPESP500RatioData!!.containsKey(date)) {
                lastShillerPe = shillerPESP500RatioData!![date]!!
            }
            if (cpiData!!.containsKey(date)) {
                lastCpi = cpiData!![date]!!
            }

            date = date.getRelativeDay(1)
            log("$date,$lastDeveloped,$lastEmerging,$lastCommodity,$lastGold,$lastdowToGold,$lastShillerPe,$lastCpi")
        }
    }

    private fun loadDevelopedData() {
        if (developedData == null) {
            developedData = loadGenericData(DEVELOPED_DATA_FILENAME)
            developedDataDaily = loadStockMarketData(DEVELOPED_DAILY_DATA_FILENAME)
        }
    }

    private fun loadEmergingData(): Map<Date, Double> {
        if (emergingData == null) {
            val indexSwitchDate = Date(28, 2, 2005)
            val monthlyData = loadGenericData(EMERGING_DATA_FILENAME).filter { it.key <= indexSwitchDate }
            val dailyData = loadStockMarketData(EMERGING_DAILY_DATA_FILENAME).filter { it.key >= indexSwitchDate }
            val ratio = monthlyData[indexSwitchDate]!! / dailyData[indexSwitchDate]!!
            val fixedEmergingData = mutableMapOf<Date, Double>()
            monthlyData.forEach {
                fixedEmergingData += Pair(it.key.copy(), it.value / ratio)
            }
            emergingData = fixedEmergingData + dailyData
        }
        return emergingData!!
    }

    private fun loadCrbAndOilData(): Map<Date, Double> {
        if (crbData == null) {
            val indexSwitchDate = Date(4, 1, 1994)
            val oilData = loadGenericData(OIL_DATA_FILENAME).filter { it.key <= indexSwitchDate }
            val crbIndexData = loadStockMarketData(CRB_DATA_FILENAME).filter { it.key >= indexSwitchDate }
            val ratio = crbIndexData[indexSwitchDate]!! / oilData[indexSwitchDate]!!
            val fixedOilData = mutableMapOf<Date, Double>()
            oilData.forEach {
                fixedOilData += Pair(it.key.copy(), it.value * ratio)
            }
            crbData = fixedOilData + crbIndexData
        }
        return crbData!!
    }

    private fun loadGoldUsdData() {
        if (goldUsdData == null) {
            goldUsdData = loadStockMarketData(GOLD_USD_DATA_FILENAME)
        }
    }

    private fun loadShillerPESP500Ratio() {
        if (shillerPESP500RatioData == null) {
            shillerPESP500RatioData = loadGenericData(SHILLER_PE_SP500_DATA_FILENAME)
        }
    }

    private fun loadDowJonesData() {
        if (dowJonesData == null) {
            dowJonesData = loadStockMarketData(DOW_JONES_DATA_FILENAME)
        }
    }

    private fun loadDowToGoldData(): Map<Date, Double> {
        loadGoldUsdData()
        loadDowJonesData()
        val initialDate = Date(27, 12, 1987)
        val endDate = Date(1, 1, 2021)
        var currentDate = initialDate
        var previousDowJones = 0.0
        var previousGold = 0.0
        val dowToGoldRatio = mutableMapOf<Date, Double>()
        while (currentDate < endDate) {
            if (dowJonesData!!.contains(currentDate)) {
                previousDowJones = dowJonesData!![currentDate]!!
            }
            if (goldUsdData!!.contains(currentDate)) {
                previousGold = goldUsdData!![currentDate]!!
            }
            dowToGoldRatio += Pair(currentDate, previousDowJones / previousGold)
            currentDate = currentDate.getRelativeDay(1)
        }
        dowToGoldRatioData = dowToGoldRatio
        return dowToGoldRatioData!!
    }

    private fun loadCpiData() {
        if (cpiData == null) {
            cpiData = loadGenericData(CPI_DATA_FILENAME)
        }
    }

    private fun loadGenericData(filename: String): Map<Date, Double> {
        val scanner = Scanner(File(filename))
        val output = mutableMapOf<Date, Double>()
        scanner.forEach {
            val data = it.split(",")
            if (data.size > 1 && data[1].toDoubleOrNull() != null) {
                val value = data[1].toDoubleOrNull()
                if (value != null) {
                    output[Date.fromString(data[0])] = value
                }
            }
        }
        return output
    }

    private fun loadStockMarketData(filename: String): Map<Date, Double> {
        val scanner = Scanner(File(filename))
        val output = mutableMapOf<Date, Double>()
        scanner.forEach {
            val data = it.split(",")
            if (data.size > 1 && data[1].toDoubleOrNull() != null) {
                val value = ShareData(data[1].toDouble(), data[2].toDouble(), data[3].toDouble(), data[4].toDouble())
                output[Date.fromString(data[0])] = value.close
            }
        }
        return output
    }
}
