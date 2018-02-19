package jp.co.atschool.maruhah.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import butterknife.BindView;
import jp.co.atschool.maruhah.Models.ModelUserList;
import jp.co.atschool.maruhah.Network.NetworkUser;
import jp.co.atschool.maruhah.Network.NetworkUserUtilListener;
import jp.co.atschool.maruhah.R;
import jp.co.atschool.maruhah.Utils.LemonProgressDialog;
import jp.co.atschool.maruhah.configs.Define;

public class Activity05UserSearch extends AppCompatActivity {

    @BindView(R.id.myListView)
    ListView myListView;
    @BindView(R.id.mySearchView)
    SearchView mySearchView;

    private LemonProgressDialog progressDialog;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_05_user_search);

        mActivity = this;

        // インジゲータ
        progressDialog = new LemonProgressDialog(this);

        // searchBar
        mySearchView = (SearchView) findViewById(R.id.mySearchView);
        // SearchViewの初期表示状態を設定
        mySearchView.setIconifiedByDefault(false);
        // フォーカスをデフォルトで当てるかどうか
//        mySearchView.setIconified(false);
        // Submitボタンの表示設定
        mySearchView.setQueryHint("スクリーンネームを入力(@以降のアルファベット)");
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String screen_name) {
                // キーボードを閉じる
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(null != inputMethodManager){
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

                if ( getApplicationContext() != null ) {
                    myListView = (ListView) findViewById(R.id.myListView);
                    myListView.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, twitter_screen_names));

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
