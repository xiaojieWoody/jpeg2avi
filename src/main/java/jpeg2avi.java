import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.cli.*;

/**
 * 为配合小写命令名称，所以类名小写
 * jpeg转avi视频
 */
public class jpeg2avi {

    public static void main(String[] args) throws Exception {

        FfmpegUtil.transformByFfmpeg(parseArgs(args));
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
        options.addOption(Option.builder("p").longOpt("jpegPath")
                .hasArg().argName("jpegPath").desc("jpeg directory path").required(true).build());
        // 设置分辨率等级
        options.addOption(Option.builder("l").longOpt("compressLevel")
                .hasArg().argName("compressLevel").desc("compress video").required(false).build());

        options.addOption(Option.builder("f").longOpt("fps")
                .hasArg().argName("fps").desc("video fps").required(false).build());

        options.addOption(Option.builder("t").longOpt("videoType")
                .hasArg().argName("videoType").desc("video type").required(false).build());

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
