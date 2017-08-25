package com.sugar.record.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sugar
 */
public class MediaRecordUtils {

    private final static String TAG = "MediaRecordUtils";

    private Surface mSurface;
    private MediaMuxer mMediaMuxer;
    private MediaCodec mMediaCodec;
    private AtomicBoolean mIsQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private boolean mMuxerStarted = false;
    private int mVideoTrackIndex = -1;

    public MediaRecordUtils() {
    }

    public void init(int width, int height) throws Exception {
        //MediaFormat这个类是用来定义视频格式相关信息的
        //video/avc,这里的avc是高级视频编码Advanced Video Coding
        //mWidth和mHeight是视频的尺寸，这个尺寸不能超过视频采集时采集到的尺寸，否则会直接crash
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", width, height);
        //COLOR_FormatSurface这里表明数据将是一个graphicbuffer元数据
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //设置码率，通常码率越高，视频越清晰，但是对应的视频也越大，这个值我默认设置成了2000000，也就是通常所说的2M，这已经不低了，如果你不想录制这么清晰的，你可以设置成500000，也就是500k
        format.setInteger(MediaFormat.KEY_BIT_RATE, 5 * 1920 * 1080);

        //设置帧率，通常这个值越高，视频会显得越流畅，一般默认我设置成30，你最低可以设置成24，不要低于这个值，低于24会明显卡顿
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);

        //IFRAME_INTERVAL是指的帧间隔，这是个很有意思的值，它指的是，关键帧的间隔时间。通常情况下，你设置成多少问题都不大。
        //比如你设置成10，那就是10秒一个关键帧。但是，如果你有需求要做视频的预览，那你最好设置成1
        //因为如果你设置成10，那你会发现，10秒内的预览都是一个截图
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        //创建一个MediaCodec的实例
        mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        //定义这个实例的格式，也就是上面我们定义的format，其他参数不用过于关注
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        //这一步非常关键，它设置的，是MediaCodec的编码源，也就是说，我要告诉mEncoder，你给我解码哪些流。
        //很出乎大家的意料，MediaCodec并没有要求我们传一个流文件进去，而是要求我们指定一个surface
        //而这个surface，其实就是我们在上一讲MediaProjection中用来展示屏幕采集数据的surface
        mSurface = mMediaCodec.createInputSurface();
        mMediaCodec.start();
    }

    public Surface getSurface() {
        return mSurface;
    }

    public void startRecord() throws Exception {
        mMediaMuxer = new MediaMuxer(FileUtils.getMediaCachePath() + "media" + System.currentTimeMillis() + ".mp4",
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        recordVirtualDisplay();
    }

    public void stop() {
        release();
    }

    private void recordVirtualDisplay() {
        while (!mIsQuit.get()) {
            int index = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 10000);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {//后续输出格式变化
                resetOutputFormat();
            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {//请求超时
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (index >= 0) {//有效输出
                if (!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                }
                encodeToVideoTrack(index);
                mMediaCodec.releaseOutputBuffer(index, false);
            }
        }
    }

    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mMediaCodec.getOutputBuffer(index);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {//是编码需要的特定数据，不是媒体数据
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            mMediaMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);//写入
        }
    }

    private void resetOutputFormat() {
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mMediaCodec.getOutputFormat();
        mVideoTrackIndex = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
        mMuxerStarted = true;
    }

    private void release() {
        mIsQuit.set(false);
        mMuxerStarted = false;
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
    }

}
