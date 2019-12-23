package com.netease.nim.uikit.business.session.utils

import com.laka.androidlib.util.StringUtils
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:
 */
object RoundUtils {

    /**
     * 四舍五入，保留两位小数点，小数点后面的数为0也不舍去
     * */
    @JvmStatic
    fun roundForHalf(str: String): String {
        if (StringUtils.isEmpty(str)) return "0.00"
        return roundForHalf(BigDecimal(str), RoundingMode.HALF_UP)
    }

    @JvmStatic
    private fun roundForHalf(arg: BigDecimal, mode: RoundingMode): String {
        return arg?.setScale(2, mode).toString()
    }

}