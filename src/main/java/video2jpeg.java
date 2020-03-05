import com.tencent.jpegutil.util.FfmpegUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * 为配合小写命令名称，所以类名小写
 * 视频转为jpeg
 */
public class video2jpeg {

    public static void main(String[] args) throws Exception {
        String videoPath = args[0];

        String ffmpegPath = null;
        try {
            // 视频父目录
            String parentPath = FfmpegUtil.getParentPath(videoPath);
            // 生成图片目录及图片名称
            String jpegDir = parentPath + File.separator + FfmpegUtil.nowDateStr() + File.separator;
            File jpegD = new File(jpegDir);
            if(!jpegD.exists()) {
                jpegD.mkdir();
            }
            // 获取ffmpeg文件
            ffmpegPath = FfmpegUtil.getFfmpegPathBySystemOS();
            // 执行ffmpeg命令
            FfmpegUtil.video2jpeg(videoPath, ffmpegPath, jpegDir);
            // 返回jpeg文件夹路径
            System.out.println("jpeg path：" + jpegDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            File ffmpeg = new File(ffmpegPath);
            System.out.println("ffmpeg...." + ffmpeg.getAbsolutePath());
            if(ffmpeg.exists()) {
                FileUtils.forceDeleteOnExit(ffmpeg);
            }
        }
    }
}
