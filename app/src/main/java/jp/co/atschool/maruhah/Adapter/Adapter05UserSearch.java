package jp.co.atschool.maruhah.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.atschool.maruhah.Activity.Activity04OtherTop;
import jp.co.atschool.maruhah.R;

/**
 * Created by nttr on 2018/01/24.
 */

public class Adapter05UserSearch extends RecyclerView.Adapter {
    private Activity mActivity;
    private LayoutInflater layoutInflater = null;
    private ArrayList twitterIdList;
    private ArrayList twitterScreenNameList;

    public Adapter05UserSearch(Activity activity, ArrayList twitter_ids, ArrayList twitter_screen_names) {
        this.mActivity = activity;
        this.layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        twitterIdList = twitter_ids;
        twitterScreenNameList = twitter_screen_names;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, Activity04OtherTop.class);
                intent.putExtra("twitter_id", (String) twitterIdList.get(position));
                intent.putExtra("twitter_screen_name", (String) twitterScreenNameList.get(position));
                mActivity.startActivity(intent);
            }
        });
        viewHolder.tvScreenName.setText(twitterScreenNameList.get(position).toString());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_05_user_search, parent, false));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return twitterScreenNameList.toArray().length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvScreenName) TextView tvScreenName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvScreenName.setTextColor(Color.BLACK);
        }
    }
}
