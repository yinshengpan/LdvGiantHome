# [腾讯mars xlog](https://github.com/Tencent/mars#mars_cn)
#### 1. [xlog简介](https://mp.weixin.qq.com/s/cnhuEodJGIbdodh0IxNeXQ?)
#### 2. [本地编译指南](https://github.com/Tencent/mars/wiki/Mars-Android-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97#local_compile)（注：编译环境需要严格按照文档的来，不然会编译失败）
#### 3. [Xlog 加密使用指引](https://github.com/Tencent/mars/wiki/Xlog-%E5%8A%A0%E5%AF%86%E4%BD%BF%E7%94%A8%E6%8C%87%E5%BC%95)
#### 4. [Xlog 加密踩坑 Couldn't load OpenSSL lib](https://blog.csdn.net/HanSnowqiang/article/details/121407503)
#### 5. [Xlog 加密踩坑 ImportError: No module named zstandard](https://blog.csdn.net/qq_35334561/article/details/120742288)
```
// 加密Key生成(python2.7.18、openssl1.1、pyelliptic-1.5.10)
cd ../module_log/tools
python gen_key.py
======================================================
save private key
8192c98aa092722ef91bcea6d5bf8f5c4f1cb490cad2a7fec1ddf90a49f2fdbd

appender_open's parameter:
293c0ce4e8aecfb1944043a872ef80136380a04aef336df52a853b8a0cd620c3ce1d4daaf8fde1014403dd66c9733e454f6662cf61176034fa2d4989811efd7
======================================================

```

#### 4. 日志解析
```
// 执行环境(python 2.7.18、pyelliptic-1.5.10、openssl1.1)
cd ../module_log/tools/decode
// 加密日志解析
python decode_mars_crypt_log_file.py xlog路径
// 未加密日志解析
python decode_mars_nocrypt_log_file.py xlog路径 
```

#### 5. 日志查看
* windows:推荐TextAnalysisTool(../module_log/tools/TextAnalysisTool/TextAnalysisTool.NET.exe)
* mac:推荐sublime text

