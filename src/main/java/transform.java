import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.cli.*;

/**
 * 为配合命令transform，所以类名小写
 */
public class transform {

    public static void main(String[] args) throws Exception {

        FfmpegUtil.transformByFfmpeg(parseArgs(args));
    }

    private static CommandLine parseArgs(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "使用信息");

        // jpeg目录路径
        options.addOption(Option.builder("p").longOpt("jpegPath")
                .hasArg().argName("jpegPath").desc("jpeg directory path").required(true).build());
        // 是否压缩
        options.addOption(Option.builder("c").longOpt("compress")
                .hasArg().argName("compress").desc("compress video").required(false).build());

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
