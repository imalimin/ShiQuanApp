package com.newthread.shiquan.utils;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lanqx on 2014/7/31.
 */
public class VideoUtil {
    public static void appendVideo(Context mContext, final String[] videos, final String append, final IVideoListener videoListener) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                videoListener.onComplete(message.what);
            }
        };
        new Thread() {
            public void run() {
                super.run();
                try {
                    Movie[] inMovies = new Movie[videos.length];
                    int index = 0;
                    for (String video : videos) {

                        inMovies[index] = MovieCreator.build(video);

                        index++;
                    }
                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }

                    Movie result = new Movie();

                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }
                    Container out = new DefaultMp4Builder().build(result);
                    FileChannel fc = new RandomAccessFile(String.format(append), "rw").getChannel();
                    out.writeContainer(fc);
                    fc.close();
                    handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void addAudio(final Context mContext, final String video, final String audio, final String append, final boolean isStayAudio, final IVideoListener videoListener) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                videoListener.onComplete(message.what);
            }
        };
        new Thread() {
            public void run() {
                super.run();
                try {
                    Movie inMovie = MovieCreator.build(video);
                    Movie inAudio = MovieCreator.build(audio);
                    Log.v("000000", "addAudio:inMovie:" + inMovie.getTimescale() + ";inAudio:" + inAudio.getTimescale());
                    Track videoTrack = null;
                    Track audioTrack1 = null;
                    Track audioTrack2 = null;
                    for (Track t : inMovie.getTracks()) {
                        if (t.getHandler().equals("vide")) {
                            videoTrack = t;
                        }
                        if (t.getHandler().equals("soun")) {
                            audioTrack2 = t;
                        }
                    }
                    for (Track t : inAudio.getTracks()) {
                        if (t.getHandler().equals("soun")) {
                            Log.v("000000", "addAudio:inMovie:" + getTrackTime(t));
                            if (getTrackTime(videoTrack) < getTrackTime(t)) {
                                audioTrack1 = croppedTrack(t, 0, (int) getTrackTime(videoTrack)*1000 + 500);
                            } else {
                                audioTrack1 = t;
                            }
                            MediaCodec mediaCodec;
                        }
                    }
                    Movie result = null;
                    result = new Movie();
                    if (audioTrack1 != null) {
                        result.addTrack(audioTrack1);
                    }
                    if (videoTrack != null) {
                        result.addTrack(videoTrack);
                    }
                    Container out = new DefaultMp4Builder().build(result);
                    FileChannel fc = new RandomAccessFile(String.format(append), "rw").getChannel();
                    out.writeContainer(fc);
                    fc.close();
                    handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static Track croppedTrack(Track track, int startMs, int endMs) {
        double startTime = startMs / 1000;
        double endTime = endMs / 1000;
        boolean timeCorrected = false;
        if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
            //true,false表示短截取；false,true表示长截取
            startTime = correctTimeToSyncSample(track, startTime, true);
            endTime = correctTimeToSyncSample(track, endTime, false);
        }
        long currentSample = 0;
        double currentTime = 0;
        long startSample = -1;
        long endSample = -1;
        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {
                // entry.getDelta() is the amount of time the current sample covers.
                if (currentTime <= startTime) {
                    // current sample is still before the new starttime
                    startSample = currentSample;
                }
                if (currentTime <= endTime) {
                    // current sample is after the new start time and still before the new endtime
                    endSample = currentSample;
                } else {
                    // current sample is after the end of the cropped video
                    break;
                }
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }
        Log.v("000000", "croppedTrack:startTime:" + startTime + ":endTime" + endTime);
        return new CroppedTrack(track, startSample, endSample);
    }

    public static double getTrackTime(Track track) {
        double currentTime = 0;
        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
            }
        }
        return currentTime;
    }

    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {
                if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                    // samples always start with 1 but we start with zero therefore +1
                    timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
                }
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    public interface IVideoListener {
        public void onComplete(int result);
    }
}
