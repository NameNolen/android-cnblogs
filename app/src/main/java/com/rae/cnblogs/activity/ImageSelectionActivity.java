package com.rae.cnblogs.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rae.cnblogs.AppUI;
import com.rae.cnblogs.GlideApp;
import com.rae.cnblogs.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 图片选择
 * Created by ChenRui on 2017/10/27 0027 14:04.
 */
public class ImageSelectionActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private ImageSelectionAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);
        showHomeAsUp();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new ImageSelectionAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageData();
    }

    /**
     * 加载系统相册的图片
     */
    private void loadImageData() {
        List<String> result = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        // 没有图片
        if (cursor == null || cursor.getCount() <= 0) return;
        while (cursor.moveToNext()) {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                result.add(path);
            }
        }
        cursor.close();

        mAdapter.setImageList(result);
        mAdapter.notifyDataSetChanged();
    }


    private static class ImageSelectionHolder extends RecyclerView.ViewHolder {
        TextView mPositionTextView;
        ImageView mImageView;
        CheckBox mCheckBox;
        View mCheckBoxLayout;

        public ImageSelectionHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_background);
            mCheckBox = itemView.findViewById(R.id.cb_checkbox);
            mCheckBoxLayout = itemView.findViewById(R.id.rl_checkbox);
            mPositionTextView = itemView.findViewById(R.id.tv_position);
        }
    }

    private static class ImageSelectionAdapter extends RecyclerView.Adapter<ImageSelectionHolder> implements View.OnClickListener {

        private final List<String> mUrls = new ArrayList<>();
        private final List<String> mSelectedList = new ArrayList<>();
        private LayoutInflater mLayoutInflater;

        public ImageSelectionAdapter() {
            super();
        }

        @Override
        public ImageSelectionHolder onCreateViewHolder(ViewGroup parent, int i) {
            if (mLayoutInflater == null) {
                mLayoutInflater = LayoutInflater.from(parent.getContext());
            }
            return new ImageSelectionHolder(mLayoutInflater.inflate(R.layout.item_image_selection, parent, false));
        }

        @Override
        public void onBindViewHolder(ImageSelectionHolder holder, int position) {
            String fileName = mUrls.get(position);
            holder.mCheckBox.setTag(position);
            holder.mCheckBoxLayout.setOnClickListener(this);
            holder.mCheckBox.setChecked(mSelectedList.contains(fileName));
            if (holder.mCheckBox.isChecked()) {
                holder.mPositionTextView.setVisibility(View.VISIBLE);
                holder.mPositionTextView.setText(String.valueOf(mSelectedList.indexOf(fileName) + 1));
            } else {
                holder.mPositionTextView.setVisibility(View.GONE);
            }
            GlideApp.with(holder.itemView).load("file://" + fileName).into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mUrls.size();
        }

        public void remove(String fileName) {
            mUrls.remove(fileName);
        }

        public void setImageList(List<String> imageList) {
            mUrls.clear();
            mUrls.addAll(imageList);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.rl_checkbox) {
                onCheckBoxClick((CompoundButton) v.findViewById(R.id.cb_checkbox));
            }
        }

        private void onCheckBoxClick(CompoundButton buttonView) {
            int position = (int) buttonView.getTag();
            String item = mUrls.get(position);
            if (buttonView.isChecked()) {
                // 不超过6张图片
                if (mSelectedList.size() >= 6) {
                    buttonView.setChecked(false);
                    AppUI.failed(buttonView.getContext(), "最多选择6张图片");
                    return;
                }
                mSelectedList.remove(item);
                mSelectedList.add(item);
            } else {
                mSelectedList.remove(item);
            }

            notifyDataSetChanged();
        }
    }
}