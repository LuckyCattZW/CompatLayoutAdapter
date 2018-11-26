[![](https://jitpack.io/v/LuckyCattZW/CompatLayoutAdapter.svg)](https://jitpack.io/#LuckyCattZW/CompatLayoutAdapter)

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

kotlin version 必须大于或等于 1.3.10

`Step 1. Add the JitPack repository to your build file `

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

`Step 2. Add the dependency`
```groovy
dependencies {
	implementation 'com.github.LuckyCattZW:CompatLayoutAdapter:x.y.z'
}
```

`注意点`
- res/layout/中的布局需要使用px
- WARP_CONTENT、MATCH_PARENT、FILL_PARENT以及明确指定为0px的值均不参与适配计算

#### 代码中

`Application中进行初始化`

```java
public class MyApplication extends Application {
    private String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        TargetScreenMetrics.get().initContent(getApplicationContext(), /*You Specs Property*/"1920x1080,1536x2048,1920x1200");
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

#### XML布局

`layout/activity.xml中添加标签`

```html
<!--你需要在每个Activity的layout中添加 app:activity_root_layout="true" 属性-->
<!--该参数主要用于防止参与计算ActionBar等系统头部布局-->
<LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        ...
        app:activity_root_layout="true">
        
        <!--other views-->

</Linearlayout>

```

#### 布局结构

`layout应依照以下进行配置`

PS：代码中引用的应该是你默认资源的layout布局.例如:setContentView(R.layout.activity_main)

```text
res/layout/activity_main.xml
res/layout/fragment.xml

# layout-结尾的4x3代表适配屏幕像素比例为4x3的设备
# 4x3 的像素比 例如: 1536x2048、1440x1920、768x1024 ....
res/layout-4x3/activity_main_4x3.xml
res/layout-4x3/fragment_4x3.xml

res/layout-8x5/activity_main_8x5.xml
res/layout-8x5/fragment_8x5.xml

res/layout-16x9/activity_main_16x9.xml
res/layout-16x9/fragment_16x9.xml
```