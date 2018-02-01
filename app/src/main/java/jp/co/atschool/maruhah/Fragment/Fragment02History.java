package jp.co.atschool.maruhah.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.atschool.maruhah.Adapter.Adapter02History;
import jp.co.atschool.maruhah.Models.ModelQuestionList;
import jp.co.atschool.maruhah.Network.NetworkQuestion;
import jp.co.atschool.maruhah.Network.NetworkUtilListener;
import jp.co.atschool.maruhah.R;

public class Fragment02History extends Fragment {

    @BindView(R.id.tlHistory) TabLayout tlHistory;
    @BindView(R.id.vpHistory) ViewPager vpHistory;

    private Activity mActivity;
    private static int mCurrent; // 横単位のposition

    // ライフサイクル1回目に呼び出される。
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // MainActivityを呼び出し、セットする。
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // このクラスをMainActivityにセットする(多分)
        View view = inflater.inflate(R.layout.fragment_02_history, container, false);
        ButterKnife.bind(this, view);

        tlHistory = (TabLayout) view.findViewById(R.id.tlHistory);
        vpHistory = (ViewPager) view.findViewById(R.id.vpHistory);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // クラスをMainActivityにセットする(多分)
        super.onViewCreated(view, savedInstanceState);

        setTabs();
        setPager();
    }

    private void setTabs() {
        tlHistory.removeAllTabs();
        for (int i = 0; i < 2; i++) {
            switch (i) {
                case 0:
                    tlHistory.addTab(tlHistory.newTab().setText("未返信"));
                    break;
                case 1:
                    tlHistory.addTab(tlHistory.newTab().setText("返信済"));
                    break;
                default:
                    break;
            }
        }
    }

    private void setPager() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        HistoryPagerAdapter adapter = new HistoryPagerAdapter(manager);
        for(int i = 0; i< 2; i++){
            adapter.fragments.add(HistoryPagerFragment.newInstance(i));
        }
        vpHistory.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrent = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vpHistory.setOffscreenPageLimit(2);
        vpHistory.setAdapter(adapter);
        tlHistory.setupWithViewPager(vpHistory);
    }

    public static class HistoryPagerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

        @BindView(R.id.rvHistory) RecyclerView rvHistory;
        private Adapter02History adapter;

        Activity mActivity;
        private LinearLayoutManager linearLayoutManager;
        private SwipeRefreshLayout mSwipeRefreshLayout; // 上に引っ張った時のくるくる

        private final static String ARG_POSITION = "ARG_POSITION";
        public int pos = 0;

        private ArrayList<String> document_keys = new ArrayList<String>();
        private ArrayList<Date> dates = new ArrayList<>();
        private ArrayList<String> bodys = new ArrayList<String>();
        private ArrayList<String> answers = new ArrayList<String>();

        public static HistoryPagerFragment newInstance(int position) {
            HistoryPagerFragment frag = new HistoryPagerFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            frag.pos = position;
            frag.setArguments(b);
            return frag;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
        }

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_02_history_pager, null);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // SwipeRefreshLayoutを作成
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(this);

            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            rvHistory = (RecyclerView) view.findViewById(R.id.rvHistory);
            rvHistory.setLayoutManager(linearLayoutManager);
            rvHistory.setNestedScrollingEnabled(false);

            reloadView();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        public void reloadView() {
            NetworkQuestion.getFirebaseQuestionList("tyano0930" ,new NetworkUtilListener() {
                @Override
                public void OnSuccess(ArrayList<ModelQuestionList> arrayList) {

                    dates = new ArrayList<Date>();
                    bodys = new ArrayList<String>();
                    document_keys = new ArrayList<String>();

                    for (int i = 0; i < arrayList.size(); i++) {
                        dates.add(arrayList.get(i).getCreated_date());
                        bodys.add(arrayList.get(i).getBody());
                        document_keys.add(arrayList.get(i).getDocument_key());
                    }

                    // Flagmentを高速で切り替えると強制終了してしまう対策で、getActivity()のnullチェックを行う。
                    if ( getActivity() != null ) {
                        adapter = new Adapter02History(getActivity(), pos, dates, bodys, document_keys);
                        rvHistory.setAdapter(adapter);
                    }

                    // 上に引っ張った場合、くるくるを止める
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            /*
            ApiUtilRanking.getApiAppQaOdekake_ranking(3, pos+1, new ApiUtilListener() {

                @Override
                public void OnWSError(String error) {
                    progressDialog.dismiss();
                    // 上に引っ張った場合、くるくるを止める
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void OnSuccess(String result) {

                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray rankingArray = json.getJSONObject("data").getJSONArray("list");

                        // reloadするときは配列を初期化する。
                        contents = new ArrayList<String>();
                        comment_counts = new ArrayList<Integer>();
                        favorite_couonts = new ArrayList<Integer>();
                        dates = new ArrayList<String>();
                        question_ids = new ArrayList<Integer>();



                        for (int i=0;i<rankingArray.length();i++){
                            contents.add(rankingArray.getJSONObject(i).getString("title") + rankingArray.getJSONObject(i).getString("description"));
                            comment_counts.add(rankingArray.getJSONObject(i).getInt("num_of_answers"));
                            favorite_couonts.add(rankingArray.getJSONObject(i).getInt("num_of_favorite"));
                            dates.add(rankingArray.getJSONObject(i).getString("pub_date"));
                            question_ids.add(rankingArray.getJSONObject(i).getInt("question_id"));
                        }

                        // Flagmentを高速で切り替えると強制終了してしまう対策で、getActivity()のnullチェックを行う。
                        if ( getActivity() != null ) {
                            adapter = new Adapter04Ranking(getActivity(), pos, question_ids, contents, comment_counts, favorite_couonts, dates);
                            rvRanking.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    // 上に引っ張った場合、くるくるを止める
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            */
        }

        /**
         * 引っ張った時の処理
         */
        @Override
        public void onRefresh() {
            // 引っ張ったタイミングでここに入る
            this.reloadView();
        }
    }

    static class HistoryPagerAdapter extends FragmentStatePagerAdapter {
        public ArrayList<HistoryPagerFragment> fragments = new ArrayList<>();
        public HistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "未返信";
                case 1:
                    return "返信済";
                default:
                    return "???";
            }

        }
    }
}
