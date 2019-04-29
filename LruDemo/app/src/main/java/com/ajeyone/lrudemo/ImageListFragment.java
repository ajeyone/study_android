package com.ajeyone.lrudemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ajeyone.lrudemo.data.DiskCacheConfig;
import com.ajeyone.lrudemo.data.MemoryCacheConfig;
import com.ajeyone.lrudemo.data.OnlineImageStore;
import com.ajeyone.lrudemo.utils.AppInfo;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ImageListFragment extends Fragment {
    private static final String TAG = "ajeyonelru";

    private OnListFragmentInteractionListener mListener;

    private ImageCache mLruCache;
    private DiskLruCache mDiskLruCache;
    private MyImageListRecyclerViewAdapter mAdapter;

    public ImageListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imagelist_list, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MyImageListRecyclerViewAdapter(OnlineImageStore.IMAGES, mListener, mLruCache, mDiskLruCache);
            recyclerView.setAdapter(mAdapter);
        }
        setHasOptionsMenu(true);
        return view;
    }

    private static class ImageCache extends LruCache<String, Bitmap> {
        public ImageCache(int maxSizeKB) {
            super(maxSizeKB);
        }

        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount() / 1024;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            int version = AppInfo.getAppVersion(context);
            File dir = DiskCacheConfig.getImageCacheDir(context);
            mDiskLruCache = DiskLruCache.open(dir, version, 1, 360 * 1024 * 1024); // 360M
        } catch (IOException e) {
            e.printStackTrace();
        }

        mLruCache = new ImageCache((int)(MemoryCacheConfig.getCacheMaxSize() / 1024));

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.image_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_cache) {
            try {
                long start = System.currentTimeMillis();
                mDiskLruCache.delete();
                long duration = System.currentTimeMillis() - start;
                Toast.makeText(getContext(), "Cache cleared, used " + duration + "ms", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onOptionsItemSelected: ", e);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mAdapter != null) {
            mAdapter.destroy();
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String imageUrl);
    }
}
