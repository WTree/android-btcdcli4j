# android-btcdcli4j
[![](https://jitpack.io/v/WTree/android-btcdcli4j.svg)](https://jitpack.io/#WTree/android-btcdcli4j)

主要代码是来源于

# https://github.com/priiduneemre/btcd-cli4j
* 只是为了方便在Android使用改动来部分代码(使用okhttp 来请求)
* rpc版本现在支持的是2.0




 ##gradle:
	    
      allprojects {
		  repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  	dependencies {
	        implementation 'com.github.WTree:android-btcdcli4j:Tag'
	}
  
  
