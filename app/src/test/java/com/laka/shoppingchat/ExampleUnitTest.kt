package com.laka.shoppingchat

import android.util.Base64
import com.ali.auth.third.core.context.KernelContext.resources
import com.laka.androidlib.util.RsaUtils
import org.junit.Test

import org.junit.Assert.*
import java.nio.charset.Charset

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun test(){
        val accid = "jFhhg6jLQVQV0K2Pz2PsWtoLolRwpkB1ypI4qdJVP5oklyQ5nGJt+zobzMc1b8lC1v/mjoXQecy/lnL3H634ZrQHsGYyIlUfehx/oF/O0nnvBYwB4RtGAXNicaJl8wQa3Zl+02LlJtTMeaZRpdgHuHwF0DB6jcS1C+Uv1V23bWhlBxhFGvkib/p6Eetq+G/k5QyXsG71K8FTDKTBACS+jgtVYUpaUhHCFG01xew1SBNHNvK8tT8KAvAB10gMFSad9FCyC3b5HOtBRftRfu7UHKdnvYodtMaaBahLSrgIwkGt/0/V8ZYSVVTLUrr5dbJx0vPkdWesZ3YNX97S2Y2dFA==\n"
        val inPrivate = resources.assets.open("rsa_public_key.pem")
        val publicKey = RsaUtils.loadPublicKey(inPrivate)
        // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
        val decryptByte = RsaUtils.decryptData(Base64.decode(accid, Base64.DEFAULT), publicKey)
        print(String(decryptByte))



    }

}
