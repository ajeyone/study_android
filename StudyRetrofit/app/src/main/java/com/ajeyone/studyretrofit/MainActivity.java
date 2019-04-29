package com.ajeyone.studyretrofit;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajeyone.studyretrofit.data.BaiduPOIBean;
import com.ajeyone.studyretrofit.data.BaiduPOISearchResponse;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ajeyoneRetrofit";

    public static final String BAIDU_MAP_BASE_URL = "http://api.map.baidu.com/place/v2/";

    private Retrofit mRetrofit;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    @BindView(R.id.float_image)
    ImageView mFloatImage;

    private BaiduSearchResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAdapter = new BaiduSearchResultAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BAIDU_MAP_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_call) {
            mAdapter.setPOIBeans(new ArrayList<>());
            loadDataUsingCall();
        } else if (id == R.id.action_rxjava) {
            mAdapter.setPOIBeans(new ArrayList<>());
            loadDataUsingObservable();
        } else {
            return false;
        }
        return true;
    }

    private void loadDataUsingObservable() {
        BaiduMapService service = mRetrofit.create(BaiduMapService.class);
        service.searchPOI_Observable("洗手间", "39.915,116.404", 1000, "json", BuildConfig.BAIDU_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaiduPOISearchResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaiduPOISearchResponse resp) {
                        if (resp != null && resp.results != null) {
                            mAdapter.setPOIBeans(resp.results);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadDataUsingCall() {
        //http://api.map.baidu.com/place/v2/search?query=银行&location=39.915,116.404&radius=2000&output=json&ak=密钥
        BaiduMapService service = mRetrofit.create(BaiduMapService.class);
        Call<BaiduPOISearchResponse> call = service.searchPOI_Call("洗手间", "39.915,116.404", 1000, "json", BuildConfig.BAIDU_API_KEY);
        call.enqueue(new Callback<BaiduPOISearchResponse>() {
            @Override
            public void onResponse(Call<BaiduPOISearchResponse> call, Response<BaiduPOISearchResponse> resp) {
                BaiduPOISearchResponse response = resp.body();
                if (response != null && response.results != null) {
                    mAdapter.setPOIBeans(response.results);
                }
            }

            @Override
            public void onFailure(Call<BaiduPOISearchResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @OnClick(R.id.float_image)
    void onFloatImageClicked() {
        mFloatImage.setVisibility(View.INVISIBLE);
    }

    private void onEventFromList(BaiduPOIBean data) {
        mFloatImage.setVisibility(View.VISIBLE);
        String url = buildStaticMapImageUrl(data, true);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .into(mFloatImage);
    }

    private static String buildStaticMapImageUrl(BaiduPOIBean bean, boolean bigOrSmall) {
        int width = bigOrSmall ? 720 : 120;
        int height = bigOrSmall ? 720 : 120;
        return String.format(Locale.ENGLISH, "http://api.map.baidu.com/staticimage/v2?ak=%s&mcode=666666&center=%f,%f&width=%d&height=%d&zoom=18",
                BuildConfig.BAIDU_API_KEY, bean.location.lng, bean.location.lat, width, height);
    }

    private static class BaiduSearchResultAdapter extends RecyclerView.Adapter<POIViewHolder> {
        private WeakReference<MainActivity> weakActivity;

        public BaiduSearchResultAdapter(MainActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        private List<BaiduPOIBean> beans;

        public void setPOIBeans(List<BaiduPOIBean> beans) {
            this.beans = beans;
            notifyDataSetChanged();
        }

        private void sendDataToActivity(BaiduPOIBean data) {
            MainActivity activity = weakActivity.get();
            if (activity != null) {
                activity.onEventFromList(data);
            }
        }

        @NonNull
        @Override
        public POIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poi, parent, false);
            POIViewHolder holder = new POIViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataToActivity(holder.data);
                }
            });
            return holder;
        }

        private PorterDuffColorFilter colorFilterEnabled = new PorterDuffColorFilter(0xffffa500, PorterDuff.Mode.SRC_ATOP);
        private PorterDuffColorFilter colorFilterDisabled = new PorterDuffColorFilter(0xff666666, PorterDuff.Mode.SRC_ATOP);

        @Override
        public void onBindViewHolder(@NonNull POIViewHolder holder, int position) {
            BaiduPOIBean bean = beans.get(position);
            holder.data = bean;
            holder.nameTextView.setText(bean.name);
            holder.addressTextView.setText(bean.address);
            holder.streetButton.setColorFilter(bean.street_id != null ? colorFilterEnabled : colorFilterDisabled);

            String url = buildStaticMapImageUrl(bean, false);
            Glide.with(holder.itemView)
                    .asBitmap()
                    .load(url)
                    .placeholder(R.drawable.map)
                    .into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return beans != null ? beans.size() : 0;
        }
    }


    static class POIViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        @BindView(R.id.name)
        TextView nameTextView;
        @BindView(R.id.address)
        TextView addressTextView;
        @BindView(R.id.button_street)
        ImageView streetButton;

        BaiduPOIBean data;

        public POIViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
