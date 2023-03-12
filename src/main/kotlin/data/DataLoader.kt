package data

import model.Date
import model.ShareData
import java.io.File
import java.util.*

object DataLoader {
    private const val DATA_FILENAME = "data/data.csv"

    private var developedData: Map<Date, Double>? = null
    private var emergingData: Map<Date, Double>? = null
    private var commodityData: Map<Date, Double>? = null
    private var goldUsdData: Map<Date, Double>? = null
    private var shillerPESP500RatioData: Map<Date, Double>? = null
    private var dowToGoldRatioData: Map<Date, Double>? = null
    private var cpiData: Map<Date, Double>? = null

    private fun loadData() {
        val scanner = Scanner(File(DATA_FILENAME))
        developedData = mutableMapOf()
        emergingData = mutableMapOf()
        commodityData = mutableMapOf()
        goldUsdData = mutableMapOf()
        dowToGoldRatioData = mutableMapOf()
        shillerPESP500RatioData = mutableMapOf()
        cpiData = mutableMapOf()
        scanner.next()
        scanner.forEach {
            val data = it.split(",")
            (developedData as MutableMap) += Pair(Date.fromString(data[0]), data[1].toDouble())
            (emergingData as MutableMap) += Pair(Date.fromString(data[0]), data[2].toDouble())
            (commodityData as MutableMap) += Pair(Date.fromString(data[0]), data[3].toDouble())
            (goldUsdData as MutableMap) += Pair(Date.fromString(data[0]), data[4].toDouble())
            (dowToGoldRatioData as MutableMap) += Pair(Date.fromString(data[0]), data[5].toDouble())
            (shillerPESP500RatioData as MutableMap) += Pair(Date.fromString(data[0]), data[6].toDouble())
            (cpiData as MutableMap) += Pair(Date.fromString(data[0]), data[7].toDouble())
        }
    }

    private fun isLoaded() = developedData != null

    fun loadDevelopedData(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return developedData!!
    }

    fun loadEmergingData(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return emergingData!!
    }

    fun loadCommodityData(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return commodityData!!
    }

    fun loadGoldUsdData(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return goldUsdData!!
    }

    fun loadShillerPESP500Ratio(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return shillerPESP500RatioData!!
    }

    fun loadDowToGoldData(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return dowToGoldRatioData!!
    }

    fun loadCpiData(): Map<Date, Double> {
        if (!isLoaded()) {
            loadData()
        }
        return cpiData!!
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
