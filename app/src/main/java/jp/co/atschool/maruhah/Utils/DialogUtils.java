package jp.co.atschool.maruhah.Utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by nttr on 2018/01/22.
 */

public class DialogUtils {
    // メッセージだけ出す簡易的なダイアログ
    public static void generalDialog(Activity activity, String message, String positiveText, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveText, positiveListener);

        alertDialog.create().show();
    }
}
