package com.rae.cnblogs.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rae.cnblogs.R;
import com.rae.cnblogs.adapter.BaseItemAdapter;
import com.rae.cnblogs.adapter.SearchSuggestionAdapter;
import com.rae.cnblogs.message.SearchEvent;
import com.rae.cnblogs.presenter.CnblogsPresenterFactory;
import com.rae.cnblogs.presenter.ISearchContract;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.swift.app.RaeFragmentAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 搜索
 * Created by ChenRui on 2017/8/28 0028 14:51.
 */
public class SearchFragment extends DialogFragment implements ISearchContract.View {
    @BindView(R.id.et_search_text)
    EditText mSearchView;
    @BindView(R.id.img_edit_delete)
    ImageView mDeleteView;

    @BindView(R.id.btn_search)
    TextView mSearchButton;

    @BindView(R.id.rec_search)
    RecyclerView mRecyclerView;

    @BindView(R.id.tab_category)
    TabLayout mTabLayout;

    @BindView(R.id.vp_search)
    ViewPager mViewPager;

    SearchSuggestionAdapter mSuggestionAdapter;

    private ISearchContract.Presenter mPresenter;
    private TextWatcher mSearchTextWatcher;
    private Unbinder mBind;

    //    @Override
    protected int getLayoutId() {
        return R.layout.fm_search;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getLayoutId());
        mPresenter = CnblogsPresenterFactory.getSearchPresenter(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBind != null)
            mBind.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSuggestionAdapter = new SearchSuggestionAdapter();
        mRecyclerView.setAdapter(mSuggestionAdapter);

        RaeFragmentAdapter adapter = new RaeFragmentAdapter(getChildFragmentManager());
        adapter.add("博客", SearchBlogFragment.newInstance(BlogType.BLOG));
        adapter.add("博主", SearchBloggerFragment.newInstance());
        adapter.add("新闻", SearchBlogFragment.newInstance(BlogType.NEWS));
        adapter.add("知识库", SearchBlogFragment.newInstance(BlogType.KB));
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager);

        mSuggestionAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mSuggestionAdapter.getItemCount() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        mSuggestionAdapter.setOnItemClickListener(new BaseItemAdapter.onItemClickListener<String>() {
            @Override
            public void onItemClick(String item) {
                // 执行搜索
                mSearchView.removeTextChangedListener(mSearchTextWatcher);
                mSearchView.setText(item);
                mSearchView.setSelection(item.length());
                mSearchView.addTextChangedListener(mSearchTextWatcher);
                preformSearch();
            }

        });

        mSearchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = mSearchView.length();
                mDeleteView.setVisibility(length > 0 ? View.VISIBLE : View.INVISIBLE);
                mSearchButton.setSelected(length > 0);
                if (length > 0) {
                    mSearchButton.setText(R.string.search);
                } else {
                    mSearchButton.setText(R.string.cancel);
                }

                // 自动完成
                mPresenter.suggest();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        mSearchView.addTextChangedListener(mSearchTextWatcher);
    }

    @OnClick(R.id.rl_edit_delete)
    public void onEditDeleteClick() {
        mSearchView.setText("");
    }

    @OnClick(R.id.btn_search)
    public void onSearchClick() {
        if (mSearchButton.isSelected()) {
            // 执行搜索
            preformSearch();
        } else {
            // 退出
//            getActivity().finish();
            dismiss();
        }
    }

    /**
     * 执行搜索
     */
    private void preformSearch() {
        // 弹下键盘
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        // 取消搜索建议
        mSuggestionAdapter.clear();
        mSuggestionAdapter.notifyDataSetChanged();
        // 显示或者隐藏
        mRecyclerView.setVisibility(mSearchView.length() > 0 ? View.GONE : View.VISIBLE);

        EventBus.getDefault().post(new SearchEvent(getSearchText()));
    }

    @Override
    public String getSearchText() {
        return mSearchView.getText().toString();
    }

    @Override
    public void onSuggestionSuccess(List<String> data) {
        mSuggestionAdapter.invalidate(data);
        mSuggestionAdapter.notifyDataSetChanged();
    }
}