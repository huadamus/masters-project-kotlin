package simulation.portfolio

import simulation.HistoricalDataStore
import model.Date

abstract class Portfolio(private val startDate: Date) {
    protected lateinit var historicalDataStore: HistoricalDataStore
    protected val assets = mutableSetOf<Asset>()
    private var monthlyBalanceProportionValues = mutableListOf<Double>()

    protected var developedSaleProfits = 0.0
    protected var emergingSaleProfits = 0.0
    protected var crbSaleProfits = 0.0
    protected var goldSaleProfits = 0.0

    fun handle(date: Date) {
        historicalDataStore.updateLastPrices(date)
        if (date.isNewMonth()) {
            historicalDataStore.updateTrails(date)
            if (date >= startDate) {
                handleDetails(date)
                monthlyBalanceProportionValues.add(getBalanceProportion())
            }
        }
    }

    abstract fun handleDetails(date: Date)

    fun getTotalInvestedByAssetType(type: Asset.Type): Double {
        return assets.filter { it.type == type }.sumOf { it.purchaseValue }
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
        return assets.filter { !it.isSold() && it.type == type }.sumOf { it.getCurrentValue(price) } + saleProfits
    }

    fun getCurrentAssetsValue(): Double {
        var value = 0.0
        assets.filter { !it.isSold() }.forEach {
            value += when (it.type) {
                Asset.Type.DEVELOPED -> it.getCurrentValue(historicalDataStore.lastDevelopedPrice)
                Asset.Type.EMERGING -> it.getCurrentValue(historicalDataStore.lastEmergingPrice)
                Asset.Type.CRB -> it.getCurrentValue(historicalDataStore.lastCrbPrice)
                Asset.Type.GOLD -> it.getCurrentValue(historicalDataStore.lastGoldPrice)
            }
        }
        return value + developedSaleProfits + goldSaleProfits + crbSaleProfits + emergingSaleProfits
    }

    fun getTotalInvestedValue(): Double {
        return assets.sumOf { it.purchaseValue } + assets.size * BROKERAGE_FEE
    }

    fun getMaxMonthlyDownwardVolatility(): Double {
        val monthlyVolatilities = mutableListOf<Double>()
        for (i in 1 until monthlyBalanceProportionValues.size) {
            monthlyVolatilities += (monthlyBalanceProportionValues[i] -
                    monthlyBalanceProportionValues[i - 1]) * ONE_HUNDRED
        }
        monthlyVolatilities += (getBalanceProportion() -
                monthlyBalanceProportionValues[monthlyBalanceProportionValues.size - 1]) * ONE_HUNDRED
        val onlyNegativeMonthlyVolatilites = monthlyVolatilities.filter { it < 0 }
        if (onlyNegativeMonthlyVolatilites.isEmpty()) {
            return 0.0
        }
        return -onlyNegativeMonthlyVolatilites.minOf { it }
    }

    protected fun buyDeveloped(date: Date) {
        val developedPurchase = buyAssetReturnNumberAndCost(historicalDataStore.lastDevelopedPrice)
        assets.add(Asset(Asset.Type.DEVELOPED, date.copy(), developedPurchase.first, developedPurchase.second))
    }

    protected fun buyEmerging(date: Date) {
        val emergingPurchase = buyAssetReturnNumberAndCost(historicalDataStore.lastEmergingPrice)
        assets.add(Asset(Asset.Type.EMERGING, date.copy(), emergingPurchase.first, emergingPurchase.second))
    }

    protected fun buyCrb(date: Date) {
        val crbPurchase = buyAssetReturnNumberAndCost(historicalDataStore.lastCrbPrice)
        assets.add(Asset(Asset.Type.CRB, date.copy(), crbPurchase.first, crbPurchase.second))
    }

    protected fun buyGold(date: Date) {
        val goldPurchase = buyAssetReturnNumberAndCost(historicalDataStore.lastGoldPrice)
        assets.add(Asset(Asset.Type.GOLD, date.copy(), goldPurchase.first, goldPurchase.second))
    }

    private fun buyAssetReturnNumberAndCost(price: Double): Pair<Int, Double> {
        if(price <= 0.0) {
            throw Exception("Price is not a positive number!")
        }
        var resourcesLeft = RESOURCES_FOR_ONE_TRANSACTION
        var totalPurchased = 0
        while (resourcesLeft >= price) {
            resourcesLeft -= price
            totalPurchased++
        }
        return Pair(totalPurchased, RESOURCES_FOR_ONE_TRANSACTION - resourcesLeft)
    }

    private fun getBalanceProportion(): Double {
        val totalAssetsValue = getTotalInvestedValue()
        if (totalAssetsValue == 0.0) {
            return 1.0
        }
        return getCurrentAssetsValue() / totalAssetsValue
    }

    protected fun sellAsset(date: Date, assetType: Asset.Type) {
        val lastDevelopedPrice = historicalDataStore.lastDevelopedPrice
        val lastEmergingPrice = historicalDataStore.lastEmergingPrice
        val lastCrbPrice = historicalDataStore.lastCrbPrice
        val lastGoldPrice = historicalDataStore.lastGoldPrice
        for (asset in assets.filter { it.type == assetType }) {
            if (!asset.isSold() && asset.purchaseDate != date) {
                when (asset.type) {
                    Asset.Type.DEVELOPED -> {
                        asset.saleDate = date.copy()
                        developedSaleProfits += asset.getCurrentValue(lastDevelopedPrice)
                    }
                    Asset.Type.EMERGING -> {
                        asset.saleDate = date.copy()
                        emergingSaleProfits += asset.getCurrentValue(lastEmergingPrice)
                    }
                    Asset.Type.CRB -> {
                        asset.saleDate = date.copy()
                        crbSaleProfits += asset.getCurrentValue(lastCrbPrice)
                    }
                    Asset.Type.GOLD -> {
                        asset.saleDate = date.copy()
                        goldSaleProfits += asset.getCurrentValue(lastGoldPrice)
                    }
                }
            }
        }
    }

    companion object {
        const val ONE_HUNDRED = 100.0
        const val RESOURCES_FOR_ONE_TRANSACTION = 10_000.0
        const val BROKERAGE_FEE = 0.0
    }

    class Asset(val type: Type, val purchaseDate: Date, private val number: Int, val purchaseValue: Double) {

        var saleDate: Date? = null

        fun getCurrentValue(currentPrice: Double) = number * currentPrice

        fun isSold() = saleDate != null

        enum class Type {
            DEVELOPED, EMERGING, CRB, GOLD
        }
    }
}
