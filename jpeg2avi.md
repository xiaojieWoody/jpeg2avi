# 概述

* 前提：`jpeg`文件夹中`jpeg`文件已按照名称排序

<img src="/Users/dingyuanjie/Documents/study/github/woodyprogram/img/image-20200305120918606.png" alt="image-20200305120918606" style="zoom:50%;" />

1. `ffmpeg`文件存在`jar`中，`jar`运行时会将其拷贝出来，最后会将其删除
2. `ffmpeg`命令要求`jpeg`文件名满足一定格式，所以拷贝`jpeg`文件到临时目录并重命名，最后会将该临时目录删除
3. 最后输出的`avi`视频在`jpeg`文件夹的同级目录下

# 使用说明

* 输入参数说明

  ```shell
  # jpeg文件夹绝对路径，必须
  -jpegPath=/xxx/xxx
  
  # 是否压缩avi视频，牺牲分辨率来减少视频大小，非必须，值为1，2，3
  -compressLevel=1        # 使用图片分辨率，但是进行编码优化
  -compressLevel=2        # 1280 X 1024
  -compressLevel=2        # 960 X 540
  ```

* 测试例子（作为参照）

  ```shell
  # 以1秒25帧从视频中截取500张jpeg图片，27MB
  不添加-compressLevel     # 输出avi视频5.2MB，会花较长时间
  -compressLevel=1     	  # 输出avi视频5.1MB，花费时间稍短一些
  -compressLevel=2        # 输出avi视频3MB，花费时间稍短一些
  -compressLevel=3        # 输出avi视频1.3MB，花费时间稍短一些
  # 1000张jpeg图片，45.7MB
  不添加-compressLevel     # 输出avi视频10MB，会花较长时间
  -compressLevel=1     	  # 输出avi视频9.6MB,花费时间稍短一些
  -compressLevel=2        # 输出avi视频5.6MB,花费时间稍短一些
  -compressLevel=3        # 输出avi视频2.3MB,花费时间稍短一些
  ```

# Linux/Mac

* 将视频转为`jpeg`图片（测试用）

  ```shell
  java -cp jpeg2avi.jar video2jpeg /dev_env/jpeg2video/test.mp4
  ```

* `jpeg`图片转`avi`视频

  ```shell
  java -cp jpeg2avi-jar-with-dependencies.jar jpeg2avi -jpegPath=/Users/dingyuanjie/work/engine/doc/jpeg-to-video/video/500jpeg
  
  java -cp jpeg2avi-jar-with-dependencies.jar jpeg2avi -jpegPath=/Users/dingyuanjie/work/engine/doc/jpeg-to-video/video/500jpeg -compressLevel=1
  ```
  

# Windows

* 将视频转为`jpeg`图片（测试用）

  ```shell
  java -cp jpeg2avi.jar video2jpeg d:\\test\\test.mp4
  ```

* 将`jpeg`图片转为`avi`视频

  ```shell
  # 不加compressLevel，转换时间会比较久
  java -cp jpeg2avi.jar jpeg2avi -jpegPath=d:\\test\\500jpeg
  java -cp jpeg2avi.jar jpeg2avi -jpegPath=d:\\test\\500jpeg -compressLevel=1
  ```
  
  

