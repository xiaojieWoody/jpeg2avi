import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * 为配合小写命令名称，所以类名小写
 * 视频转为jpeg
 */
public class video2jpeg {

    public static void main(String[] args) throws Exception {

        // 解析参数
        CommandLine commandLine = parseArgs(args);
        String videoPath = commandLine.getOptionValue("p");
        String fps = commandLine.getOptionValue("f");

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
     * 解析输入参数
     * @param args
     * @return
     * @throws Exception
     */
    private static CommandLine parseArgs(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "使用信息");

        // jpeg目录路径
        options.addOption(Option.builder("p").longOpt("videoPath")
                .hasArg().argName("videoPath").desc("video directory path").required(true).build());

        options.addOption(Option.builder("f").longOpt("fps")
                .hasArg().argName("fps").desc("video fps").required(false).build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine result = null;

        try {
            result = parser.parse(options, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            formatter.printHelp("RandomGenerator", options, true);
            System.exit(1);
            throw e;
        }

        if (args.length == 0 || result.hasOption("h")) {
            formatter.printHelp("RandomGenerator", options, true);
            System.exit(1);
        }

        return result;
    }
}
