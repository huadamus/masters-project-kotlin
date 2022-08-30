package data

import DAILY
import model.ShareData
import model.Date
import java.io.File
import java.util.*

object DataLoader {
    private const val DEVELOPED_DATA_FILENAME = "data/msci_world.csv"
    private const val EMERGING_DATA_FILENAME = "data/msci_emerging.csv"
    private const val CRB_DATA_FILENAME = "data/crb.csv"
    private const val OIL_DATA_FILENAME = "data/crude_oil.csv"
    private const val GOLD_USD_DATA_FILENAME = "data/gold_usd.csv"
    private const val SHILLER_PE_SP500_DATA_FILENAME = "data/shiller_sp_500_pe_ratio.csv"
    private const val DOW_TO_GOLD_RATIO_DATA_FILENAME = "data/dow_to_gold_ratio.csv"
    private const val DEVELOPED_DAILY_DATA_FILENAME = "data/msci_world_daily.csv"
    private const val EMERGING_DAILY_DATA_FILENAME = "data/msci_emerging_daily.csv"
    private const val DOW_JONES_DATA_FILENAME = "data/dow_jones.csv"

    private var developedData: Map<Date, Double>? = null
    private var emergingData: Map<Date, Double>? = null
    private var crbData: Map<Date, Double>? = null
    private var goldUsdData: Map<Date, Double>? = null
    private var shillerPESP500RatioData: Map<Date, Double>? = null
    private var dowToGoldRatioData: Map<Date, Double>? = null
    private var dowJonesData: Map<Date, Double>? = null

    fun loadDevelopedData(): Map<Date, Double> {
        if (developedData == null) {
            developedData = if(DAILY) {
                loadStockMarketData(DEVELOPED_DAILY_DATA_FILENAME)
            } else {
                loadGenericData(DEVELOPED_DATA_FILENAME)
            }
        }
        return developedData!!
    }

    fun loadEmergingData(): Map<Date, Double> {
        if (emergingData == null) {
            emergingData = if(DAILY) {
                loadStockMarketData(EMERGING_DAILY_DATA_FILENAME)
            } else {
                loadGenericData(EMERGING_DATA_FILENAME)
            }
        }
        return emergingData!!
    }

    fun loadCrbAndOilData(): Map<Date, Double> {
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

    fun loadGoldUsdData(): Map<Date, Double> {
        if (goldUsdData == null) {
            goldUsdData = loadStockMarketData(GOLD_USD_DATA_FILENAME)
        }
        return goldUsdData!!
    }

    fun loadShillerPESP500Ratio(): Map<Date, Double> {
        if (shillerPESP500RatioData == null) {
            shillerPESP500RatioData = loadGenericData(SHILLER_PE_SP500_DATA_FILENAME)
        }
        return shillerPESP500RatioData!!
    }

    private fun loadDowJonesData(): Map<Date, Double> {
        if (dowJonesData == null) {
            dowJonesData = loadStockMarketData(DOW_JONES_DATA_FILENAME)
        }
        return dowJonesData!!
    }

    fun loadDowToGoldData(): Map<Date, Double> {
        loadGoldUsdData()
        loadDowJonesData()
        val initialDate = Date(27, 12, 1987)
        val endDate = Date(1, 1, 2021)
        var currentDate = initialDate
        var previousDowJones = 0.0
        var previousGold = 0.0
        val dowToGoldRatio = mutableMapOf<Date, Double>()
        while(currentDate < endDate) {
            if(dowJonesData!!.contains(currentDate)) {
                previousDowJones = dowJonesData!![currentDate]!!
            }
            if(goldUsdData!!.contains(currentDate)) {
                previousGold = goldUsdData!![currentDate]!!
            }
            dowToGoldRatio += Pair(currentDate, previousDowJones / previousGold)
            currentDate = currentDate.getRelativeDay(1)
        }
        dowToGoldRatioData = dowToGoldRatio
        return dowToGoldRatioData!!
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

    fun loadStockMarketData(filename: String): Map<Date, Double> {
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
