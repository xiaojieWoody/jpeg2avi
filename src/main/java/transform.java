import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 为配合命令transform，所以类名小写
 */
public class transform {

    public static void main(String[] args) throws IOException {
        String sourcePath = args[0];
        FfmpegUtil.test(sourcePath);
    }
}
