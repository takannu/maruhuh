package jp.co.atschool.maruhah.Network;

import java.util.ArrayList;

import jp.co.atschool.maruhah.Models.ModelUserList;

/**
 * Created by nttr on 2018/01/29.
 */

public interface NetworkUserUtilListener {
    void OnSuccess(ArrayList<ModelUserList> arrayList);
}

