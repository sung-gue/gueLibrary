package com.breakout.sample.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.breakout.sample.Log;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * get android adid
 *
 * @author sung-gue
 * @version 1.0 (2015-03-08)
 */
public class GetAdidTask extends AsyncTask<Void, Void, String> {
    public interface OnFinishGetAdidListener {
        void OnFinishGetAdid(String adid);
    }

    private String TAG = getClass().getSimpleName();

    private WeakReference<Context> _context;

    private OnFinishGetAdidListener _listener;

    public GetAdidTask(Context context, OnFinishGetAdidListener listener) {
        _context = new WeakReference<>(context);
        _listener = listener;
    }

    protected String doInBackground(final Void... params) {
        String adid = null;
        try {
            Context context = _context.get();
            adid = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return adid;
    }

    protected void onPostExecute(final String adid) {
        _listener.OnFinishGetAdid(adid);
    }
}