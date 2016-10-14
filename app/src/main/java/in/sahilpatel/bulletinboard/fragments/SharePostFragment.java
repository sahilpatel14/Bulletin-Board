package in.sahilpatel.bulletinboard.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import in.sahilpatel.bulletinboard.R;
import in.sahilpatel.bulletinboard.model.Post;
import in.sahilpatel.bulletinboard.model.User;

/**
 * Created by Administrator on 10/6/2016.
 */

public class SharePostFragment extends DialogFragment {

    private static final String TAG = "SharePostFragment";
    private Post post;
    private View view;
    private ImageView imageView;

    public void setPost(Post post) {
        this.post = post;
    }

    public void setView(View view) {
        this.view = view;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.layout_share_post, container, false);

        imageView = (ImageView)rootView.findViewById(R.id.screenshot_container);

        //imageView.setImageBitmap(bitmap);

        rootView.findViewById(R.id.button_take_screenShot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePost(screenShot(SharePostFragment.this.view));
            }
        });
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setTitle("Share Post");
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        Bitmap bitmap = screenShot(SharePostFragment.this.view);
        imageView.setImageBitmap(bitmap);
    }


    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void sharePost(Bitmap bitmap) {
        Intent intent = new Intent();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);

        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM,bos.toByteArray());
        startActivity(Intent.createChooser(intent, "share via.."));
    }
}
