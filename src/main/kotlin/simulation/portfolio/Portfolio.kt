package simulation.portfolio

import DAILY
import simulation.HistoricalDataStore
import model.Date
import output.round
import java.io.Serializable

abstract class Portfolio(private val startDate: Date) {
    protected lateinit var historicalDataStore: HistoricalDataStore
    val assetsAndReasonsForBuying = mutableSetOf<Pair<Asset, String>>()
    var balanceState = mutableListOf<Pair<Date, Double>>()

    protected var cash = INITIAL_CASH

    protected var developedSaleProfits = 0.0
    protected var emergingSaleProfits = 0.0
    protected var crbSaleProfits = 0.0
    protected var goldSaleProfits = 0.0
    var totalSpentOnBrokerageFees = 0.0
    fun handle(date: Date) {
        if (DAILY) {
            historicalDataStore.updateLastPrices(date)
            historicalDataStore.updateTrails(date)
        } else {
            if (date.isNewMonth()) {
                historicalDataStore.updateLastPrices(date)
                historicalDataStore.updateTrails(date)
            }
        }
        if (date >= startDate) {
            if (DAILY) {
                handleDetails(date)
            }
            if (date.isNewMonth()) {
                if (!DAILY) {
                    handleDetails(date)
                }
                balanceState += Pair(date, getBalance())
            }
        }
    }

    fun getBalance(): Double {
        return cash + getCurrentAssetsValue()
    }

    fun assignFinalValues() {
        val lastDevelopedPrice = historicalDataStore.lastDevelopedPrice
        val lastEmergingPrice = historicalDataStore.lastEmergingPrice
        val lastCrbPrice = historicalDataStore.lastCrbPrice
        val lastGoldPrice = historicalDataStore.lastGoldPrice
        for (asset in assetsAndReasonsForBuying) {
            if (!asset.first.isSold()) {
                val price: Double = when (asset.first.type) {
                    Asset.Type.DEVELOPED -> {
                        lastDevelopedPrice
                    }

                    Asset.Type.EMERGING -> {
                        lastEmergingPrice
                    }

                    Asset.Type.CRB -> {
                        lastCrbPrice
                    }

                    Asset.Type.GOLD -> {
                        lastGoldPrice
                    }
                }
                asset.first.saleValue = asset.first.getCurrentValue(price)
            }
        }
    }

    abstract fun handleDetails(date: Date)

    fun getTotalInvestedByAssetType(type: Asset.Type): Double {
        return assetsAndReasonsForBuying.filter { it.first.type == type }.sumOf { it.first.purchaseValue }
    }

    fun getCurrentAssetsValueByAssetType(type: Asset.Type): Double {
        val price = when (type) {
            Asset.Type.DEVELOPED -> historicalDataStore.lastDevelopedPrice
            Asset.Type.EMERGING -> historicalDataStore.lastEmergingPrice
            Asset.Type.CRB -> historicalDataStore.lastCrbPrice
            Asset.Type.GOLD -> historicalDataStore.lastGoldPrice
        }
        val saleProfits = when (type) {
            Asset.Type.DEVELOPED -> developedSaleProfits
            Asset.Type.EMERGING -> emergingSaleProfits
            Asset.Type.CRB -> crbSaleProfits
            Asset.Type.GOLD -> goldSaleProfits
        }
        return assetsAndReasonsForBuying.filter { !it.first.isSold() && it.first.type == type }
            .sumOf { it.first.getCurrentValue(price) } + saleProfits
    }

    private fun getCurrentAssetsValue(): Double {
        var value = 0.0
        assetsAndReasonsForBuying.filter { !it.first.isSold() }.forEach {
            value += when (it.first.type) {
                Asset.Type.DEVELOPED -> it.first.getCurrentValue(historicalDataStore.lastDevelopedPrice)
                Asset.Type.EMERGING -> it.first.getCurrentValue(historicalDataStore.lastEmergingPrice)
                Asset.Type.CRB -> it.first.getCurrentValue(historicalDataStore.lastCrbPrice)
                Asset.Type.GOLD -> it.first.getCurrentValue(historicalDataStore.lastGoldPrice)
            }
        }
        return value
    }

    fun getMaxMonthlyDownwardVolatility(): Double {
        val monthlyVolatilities = mutableListOf<Double>()
        for (i in 1 until balanceState.size) {
            monthlyVolatilities += (balanceState[i].second -
                    balanceState[i - 1].second) / balanceState[i - 1].second * ONE_HUNDRED
        }
        monthlyVolatilities += (getBalance() -
                balanceState[balanceState.size - 1].second) /
                balanceState[balanceState.size - 1].second * ONE_HUNDRED
        val onlyNegativeMonthlyVolatilites = monthlyVolatilities.filter { it < 0 }
        if (onlyNegativeMonthlyVolatilites.isEmpty()) {
            return 0.0
        }
        return -onlyNegativeMonthlyVolatilites.minOf { it }
    }

    protected fun buyDeveloped(date: Date, reason: String) {
        val developedPurchase = buyAssetReturnNumberAndCost(date, historicalDataStore.lastDevelopedPrice)
        if (developedPurchase.first > 0) {
            assetsAndReasonsForBuying.add(
                Pair(
                    Asset(
                        Asset.Type.DEVELOPED,
                        date.copy(),
                        developedPurchase.first,
                        developedPurchase.second
                    ), reason
                )
            )
        }
    }

    protected fun buyEmerging(date: Date, reason: String) {
        val emergingPurchase = buyAssetReturnNumberAndCost(date, historicalDataStore.lastEmergingPrice)
        if (emergingPurchase.first > 0) {
            assetsAndReasonsForBuying.add(
                Pair(
                    Asset(
                        Asset.Type.EMERGING,
                        date.copy(),
                        emergingPurchase.first,
                        emergingPurchase.second
                    ), reason
                )
            )
        }
    }

    protected fun buyCrb(date: Date, reason: String) {
        val crbPurchase = buyAssetReturnNumberAndCost(date, historicalDataStore.lastCrbPrice)
        if (crbPurchase.first > 0) {
            assetsAndReasonsForBuying.add(
                Pair(
                    Asset(
                        Asset.Type.CRB,
                        date.copy(),
                        crbPurchase.first,
                        crbPurchase.second
                    ), reason
                )
            )
        }
    }

    protected fun buyGold(date: Date, reason: String) {
        val goldPurchase = buyAssetReturnNumberAndCost(date, historicalDataStore.lastGoldPrice)
        if (goldPurchase.first > 0) {
            assetsAndReasonsForBuying.add(
                Pair(
                    Asset(
                        Asset.Type.GOLD,
                        date.copy(),
                        goldPurchase.first,
                        goldPurchase.second
                    ), reason
                )
            )
        }
    }

    private fun buyAssetReturnNumberAndCost(date: Date, price: Double): Pair<Int, Double> {
        if (price <= 0.0) {
            throw Exception("Price ($price) at date $date is not a positive number!")
        }
        if (cash >= RESOURCES_FOR_ONE_TRANSACTION + BROKERAGE_FEE) {
            var resourcesLeft = RESOURCES_FOR_ONE_TRANSACTION
            var totalPurchased = 0
            while (resourcesLeft >= price + BROKERAGE_FEE) {
                resourcesLeft -= price
                totalPurchased++
            }
            cash -= RESOURCES_FOR_ONE_TRANSACTION - resourcesLeft
            cash -= BROKERAGE_FEE
            totalSpentOnBrokerageFees += BROKERAGE_FEE
            return Pair(totalPurchased, RESOURCES_FOR_ONE_TRANSACTION - resourcesLeft)
        }
        return Pair(0, 0.0)
    }

    private fun getBalanceProportion(): Double {
        return getBalance() / INITIAL_CASH
    }

    protected fun sellAsset(date: Date, assetType: Asset.Type) {
        val lastDevelopedPrice = historicalDataStore.lastDevelopedPrice
        val lastEmergingPrice = historicalDataStore.lastEmergingPrice
        val lastCrbPrice = historicalDataStore.lastCrbPrice
        val lastGoldPrice = historicalDataStore.lastGoldPrice
        for (asset in assetsAndReasonsForBuying.filter { it.first.type == assetType }) {
            if (!asset.first.isSold() && asset.first.purchaseDate != date) {
                when (asset.first.type) {
                    Asset.Type.DEVELOPED -> {
                        asset.first.saleDate = date.copy()
                        developedSaleProfits += asset.first.getCurrentValue(lastDevelopedPrice)
                        cash += asset.first.getCurrentValue(lastDevelopedPrice)
                        cash -= BROKERAGE_FEE
                        totalSpentOnBrokerageFees += BROKERAGE_FEE
                    }

                    Asset.Type.EMERGING -> {
                        asset.first.saleDate = date.copy()
                        emergingSaleProfits += asset.first.getCurrentValue(lastEmergingPrice)
                        cash += asset.first.getCurrentValue(lastEmergingPrice)
                        cash -= BROKERAGE_FEE
                        totalSpentOnBrokerageFees += BROKERAGE_FEE
                    }

                    Asset.Type.CRB -> {
                        asset.first.saleDate = date.copy()
                        crbSaleProfits += asset.first.getCurrentValue(lastCrbPrice)
                        cash += asset.first.getCurrentValue(lastCrbPrice)
                        cash -= BROKERAGE_FEE
                        totalSpentOnBrokerageFees += BROKERAGE_FEE
                    }

                    Asset.Type.GOLD -> {
                        asset.first.saleDate = date.copy()
                        goldSaleProfits += asset.first.getCurrentValue(lastGoldPrice)
                        cash += asset.first.getCurrentValue(lastGoldPrice)
                        cash -= BROKERAGE_FEE
                        totalSpentOnBrokerageFees += BROKERAGE_FEE
                    }
                }
            }
        }
    }

    companion object {
        const val ONE_HUNDRED = 100.0
        const val RESOURCES_FOR_ONE_TRANSACTION = 10_000.0
        const val BROKERAGE_FEE = 50.0
        const val INITIAL_CASH = 30 * RESOURCES_FOR_ONE_TRANSACTION
    }

    class Asset(val type: Type, val purchaseDate: Date, private val number: Int, val purchaseValue: Double) :
        Serializable {

        var saleDate: Date? = null
        var saleValue: Double? = null

        fun getCurrentValue(currentPrice: Double): Double {
            return number * currentPrice
        }

        fun isSold() = saleDate != null

        enum class Type {
            DEVELOPED, EMERGING, CRB, GOLD
        }

        override fun toString(): String {
            return "Type: $type, Bought/sold for: ${purchaseValue.round()}/${saleValue?.round()}, Dates: $purchaseDate, $saleDate"
        }
    }
}
