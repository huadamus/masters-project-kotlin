@file:Suppress("unused")

package data

import model.Date

//start year is 1988
//end year is 2018

val DATASET_PERIOD_24_RANDOM_1 = Pair(
    24, Pair(
        listOf(
            Pair(Date(1, 1, 1998), Date(1, 1, 2000)),
            Pair(Date(1, 1, 2016), Date(1, 1, 2018)),
            Pair(Date(1, 1, 1994), Date(1, 1, 1996)),
            Pair(Date(1, 1, 2004), Date(1, 1, 2006)),
            Pair(Date(1, 1, 1996), Date(1, 1, 1998)),
            Pair(Date(1, 1, 2008), Date(1, 1, 2010)),
            Pair(Date(1, 1, 1988), Date(1, 1, 1990)),
            Pair(Date(1, 1, 2010), Date(1, 1, 2012)),
            Pair(Date(1, 1, 1990), Date(1, 1, 1992)),
            Pair(Date(1, 1, 1992), Date(1, 1, 1994)),
        ), listOf(
            Pair(Date(1, 1, 2000), Date(1, 1, 2002)),
            Pair(Date(1, 1, 2002), Date(1, 1, 2004)),
            Pair(Date(1, 1, 2012), Date(1, 1, 2014)),
            Pair(Date(1, 1, 2006), Date(1, 1, 2008)),
            Pair(Date(1, 1, 2014), Date(1, 1, 2016)),
        )
    )
)

val DATASET_PERIOD_24_RANDOM_2 = Pair(
    24, Pair(
        listOf(
            Pair(Date(1, 1, 2016), Date(1, 1, 2018)),
            Pair(Date(1, 1, 1990), Date(1, 1, 1992)),
            Pair(Date(1, 1, 2006), Date(1, 1, 2008)),
            Pair(Date(1, 1, 2012), Date(1, 1, 2014)),
            Pair(Date(1, 1, 2004), Date(1, 1, 2006)),
            Pair(Date(1, 1, 1998), Date(1, 1, 2000)),
            Pair(Date(1, 1, 2014), Date(1, 1, 2016)),
            Pair(Date(1, 1, 2000), Date(1, 1, 2002)),
            Pair(Date(1, 1, 2002), Date(1, 1, 2004)),
            Pair(Date(1, 1, 1994), Date(1, 1, 1996)),
        ), listOf(
            Pair(Date(1, 1, 1988), Date(1, 1, 1990)),
            Pair(Date(1, 1, 2010), Date(1, 1, 2012)),
            Pair(Date(1, 1, 1996), Date(1, 1, 1998)),
            Pair(Date(1, 1, 1992), Date(1, 1, 1994)),
            Pair(Date(1, 1, 2008), Date(1, 1, 2010)),
        )
    )
)

val DATASET_PERIOD_24_MANUAL = Pair(
    24, Pair(
        listOf(
            Pair(Date(1, 1, 1990), Date(1, 1, 1992)),
            Pair(Date(1, 1, 1992), Date(1, 1, 1994)),
            Pair(Date(1, 1, 1994), Date(1, 1, 1996)),
            Pair(Date(1, 1, 1996), Date(1, 1, 1998)),
            Pair(Date(1, 1, 1998), Date(1, 1, 2000)),
            Pair(Date(1, 1, 2000), Date(1, 1, 2002)),
            Pair(Date(1, 1, 2002), Date(1, 1, 2004)),
            Pair(Date(1, 1, 2006), Date(1, 1, 2008)),
            Pair(Date(1, 1, 2008), Date(1, 1, 2010)),
            Pair(Date(1, 1, 2014), Date(1, 1, 2016)),
        ), listOf(
            Pair(Date(1, 1, 1988), Date(1, 1, 1990)),
            Pair(Date(1, 1, 2004), Date(1, 1, 2006)),
            Pair(Date(1, 1, 2010), Date(1, 1, 2012)),
            Pair(Date(1, 1, 2012), Date(1, 1, 2014)),
            Pair(Date(1, 1, 2016), Date(1, 1, 2018)),
        )
    )
)

val DATASET_PERIOD_48_RANDOM_1 = Pair(
    48, Pair(
        listOf(
            Pair(Date(day = 1, month = 1, year = 2004), Date(day = 1, month = 1, year = 2008)),
            Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2004)),
            Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1992)),
            Pair(Date(day = 1, month = 1, year = 1996), Date(day = 1, month = 1, year = 2000)),
            Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2016)),
            Pair(Date(day = 1, month = 1, year = 2016), Date(day = 1, month = 1, year = 2020)),
        ), listOf(
            Pair(Date(day = 1, month = 1, year = 1992), Date(day = 1, month = 1, year = 1996)),
            Pair(Date(day = 1, month = 1, year = 2008), Date(day = 1, month = 1, year = 2012)),
        )
    )
)

val DATASET_PERIODS_12_24_36_48_72_RANDOM_1 = Pair(
    listOf(12, 18, 24, 36, 48, 72), listOf(
        Pair(
            listOf(
                Pair(Date(day = 1, month = 1, year = 2003), Date(day = 1, month = 1, year = 2004)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2013)),
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2001)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1989)),
                Pair(Date(day = 1, month = 1, year = 2017), Date(day = 1, month = 1, year = 2018)),
                Pair(Date(day = 1, month = 1, year = 1991), Date(day = 1, month = 1, year = 1992)),
                Pair(Date(day = 1, month = 1, year = 2004), Date(day = 1, month = 1, year = 2005)),
                Pair(Date(day = 1, month = 1, year = 2001), Date(day = 1, month = 1, year = 2002)),
                Pair(Date(day = 1, month = 1, year = 2016), Date(day = 1, month = 1, year = 2017)),
                Pair(Date(day = 1, month = 1, year = 2002), Date(day = 1, month = 1, year = 2003)),
                Pair(Date(day = 1, month = 1, year = 2011), Date(day = 1, month = 1, year = 2012)),
                Pair(Date(day = 1, month = 1, year = 1998), Date(day = 1, month = 1, year = 1999)),
                Pair(Date(day = 1, month = 1, year = 2014), Date(day = 1, month = 1, year = 2015)),
                Pair(Date(day = 1, month = 1, year = 1989), Date(day = 1, month = 1, year = 1990)),
                Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 1, year = 1995)),
                Pair(Date(day = 1, month = 1, year = 1997), Date(day = 1, month = 1, year = 1998)),
                Pair(Date(day = 1, month = 1, year = 2009), Date(day = 1, month = 1, year = 2010)),
                Pair(Date(day = 1, month = 1, year = 2010), Date(day = 1, month = 1, year = 2011)),
                Pair(Date(day = 1, month = 1, year = 1990), Date(day = 1, month = 1, year = 1991)),
                Pair(Date(day = 1, month = 1, year = 1995), Date(day = 1, month = 1, year = 1996))
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 1993), Date(day = 1, month = 1, year = 1994)),
                Pair(Date(day = 1, month = 1, year = 2007), Date(day = 1, month = 1, year = 2008)),
                Pair(Date(day = 1, month = 1, year = 2008), Date(day = 1, month = 1, year = 2009)),
                Pair(Date(day = 1, month = 1, year = 1992), Date(day = 1, month = 1, year = 1993)),
                Pair(Date(day = 1, month = 1, year = 1999), Date(day = 1, month = 1, year = 2000)),
                Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 1, year = 2007)),
                Pair(Date(day = 1, month = 1, year = 2013), Date(day = 1, month = 1, year = 2014)),
                Pair(Date(day = 1, month = 1, year = 2015), Date(day = 1, month = 1, year = 2016)),
                Pair(Date(day = 1, month = 1, year = 2005), Date(day = 1, month = 1, year = 2006)),
                Pair(Date(day = 1, month = 1, year = 1996), Date(day = 1, month = 1, year = 1997)),
            )
        ),
        Pair(
            listOf(
                Pair(Date(day = 1, month = 7, year = 2013), Date(day = 1, month = 1, year = 2015)),
                Pair(Date(day = 1, month = 7, year = 2016), Date(day = 1, month = 1, year = 2018)),
                Pair(Date(day = 1, month = 1, year = 2009), Date(day = 1, month = 7, year = 2010)),
                Pair(Date(day = 1, month = 7, year = 2007), Date(day = 1, month = 1, year = 2009)),
                Pair(Date(day = 1, month = 7, year = 1998), Date(day = 1, month = 1, year = 2000)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 7, year = 2013)),
                Pair(Date(day = 1, month = 7, year = 1995), Date(day = 1, month = 1, year = 1997)),
                Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 7, year = 2007)),
                Pair(Date(day = 1, month = 7, year = 2004), Date(day = 1, month = 1, year = 2006)),
                Pair(Date(day = 1, month = 7, year = 1989), Date(day = 1, month = 1, year = 1991)),
                Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 7, year = 1995)),
                Pair(Date(day = 1, month = 1, year = 1991), Date(day = 1, month = 7, year = 1992)),
                Pair(Date(day = 1, month = 1, year = 2003), Date(day = 1, month = 7, year = 2004)),
                Pair(Date(day = 1, month = 7, year = 1992), Date(day = 1, month = 1, year = 1994)),
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 1997), Date(day = 1, month = 7, year = 1998)),
                Pair(Date(day = 1, month = 7, year = 2001), Date(day = 1, month = 1, year = 2003)),
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 7, year = 2001)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 7, year = 1989)),
                Pair(Date(day = 1, month = 7, year = 2010), Date(day = 1, month = 1, year = 2012)),
                Pair(Date(day = 1, month = 1, year = 2015), Date(day = 1, month = 7, year = 2016)),
            )
        ),
        Pair(
            listOf(
                Pair(Date(day = 1, month = 1, year = 2004), Date(day = 1, month = 1, year = 2006)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1990)),
                Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 1, year = 2008)),
                Pair(Date(day = 1, month = 1, year = 2008), Date(day = 1, month = 1, year = 2010)),
                Pair(Date(day = 1, month = 1, year = 2016), Date(day = 1, month = 1, year = 2018)),
                Pair(Date(day = 1, month = 1, year = 1998), Date(day = 1, month = 1, year = 2000)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2014)),
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2002)),
                Pair(Date(day = 1, month = 1, year = 1990), Date(day = 1, month = 1, year = 1992)),
                Pair(Date(day = 1, month = 1, year = 2014), Date(day = 1, month = 1, year = 2016)),
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 1, year = 1996)),
                Pair(Date(day = 1, month = 1, year = 1992), Date(day = 1, month = 1, year = 1994)),
                Pair(Date(day = 1, month = 1, year = 2010), Date(day = 1, month = 1, year = 2012)),
                Pair(Date(day = 1, month = 1, year = 2002), Date(day = 1, month = 1, year = 2004)),
                Pair(Date(day = 1, month = 1, year = 1996), Date(day = 1, month = 1, year = 1998)),
            )
        ),
        Pair(
            listOf(
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2004)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2016)),
                Pair(Date(day = 1, month = 1, year = 2004), Date(day = 1, month = 1, year = 2008)),
                Pair(Date(day = 1, month = 1, year = 1996), Date(day = 1, month = 1, year = 2000)),
                Pair(Date(day = 1, month = 1, year = 1992), Date(day = 1, month = 1, year = 1996)),
                Pair(Date(day = 1, month = 1, year = 2016), Date(day = 1, month = 1, year = 2020)),
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 2008), Date(day = 1, month = 1, year = 2012)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1992)),
            )
        ),
        Pair(
            listOf(
                Pair(Date(day = 1, month = 1, year = 1997), Date(day = 1, month = 1, year = 2000)),
                Pair(Date(day = 1, month = 1, year = 2015), Date(day = 1, month = 1, year = 2018)),
                Pair(Date(day = 1, month = 1, year = 1991), Date(day = 1, month = 1, year = 1994)),
                Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 1, year = 2009)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1991)),
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2003)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2015)),
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 1, year = 1997)),
                Pair(Date(day = 1, month = 1, year = 2003), Date(day = 1, month = 1, year = 2006)),
                Pair(Date(day = 1, month = 1, year = 2009), Date(day = 1, month = 1, year = 2012)),
            )
        ),
        Pair(
            listOf(
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2004)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2016)),
                Pair(Date(day = 1, month = 1, year = 2004), Date(day = 1, month = 1, year = 2008)),
                Pair(Date(day = 1, month = 1, year = 1996), Date(day = 1, month = 1, year = 2000)),
                Pair(Date(day = 1, month = 1, year = 1992), Date(day = 1, month = 1, year = 1996)),
                Pair(Date(day = 1, month = 1, year = 2016), Date(day = 1, month = 1, year = 2020)),
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 2008), Date(day = 1, month = 1, year = 2012)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1992)),
            )
        ),
        Pair(
            listOf(
                Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 1, year = 2012)),
                Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2006)),
                Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2018)),
                Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1994)),
            ),
            listOf(
                Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 1, year = 2000)),
            )
        ),
    )
)

val CROSS_VALIDATION_DATASET_24 = Triple(
    24, 3, listOf(
        Pair(Date(1, 1, 1998), Date(1, 1, 2000)),
        Pair(Date(1, 1, 2016), Date(1, 1, 2018)),
        Pair(Date(1, 1, 1994), Date(1, 1, 1996)),

        Pair(Date(1, 1, 2004), Date(1, 1, 2006)),
        Pair(Date(1, 1, 1996), Date(1, 1, 1998)),
        Pair(Date(1, 1, 2008), Date(1, 1, 2010)),

        Pair(Date(1, 1, 1988), Date(1, 1, 1990)),
        Pair(Date(1, 1, 2010), Date(1, 1, 2012)),
        Pair(Date(1, 1, 1990), Date(1, 1, 1992)),

        Pair(Date(1, 1, 1992), Date(1, 1, 1994)),
        Pair(Date(1, 1, 2000), Date(1, 1, 2002)),
        Pair(Date(1, 1, 2002), Date(1, 1, 2004)),

        Pair(Date(1, 1, 2012), Date(1, 1, 2014)),
        Pair(Date(1, 1, 2006), Date(1, 1, 2008)),
        Pair(Date(1, 1, 2014), Date(1, 1, 2016)),
    )
)

val CROSS_VALIDATION_DATASET_18_72 = Triple(
    18, 4, listOf(
        Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 7, year = 1989)),
        Pair(Date(day = 1, month = 7, year = 1989), Date(day = 1, month = 1, year = 1991)),
        Pair(Date(day = 1, month = 1, year = 1991), Date(day = 1, month = 7, year = 1992)),
        Pair(Date(day = 1, month = 7, year = 1992), Date(day = 1, month = 1, year = 1994)),
        Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 7, year = 1995)),
        Pair(Date(day = 1, month = 7, year = 1995), Date(day = 1, month = 1, year = 1997)),
        Pair(Date(day = 1, month = 1, year = 1997), Date(day = 1, month = 7, year = 1998)),
        Pair(Date(day = 1, month = 7, year = 1998), Date(day = 1, month = 1, year = 2000)),
        Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 7, year = 2001)),
        Pair(Date(day = 1, month = 7, year = 2001), Date(day = 1, month = 1, year = 2003)),
        Pair(Date(day = 1, month = 1, year = 2003), Date(day = 1, month = 7, year = 2004)),
        Pair(Date(day = 1, month = 7, year = 2004), Date(day = 1, month = 1, year = 2006)),
        Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 7, year = 2007)),
        Pair(Date(day = 1, month = 7, year = 2007), Date(day = 1, month = 1, year = 2009)),
        Pair(Date(day = 1, month = 1, year = 2009), Date(day = 1, month = 7, year = 2010)),
        Pair(Date(day = 1, month = 7, year = 2010), Date(day = 1, month = 1, year = 2012)),
        Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 7, year = 2013)),
        Pair(Date(day = 1, month = 7, year = 2013), Date(day = 1, month = 1, year = 2015)),
        Pair(Date(day = 1, month = 1, year = 2015), Date(day = 1, month = 7, year = 2016)),
        Pair(Date(day = 1, month = 7, year = 2016), Date(day = 1, month = 1, year = 2018)),
    )
)

val CROSS_VALIDATION_DATASET_36_72 = Triple(
    36, 2,
    listOf(
        Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1991)),
        Pair(Date(day = 1, month = 1, year = 1991), Date(day = 1, month = 1, year = 1994)),
        Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 1, year = 1997)),
        Pair(Date(day = 1, month = 1, year = 1997), Date(day = 1, month = 1, year = 2000)),
        Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2003)),
        Pair(Date(day = 1, month = 1, year = 2003), Date(day = 1, month = 1, year = 2006)),
        Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 1, year = 2009)),
        Pair(Date(day = 1, month = 1, year = 2009), Date(day = 1, month = 1, year = 2012)),
        Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2015)),
        Pair(Date(day = 1, month = 1, year = 2015), Date(day = 1, month = 1, year = 2018)),
    )
)

val CROSS_VALIDATION_DATASET_72_72 = Triple(
    72, 1,
    listOf(
        Pair(Date(day = 1, month = 1, year = 1988), Date(day = 1, month = 1, year = 1994)),
        Pair(Date(day = 1, month = 1, year = 1994), Date(day = 1, month = 1, year = 2000)),
        Pair(Date(day = 1, month = 1, year = 2000), Date(day = 1, month = 1, year = 2006)),
        Pair(Date(day = 1, month = 1, year = 2006), Date(day = 1, month = 1, year = 2012)),
        Pair(Date(day = 1, month = 1, year = 2012), Date(day = 1, month = 1, year = 2018)),
    )
)

val CROSS_VALIDATION_DATASET_DAILY = Triple(
    18, 1,
    listOf(
        Pair(Date(day = 1, month = 8, year = 2012), Date(day = 1, month = 2, year = 2014)),
        Pair(Date(day = 1, month = 2, year = 2014), Date(day = 1, month = 8, year = 2015)),
        Pair(Date(day = 1, month = 8, year = 2015), Date(day = 1, month = 2, year = 2017)),
        Pair(Date(day = 1, month = 2, year = 2017), Date(day = 1, month = 8, year = 2018)),
    )
)
