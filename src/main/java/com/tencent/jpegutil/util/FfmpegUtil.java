package com.tencent.jpegutil.util;

import com.tencent.jpegutil.test.FfmpegTest;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
     * @param sourcePath
     * @param targetPath
     */
    public static void transformJpeg2Video(String sourcePath, String targetPath) throws IOException {
        // 校验zip
        ZipFile jpegZip = new ZipFile(sourcePath);
        if(! jpegZip.isValidZipFile()) {
            throw new ZipException("The file is illegal or does not exist ");
        }

        // zip 父目录路径
        String tmpParent = getParentPath(sourcePath);
        System.out.println("tmpParent....." + tmpParent);

        String ffmpegPath = null;
        String unZipTmpDir = null;
        try {
            // 解压zip文件到临时目录
            unZipTmpDir = tmpParent + File.separator + "unzip_tmp_" + nowDateStr();
            System.out.println("unZipTmpDir....." + unZipTmpDir);
            unZip(sourcePath, unZipTmpDir);

            // 获取ffmpeg路径
            ffmpegPath = getFfmpegPathBySystemOS();
            System.out.println("ffmpegPath....." + ffmpegPath);

            // 解压后 jpeg文件所在目录
            String zipName = jpegZip.getFile().getName();
            String zipDirName = zipName.substring(0, zipName.indexOf("."));
            File jpegDir = new File(unZipTmpDir + File.separator + zipDirName );
            // 所有jpeg文件
            File[] files = jpegDir.listFiles();
            // 文件排序
            FfmpegTest.sortFile(files);

            // 拷贝文件到临时目录
//            for(int i = 0 ; i < files.length; i ++) {
//                if(!files[i].isFile() || !files[i].getName().contains("jpeg")) {
//                    continue;
//                }
//                FileUtils.copyFile(files[i], new File(tmp.getAbsolutePath() + File.separator + String.format("%05d", i) +".jpeg"));
//                System.out.println(files[i].getName());
//            }
            // jpeg文件重命名
            for(int i = 0; i < files.length; i++) {
                File jpeg = files[i];
                if(!jpeg.isFile() || !jpeg.getName().toLowerCase().contains("jpeg")) {
                    continue;
                }
                jpeg.renameTo(new File(jpegDir.getAbsolutePath() + File.separator + String.format("%05d", i) +".jpeg"));
            }

            // 图片转视频
            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-f");
            command.add("image2");
            command.add("-i");
            command.add(jpegDir + File.separator + "%5d.jpeg");
            command.add("-r");
            command.add("25");
            command.add(targetPath);
            // 执行命令
            FfmpegTest.process(command);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 删除临时目录、ffmpeg文件
            if(!StringUtils.isBlank(unZipTmpDir)) {
                FileUtils.deleteDirectory(new File(unZipTmpDir));
            }
            if(!StringUtils.isBlank(ffmpegPath)) {
                FileUtils.deleteQuietly(new File(ffmpegPath));
            }
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
            String[] split = filePath.split(File.separator);
            if(split.length < 2) {
                return null;
            }
            result = StringUtils.join(Arrays.copyOfRange(split, 0, split.length - 1), File.separator) + File.separator;
        }

        return result;
    }

    /**
     * 解压zip到指定目录
     * @param zipFileName
     * @param targetPath
     * @throws IOException
     */
    public static void unZip(String zipFileName, String targetPath) throws IOException {
        ZipFile zipFile = new ZipFile(zipFileName);
        if (!zipFile.isValidZipFile()) {
            throw new ZipException("The file is illegal or does not exist ");
        }

        File unZipDir = new File(targetPath);
        if(!unZipDir.exists()) {
            unZipDir.mkdir();
        }

        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
        for (int i = 0; i < fileHeaderList.size(); i++) {
            FileHeader fileHeader = fileHeaderList.get(i);
            zipFile.extractFile(fileHeader, targetPath);
        }
    }

    /**
     * 判断系统类型，解压jar包，获取ffmpeg
     * @return
     * @throws IOException
     */
    public static String getFfmpegPathBySystemOS() throws Exception {
        // 获取jar绝对路径
        String resourcePath = FfmpegUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // 获取jar父目录
//        String tmpParent = getParentPath(resourcePath);
        // jar包同级目录下创建临时目录tmp
//        File tmpDir = new File(tmpParent + "tmp");
//        if(!tmpDir.exists()) {
//            tmpDir.mkdir();
//        }

        // 解压 jar包 到临时目录
//        String tmpAbsoutePath = tmpDir.getAbsolutePath();
//        String shellCmd = "tar -zxvf " + resourcePath +" -C " + tmpAbsoutePath;
//        System.out.println(shellCmd);
//        String [] shell = {"/bin/bash", "-c", shellCmd};
//        Process process = Runtime.getRuntime().exec(shell);
//        process.getOutputStream().close();

        String ffmpegPath = null;

        // 操作系统类型 windows linux mac
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("system os ....." + osName);

        if(osName.contains("windows")) {
            // 获取的 jar 路径为 /D:/test/jpeg2video.jar
//            String str = "/D:/test/jpeg2video.jar";
            String substring = resourcePath.substring(resourcePath.indexOf("/") + 1);
            resourcePath = substring.replace("/", "\\\\");
            // 获取 jar包中ffmpeg
            ffmpegPath = getJarFile(resourcePath, "ffmpeg/windows/ffmpeg.exe");
        } else if(osName.contains("mac")) {
            ffmpegPath = getJarFile(resourcePath, "ffmpeg/mac/ffmpeg");
        } else {
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

//    /**
//     * 设置文件可执行权限
//     * @param filePath
//     */
//    public static void setExecAuth(String filePath) throws Exception {
//        System.out.println("设置可执行权限...." + filePath);
////        Runtime runtime = getRuntime();
//////        String command = "chmod 770 " + "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/exec_test/ffmpeg";
////        String command = "chmod 770 " + filePath;
////        try {
////            Process process = runtime.exec(command);
////            process.waitFor();
////            int existValue = process.exitValue();
////            if(existValue != 0){
////                throw new RuntimeException("设置可执行权限失败！");
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//        List<String> command = new ArrayList<>();
//        command.add("chown");
//        command.add("+x");
//        command.add(filePath);
//
//        FfmpegTest.process(command);
//    }

    /**
     * 获取jar中文件到jar所在目录
     * @param jarPath    jar文件路径
     * @param targetFilePath  jar中文件路径
     */
    public static String getJarFile(String jarPath, String targetFilePath) throws IOException {

        JarFile jar = new JarFile(jarPath);

        // jar包所在目录
        String parentPath = getParentPath(jarPath);
        // 目标文件名称

        String substring = targetFilePath.substring(targetFilePath.lastIndexOf(File.separator) + 1);

        File targetFile = new File(parentPath, substring);
        System.out.println("getJarFile targetFile..." + targetFile.getAbsolutePath());
        Enumeration<JarEntry> entries = jar.entries();
        boolean flag = false;
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            // 遍历jar包，判断目标文件
            if(name.equals(targetFilePath)) {
                InputStream inputStream = jar.getInputStream(jarEntry);
                FileUtils.copyInputStreamToFile(inputStream, targetFile);
                targetFile.setExecutable(true);
                flag = true;
            }
        }
        if(!flag) {
            throw new RuntimeException("jar中目标文件不存在!");
        }
        return targetFile.getAbsolutePath();
    }
}
