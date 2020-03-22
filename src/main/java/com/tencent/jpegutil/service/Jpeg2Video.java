package com.tencent.jpegutil.service;

import com.tencent.jpegutil.constant.CommonConstant;
import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author aic
 * 供外部调用
 */
public class Jpeg2Video {

    public static void jepg2Video(String jpegPath, String compressLevel, String fps, String videoType) throws IOException {

        int videoFps = FfmpegUtil.getFps(fps);

        // 生成的视频文件存放在jpeg的同级目录下
        String vType = CommonConstant.VIDEO_DEFAULT_TYPE;
        if(!StringUtils.isBlank(videoType)) {
            vType = videoType.toLowerCase();
        }
        String videoPath = FfmpegUtil.getParentPath(jpegPath) + FfmpegUtil.nowDateStr() + "." + vType ;
        System.out.println("video path: " + videoPath);

        // jpeg图片转视频
        FfmpegUtil.transformJpeg2Video(jpegPath, videoPath,compressLevel, videoFps);
    }

}
