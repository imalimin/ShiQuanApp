package com.newthread.shiquan.media;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by 翌日黄昏 on 2014/8/16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AvcEncoder {
    private static final String TAG = "AvcEncoder";
    //    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
//    private static final int FRAME_RATE = 24;               // 30fps
//    private static final int IFRAME_INTERVAL = 1;           // 5 seconds between I-frames
    private static final long DURATION_SEC = 8;             // 8 seconds of video
    private final int videoBitRate = 512 * 1024 * 8;
    private int width = 0;
    private int height = 0;
    private File OUTPUT_DIR;
    private ArrayList<byte[]> data;

    private static final boolean VERBOSE = false;           // lots of logging

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 15;               // 15fps
    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames


    // encoder / muxer state
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private int mTrackIndex;
    private boolean mMuxerStarted;

    // allocate one of these up front so we don't need to do it every time
    private MediaCodec.BufferInfo mBufferInfo;

    public AvcEncoder(int width, int height, File file, ArrayList<byte[]> data) {
        this.width = width;
        this.height = height;
        this.OUTPUT_DIR = file;
        this.data = data;
    }

    public void startVideoToMp4() {
        // QVGA at 2Mbps
        try {
            prepareEncoder();
            feedData();
            drainEncoder();
        } finally {
            // release encoder, muxer, and input Surface
            releaseEncoder();
        }

        // To test the result, open the file with MediaExtractor, and get the format.  Pass
        // that into the MediaCodec decoder configuration, along with a SurfaceTexture surface,
        // and examine the output with glReadPixels.
    }

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    private void prepareEncoder() {
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        //
        // If you want to have two EGL contexts -- one for display, one for recording --
        // you will likely want to defer instantiation of CodecInputSurface until after the
        // "display" EGL context is created, then modify the eglCreateContext call to
        // take eglGetCurrentContext() as the share_context argument.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mEncoder.start();

        // Output filename.  Ideally this would use Context.getFilesDir() rather than a
        // hard-coded output directory.
        Log.d(TAG, "output file is " + OUTPUT_DIR);


        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        try {
            mMuxer = new MediaMuxer(OUTPUT_DIR.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioe) {
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    public void finish() {
        // エンコード・デコードを終了
        mForceInputEOS = true;
    }
    private boolean mForceInputEOS = false;
    private void feedData() {
        Log.v(TAG, "putFrame:" + data.size());
        final int TIMEOUT_USEC = 10000;
        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();

        boolean sawInputEOS = false;
        int numInputFrames = 0;
        int maxInputFrames = -1;
        long lap0 = 0;

        int inputBufferIndex = 0;
        for (byte[] input : data) {
            inputBufferIndex = mEncoder.dequeueInputBuffer(TIMEOUT_USEC);

            if (inputBufferIndex >= 0) {
                //Log.d("sakalog", "encoder input buf index " + inputBufIndex);
                ByteBuffer dstBuf = inputBuffers[inputBufferIndex];

                long presentationTimeUs = 0;

                // 1フレームぶんのデータをqueueInputBufferを使ってCodec(エンコーダ)へ提示する
                // maxInputFramesフレームに達したらデータの代わりにBUFFER_FLAG_END_OF_STREAMフラグを提示する
                if ((maxInputFrames > 0 && numInputFrames >= maxInputFrames) || mForceInputEOS) {
                    Log.d("sakalog", "saw input EOS.");
                    sawInputEOS = true;
                } else {
                    dstBuf.clear();
                    dstBuf.put(input);
                    presentationTimeUs = numInputFrames*1000000/FRAME_RATE;
                    numInputFrames++;
                    lap0 = System.currentTimeMillis();
                }

                mEncoder.queueInputBuffer(
                        inputBufferIndex,
                        0 /* offset */,
                        input.length,
                        presentationTimeUs,
                        sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
            }
//            inputBufferIndex = mEncoder.dequeueInputBuffer(-1);
//            if (inputBufferIndex >= 0) {
//                Log.v(TAG, "putFrame:" + inputBufferIndex);
//                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                inputBuffer.clear();
//                inputBuffer.put(input, 0, input.length);
//                mEncoder.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
//
//            }
        }
        Log.v(TAG, "inputBuffers:" + inputBuffers.length);
//        mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
    }

    private void releaseEncoder() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    /**
     * Extracts all pending data from the encoder.
     * <p/>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    private void drainEncoder() {
        final int TIMEOUT_USEC = 10000;
        ByteBuffer[] codecOutputBuffers = mEncoder.getOutputBuffers();
        Log.v(TAG, "mBufferInfo.size:" + mBufferInfo.size);
        while (true) {
            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            Log.v(TAG, "outputBufferIndex:" + outputBufferIndex);
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.v(TAG, "INFO_TRY_AGAIN_LATER:" + MediaCodec.INFO_TRY_AGAIN_LATER);
                break;
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mEncoder.getOutputFormat();
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                codecOutputBuffers = mEncoder.getOutputBuffers();
            } else {
                // Write to muxer
                ByteBuffer encodedData = codecOutputBuffers[outputBufferIndex];
//                Log.v(TAG, "encodedData:" + encodedData.array().length);
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    Log.v(TAG, "drainEncoder:Write to muxer:" + mBufferInfo.size);
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                }

                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                // End write to muxer
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.v(TAG, "BUFFER_FLAG_END_OF_STREAM:" + MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                }
            }
        }
    }
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                Log.v(TAG, "INFO_TRY_AGAIN_LATER:" + MediaCodec.INFO_TRY_AGAIN_LATER);
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    Log.v(TAG, "drainEncoder:Write to muxer");
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    Log.v(TAG, "BUFFER_FLAG_END_OF_STREAM:" + MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;      // out of while
                }
            }
        }
    }
}

