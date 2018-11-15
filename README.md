# CompatLayoutAdapter
按照屏幕像素比例适配

### 参考图
- 分辨率：1536x2048
- 尺寸：8.9英寸
- 比例：4:3    
`该尺寸为默认`
<img width="360px" src="https://github.com/LuckyCattZW/CompatLayoutAdapter/blob/master/image/4x3_1536x2048_8.9in.png"/>

- 分辨率：1440x1920
- 尺寸：7英寸
- 比例：4:3    
<img width="360px" src="https://github.com/LuckyCattZW/CompatLayoutAdapter/blob/master/image/4x3_1440x1920_7in.png"/>

- 分辨率：768x1024
- 尺寸：7英寸
- 比例：4:3    
<img width="360px" src="https://github.com/LuckyCattZW/CompatLayoutAdapter/blob/master/image/4x3_768x1024_7in.png"/>

- 分辨率：768x1024
- 尺寸：14英寸
- 比例：4:3    
<img width="360px" src="https://github.com/LuckyCattZW/CompatLayoutAdapter/blob/master/image/4x3_768x1024_14in.png"/>

### 使用方式

#### 代码中

`CompatLayoutAdapter\library\src\main\java\catt.compat.layout\Configture.kt`
```kotlin
/**
* 你需要将不同比例以及该比例尺寸的对照宽/高像素传入Map中
* 
* 如何计算比例
* 最大公约数 = widthPixel % heightPixel
* 计算比例 "${widthPixel / 最大公约数}:${heightPixel / 最大公约数}"
* 
*/
class Configture{
    val originMetricsMap: Map<String, OriginScreenMetrics> by lazy {
        mapOf(
            "4:3" to OriginScreenMetrics(
                intArrayOf(4, 3), 1536, 2048, 288.995F, 288.995F, "xhdpi", 320, 8.9F
            ),
            "16:9" to OriginScreenMetrics(intArrayOf(16, 9), 1920, 1080)
        )
    }
}
```

`Activity`

```kotlin
//你需要继承CompatLayoutActivity
class MainActivity : CompatLayoutActivity()
```

`Fragment`

```kotlin
//你需要继承CompatLayoutFragment()
class MyFragment : CompatLayoutFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        /*你需要使用compatCreateView方法传递需要创建的layoutID*/compatCreateView(R.layout.fragment, container)
        
}
```

`DialogFragment`

```kotlin
class MyDialogFragment : CompatLayoutDialogFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        /*你需要使用compatCreateView方法传递需要创建的layoutID*/compatCreateView(R.layout.dialog, container)
}
``` 

#### 资源 res/layout

`layout应依照以下进行配置`

PS：代码中引用的应该是你默认资源的layout布局.例如:setContentView(R.layout.activity_main)

```text
res/layout/activity_main.xml
res/layout/fragment.xml

res/layout-4x3/activity_main_4x3.xml
res/layout-4x3/fragment_4x3.xml

res/layout-8x5/activity_main_8x5.xml
res/layout-8x5/fragment_8x5.xml

res/layout-16x9/activity_main_16x9.xml
res/layout-16x9/fragment_16x9.xml
```
