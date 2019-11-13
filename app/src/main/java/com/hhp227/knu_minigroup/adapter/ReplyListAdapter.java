package com.hhp227.knu_minigroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.dto.ReplyItem;
import com.hhp227.knu_minigroup.volley.util.CircularNetworkImageView;

import java.util.List;

public class ReplyListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ReplyItem> replyItems;
    private String date;
    ImageLoader imageLoader = app.AppController.getInstance().getImageLoader();

    public ReplyListAdapter(Activity activity, List<ReplyItem> replyItems) {
        this.activity = activity;
        this.replyItems = replyItems;
    }

    @Override
    public int getCount() {
        return replyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return replyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.reply_item, null);
        if (imageLoader == null)
            imageLoader = app.AppController.getInstance().getImageLoader();

        CircularNetworkImageView profileImage = convertView.findViewById(R.id.cniv_profile_image);
        TextView name = convertView.findViewById(R.id.tv_name);
        TextView reply = convertView.findViewById(R.id.tv_reply);
        TextView timeStamp = convertView.findViewById(R.id.tv_timestamp);

        // 댓글 데이터 얻기
        ReplyItem replyItem = replyItems.get(position);

        profileImage.setImageUrl(null, imageLoader);
        profileImage.setDefaultImageResId(R.drawable.profile_img_circle);
        profileImage.setErrorImageResId(R.drawable.profile_img_circle);
        name.setText(replyItem.getName());
        reply.setText(replyItem.getReply());
        timeStamp.setText(replyItem.getTimestamp());

        return convertView;
    }
}