package com.newthread.shiquan.ftp;

/**
 * Created by lanqx on 2014/9/6.
 */
public interface IRetrieveListener {
    public void onStart(long size);
    public void onTrack(long nowOffset);
    public void onError(Object obj);
    public void onCancel(Object obj);
    public void onDone();
}