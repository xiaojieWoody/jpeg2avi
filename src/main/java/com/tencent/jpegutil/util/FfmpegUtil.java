package com.tencent.jpegutil.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FfmpegUtil {

    public static void test(String jpegPath) throws IOException {
//        String source = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/jpegs.zip";
//        String result = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/1.avi";
        String parentPath = getParentPath(jpegPath) + File.separator + nowDateStr() + ".avi";
        System.out.println("parentPath..." + parentPath);
        transformJpeg2Video(jpegPath, parentPath);
    }

    /**
     * Jpeg图片转为mp4视频
     * @param sourcePath    jpeg图片目录
     * @param targetPath    avi视频目录
     */
    public static void transformJpeg2Video(String sourcePath, String targetPath) throws IOException {

        File sourceFile = new File(sourcePath);
        if(!sourceFile.exists() || !sourceFile.isDirectory()) {
            throw new RuntimeException("jpeg file not exits or not a Directory！");
        }

        // jpeg目录的父目录路径
        String tmpParent = getParentPath(sourcePath);
        System.out.println("tmpParent....." + tmpParent);

        String ffmpegPath = null;
        String jpegTmpDir = null;
        try {
            // jpeg目录同级目录下的临时目录
            jpegTmpDir = tmpParent + File.separator + "jpeg_tmp_" + nowDateStr();
            System.out.println("jpeg_tmp_....." + jpegTmpDir);

            // 获取ffmpeg路径
            ffmpegPath = getFfmpegPathBySystemOS();
            System.out.println("ffmpegPath....." + ffmpegPath);

            // 所有jpeg文件
            File[] files = sourceFile.listFiles();

            // 文件排序
            sortFile(files);

            // 拷贝文件到临时目录,jpeg重命名
            copyFileToTmpDir(files, jpegTmpDir);

            // 图片转视频
            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-f");
            command.add("image2");
            command.add("-i");
            command.add(jpegTmpDir + File.separator + "%5d.jpeg");
            command.add("-r");
            command.add("25");
            command.add(targetPath);
            // 执行命令
            process(command);
        } catch (Exception e) {
            e.printStackTrace();
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
    private static void copyFileToTmpDir(File[] files, String targetAbsolutePath) throws IOException {

        for(int i = 0 ; i < files.length; i ++) {
            if(!files[i].isFile() || !files[i].getName().contains("jpeg")) {
                continue;
            }
            FileUtils.copyFile(files[i], new File(targetAbsolutePath + File.separator + String.format("%05d", i) +".jpeg"));
        }
    }

    /**
     * 获取父目录路径
     * @param filePath
     * @return
     */
    private static String getParentPath(String filePath) {
        if(StringUtils.isBlank(filePath)) {
            return null;
        }
        String result  = null;
        // windows
        if(filePath.contains(":")) {
            System.out.println("getParentPath filePath..." + filePath);
            if(!filePath.contains("\\\\")) {
                System.out.println("filePath..." + filePath);
                return filePath;
            }
            result = filePath.substring(0, filePath.lastIndexOf("\\\\"));
            System.out.println("result..." + result);
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
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("windows")) {
            // 获取的 jar 路径为 /D:/test/jpeg2video.jar
            String substring = resourcePath.substring(resourcePath.indexOf("/") + 1);
            resourcePath = substring.replace("/", "\\\\");
            // 获取 jar包中ffmpeg
            ffmpegPath = getJarFile(resourcePath, "ffmpeg/windows/ffmpeg.exe");
        } else if(osName.contains("mac")) {
            ffmpegPath = getJarFile(resourcePath, "ffmpeg/mac/ffmpeg");
        } else {
            // ffmpeg/linux/ffmpeg  为jar包中相对路径
            ffmpegPath = getJarFile(resourcePath, "ffmpeg/linux/ffmpeg");
        }

        System.out.println("ffmpegPath....." + ffmpegPath);

        File ffmpeg = new File(ffmpegPath);
        if(!ffmpeg.exists() || !ffmpeg.canExecute()) {
            System.out.println("ffmpeg not exists or can not execute !..........");
            throw new RuntimeException("ffmpeg ffmpeg not exists or can not execute .....");
        }

        return ffmpegPath;
    }

    /**
     * 获取当前时间字符串，格式为yyyyMMddHHmmSS
     * @return
     */
    private static String nowDateStr() {
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
        String substring = targetFilePath.substring(targetFilePath.lastIndexOf(File.separator) + 1);
        // 目标文件
        File targetFile = new File(parentPath, substring);
        System.out.println("getJarFile targetFile..." + targetFile.getAbsolutePath());

        // 获取jar包中文件流，ffmpeg/mac/ffmpeg
        InputStream resourceAsStream = FfmpegUtil.class.getClassLoader().getResourceAsStream(targetFilePath);
        if(null == resourceAsStream) {
            throw new RuntimeException("jar中ffmpeg文件不存在!");
        }
        FileUtils.copyInputStreamToFile(resourceAsStream, targetFile);
        targetFile.setExecutable(true);
        System.out.println("targetFile.getAbsolutePath()......" + targetFile.getAbsolutePath());
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
            System.out.println("jpeg transform to avi success!");
        } else {
            System.out.println("jpeg transform to avi fail!");
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
}
