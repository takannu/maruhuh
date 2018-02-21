package jp.co.atschool.maruhah.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.atschool.maruhah.Activity.Activity04OtherTop;
import jp.co.atschool.maruhah.Models.ModelUserList;
import jp.co.atschool.maruhah.Network.NetworkUser;
import jp.co.atschool.maruhah.Network.NetworkUserUtilListener;
import jp.co.atschool.maruhah.R;
import jp.co.atschool.maruhah.Utils.LemonProgressDialog;
import jp.co.atschool.maruhah.configs.Define;

public class Fragment03Search extends Fragment {
    @BindView(R.id.myListView)
    ListView myListView;
    @BindView(R.id.mySearchView)
    SearchView mySearchView;
    @BindView(R.id.ivMyProfile)
    ImageView ivMyProfile;

    private LemonProgressDialog progressDialog;
    private Activity mActivity;

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
        final View view = inflater.inflate(R.layout.fragment_03_search, container, false);
        ButterKnife.bind(this, view);

        // インジゲータ
        progressDialog = new LemonProgressDialog(this.getContext());

        myListView = (ListView) view.findViewById(R.id.myListView);

        // searchBar
        mySearchView = (SearchView) view.findViewById(R.id.mySearchView);
        // SearchViewの初期表示状態を設定
        mySearchView.setIconifiedByDefault(false);
        // フォーカスをデフォルトで当てるかどうか
//        mySearchView.setIconified(false);
        // Submitボタンの表示設定
        mySearchView.setQueryHint("@以降のアルファベット名を入力");
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String screen_name) {
                // キーボードを閉じる
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(null != inputMethodManager){
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // 検索結果へ画面遷移
                reloadView(Define.ReloadType.Overall, screen_name);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // クラスをMainActivityにセットする(多分)
        super.onViewCreated(view, savedInstanceState);
    }

    private void reloadView(Define.ReloadType refreshType, String screen_name) {

        if (refreshType == Define.ReloadType.Overall) {
            progressDialog.show();
        }

        NetworkUser.getFirebaseUserList(screen_name,new NetworkUserUtilListener() {
            @Override
            public void OnSuccess(ArrayList<ModelUserList> arrayList) {


                final ArrayList<String> twitter_ids = new ArrayList<String>();
                final ArrayList<String> twitter_screen_names = new ArrayList<String>();

                for (int i = 0; i < arrayList.size(); i++) {
                    twitter_ids.add(arrayList.get(i).getTwitter_id());
                    twitter_screen_names.add(arrayList.get(i).getTwitter_screen_name());
                }

                if ( getActivity() != null ) {
                    myListView.setAdapter(new ArrayAdapter(getActivity(), R.layout.item_03_search, R.id.tvUserName,twitter_screen_names));

                    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // 検索結果へ画面遷移
                            showSearchDetail(twitter_ids.get(position));
                        }
                    });
                }
                progressDialog.dismiss();
            }
        });
    }

    private void showSearchDetail(String twitter_id) {
        Intent intent = new Intent(mActivity.getApplicationContext(), Activity04OtherTop.class);

        intent.putExtra("twitter_id", twitter_id);
        startActivity(intent);
    }

}
