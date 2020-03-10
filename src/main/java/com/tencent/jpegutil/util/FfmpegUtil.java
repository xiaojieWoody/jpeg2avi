package com.tencent.jpegutil.util;

import com.tencent.jpegutil.constant.CommonConstant;
import com.tencent.jpegutil.level.CompressLevelEnum;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FfmpegUtil {

    /**
     * 通过Ffmpeg，将图片转为视频
     * @param commandLine
     * @throws IOException
     */
    public static void transformByFfmpeg(CommandLine commandLine) throws IOException {
        // jpeg图片目录路径
        String jpegPath = commandLine.getOptionValue("p");
        String level = commandLine.getOptionValue("l");

        // 生成的视频文件存放在jpeg的同级目录下
        String aviPath = getParentPath(jpegPath) + nowDateStr() + CommonConstant.VIDEO_TYPE ;
        System.out.println("video path: " + aviPath);

        // jpeg图片转avi视频
        transformJpeg2Video(jpegPath, aviPath,level);
    }

    /**
     * Jpeg图片转为mp4视频
     * @param sourcePath    jpeg图片目录
     * @param targetPath    avi视频目录
     */
    public static void transformJpeg2Video(String sourcePath, String targetPath, String level) throws IOException {

        File sourceFile = new File(sourcePath);
        if(!sourceFile.exists() || !sourceFile.isDirectory()) {
            throw new RuntimeException("jpeg file not exits or not a Directory！");
        }

        // jpeg目录的父目录路径
        String tmpParent = getParentPath(sourcePath);

        String ffmpegPath = null;
        String jpegTmpDir = null;
        try {
            // jpeg目录同级目录下的临时目录
            jpegTmpDir = tmpParent + File.separator + "jpeg_tmp_" + nowDateStr();

            // 获取ffmpeg路径
            ffmpegPath = getFfmpegPathBySystemOS();

            // 所有jpeg文件
            File[] files = sourceFile.listFiles();
            if(files.length < 1) {
                throw new RuntimeException("jpeg not exists!");
            }

            // 文件排序
            sortFile(files);

            // 拷贝文件到临时目录,jpeg重命名
            copyFileToTmpDir(files, jpegTmpDir);

            // 图片转视频
            List<String> command = new ArrayList<>();
            // ffmpeg可执行文件路径
            command.add(ffmpegPath);
            // 处理的线程个数
            command.add("-threads");
            command.add("2");
            // 指定为图片
            command.add("-f");
            command.add("image2");

            // 获取图片帧
            // 视频帧率，默认25（一般视频默认值），-r 25，1秒播25个图片
            // 视频时长(秒) = 图片数 / r
            int jpegTbr = getJpegTbr(files[0].getAbsolutePath(), ffmpegPath);
            if(jpegTbr != 0) {
                command.add("-r");
                command.add("" + jpegTbr);
            }

            // jpeg目录中图片（图片有序且名称要符合配置的正则表达式）
            command.add("-i");
            command.add(jpegTmpDir + File.separator + "%5d" + "." + CommonConstant.JPEG);

            // 默认分辨率 1280x720
            // 根据level设置分辨率
            if(level != null) {
                setCompressLevel(command, level);
            } else {
                // 编码格式，控制分辨率(清晰度和体积)
                // libx264 和图片一样清晰度 -vcodec libx264
                // 默认为mpeg4，清晰度一般，体积小
                command.add("-vcodec");
                command.add("libx264");
                // 编码优化参数，零延迟，否则转换出的视频播放会出现卡顿现象
                command.add("-tune");
                command.add("zerolatency");
                // 加快编码速率，需在大小和速率做平衡
                command.add("-b:v");
                command.add("2000k");
            }

            // 视频路径
            command.add(targetPath);
            // 执行命令
            process(command);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jpeg transform to avi fail!");
        } finally {
            // 删除jpeg临时目录
            if(!StringUtils.isBlank(jpegTmpDir)) {
                FileUtils.deleteDirectory(new File(jpegTmpDir));
            }
            // 删除ffmpeg文件
            if(!StringUtils.isBlank(ffmpegPath)) {
                FileUtils.forceDeleteOnExit(new File(ffmpegPath));
            }
        }
    }

    /**
     * 复制文件并指定名称，到指定目录
     * @param files
     * @param targetAbsolutePath
     * @throws IOException
     */
    public static void copyFileToTmpDir(File[] files, String targetAbsolutePath) throws IOException {

        File targetDir = new File(targetAbsolutePath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }

        for(int i = 0 ; i < files.length; i ++) {
            if(!files[i].isFile() || !files[i].getName().contains(CommonConstant.JPEG)) {
                continue;
            }
            FileUtils.copyFile(files[i], new File(targetAbsolutePath + File.separator + String.format("%05d", i) + "." + CommonConstant.JPEG));
        }

        if(targetDir.listFiles().length < 1) {
            throw new RuntimeException("jpeg not exists!");
        }
    }

    /**
     * 获取父目录路径
     * @param filePath
     * @return
     */
    public static String getParentPath(String filePath) {
        if(StringUtils.isBlank(filePath)) {
            return null;
        }
        String result  = null;
        // windows
        if(filePath.contains(":")) {
            if(!filePath.contains(CommonConstant.WIN_SEPARATOR)) {
                return filePath + File.separator;
            }
            result = filePath.substring(0, filePath.lastIndexOf(CommonConstant.WIN_SEPARATOR)) + File.separator;
        } else {
            // linux mac
            String[] split = filePath.split(File.separator);
            if(split.length < 2) {
                return null;
            }
            result = StringUtils.join(Arrays.copyOfRange(split, 0, split.length - 1), File.separator) + File.separator;
        }

        return result;
    }

    /**
     * 判断系统类型，解压jar包，获取ffmpeg
     * @return
     * @throws IOException
     */
    public static String getFfmpegPathBySystemOS() throws Exception {

        // 获取jar包绝对路径
        String resourcePath = FfmpegUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // ffmpeg路径
        String ffmpegPath = null;

        // 操作系统类型 windows linux mac
        String osName = System.getProperty(CommonConstant.OS_NAME).toLowerCase();

        if(osName.contains(CommonConstant.WINDOWS)) {
            // 获取的 jar 路径为 /D:/test/jpeg2video.jar
            String substring = resourcePath.substring(resourcePath.indexOf(CommonConstant.DEFAULT_SEPARATOR) + 1);
            resourcePath = substring.replace(CommonConstant.DEFAULT_SEPARATOR, CommonConstant.WIN_SEPARATOR);
            // 获取 jar包中ffmpeg
            ffmpegPath = getJarFile(resourcePath, CommonConstant.WIN_JAR_FFMPEG_PATH);

        } else if(osName.contains(CommonConstant.MAC)) {
            ffmpegPath = getJarFile(resourcePath, CommonConstant.MAC_JAR_FFMPEG_PATH);

        } else {
            // ffmpeg/linux/ffmpeg  为jar包中相对路径
            ffmpegPath = getJarFile(resourcePath, CommonConstant.LINUX_JAR_FFMPEG_PATH);
        }

        File ffmpeg = new File(ffmpegPath);
        if(!ffmpeg.exists() || !ffmpeg.canExecute()) {
            throw new RuntimeException("ffmpeg not exists or can not execute .....");
        }

        return ffmpegPath;
    }

    /**
     * 获取当前时间字符串，格式为yyyyMMddHHmmSS
     * @return
     */
    public static String nowDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        return sdf.format(new Date());
    }

    /**
     * 获取jar中文件到jar所在目录
     * @param jarPath    jar文件路径
     * @param targetFilePath  jar中文件路径
     */
    public static String getJarFile(String jarPath, String targetFilePath) throws IOException {

        // jar包所在目录
        String parentPath = getParentPath(jarPath);
        // 目标文件名称
        String targetName = targetFilePath.substring(targetFilePath.lastIndexOf(File.separator) + 1);
        // 目标文件
        File targetFile = new File(parentPath, targetName);

        // 获取jar包中文件流，ffmpeg/mac/ffmpeg
        InputStream resourceAsStream = FfmpegUtil.class.getClassLoader().getResourceAsStream(targetFilePath);
        if(null == resourceAsStream) {
            throw new RuntimeException("jar ffmpeg not exists!");
        }
        FileUtils.copyInputStreamToFile(resourceAsStream, targetFile);
        targetFile.setExecutable(true);
        return targetFile.getAbsolutePath();
    }

    /**
     * 执行命令
     * @param command
     * @throws Exception
     */
    public static void process(List<String> command) throws Exception{

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        if(0 == process.waitFor()) {
            System.out.println("success!");
        } else {
            System.out.println("fail!");
        }
    }

    /**
     * 文件按名称升序排序
     * @param files
     */
    public static void sortFile(File[] files) {
        Collections.sort(Arrays.asList(files), (o1, o2) -> {
            if(o1.isDirectory() && o2.isFile()) {
                return -1;
            }
            if(o1.isFile() && o2.isDirectory()) {
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        });
    }

    /**
     * 视频转图片
     * 生成的图片位于视频同级目录文件夹中
     * @param videoPath
     * @param ffmpegPath
     */
    public static void video2jpeg(String videoPath, String ffmpegPath, String jpegPath) throws Exception {

        List<String> command = new ArrayList<>();
        // ffmpeg文件路径
        command.add(ffmpegPath);
        // 视频路径
        command.add("-i");
        command.add(videoPath);
        // 1 秒 25帧 （1秒生成25张图片）
        command.add("-r");
        command.add("25");
        // 图片
        command.add("-f");
        command.add("image2");
        // 生成图片目录及图片名称
        command.add(jpegPath + "%5d.jpeg");

        // 执行ffmpeg命令
        process(command);
    }

    /**
     * 根据设置的level设置分辨率
     * @param command
     * @param level
     */
    public static void setCompressLevel(List<String> command, String level) {

        // 使用图片本身分辨率，使用默认的编码格式
        if("1".equals(level)) {
            return;
        }

        // 根据level获取对应的分辨率的值
        String contentByLevel = CompressLevelEnum.getContentByLevel(level);
        if(contentByLevel == null) {
            throw new RuntimeException("level param is not right");
        }
        // 分辨率
        command.add("-vf");
        command.add(contentByLevel);
    }

    /**
     * 获取图片帧
     * @param jpegPath
     * @return
     */
    public static int getJpegTbr(String jpegPath, String ffmpegPath) throws Exception {

        int result = 0;

        // 构建命令
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-i");
        command.add(jpegPath);
        command.add("-hide_banner");

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        // 获取命令执行后的返回结果
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String content = null;
        while ((content = br.readLine()) != null) {
            if(content.contains(CommonConstant.TBR)) {
                String[] contentSplit = content.split(",");
                // 遍历
                List<String> tbrList = Arrays.stream(contentSplit).filter(str -> str.contains(CommonConstant.TBR)).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(tbrList) && tbrList .size() == 1) {
                    //  25 tbr 替换为 25
                    result = Integer.parseInt(tbrList.get(0).replace(CommonConstant.TBR, "").trim());
                } else {
                    return result;
                }
            }
        }
        return result;
    }
}
