package jp.co.atschool.maruhah.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.atschool.maruhah.Activity.Activity03QuestionDetail;
import jp.co.atschool.maruhah.R;

/**
 * Created by nttr on 2018/01/24.
 */

public class Adapter02History extends RecyclerView.Adapter {
    private Activity mActivity;
    private LayoutInflater layoutInflater = null;
    private ArrayList bodyList;
    private ArrayList answerList;
    private ArrayList dateList;
    private ArrayList documentKeyList;

    public Integer poss = 0; // 横軸の位置

    public Adapter02History(Activity activity, Integer position, ArrayList dates, ArrayList bodys, ArrayList answers, ArrayList document_keys) {
        this.mActivity = activity;
        this.layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        poss = position; // 横軸の位置
        dateList = dates;
        bodyList = bodys;
        answerList = answers;
        documentKeyList = document_keys;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, Activity03QuestionDetail.class);
                intent.putExtra("body", (String) bodyList.get(position));
                intent.putExtra("document_key", (String) documentKeyList.get(position));
                mActivity.startActivity(intent);
            }
        });

        viewHolder.tvHistoryDate.setText(dateList.get(position).toString());
        viewHolder.tvHistoryQuestion.setText(bodyList.get(position).toString());
        viewHolder.tvHistoryAnswer.setText(answerList.get(position).toString());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_02_history, parent, false));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bodyList.toArray().length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvHistoryDate)
        TextView tvHistoryDate;
        @BindView(R.id.tvHistoryQuestion)
        TextView tvHistoryQuestion;
        @BindView(R.id.tvHistoryAnswer)
        TextView tvHistoryAnswer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvHistoryDate = (TextView) itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryQuestion = (TextView) itemView.findViewById(R.id.tvHistoryQuestion);
            tvHistoryAnswer = (TextView) itemView.findViewById(R.id.tvHistoryAnswer);
        }
    }
}
