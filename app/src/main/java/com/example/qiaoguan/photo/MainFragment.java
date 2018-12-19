package com.example.qiaoguan.photo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;
import com.example.qiaoguan.photo.BaseView.BaseMainFragment;
import com.example.qiaoguan.photo.Http.Network;
import com.example.qiaoguan.photo.Http.Response.GetPhotosResponse;
import com.example.qiaoguan.photo.Utils.MyLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends BaseMainFragment {
    private static final String TAG = "MainFragment";
    @BindView(R.id.photo_recycle)
    RecyclerView photoRecycle;

    private PhotoAdapter mAdapter;
    private List<PhotoItem> items = new ArrayList<>();
    private ThumbnailDownLoader<BaseViewHolder> mThumbnailDownloader;
    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        photoRecycle.setLayoutManager(new GridLayoutManager(getContext(),2));
        initDownloader();
        getPhotoItems();
        return view;
    }

    private void initDownloader() {
        mThumbnailDownloader = new ThumbnailDownLoader<>();
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        MyLog.i(TAG, "Background thread started");
    }

    private void bindAdapter() {
        if (isAdded()) {
            mAdapter = new PhotoAdapter(items,mCompositeDisposable);
            mAdapter.bindToRecyclerView(photoRecycle);
        }
    }

    private void getPhotoItems() {
        Network.getPhotoApis().getPhtots(100).enqueue(new Callback<GetPhotosResponse>() {
            @Override
            public void onResponse(Call<GetPhotosResponse> call, Response<GetPhotosResponse> response) {
                GetPhotosResponse returnResponse = response.body();
                if (returnResponse == null){
                    MyLog.i(TAG, "error response = null");
                    return;
                }
                if (!returnResponse.error) {
                    items = returnResponse.results;
                    bindAdapter();
                }else
                    MyLog.i(TAG, "error: unknow");
            }

            @Override
            public void onFailure(Call<GetPhotosResponse> call, Throwable t) {
                MyLog.i(TAG, "error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        mCompositeDisposable.clear();
    }
}
