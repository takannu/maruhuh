package jp.co.atschool.maruhah.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import jp.co.atschool.maruhah.R;

public class LemonProgressDialog extends Dialog {

    public LemonProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);

        getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        setContentView(R.layout.layout_progress_dialog);
    }

    @Override
    public void show() {
        try{
            super.show();
        }catch (RuntimeException e){

        }
    }

    @Override
    public void dismiss() {
        try{
            super.dismiss();
        }catch (IllegalArgumentException e){

        }
    }

}
