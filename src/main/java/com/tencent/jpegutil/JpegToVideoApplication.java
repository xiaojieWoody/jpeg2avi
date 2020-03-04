package com.tencent.jpegutil;

import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static java.lang.Runtime.getRuntime;

//@SpringBootApplication
public class JpegToVideoApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(JpegToVideoApplication.class, args);
//	}

    public static void main(String[] args) throws Exception {
//        FfmpegUtil.setExecAuth("/Users/dingyuanjie/work/engine/doc/jpeg-to-video/exec_test/ffmpeg3");
        JarFile jar = new JarFile("/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpeg-to-video-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
        File f = new File("/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/ffmpeg");

//        JarEntry jarEntry1 = jar.getJarEntry("ffmpeg/mac/ffmpeg");
//        InputStream inputStream = jar.getInputStream(jarEntry1);
//        FileUtils.copyInputStreamToFile(inputStream, f);
//        f.setExecutable(true);

//        Enumeration<JarEntry> entries = jar.entries();
//        while (entries.hasMoreElements()) {
//            jar.getJarEntry("ffmpeg/mac/ffmpe");
//            JarEntry jarEntry = entries.nextElement();
//            String name = jarEntry.getName();
//            if(name.equals("ffmpeg/mac/ffmpeg")) {
//                InputStream inputStream = jar.getInputStream(jarEntry);
//                FileUtils.copyInputStreamToFile(inputStream, f);
//                f.setExecutable(true);
//            }
//        }
//        String targetFilePath = "/ffmpeg/result/ffmpeg";
//        String substring = targetFilePath.substring(targetFilePath.lastIndexOf(File.separator) + 1);

//        FfmpegUtil.getJarFile("/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpeg-to-video-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
//                "ffmpeg/linux/ffmpeg");

//        String filePath = "d:\\test\\jpeg.zip";
//        String filePath = "d:";
//        if(!filePath.contains("\\")) {
//            System.out.println(111);
//        }
//        String result = filePath.substring(0, filePath.lastIndexOf("\\"));
//        System.out.println(result);

//        String str = "/D:/test/jpeg2video.jar";
//        String substring = str.substring(str.indexOf("/") + 1);
//        String replace = substring.replace("/", "\\\\");
    }
}
