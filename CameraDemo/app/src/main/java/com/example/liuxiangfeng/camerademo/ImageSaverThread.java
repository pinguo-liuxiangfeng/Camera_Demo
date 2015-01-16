package com.example.liuxiangfeng.camerademo;

import android.util.Log;

import com.example.liuxiangfeng.camerademo.utils.FileUtil;

import java.util.ArrayList;
/**
 * Created by liuxiangfeng on 15-1-15.
 */
// We use a queue to store the SaveRequests that have not been completed
// yet. The main thread puts the request into the queue. The saver thread
// gets it from the queue, does the work, and removes it from the queue.
//
// There are several cases the main thread needs to wait for the saver
// thread to finish all the work in the queue:
// (1) When the activity's onPause() is called, we need to finish all the
// work, so other programs (like Gallery) can see all the images.
// (2) When we need to show the SharePop, we need to finish all the work
// too, because we want to show the thumbnail of the last image taken.
//
// If the queue becomes too long, adding a new request will block the main
// thread until the queue length drops below the threshold (QUEUE_LIMIT).
// If we don't do this, we may face several problems: (1) We may OOM
// because we are holding all the jpeg data in memory. (2) We may ANR
// when we need to wait for saver thread finishing all the work (in
// onPause() or showSharePopup()) because the time to finishing a long queue
// of work may be too long.
// 上面的话很重要，认真分析
public class ImageSaverThread extends Thread {
    private static final int QUEUE_LIMIT = 3;

    private ArrayList<SaveRequest> mQueue;
    private boolean mStop;

    // Runs in main thread
    public ImageSaverThread() {
        // SaveRequest是用来存储每张图片里面必须存储的信息的
        mQueue = new ArrayList<SaveRequest>();
        start();
    }

    // Runs in main thread
    public void addImage(final byte[] data) {
        Log.d("ImageSaverThread", " addImage...");
        SaveRequest r = new SaveRequest();
        r.data = data;
        r.dateTaken = System.currentTimeMillis();
        synchronized (this) {
            while (mQueue.size() >= QUEUE_LIMIT) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    // ignore.
                }
            }
            mQueue.add(r);
            notifyAll();  // Tell saver thread there is new work to do.
        }
    }

    // Runs in saver thread
    @Override
    public void run() {
        Log.d("ImageSaverThread", " run...begin.");
        while (true) {
            SaveRequest r;
            synchronized (this) {
                if (mQueue.isEmpty()) {
                    notifyAll();  // notify main thread in waitDone

                    // Note that we can only stop after we saved all images
                    // in the queue.
                    if (mStop) break;

                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        // ignore.
                    }
                    continue;
                }
                r = mQueue.get(0);
            }
            String jpegName = FileUtil.createJpegName(r.dateTaken);
            FileUtil.saveBitmap(jpegName, r.data);
            synchronized(this) {
                mQueue.remove(0);
                notifyAll();  // the main thread may wait in addImage
            }
        }
        Log.d("ImageSaverThread", " run...end.");
    }

    // Runs in main thread
    public void waitDone() {
        synchronized (this) {
            while (!mQueue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    // ignore.
                }
            }
        }
    }

    // Runs in main thread
    public void finish() {
        waitDone();
        synchronized (this) {
            mStop = true;
            notifyAll();
        }
        try {
            join();
        } catch (InterruptedException ex) {
            // ignore.
        }
    }



    // Each SaveRequest remembers the data needed to save an image.
    public static class SaveRequest {
        byte[] data;
        long dateTaken;
    }
}