package com.hhp227.knu_minigroup.adapter;

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
import com.hhp227.knu_minigroup.dto.GroupItem;

import java.util.List;

public class GroupListAdapter extends BaseAdapter {
    private static final int NAME_MAX_LINE = 2;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> groupItemKeys;
    private List<GroupItem> groupItemValues;
    private ViewHolder viewHolder;

    public GroupListAdapter(Context context, List<String> groupItemKeys, List<GroupItem> groupItemValues) {
        this.context = context;
        this.groupItemKeys = groupItemKeys;
        this.groupItemValues = groupItemValues;
    }

    @Override
    public int getCount() {
        return groupItemValues.size();
    }

    @Override
    public Object getItem(int position) {
        return groupItemValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.group_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        ImageView groupImage = convertView.findViewById(R.id.iv_group_image);
        GroupItem groupItem = groupItemValues.get(position);

        viewHolder.groupName.setText(groupItem.getName());
        viewHolder.groupName.setMaxLines(NAME_MAX_LINE);
        viewHolder.groupInfo.setText(groupItem.getJoinType().equals("0") ? "가입방식: 자동 승인" : "가입방식: 운영자 승인 확인");

        Glide.with(context).load(groupItem.getImage()).apply(RequestOptions.errorOf(R.drawable.bg_no_image)).into(groupImage);

        return convertView;
    }

    public String getKey(int position) {
        return groupItemKeys.get(position);
    }

    private static class ViewHolder {
        private TextView groupName, groupInfo;

        ViewHolder(View itemView) {
            groupName = itemView.findViewById(R.id.tv_group_name);
            groupInfo = itemView.findViewById(R.id.tv_info);
        }
    }
}
