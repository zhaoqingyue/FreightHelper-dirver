package com.yunbaba.freighthelper.ui.fragment.task;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.customview.LazyLoadFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends LazyLoadFragment {

	// 标志位，标志已经初始化完成。
	private boolean isPrepared;
	private boolean isLoadFinish = false;
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	public String corpid;
	public String taskid;

	public static ImageDetailFragment newInstance(String imageUrl, String corpid, String taskid) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);

		args.putString("corpid", corpid);
		args.putString("taskid", taskid);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
		corpid = getArguments() != null ? getArguments().getString("corpid") : null;
		taskid = getArguments() != null ? getArguments().getString("taskid") : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_image_detail, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				if (getActivity() != null)
					getActivity().finish();
			}
		});

		progressBar = (ProgressBar) v.findViewById(R.id.loading);

		isPrepared = true;

		if (!isLoadFinish)
			progressBar.setVisibility(View.VISIBLE);

		lazyLoad();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		AppStatApi.statOnPageStart(getContext(), this);
	}

	@Override
	public void onPause() {
		super.onPause();
		AppStatApi.statOnPagePause(getContext(), this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	protected void lazyLoad() {

		if (!isPrepared || !isVisible || isLoadFinish) {
			return;
		}

		LazyLoadImage();
	}

	private void LazyLoadImage() {


		if (mImageView.getDrawable() == null && isLoadFinish != true)
			progressBar.setVisibility(View.VISIBLE);
		
		if(isLoadFinish)
			return;
		

		if (!mImageUrl.contains("http") && mImageUrl.contains(".jpg")) {

			Picasso.with(this.getContext()).load("file://" + mImageUrl) // .resize(100,
																		// 100)
																		// //.fit()
					.into(mImageView, new Callback() {

						@Override
						public void onSuccess() {

							isLoadFinish = true;

							if (getActivity() != null) {
								progressBar.setVisibility(View.GONE);
								mAttacher.update();
							}
						}

						@Override
						public void onError() {

							if (getActivity() != null) {
								Toast.makeText(getActivity(), "图片加载失败", Toast.LENGTH_SHORT).show();
								progressBar.setVisibility(View.GONE);
							}
						}
					});

		} else if (mImageUrl.contains("http") && mImageUrl.contains(".jpg")) {

			Picasso.with(this.getContext()).load(mImageUrl) // .resize(100,
					// 100)
					// //.fit()

					.into(mImageView, new Callback() {

						@Override
						public void onSuccess() {

							isLoadFinish = true;

							if (getActivity() != null) {
								progressBar.setVisibility(View.GONE);
								mAttacher.update();
							}
						}

						@Override
						public void onError() {

							if (getActivity() != null) {
								Toast.makeText(getActivity(), "图片加载失败", Toast.LENGTH_SHORT).show();
								progressBar.setVisibility(View.GONE);
							}
						}
					});

		} else {

			// MLog.e("check", DeliveryApi.getSourcepicUrl(corpid, taskid,
			// mImageUrl));
			Picasso.with(this.getContext()).load(DeliveryApi.getSourcepicUrl(corpid, taskid, mImageUrl)) // .fit()
					// //.resize(100,
					// 100).centerCrop()
					// //
					.into(mImageView, new Callback() {

						@Override
						public void onSuccess() {

							isLoadFinish = true;


							if (getActivity() != null) {
								progressBar.setVisibility(View.GONE);
								mAttacher.update();
							}
						}

						@Override
						public void onError() {


							if (getActivity() != null) {

								Toast.makeText(getActivity(), "图片加载失败", Toast.LENGTH_SHORT).show();
								progressBar.setVisibility(View.GONE);
							}
						}
					});
		}
	}
}
