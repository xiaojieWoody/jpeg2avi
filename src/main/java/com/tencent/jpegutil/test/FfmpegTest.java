package com.tencent.jpegutil.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class FfmpegTest {

    private static final String ffmpegPath = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/ffmpeg";
//    private static final String ffmpegPath = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/tmp/ffmpeg/mac/ffmpeg";
    private static final String resultPath = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/new.jpg";
    private static final String sourcePath = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/resource/test.mp4";

    public static void main(String[] args) throws Exception{
//        video2Jpeg();
//        vide2Jpeg2();
        jpeg2video();
//        copyFile();
    }

    public static void process(List<String> command) throws Exception{

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader isr = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
        }
        if(br != null) {
            br.close();
        }
        if(isr != null) {
            isr.close();
        }
        if(errorStream != null) {
            errorStream.close();
        }
    }

    // 视频按帧截图  ffmpeg.exe -ss 00:00:01 -y -i test1.mp4 -vframes 1 new.jpg
    // ./ffmpeg -ss 00:00:01 -y -i test.mp4 -vframes 1 new.jpg
    // 此处：-ss position 搜索到指定的时间 [-]hh:mm:ss[.xxx]的格式也支持，-vframes 设置转换多少桢(frame)的视频，此命令是获取第一秒第一帧的截图
    public static void video2Jpeg() throws Exception {
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-ss");
        command.add("00:00:01");
        command.add("-y");
        command.add("-i");
        command.add(sourcePath);
        command.add("-vframes");
        command.add("1");
        command.add(resultPath);

        process(command);
    }

    // 视频转为图片
    // ffmpeg -i scenery.mp4 -r 2 -f image2 img2/img-%3d.jpg
    public static void vide2Jpeg2() throws Exception {
        String result = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpg/";

        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-i");
        command.add(sourcePath);
        command.add("-r");
        command.add("2");
        command.add("-f");
        command.add("image2");
        command.add(result + "img-%3d.jpg");

        process(command);
    }

    // 将图片转为视频
    // ffmpeg -f image2 -i img/img2%4d.jpg -r 25 video_img.mp4
    public static void jpeg2video() throws Exception {
        String source = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpg/";
        String result = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpg/3.flv";

        // chmod u+x /Users/abc/xyz.exe

        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-f");
        command.add("image2");
        command.add("-i");
//        command.add(source + "^[1-9]\\d*$.jpg");
        command.add(source + "img-%3d.jpg");
        command.add("-r");
        command.add("25");
        command.add(result);

        process(command);
    }

    private static void copyFile() throws IOException {
//        String source = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpg/";
        String source = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpegs/";
        String result = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/ffmpeg/result/jpegs/1.mp4";

        File tmp = null;
        try {
            String[] split = source.split(File.separator);
            String tmpParent = StringUtils.join(Arrays.copyOfRange(split, 0, split.length - 1), File.separator) + File.separator;
            // 创建临时目录
            tmp = new File(tmpParent + "tmp");
            if(!tmp.mkdir()) {
                System.err.println(".................");
            }
            File jpgDir = new File(source);
            if(!jpgDir.isDirectory()) {
                System.err.println("....111.............");
            }

            File[] files = jpgDir.listFiles();

            // 文件排序
            sortFile(files);

            // 拷贝文件到临时目录
            for(int i = 0 ; i < files.length; i ++) {
                if(!files[i].isFile() || !files[i].getName().contains("jpeg")) {
                    continue;
                }
                FileUtils.copyFile(files[i], new File(tmp.getAbsolutePath() + File.separator + String.format("%05d", i) +".jpeg"));
                System.out.println(files[i].getName());
            }

            // 图片转视频
            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-f");
            command.add("image2");
            command.add("-i");
            command.add(tmp.getAbsolutePath() + File.separator + "%5d.jpeg");
            command.add("-r");
//            command.add("25");
            command.add("120");
            command.add(result);

            process(command);

//            Thread.sleep(2000);
        } catch (Exception e) {

        } finally {
            if(null != tmp) {
//                FileUtils.deleteDirectory(tmp);
            }
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
