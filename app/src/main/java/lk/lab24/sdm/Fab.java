package lk.lab24.sdm;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class Fab {
    private Context context;
    FloatingActionButton fab,resume,add;
    boolean isExpand;




    public Fab(Context context, final FloatingActionButton fab, final FloatingActionButton resume, final FloatingActionButton add){
        this.fab = fab;
        this.resume= resume;
        this.add = add;
        isExpand = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resume.setTooltipText("Downldo Resume");
            add.setTooltipText("New Downlod");
        }

        final Animation animationOpen = AnimationUtils.loadAnimation(context, R.anim.fabopen);
        final Animation animationClose = AnimationUtils.loadAnimation(context, R.anim.fabclose);

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if(isExpand == false){
                    isExpand = true;
                    resume.startAnimation(animationOpen);
                    add.startAnimation(animationOpen);
                    resume.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    fab.animate().rotation(180);

                }else{
                    isExpand = false;
                    resume.startAnimation(animationClose);
                    add.startAnimation(animationClose);
                    resume.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    fab.animate().rotation(0);




                }
            }
        });







    }




}




