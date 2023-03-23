# Walletverse  Core 接入文档

> [项目地址](https://github.com/Walletverse/walletverse-android)
> 可下载demo查看具体接入细节
>
> [aar包](https://github.com/Walletverse/walletverse-resouces) 下载地址

### 1、 集成

---

[comment]: <> (下载[aar]&#40;http://walletverse.tech/&#41;包，并添加到工程中的libs目录中)

##### 1.1 主工程 build.gradle 中加入如下代码：

```groovy
repositories {
    ... ...
    flatDir {
        dirs 'libs'
    }
}

```

##### 1.2 在引用的module 的build.gradle 中加入如下代码：

```groovy
dependencies {
    ... ...
    implementation(name: 'walletverse_core-1.0.0', ext: 'aar')
}

```

#### 或者

```groovy
dependencies {
    ... ...
    implementation files('libs/walletverse_core-1.0.0.aar')
}

```

##### 1.3 SDK需要以下权限

```html

<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
```

### 2、 初始化

> 初始化需要下列配置参数，如APP中有切换语言，切换显示单位，切换换算单位功能，请调用正确方法通知SDK，相关方法调用请参照文档下方 --《其它相关调用》
---

```kotlin
APPID = "testappid"
APPKEY = "testappkey"
uuid //设备唯一id
mELanguage //enum 语言
mECurrency //enum 显示单位
mEUnit //enum 代币换算单位
userConfig = UserConfig(uuid, mELanguage, mECurrency, mEUnit)


Walletverse.install(this, Constants.APPID, Constants.APPKEY, userConfig, object : VoidCallback {
    override fun onResult() {
        //init success
        Log.d("WalletverseApplication", "sdk init success")
    }

    override fun onError(error: Exception) {
        Log.e("WalletverseApplication", "onError: ${error.toString()}")
    }
})

```

### 3、 调用

---

##### 3.0 说明

> 为了方便开发这使用，在使用SDK的api调用时，我们会限定请求参数数据，已帮助开发者
>
>如果您的项目中使用kotlin开发语言，并且支持协程，SDK也同样支持协程的调用方式，例如3.1。其他方法也是一样的调用方式

##### 3.1 创建账户

方式一、

```kotlin
 Walletverse.sInstance.generateAccount(
    DefaultParams("0x1"),
    object : ResultCallback<Account> {
        override fun onResult(data: Result<Account>) {
            //创建账户成功
            Log.i(TAG, "onResult: ${data.getOrNull()?.address}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)
```

方式二、

```kotlin
CoroutineScope(Dispatchers.Main).launch {
    try {
        account = Walletverse.sInstance.generateAccountAsync(DefaultParams("0x1"))
    } catch (e: Exception) {
        //错误信息
    }
}
```

##### 3.2 创建助记词

```kotlin
 Walletverse.sInstance.generateMnemonic(
    DefaultParams(
        chainId: String?, //链id
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        //创建助记词成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)


```

##### 3.3 获取私钥

```kotlin
 Walletverse.sInstance.getPrivateKey(
    GetPrivateKeyParams(
        chainId: String?, //链id
    mnemonic: String? //助记词
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        //创建私钥成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)


```

##### 3.4 获取地址

```kotlin
 Walletverse.sInstance.getAddress(
    GetAddressParams(
        chainId: String?, //链id
    privateKey: String? //私钥
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        //获取地址 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)


```

##### 3.5 验证地址合法

```kotlin
 Walletverse.sInstance.validateAddress(
    ValidateAddressParams(
        chainId: String?, //链id
    address: String? //地址
),
object : ResultCallback<Boolean> {
    override fun onResult(data: Result<Boolean>) {
        //成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)


```

##### 3.6 验证私钥合法

```kotlin
 Walletverse.sInstance.validatePrivateKey(
    ValidatePrivateKeyParams(
        chainId: String?, //链id
    privateKey: String? //私钥
),
object : ResultCallback<Boolean> {
    override fun onResult(data: Result<Boolean>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.7 验证助记词合法

```kotlin
 Walletverse.sInstance.validateMnemonic(
    ValidateMnemonicParams(
        chainId: String?, //链id
    mnemonic: String? //助记词
),
object : ResultCallback<Boolean> {
    override fun onResult(data: Result<Boolean>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.8 签名信息

```kotlin
 Walletverse.sInstance.signMessage(
    SignMessageParams(
        chainId: String?, //链id
    privateKey: String?, //私钥
message: String?,
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.9 签名交易

```kotlin
 Walletverse.sInstance.signTransaction(
    SignTransactionParams(
        chainId: String?, //链id
    privateKey: String?, //私钥
to: String?,   //收款地址
value: String?, //数量
decimals: Int?, //精度
gasPrice: String?,
gasLimit: String?,
nonce: String?,
inputData: String?,  //当转账为子币时，此参数必传
contractAddress: String?,  //当转账为子币时，此参数必传
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        sign = data.getOrNull() //签名后的数据
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.10 发送交易

```kotlin
 Walletverse.sInstance.transaction(
    TransactionParams(
        chainId: String?,
    from: String?,
to: String?,//收款地址
sign: String?, //签名
value: String?,//数量
contractAddress: String?, //当转账为子币时，此参数必传
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        //hash不为空表示交易发送成功，但具体是否转账成功，需要链上信息作为最终确认结果
        val hash = data.getOrNull()
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.11 签名并发送交易

```kotlin
 Walletverse.sInstance.signAndTransaction(
    SignAndTransactionParams(
        chainId: String?,
    privateKey: String?,
from: String?,
to: String?,   //收款地址
value: String?, //数量
decimals: Int?,
gasPrice: String?,
gasLimit: String?,
inputData: String?="",//子币转账必传
contractAddress: String? = "",//当转账为子币时，此参数必传
walletPin: String?
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        //hash不为空表示交易发送成功，但具体是否转账成功，需要链上信息作为最终确认结果
        val hash = data.getOrNull()
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.12 创建合约Transfer信息

```kotlin
 Walletverse.sInstance.generateTransferData(
    GenerateTransferParams(
        chainId: String?,
    privateKey: String?,
to: String?,
value: String?,
contractAddress: String?,
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.13 创建合约Approve信息

```kotlin
 Walletverse.sInstance.generateApproveData(
    GenerateTransferParams(
        chainId: String?,
    privateKey: String?,
to: String?,
value: String?,
contractAddress: String?,
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.14 获取Nonce

```kotlin
 Walletverse.sInstance.nonce(
    GetParams(
        chainId: String?,
    address: String?,//当前代币地址
contractAddress: String?,//可选
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        val nonce = data.getOrNull()
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.15 获取Balance

```kotlin
 Walletverse.sInstance.balance(
    GetParams(
        chainId: String?,
    address: String?,
contractAddress: String?,
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.16 获取Decimals

```kotlin
 Walletverse.sInstance.decimals(
    GetParams(
        chainId: String?,
    address: String?,
contractAddress: String?,
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.17 获取GasFee

```kotlin
 Walletverse.sInstance.fee(
    FeeParams(
        chainId: String?,
    from: String?,
to: String?,//如果是子币，请传合约地址contractAddress
value: String?,//如果是子币，请传""或者"0"
decimals: String?,
"data":"",//合约数据，入参为encodeERC20ABI方法返回数据
),
object : ResultCallback<Fee> {
    override fun onResult(data: Result<Fee>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.18 获取GasPrice

```kotlin
 Walletverse.sInstance.getGasPrice(
    DefaultParams(
        chainId: String?,
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.19 获取币价格

```kotlin
 Walletverse.sInstance.getPrice(
    GetPriceParams(
        symbol: String,
        contractAddress: String
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.20 生成钱包id

```kotlin
 Walletverse.sInstance.generateWid(
    appId :String,
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.21 支持的主链（暂时只支持EVM）

```kotlin
 Walletverse.sInstance.getSupportChains(
    GetChainsParams(VM.EVM),
    object : ResultCallback<MutableList<Coin>?> {
        override fun onResult(data: Result<MutableList<Coin>?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.22 代币列表

```kotlin
 Walletverse.sInstance.getTokenList(
    TokenParams(
        page: Int,
        size: Int,
        chainId: String
    ),
    object : ResultCallback<MutableList<Coin>?> {
        override fun onResult(data: Result<MutableList<Coin>?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.23 搜索代币

```kotlin
 Walletverse.sInstance.getToken(
    GetTokenParams(
        chainId: String,
        contractAddress: String
    ),
    object : ResultCallback<Coin?> {
        override fun onResult(data: Result<Coin?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.24 获取交易记录

```kotlin
 Walletverse.sInstance.getTransactionRecords(
    TransactionRecordParams(
        page: Int,
        size: Int,
        chainId: String,
        address: String,
        condition: Condition,  //交易记录类型
        contractAddress: String = ""
),
object : ResultCallback<MutableList<TransactionRecord>> {
    override fun onResult(data: Result<MutableList<TransactionRecord>>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.25 初始化主链

> 如果您的app有选择主链的逻辑，可忽略此方法。此方法用于App中没有明确选择主链的界面，并且需要初始化主链时调用，可初始化的主链为SDK支持的主链

```kotlin
 Walletverse.sInstance.initChain(
    InitChainParams(
        "wid",     //钱包id
        "address", //当前钱包地址
        "privateKey", //私钥
        "encodePin", //以demo为例，入参为创建钱包时的6位pin，调用encodeAuth后的数据，如果没有可传""，注意：传""，我们也会加密，在您需要解密时，请使用""作为解密参数
        EChain.ETH //主链
    ),
    object : ResultCallback<Coin?> {
        override fun onResult(data: Result<Coin?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.26 加密PIN

> demo用于加密PIN

```kotlin
 Walletverse.sInstance.encodeAuth(
    EncodeAuthParams(
        "pin",  //密码
        "UniqueDeviceId" //设备唯一id
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.27 解密PIN

```kotlin
 Walletverse.sInstance.encodeAuth(
    DecodeAuthParams(
        "encodePin",  //加密后的密码
        "UniqueDeviceId" //设备唯一id
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.28 加密数据

> 您可以用此方法加密任何你想加密的数据

```kotlin
 Walletverse.sInstance.encodeMessage(
    EncodeMessageParams(
        "message",   //需要加密的数据
        "encodePin"  //一般为加密后的密码
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.29 解密数据

```kotlin
 Walletverse.sInstance.decodeMessage(
    DecodeMessageParams(
        "message",    //需要解密的数据
        "encodePin"  //一般为加密后的密码
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.30 保存币信息到当前钱包资产

```kotlin
 Walletverse.sInstance.saveWalletCoin(
    SaveCoinParams(
        "wid",   //钱包id
        "encodePin", //加密后的pin
        coin //需要保存的coin实体
    ),
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.31 删除当前钱包资产代币

```kotlin
 Walletverse.sInstance.deleteWalletCoin(
    coin, //需要删除的coin实体,
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.32 更新钱包资产的代币信息

```kotlin
 Walletverse.sInstance.updateWalletCoin(
    coin, //需要更新的coin实体,
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.33 查询钱包资产的代币信息

```kotlin
 Walletverse.sInstance.queryWalletCoin(
    GetWalletCoinParams(
        wid: String, //钱包id
        contract: String, //可选 主链
        symbol: String, //可选 币名
        address: String, //可选 地址
    ),
    object : ResultCallback<Coin?> {
        override fun onResult(data: Result<Coin?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.34 查询钱包资产的所有币

```kotlin
 Walletverse.sInstance.queryWalletCoins(
    GetWalletCoinParams(
        wid:String
    ),
    object : ResultCallback<MutableList<Coin>?> {
        override fun onResult(data: Result<MutableList<Coin>?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.35 保存钱包身份

```kotlin
 Walletverse.sInstance.insertIdentity(
    identity, //Identity实体
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.36 删除钱包身份

```kotlin
 Walletverse.sInstance.deleteIdentity(
    identity, //Identity实体
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.37 更新钱包身份信息

```kotlin
 Walletverse.sInstance.updateIdentity(
    identity, //Identity实体
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.38 查询钱包身份信息

```kotlin
 Walletverse.sInstance.queryIdentity(
    wid :String, //钱包id
    object : ResultCallback<Identity?> {
        override fun onResult(data: Result<Identity?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.39 查询钱包所有身份信息

```kotlin
 Walletverse.sInstance.queryIdentities(
    object : ResultCallback<MutableList<Identity>?> {
        override fun onResult(data: Result<MutableList<Identity>?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.40 保存主链及代币信息

```kotlin
 Walletverse.sInstance.insertCoin(
    coin,//Coin实体
    object : ResultCallback<Boolean> {
        override fun onResult(data: Result<Boolean>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.41 查询主链信息或代币信息

```kotlin
 Walletverse.sInstance.queryCoin(
    GetCoinParams(
        contract: String, //可选 主链
        symbol: String, //可选 币名
        address: String, //可选 地址
    )
    object : ResultCallback<Coin?> {
        override fun onResult(data: Result<Coin?>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.42 查询主链及代币信息列表

```kotlin
 Walletverse.sInstance.queryCoins(
    GetCoinParams(
        chainId: String = "",
    contract: String = "",
symbol: String = "",
),//查询所有,可不传
object : ResultCallback<MutableList<Coin>?> {
    override fun onResult(data: Result<MutableList<Coin>?>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.43 获取签名数据

```kotlin
 Walletverse.sInstance.encodeERC20ABI(
    EncodeERC20ABIParams(
        chainId: String,
        contractMethod: String, //合约方法
        contractAddress: String,
        params: ArrayList< String >, //合约参数
    abi: ArrayList< MutableMap < String,Any > > //自定义
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.44 转十六进制字符串

```kotlin
 Walletverse.sInstance.toHex(
    HexParams(
        value, //需要转换的数据
        decimals //可选，具体使用场景请查看demo
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

### 联合登录相关调用

> 以Google登录为例

1.注册Google账号

2.打开[Google登录Android文档](https://developers.google.com/identity/sign-in/android/start-integrating)官方文档，按要求配置相关Google
Play相关服务

3.获取Google登录返回的唯一id

4.创建web2.0钱包

> 4.1 调用 generateWidWithWeb2()生成钱包id,用于存储本地数据库Identity使用(sdk内部处理)
>
>4.2 调用 signInWeb2()验证2.0用户数据，如果返回结果为空，则表示为新用户。如果不为空，则表示为已注册用户，需要进行2.0钱包恢复流程（见下恢复2.0钱包流程）。
>
>4.3 调用 generateMnemonic()生成助记词，以及初始化主链，钱包名称，2.0钱包密码，钱包PIN（没有可传""）等,调用createWeb2Wallet()创建2.0钱包。

5.恢复web2.0钱包

> 5.1 调用 generateWidWithWeb2()生成钱包id,用于存储本地数据库Identity使用(sdk内部处理)
>
> 5.2 调用 signInWeb2()验证2.0用户数据，如果返回结果不为空，则表示为已注册用户，需要进行2.0钱包恢复流程，此方法会返回加密分割后的助记词。
>
> 5.3 调用 restoreWeb2Wallet()，将signInWeb2()返回的wallets，shards，2.0钱包密码等作为参数，恢复钱包。

> 具体参数请查看demo

##### 3.45 联合登录生成钱包id

```kotlin
 Walletverse.sInstance.generateWidWithWeb2(
    FederatedParams(
        providerKey: String, //一般为用户的邮箱，如果没有获取到邮箱，可以使用用户昵称
        providerUid: String, //联合登录平台的用户唯一id
        providerId: Channel, //enum，SDK提供的联合登录渠道
        auth: String         //如果email登录方式，此参数必传，联合登录方式可不传 
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.46 验证2.0用户数据

```kotlin
 Walletverse.sInstance.signInWeb2(
    FederatedParams(
        providerKey: String, //一般为用户的邮箱，如果没有获取到邮箱，可以使用用户昵称
        providerUid: String, //联合登录平台的用户唯一id
        providerId: Channel, //enum，SDK提供的联合登录渠道
        auth: String         //如果email登录方式，此参数必传，联合登录方式可不传 
    ),
    object : ResultCallback<Federated> {
        override fun onResult(data: Result<Federated>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
            result = data.getOrNull()
            //如果result为空，则表示新用户，否则为已注册的用户
            result.wid //钱包id
            result.shards //加密分割后的shard数组
            result.wallets //钱包的资产，如果用户成功使用联合登录方式登录，并添加了代币，此参数返回不为空。反之为空
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.47 创建2.0用户数据

```kotlin
 Walletverse.sInstance.userCrypto(
    RestoreWeb2Params(
        shards: List< String >,  //助记词分割后的shard
    wallets: List< Coin >, //钱包资产
walletName: String,  //钱包名
walletPin: String,   //钱包PIN
password: String,    //2.0钱包登录密码（此密码不同于PIN，此密码用于登录2.0钱包和恢复2.0钱包所用）
federatedParams: FederatedParams //联合登录参数
),
object : ResultCallback<Boolean> {
    override fun onResult(data: Result<Boolean>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.48 创建2.0钱包

```kotlin
 Walletverse.sInstance.createWeb2Wallet(
    CreateWeb2Params(
        mnemonic: String,  //助记词
        wallets: List< Coin >, //钱包资产
    walletName: String,  //钱包名
    walletPin: String,   //钱包PIN
    password: String,    //2.0钱包登录密码（此密码不同于PIN，此密码用于登录2.0钱包和恢复2.0钱包所用）
    federatedParams: FederatedParams //联合登录参数
),
object : ResultCallback<Boolean> {
    override fun onResult(data: Result<Boolean>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.49 恢复2.0钱包

```kotlin
 Walletverse.sInstance.restoreWeb2Wallet(
    RestoreWeb2Params(
        shards: List< String >, //加密分割后的shard数组
    wallets: List< Coin >,  //钱包资产
walletName: String,//钱包名
walletPin: String,//钱包PIN
password: String,//2.0钱包登录密码（此密码不同于PIN，此密码用于登录2.0钱包和恢复2.0钱包所用）
federatedParams: FederatedParams //联合登录参数
),
object : ResultCallback<Boolean> {
    override fun onResult(data: Result<Boolean>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.50 获取图形验证码

```kotlin
 Walletverse.sInstance.getGraphicsCode(
    object : ResultCallback<EmailCode> {
        override fun onResult(data: Result<EmailCode>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
            result = data.getOrNull()
            data = result.data  //svg数据
            text = result.text  //用于获取邮箱验证码入参
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.51 获取邮箱验证码

```kotlin
 Walletverse.sInstance.getEmailCode(
    EmailCodeParams(
        vcode: String, //图形验证码
        text: String,  //与图形验证码一起返回的text
    ),
    object : ResultCallback<EmailCode> {
        override fun onResult(data: Result<EmailCode>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.52 验证邮箱验证码

```kotlin
 Walletverse.sInstance.requestEmailVerify(
    EmailVerifyParams(
        vcode: String = "",//邮箱验证码
    account: String,//email账户
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        auth = data.getOrNull() //auth用于创建或者恢复2.0钱包时使用
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

##### 3.53 加密分割助记词

```kotlin
 Walletverse.sInstance.encodeShard(
    EncodeShardParams(
        shards: String, //助记词
        password: String,//2.0钱包密码
        wid: String,//2.0钱包id
    ),
    object : ResultCallback<MutableList<String>> {
        override fun onResult(data: Result<MutableList<String>>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
            val shards = data.getOrNull() //助记词加密分割后的数组
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

##### 3.54 解密分割后的助记词

```kotlin
 Walletverse.sInstance.decodeShard(
    DecodeShardParams(
        shards: List< String >, //助记词加密分割后的数组
    password: String,//2.0钱包密码
    wid: String,//2.0钱包id
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        val mnemonic = data.getOrNull() //助记词
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

### 其它相关调用

##### 3.55 获取sdk版本号

```kotlin
Walletverse.sInstance.getSDKVersionCode()
```

##### 3.56 获取sdk版本名

```kotlin
Walletverse.sInstance.getSDKVersionName()
```

##### 3.57 更改显示单位

```kotlin
Walletverse.sInstance.changeCurrency(currency: Currency)
```

##### 3.58 更改显示语言

```kotlin
Walletverse.sInstance.changeLanguage(language: Language)
```

##### 3.59 更改换算单位

```kotlin
Walletverse.sInstance.changeUnit(unit: Unit)
```

##### 3.60 校验联合登录方式的密码

```kotlin
Walletverse.sInstance.validatePassword(value: String): Boolean
```

### WebView相关

> 基于SDK支持的主链，提供了DApp相关的交互能力。
> 开发者可使用自己的WebView继承WalletverseDAppWebView，也可以直接使用WalletverseDAppWebView

##### 加载DApp

```kotlin

    data class DApp(
        val wid: String,//钱包id
        val name: String,//dapp名称
        val url: String,//dapp地址
        val chain: EChain//主链，请正确注入主链。例如：dapp是Pancake，则入参为EChain.BNB
    )

    WalletverseDAppWebView.loadDApp(dapp:DApp)


```

##### DApp数据返回

```kotlin

    WalletverseDAppWebView.setResultCallback(object : ResultCallback<String> {

        override fun onError(error: Exception) {

        }

        override fun onResult(data: Result<String>) {
             val dappData = data.getOrNull() //返回数据
             //目前提供的dapp交互方法有dappsSign，dappsSignSend，dappsSignMessage。
             ....//处理数据相关请查看demo中WalletverseDAppActivity相关处理
        
             //注意：
             // 当处理数据后，请调用WalletverseDAppWebView.jsCallback( id!!, "", data )通知js数据处理结果。
             // 如果处理数据失败或取消，请调用WalletverseDAppWebView.jsCallback( id!!, "", "")通知js数据处理结果
        }
})

```





### 币转账流程

> 完整的转账流程如下：
>
> 1.获取nonce

```kotlin
 Walletverse.sInstance.nonce(
    GetParams(
        chainId: String?,
    address: String?,//当前代币地址
contractAddress: String?,//可选
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        val nonce = data.getOrNull()
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

> 2.获取inputData (主币转账无需调用)

```kotlin
 Walletverse.sInstance.encodeERC20ABI(
    EncodeERC20ABIParams(
        chainId: String,
        "transfer", //合约方法
        contractAddress: String,
        arrayListOf(
            address,//收款地址
            value //转账数量，（需转为16进制） 例：Walletverse.sInstance.toHexAsync(HexParams(value, decimals))
        ),
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
            val inputData = data.getOrNull()
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

> 3.解密私钥

```kotlin
 Walletverse.sInstance.decodeMessage(
    DecodeMessageParams(
        "privateKeyEn",    //加密后的私钥
        "encodePin"  //加密私钥所使用的密码
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

> 4.签名交易信息

```kotlin
 Walletverse.sInstance.signTransaction(
    SignTransactionParams(
        chainId: String?, //链id
    privateKey: String?, //私钥
to: String?,   //收款地址
value: String?, //数量
decimals: Int?, //精度
gasPrice: String?,
gasLimit: String?,
nonce: String?,
inputData: String?,  //当转账为子币时，此参数必传
contractAddress: String?,  //当转账为子币时，此参数必传
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        sign = data.getOrNull() //签名后的数据
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

> 5.发送交易

```kotlin
 Walletverse.sInstance.transaction(
    TransactionParams(
        chainId: String?,
    from: String?,
to: String?,//收款地址
sign: String?, //签名
value: String?,//数量
contractAddress: String?, //当转账为子币时，此参数必传
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        //hash不为空表示交易发送成功，但具体是否转账成功，需要链上信息作为最终确认结果
        val hash = data.getOrNull()
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```

> 以上为整个交易发送过程，如不关心发送过程，请调用如下方法，提供对应参数即可

> 1.获取inputData (主币转账无需调用)

```kotlin
 Walletverse.sInstance.encodeERC20ABI(
    EncodeERC20ABIParams(
        chainId: String,
        "transfer", //合约方法
        contractAddress: String,
        arrayListOf(
            address,//收款地址
            value //转账数量，（需转为16进制） 例：Walletverse.sInstance.toHexAsync(HexParams(value, decimals))
        ),
    ),
    object : ResultCallback<String> {
        override fun onResult(data: Result<String>) {
            // 成功
            Log.i(TAG, "onResult: ${data.getOrNull()}")
            val inputData = data.getOrNull()
        }

        override fun onError(error: Exception) {
            Log.e(TAG, "onError: $error")
        }
    }
)

```

> 2.签名并发送交易

```kotlin
 Walletverse.sInstance.signAndTransaction(
    SignAndTransactionParams(
        chainId: String?,
    privateKey: String?,
from: String?,
to: String?,   //收款地址
value: String?, //数量
decimals: Int?,
gasPrice: String?,
gasLimit: String?,
inputData: String?="",//子币转账必传
contractAddress: String? = "",//当转账为子币时，此参数必传
walletPin: String?
),
object : ResultCallback<String> {
    override fun onResult(data: Result<String>) {
        // 成功
        Log.i(TAG, "onResult: ${data.getOrNull()}")
        //hash不为空表示交易发送成功，但具体是否转账成功，需要链上信息作为最终确认结果
        val hash = data.getOrNull()
    }

    override fun onError(error: Exception) {
        Log.e(TAG, "onError: $error")
    }
}
)

```
