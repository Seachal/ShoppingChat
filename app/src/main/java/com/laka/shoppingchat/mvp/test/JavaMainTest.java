package com.laka.shoppingchat.mvp.test;

import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.RsaUtils;

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:
 */
public class JavaMainTest {

    private static String privateKey =
            "MIIEpAIBAAKCAQEA3c3rkxWFQ90C3suebKYofZdkkg92nst7sUwxUkcLxenv1ELX\n" +
                    "Trtn0Mtf5ncONLCBbNXmz7IuHyt9dkqIGmUgvas5wJHaJCuRo2cw+uVdRPV5IWZ6\n" +
                    "K7GpHuEQxKwVatfs1lFMYTuDu3kFKi15AkyXQS2DhAVuTF86ogCsSBIpd6jQgwAs\n" +
                    "PfiqSFGxkXCfg65spu+SR9ktxtIN8urbyulqHBFLz4OKi2NEBCSNOp6PJYfHKBaC\n" +
                    "qUki0H+VwCCj9cM6Kjo195Hgn9tXXmaiYFSDHruMb8FzOO32fKP2Ib+6dMhb4kNK\n" +
                    "ZLKTX6GIAVsHmzdnOZpH2madVRzBVYuPwwAfqwIDAQABAoIBAQDC732A8UMEV7N0\n" +
                    "F8SOfKHNb0bGT6zS9scpFWurgiFIucc759rOqUoeaP9Jz4y+pfm5q2yFUXXTkyda\n" +
                    "1To06HgT+e5x3j232ErRL5oOh5KnTo482pG44RVfvDI+h3bzRf4yhZT5R5MJKt1t\n" +
                    "KjXV3XZ5pASX/SXpHTCsvKatWLj1Hdf0XJw73PzAzIHSNBG1SoRcCfDKdOwU76Zr\n" +
                    "Z8NmcFmcn3S/W2VcS1BqtsMYOnfJD5GfQgTWz7VwlBJtZelXbJWtLlKqbBY/nYNA\n" +
                    "GcoNtHDraybTMrV7N1m4yIb+WDTb+y9pXe90ezttxzyhqaDNt5oJ9ugqziBK/ERC\n" +
                    "K7F7sguhAoGBAPCJHuqzSsoPtHyirO/xuKfx+9q8fb8knp0DXoOjRgTw7kUJ7oKf\n" +
                    "8MzZW+5EvFdzMWiNhlHDgsJd2f0MNOgsSKoenD50i63iTWycwmd8vCKIcO4wqoU+\n" +
                    "ZD1DHRkJfqrGMWJVivQGR9poYL5IYFrmuhdQl261rOHnXfJMVMiY476ZAoGBAOwQ\n" +
                    "gmEEKZWGszj5XqSIWY0aOa4eu4EUTcqmU7v/VfPIT5T3+7c73V0jsZlI5t3We9Wg\n" +
                    "Xpa/DRbVyk3t4sLUiYk8v2ZZEFjAgbMMrfRB+oCbSXGV506hmQ2ghlujMiPMIyIr\n" +
                    "vehBqENJQMJt73jgAlucPWRSqeC5CxKQtlSjh87jAoGAQWZW9KN5E0V/4l9su/Sy\n" +
                    "K1+9BbU4T29KfRB8czhCgndroXPY2MVZ+KikuT0RJInahj3spnCNLgHMkmPuQq4/\n" +
                    "t5mJC+YuUEH2oTBiHzcoBA+q/OhYhM/4+zszPyp4uUAYD0+I716BzN2SaEpPgIe5\n" +
                    "UzuDYQOelPGOdqHQb5Y6XgkCgYArARrvV5XHwqE1uOAP8zL1LJjjan/YFP3S9Bf/\n" +
                    "AJYE8jHlPoPhros2I4GAHjLIqQEoOq3gom9dAJd5OOP9gECJ+sIXgBQlRvSZzK6H\n" +
                    "99kNwrxPb/KSdYfvXpX7bbB9qvufhv5yqDth/p9IjER67bwgOkdI1nJSGQO2XxyI\n" +
                    "ML5V5wKBgQDBwB8SZEbgyFN3SIcEFD7bUevVdwkncz+YtlwIVyeAzo0RFV/beFyR\n" +
                    "Q7e+flJeeO/s2R1YXJF3puOFWF5Mlk9nmCDfuGOrNjQ/pSwYV+eaSr9kzu7iLQRo\n" +
                    "CTuczJ/apuBUkjReM0ZFAx26qyX7CvNECIz3piqTtBLwJeuS9w+rrA==";

    private static String publicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3c3rkxWFQ90C3suebKYo\n" +
                    "fZdkkg92nst7sUwxUkcLxenv1ELXTrtn0Mtf5ncONLCBbNXmz7IuHyt9dkqIGmUg\n" +
                    "vas5wJHaJCuRo2cw+uVdRPV5IWZ6K7GpHuEQxKwVatfs1lFMYTuDu3kFKi15AkyX\n" +
                    "QS2DhAVuTF86ogCsSBIpd6jQgwAsPfiqSFGxkXCfg65spu+SR9ktxtIN8urbyulq\n" +
                    "HBFLz4OKi2NEBCSNOp6PJYfHKBaCqUki0H+VwCCj9cM6Kjo195Hgn9tXXmaiYFSD\n" +
                    "HruMb8FzOO32fKP2Ib+6dMhb4kNKZLKTX6GIAVsHmzdnOZpH2madVRzBVYuPwwAf\n" +
                    "qwIDAQAB";


    public static void main(String[] args) throws Exception {

    }

}
