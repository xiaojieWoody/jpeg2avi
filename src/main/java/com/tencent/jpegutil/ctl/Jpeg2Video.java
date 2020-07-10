package com.tencent.jpegutil.ctl;

import com.tencent.jpegutil.constant.CommonConstant;
import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 图片转视频
 */
public class Jpeg2Video {

    public static final String OPT_HELP = "help";
    public static final String JPEG_PATH = "jpegPath";
    public static final String COMPRESS_LEVEL = "compressLevel";
    public static final String VIDEO_FPS = "fps";
    public static final String VIDEO_TYPE = "videoType";

    public static final String VIDEO_PATH = "videoPath";

    private static CommandLine commandLine;
    private static Options options;

    public static void main(String[] args) throws Exception {

        // 解析启动参数
        parseArgs(args);
        if (commandLine.hasOption(OPT_HELP)) {
            printHelpMessage();
            return;
        }

        // jpeg图片目录路径
        String jpegPath = null;
        if (commandLine.hasOption(JPEG_PATH)) {
            jpegPath = commandLine.getOptionValue(JPEG_PATH);
        }

        // 视频压缩
        String level = null;
        if (commandLine.hasOption(COMPRESS_LEVEL)) {
            level = commandLine.getOptionValue(COMPRESS_LEVEL);
        }

        // 视频格式
        String type = null;
        // 默认 avi 格式
        String videoType = CommonConstant.VIDEO_DEFAULT_TYPE;
        if (commandLine.hasOption(VIDEO_TYPE)) {
            type = commandLine.getOptionValue(VIDEO_TYPE);
            videoType = type;
        }

        // 视频帧率
        int fps = 25;
        if (commandLine.hasOption(VIDEO_FPS)) {
            String fpsStr = commandLine.getOptionValue(VIDEO_FPS);
            fps = FfmpegUtil.getFps(fpsStr);
        }

        // 视频目录路径
        String sourceVideoPath = null;
        if (commandLine.hasOption(VIDEO_PATH)) {
            sourceVideoPath = commandLine.getOptionValue(VIDEO_PATH);
        }

        if(StringUtils.isNotBlank(sourceVideoPath)) {
            // 视频转图片
            video2Jpeg(sourceVideoPath, String.valueOf(fps));
        } else {
            // jpeg图片转视频
            // 生成的视频文件存放在jpeg的同级目录下
            String videoPath = FfmpegUtil.getParentPath(jpegPath) + FfmpegUtil.nowDateStr() + "." + videoType ;
            System.out.println("video path: " + videoPath);
            FfmpegUtil.transformJpeg2Video(jpegPath, videoPath, level,  fps);
        }
    }

    /**
     * 解析输入参数
     * @param args
     * @return
     * @throws Exception
     */
    private static void parseArgs(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        options = new Options();

        Options options = new Options();
        options.addOption("h", OPT_HELP, false, "print help message");
        // jpeg目录路径
        options.addOption("p", JPEG_PATH, true, "jpeg directory path");
        // 设置分辨率等级
        options.addOption("c", COMPRESS_LEVEL, true, "compress video");
        options.addOption("f", VIDEO_FPS, true, "video fps");
        options.addOption("t", VIDEO_TYPE, true, "video type");
        options.addOption("v", VIDEO_PATH, true, "video path");

        commandLine = parser.parse(options, args);
    }

    /**
     * video转jpeg
     * @param videoPath
     * @param fps
     * @throws IOException
     */
    public static void video2Jpeg(String videoPath, String fps) throws IOException {
        File video = new File(videoPath);
        if(!video.exists() || !video.isFile()) {
            throw new RuntimeException("video not exists!");
        }

        String ffmpegPath = null;
        String jpegDir = null;
        try {
            // 视频父目录
            String parentPath = FfmpegUtil.getParentPath(videoPath);
            // 生成图片目录及图片名称
            jpegDir = parentPath + File.separator + FfmpegUtil.nowDateStr() + File.separator;
            File jpegD = new File(jpegDir);
            if(!jpegD.exists()) {
                jpegD.mkdir();
            }
            // 获取ffmpeg文件
            ffmpegPath = FfmpegUtil.getFfmpegPathBySystemOS();
            // 执行ffmpeg命令
            FfmpegUtil.video2jpeg(videoPath, ffmpegPath, jpegDir, fps);
            // 返回jpeg文件夹路径
            System.out.println("jpeg path：" + jpegDir);
        } catch (Exception e) {
            e.printStackTrace();
            File jpegD = new File(jpegDir);
            if (jpegD.exists()) {
                FileUtils.forceDeleteOnExit(jpegD);
            }
        } finally {
            File ffmpeg = new File(ffmpegPath);
            if(ffmpeg.exists()) {
                FileUtils.forceDeleteOnExit(ffmpeg);
            }
        }
    }

    /**
     * 打印命令帮助信息
     */
    static void printHelpMessage() {
        new HelpFormatter().printHelp("commandName [OPTIONS] <JPEG-VIDEO>", options);
    }
}
