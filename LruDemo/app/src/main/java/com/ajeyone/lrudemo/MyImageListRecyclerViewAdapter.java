package com.ajeyone.lrudemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajeyone.lrudemo.ImageListFragment.OnListFragmentInteractionListener;
import com.ajeyone.lrudemo.utils.MD5;
import com.ajeyone.lrudemo.utils.NoLeakAsyncTask;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.recyclerview.widget.RecyclerView;

public class MyImageListRecyclerViewAdapter extends RecyclerView.Adapter<MyImageListRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ajeyonelru";

    private LruCache<String, Bitmap> mLruCache;
    private DiskLruCache mDiskLruCache;

    private HashSet<ImageWorkTask> mTasks = new HashSet<>();

    private final List<String> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyImageListRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener, LruCache<String, Bitmap> lruCache, DiskLruCache diskLruCache) {
        mValues = items;
        mListener = listener;
        mLruCache = lruCache;
        mDiskLruCache = diskLruCache;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_imagelist, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mImageUrl);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mImageUrl = mValues.get(position);
        Log.d(TAG, "onBindViewHolder: [" + position + "], " + holder.mImageUrl);
        loadImageToView(holder.mImageUrl, holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private static ExecutorService sThreadPool = Executors.newFixedThreadPool(5);

    private void loadImageToView(String url, ImageView imageView) {
        Log.d(TAG, "loadImageToView: start executing task for " + url);
        imageView.setTag(url);

        Bitmap bitmap = mLruCache.get(url);
        if (bitmap != null) {
            Log.d(TAG, "loadImageToView: memory cache hits for " + url);
            imageView.setImageBitmap(bitmap);
        } else {
            Log.d(TAG, "loadImageToView: NO memory cache hits for " + url);
            imageView.setImageResource(R.drawable.placeholder);
            ImageWorkTask task = new ImageWorkTask(url, imageView);
            task.executeOnExecutor(sThreadPool);
            mTasks.add(task);
        }
    }

    public void destroy() {
        for (ImageWorkTask task : mTasks) {
            task.cancel(true);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mDescriptionTextView;
        public String mImageUrl;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.image);
            mDescriptionTextView = view.findViewById(R.id.description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionTextView.getText() + "'";
        }
    }

    private class ImageWorkTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private WeakReference<ImageView> imageViewRef;

        public ImageWorkTask(String url, ImageView imageView) {
            this.url = url;
            imageViewRef = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            DiskLruCache.Snapshot snapshot = getDiskCacheSnapshot();
            if (snapshot == null) {
                Log.d(TAG, "doInBackground: NO disk cache hits for " + url);
                downloadImageToDisk();
                snapshot = getDiskCacheSnapshot();
            } else {
                Log.d(TAG, "doInBackground: disk cache hits for " + url);
            }
            if (snapshot != null) {
                return loadImageFromSnapshot(snapshot);
            }
            return null;
        }

        private Bitmap loadImageFromSnapshot(DiskLruCache.Snapshot snapshot) {
            Log.d(TAG, "loadImageFromSnapshot: load disk image for " + url);
            try (InputStream input = new BufferedInputStream(snapshot.getInputStream(0))) {
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private DiskLruCache.Snapshot getDiskCacheSnapshot() {
            try {
                return mDiskLruCache.get(MD5.md5(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void downloadImageToDisk() {
            try {
                Log.d(TAG, "downloadImageToDisk: " + url);
                String key = MD5.md5(this.url);
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                Log.d(TAG, "downloadImageToDisk: editor ok: " + (editor != null) + ", " + url);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (downloadToOutputStream(outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private HttpURLConnection newConnection() throws IOException {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(0);
            connection.setConnectTimeout(3000);
            return connection;
        }

        private boolean downloadToOutputStream(OutputStream outputStream) {
            Log.d(TAG, "downloadToOutputStream: " + url);
            HttpURLConnection connection = null;
            InputStream input = null;
            try {
                connection = newConnection();
                input = connection.getInputStream();
                Bitmap bitmap = getBitmapFromInputStream(input);
                if (bitmap != null) {
                    mLruCache.put(url, bitmap);
                    outputStream = new BufferedOutputStream(outputStream);
                    boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    Log.d(TAG, "downloadToOutputStream: download result: " + result + ", " + url);
                    return result;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: " + e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: " + e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "downloadToOutputStream: download failed: " + url);
            return false;
        }

        private Bitmap getBitmapFromInputStream(InputStream input) {
            Bitmap bitmap = null;
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = input.read(buffer)) >= 0) {
                    output.write(buffer, 0, len);
                }
                byte[] array = output.toByteArray();
                int scale = calculateScaleOfImage(array, 240 * 2, 240 * 2);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = scale;
                bitmap = BitmapFactory.decodeByteArray(array, 0, array.length, option);
                Log.d(TAG, "calculateScaleOfImage: sampled size: (" + bitmap.getWidth() + "," + bitmap.getHeight() + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        private int calculateScaleOfImage(byte[] array, int maxWidth, int maxHeight) {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(array, 0, array.length, option);
            Log.d(TAG, "calculateScaleOfImage: original size: (" + option.outWidth + "," + option.outHeight + ")");
            int xScale = Math.round((float) option.outWidth / maxWidth);
            int yScale = Math.round((float) option.outHeight / maxHeight);
            return Math.max(xScale, yScale);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "onPostExecute: " + url);
            ImageView imageView = imageViewRef.get();
            if (imageView != null && url.equals(imageView.getTag())) {
                imageView.setImageBitmap(bitmap);
            }
            mTasks.remove(this);
        }
    }
}
