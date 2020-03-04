//package com.tencent.jpegutil.test;
//
//
//import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
//
//import org.bytedeco.javacpp.avcodec;
//import org.bytedeco.javacpp.opencv_core;
//import org.bytedeco.javacpp.opencv_core.IplImage;
//import org.bytedeco.javacv.FFmpegFrameRecorder;
//import org.bytedeco.javacv.FrameRecorder;
//import org.bytedeco.javacv.OpenCVFrameConverter;
//
//import java.io.File;
//
//public class Jpeg2VideoTest {
//
//    public static void main(String[] args) throws FrameRecorder.Exception {
//
//        String jpegPath = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/jpegs/";
//        String videoPath = "/Users/dingyuanjie/work/engine/doc/jpeg-to-video/video/f1.flv";
//
//        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoPath, 640,480);
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1);
//        recorder.setFormat("flv");
//        recorder.setFrameRate(20);
//        recorder.setPixelFormat(0);
//        recorder.start();
//
//        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
//        File file = new File(jpegPath);
//        File[] files = file.listFiles();
//        for(int i = 0; i <= files.length; i++) {
////            if(files[i].getName().contains("img_tort")) {
////                continue;
////            }
////            String fname = jpegPath + files[i].getName();
//            String absolutePath = files[i].getAbsolutePath();
//            IplImage image = cvLoadImage(absolutePath);
//            recorder.record(converter.convert(image));
//            opencv_core.cvReleaseImage(image);
//        }
//        recorder.stop();
//        recorder.release();
//    }
//}
