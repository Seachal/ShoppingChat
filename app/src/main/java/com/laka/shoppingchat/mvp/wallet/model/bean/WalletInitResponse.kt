package com.laka.shoppingchat.mvp.wallet.model.bean

import com.google.gson.annotations.SerializedName

data class WalletInitResponse(
    @SerializedName("sdkInit")
    var sdkInit: HashMap<String, Any> = HashMap()
)

data class WalletBean(
    @SerializedName("oid_partner")
    var oidPartner: String = "",
    @SerializedName("sign_type")
    var signType: String = "",
    @SerializedName("timestamp")
    var timestamp: String = "",
    @SerializedName("type_user")
    var typeUser: String = "",
    @SerializedName("user_id")
    var userId: String = "",
    @SerializedName("sign")
    var sign: String = "",
    @SerializedName("flag_para")
    var flagPara: String = ""
)