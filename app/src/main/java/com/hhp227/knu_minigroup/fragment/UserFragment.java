package com.hhp227.knu_minigroup.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hhp227.knu_minigroup.R;

public class UserFragment extends DialogFragment {
    private Button send, close;
    private ImageView profileImage;
    private String name, image, value;
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
            image = bundle.getString("image");
            value = bundle.getString("value");
        }

        Glide.with(getActivity()).load(image).apply(RequestOptions.errorOf(R.drawable.profile_img_circle).circleCrop()).into(profileImage);
        userName.setText(name);
        send.setText("메시지 보내기");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "준비중입니다.", Toast.LENGTH_LONG).show();
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
