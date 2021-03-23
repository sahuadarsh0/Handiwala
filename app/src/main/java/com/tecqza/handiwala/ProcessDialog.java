package com.tecqza.handiwala;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

public class ProcessDialog {

    Context context;
    String title;
    Dialog processDialog;
    ImageView imageView;
    TextView tv;
    public ProcessDialog(Context context, String title){
        this.context=context;
        this.title=title;
        processDialog=new Dialog(context);
        processDialog.setContentView(R.layout.loading_dialog);
        processDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        processDialog.setCancelable(false);
        imageView=processDialog.findViewById(R.id.process);
        AnimationDrawable processAnimation;
        imageView.setBackgroundResource(R.drawable.process_style1);
        processAnimation=(AnimationDrawable) imageView.getBackground();
        processAnimation.start();
    }



    public void show(){
        processDialog.show();
    }

    public void dismiss(){
        processDialog.dismiss();
    }
}
