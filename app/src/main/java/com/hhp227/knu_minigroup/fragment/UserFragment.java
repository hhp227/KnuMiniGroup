package com.hhp227.knu_minigroup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hhp227.knu_minigroup.ChatActivity;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.app.EndPoint;

public class UserFragment extends DialogFragment {
    private Button send, close;
    private ImageView profileImage;
    private String name, imageId, value;
    private TextView userName;

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        profileImage = rootView.findViewById(R.id.iv_profile_image);
        userName = rootView.findViewById(R.id.tv_name);
        send = rootView.findViewById(R.id.b_send);
        close = rootView.findViewById(R.id.b_close);

        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            imageId = bundle.getString("image");
            value = bundle.getString("value");
        }
        Glide.with(getActivity()).load(EndPoint.USER_IMAGE.replace("{IMAGE_ID}", imageId)).apply(RequestOptions.errorOf(R.drawable.profile_img_circle).circleCrop()).into(profileImage);
        userName.setText(name);
        send.setText("메시지 보내기");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("image", imageId);
                startActivity(intent);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFragment.this.dismiss();
            }
        });
        return rootView;
    }
}