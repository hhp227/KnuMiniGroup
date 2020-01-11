package com.hhp227.knu_minigroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.app.EndPoint;
import com.hhp227.knu_minigroup.dto.ReplyItem;

import java.util.List;

public class ReplyListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<String> replyItemKeys;
    private List<ReplyItem> replyItemValues;

    public ReplyListAdapter(Activity activity, List<String> replyItemKeys, List<ReplyItem> replyItemValues) {
        this.activity = activity;
        this.replyItemKeys = replyItemKeys;
        this.replyItemValues = replyItemValues;
    }

    @Override
    public int getCount() {
        return replyItemValues.size();
    }

    @Override
    public Object getItem(int position) {
        return replyItemValues.get(position);
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

        ImageView profileImage = convertView.findViewById(R.id.iv_profile_image);
        TextView name = convertView.findViewById(R.id.tv_name);
        TextView reply = convertView.findViewById(R.id.tv_reply);
        TextView timeStamp = convertView.findViewById(R.id.tv_timestamp);

        // 댓글 데이터 얻기
        ReplyItem replyItem = replyItemValues.get(position);

        Glide.with(activity)
                .load(replyItem.getUid() != null ? EndPoint.USER_IMAGE.replace("{UID}", replyItem.getUid()) : null)
                .apply(new RequestOptions().circleCrop().error(R.drawable.profile_img_circle))
                .into(profileImage);
        name.setText(replyItem.getName());
        reply.setText(replyItem.getReply());
        timeStamp.setText(replyItem.getDate());

        return convertView;
    }

    public String getKey(int position) {
        return replyItemKeys.get(position);
    }
}
