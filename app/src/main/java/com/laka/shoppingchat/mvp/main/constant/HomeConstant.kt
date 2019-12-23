package com.laka.shoppingchat.mvp.main.constant

import android.support.annotation.StringDef

/**
 * @Author:Rayman
 * @Date:2019/1/7
 * @Description:主页常量类
 */
object HomeConstant {

    const val BIND_UNION_REQUEST_CODE:Int = 0x2011
    const val KEY_REQUEST_CODE:String = "requestCode"
    const val KEY_RESULT_CODE:String = "resultCode"

    //购聊自定义图片缓存目录
    const val ERGOU_IMAGE_CACHE_PATH = "ergou_image"
    //首页活动弹窗SP存储key
    const val KEY_HOME_POPUP_BEAN = "home_popup_bean"

    /**
     * requestCode
     * */
    const val REQUEST_CODE_INTO_ACTIVITS_POPUP_DETAIL = 0x001 //点击活动弹窗

    /**
     * description:部分页面常量限定
     **/
    const val WEB_KERNEL = "WEB_KERNEL"
    const val WEB_URL = "WEB_URL"
    const val TITLE = "title"
    const val WEB_TITLE = "WEB_TITLE"

    /**
     * scene_extra：扩展字段的一些key
     * */
    const val TOPIC_BIG_IMAGE_URL = "big_img_url"

    //上一次搜索弹窗或者购小二搜索的关键词
    const val PRE_SEARCH_KEY: String = "PRE_SEARCH_KEY"

    //是否是淘口令的匹配规则，新版的淘口令不仅仅是使用人民币符号 ￥ ,还会使用 $ 或者其他货币符号，例如
    // € £ Ұ ₴ $ ₰ ¢ ₤ ¥ ₳ ₲ ₪ ₵ ₣ ₱ ฿ ¤ ₡ ₮ ₭ ₩ރ 円 ₢ ₥ ₫ ₦ ł ﷼ ₠ ₧ ₯ ₨ č र ₹ ƒ ₸ ￠
    private const val TAO_COMMAND_PATTERN1: String = "￥([a-zA-Z0-9]{11})￥"
    private const val TAO_COMMAND_PATTERN2: String = "₴([a-zA-Z0-9]{11})₴"
    private const val TAO_COMMAND_PATTERN3: String = "$([a-zA-Z0-9]{11})$"
    private const val TAO_COMMAND_PATTERN4: String = "₤([a-zA-Z0-9]{11})₤"
    private const val TAO_COMMAND_PATTERN5: String = "€([a-zA-Z0-9]{11})€"
    private const val TAO_COMMAND_PATTERN6: String = "¥([a-zA-Z0-9]{11})¥"
    private const val TAO_COMMAND_PATTERN7: String = "£([a-zA-Z0-9]{11})£"
    private const val TAO_COMMAND_PATTERN8: String = "₰([a-zA-Z0-9]{11})₰"
    private const val TAO_COMMAND_PATTERN9: String = "¢([a-zA-Z0-9]{11})¢"
    private const val TAO_COMMAND_PATTERN10: String = "₳([a-zA-Z0-9]{11})₳"
    private const val TAO_COMMAND_PATTERN11: String = "₲([a-zA-Z0-9]{11})₲"
    private const val TAO_COMMAND_PATTERN12: String = "₪([a-zA-Z0-9]{11})₪"
    private const val TAO_COMMAND_PATTERN13: String = "₵([a-zA-Z0-9]{11})₵"
    private const val TAO_COMMAND_PATTERN14: String = "₣([a-zA-Z0-9]{11})₣"
    private const val TAO_COMMAND_PATTERN15: String = "₱([a-zA-Z0-9]{11})₱"
    private const val TAO_COMMAND_PATTERN16: String = "฿([a-zA-Z0-9]{11})฿"
    private const val TAO_COMMAND_PATTERN17: String = "¤([a-zA-Z0-9]{11})¤"
    private const val TAO_COMMAND_PATTERN18: String = "₡([a-zA-Z0-9]{11})₡"
    private const val TAO_COMMAND_PATTERN19: String = "₮([a-zA-Z0-9]{11})₮"
    private const val TAO_COMMAND_PATTERN20: String = "₭([a-zA-Z0-9]{11})₭"
    private const val TAO_COMMAND_PATTERN21: String = "円([a-zA-Z0-9]{11})円"
    private const val TAO_COMMAND_PATTERN22: String = "₢([a-zA-Z0-9]{11})₢"
    private const val TAO_COMMAND_PATTERN23: String = "₥([a-zA-Z0-9]{11})₥"
    private const val TAO_COMMAND_PATTERN24: String = "₫([a-zA-Z0-9]{11})₫"
    private const val TAO_COMMAND_PATTERN25: String = "₦([a-zA-Z0-9]{11})₦"
    private const val TAO_COMMAND_PATTERN26: String = "ł([a-zA-Z0-9]{11})ł"
    private const val TAO_COMMAND_PATTERN27: String = "₠([a-zA-Z0-9]{11})₠"
    private const val TAO_COMMAND_PATTERN28: String = "₧([a-zA-Z0-9]{11})₧"
    private const val TAO_COMMAND_PATTERN29: String = "₯([a-zA-Z0-9]{11})₯"
    private const val TAO_COMMAND_PATTERN30: String = "₨([a-zA-Z0-9]{11})₨"
    private const val TAO_COMMAND_PATTERN31: String = "č([a-zA-Z0-9]{11})č"
    private const val TAO_COMMAND_PATTERN32: String = "र([a-zA-Z0-9]{11})र"
    private const val TAO_COMMAND_PATTERN33: String = "₹([a-zA-Z0-9]{11})₹"
    private const val TAO_COMMAND_PATTERN34: String = "ƒ([a-zA-Z0-9]{11})ƒ"
    private const val TAO_COMMAND_PATTERN35: String = "₸([a-zA-Z0-9]{11})₸"
    private const val TAO_COMMAND_PATTERN36: String = "￠([a-zA-Z0-9]{11})￠"
    private const val TAO_COMMAND_PATTERN37: String = "Ұ([a-zA-Z0-9]{11})Ұ"
    private const val TAO_COMMAND_PATTERN38: String = "₩([a-zA-Z0-9]{11})₩"
    private const val TAO_COMMAND_PATTERN39: String = "﷼([a-zA-Z0-9]{11})﷼"
    //匹配所有淘口令的正则表达式
    const val TAO_COMMAND_PATTERN_ALL: String = "$TAO_COMMAND_PATTERN1" +
            "|$TAO_COMMAND_PATTERN2" +
            "|$TAO_COMMAND_PATTERN3" +
            "|$TAO_COMMAND_PATTERN4" +
            "|$TAO_COMMAND_PATTERN5" +
            "|$TAO_COMMAND_PATTERN6" +
            "|$TAO_COMMAND_PATTERN7" +
            "|$TAO_COMMAND_PATTERN8" +
            "|$TAO_COMMAND_PATTERN9" +
            "|$TAO_COMMAND_PATTERN10" +
            "|$TAO_COMMAND_PATTERN11" +
            "|$TAO_COMMAND_PATTERN12" +
            "|$TAO_COMMAND_PATTERN13" +
            "|$TAO_COMMAND_PATTERN14" +
            "|$TAO_COMMAND_PATTERN15" +
            "|$TAO_COMMAND_PATTERN16" +
            "|$TAO_COMMAND_PATTERN17" +
            "|$TAO_COMMAND_PATTERN18" +
            "|$TAO_COMMAND_PATTERN19" +
            "|$TAO_COMMAND_PATTERN20" +
            "|$TAO_COMMAND_PATTERN21" +
            "|$TAO_COMMAND_PATTERN22" +
            "|$TAO_COMMAND_PATTERN23" +
            "|$TAO_COMMAND_PATTERN24" +
            "|$TAO_COMMAND_PATTERN25" +
            "|$TAO_COMMAND_PATTERN26" +
            "|$TAO_COMMAND_PATTERN27" +
            "|$TAO_COMMAND_PATTERN28" +
            "|$TAO_COMMAND_PATTERN29" +
            "|$TAO_COMMAND_PATTERN30" +
            "|$TAO_COMMAND_PATTERN31" +
            "|$TAO_COMMAND_PATTERN32" +
            "|$TAO_COMMAND_PATTERN33" +
            "|$TAO_COMMAND_PATTERN34" +
            "|$TAO_COMMAND_PATTERN35" +
            "|$TAO_COMMAND_PATTERN36" +
            "|$TAO_COMMAND_PATTERN37" +
            "|$TAO_COMMAND_PATTERN38" +
            "|$TAO_COMMAND_PATTERN39"

}