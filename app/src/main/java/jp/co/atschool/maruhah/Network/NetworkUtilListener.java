package jp.co.atschool.maruhah.Network;

import java.util.ArrayList;

import jp.co.atschool.maruhah.Models.ModelQuestionList;

/**
 * Created by nttr on 2018/01/29.
 */

public interface NetworkUtilListener {
    void OnSuccess(ArrayList<ModelQuestionList> arrayList);
}
