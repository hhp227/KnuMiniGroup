package com.hhp227.knu_minigroup.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.database.*;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.dto.GroupItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupGridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> groupItemKeys;
    private List<GroupItem> groupItemValues;
    private ViewHolder viewHolder;

    public GroupGridAdapter(Context context, List<String> groupItemKeys, List<GroupItem> groupItemValues) {
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
            convertView = layoutInflater.inflate(R.layout.group_grid_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        GroupItem groupItem = groupItemValues.get(position);

        if (!groupItem.isAd()) {
            viewHolder.groupLayout.setVisibility(View.VISIBLE);
            viewHolder.adView.setVisibility(View.GONE);
        } else {
            AdLoader.Builder builder = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110");
            final View finalConvertView = convertView;
            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    MediaView mediaView = finalConvertView.findViewById(R.id.ad_media);
                    TextView headlineView = finalConvertView.findViewById(R.id.ad_headline);
                    RatingBar starRating = finalConvertView.findViewById(R.id.ad_stars);
                    TextView bodyView = finalConvertView.findViewById(R.id.ad_body);
                    TextView advertiser = finalConvertView.findViewById(R.id.ad_advertiser);
                    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                    viewHolder.adView.setMediaView(mediaView);
                    viewHolder.adView.setHeadlineView(headlineView);
                    viewHolder.adView.setBodyView(bodyView);
                    viewHolder.adView.setStarRatingView(starRating);
                    viewHolder.adView.setAdvertiserView(advertiser);
                    headlineView.setText(unifiedNativeAd.getHeadline());
                    viewHolder.adView.getMediaView().setMediaContent(unifiedNativeAd.getMediaContent());
                    if (unifiedNativeAd.getBody() != null) {
                        bodyView.setText(unifiedNativeAd.getBody());
                        viewHolder.adView.getBodyView().setVisibility(View.VISIBLE);
                    } else
                        viewHolder.adView.getBodyView().setVisibility(View.INVISIBLE);
                    if (unifiedNativeAd.getStarRating() != null) {
                        starRating.setRating(unifiedNativeAd.getStarRating().floatValue());
                        viewHolder.adView.getStarRatingView().setVisibility(View.VISIBLE);
                    } else
                        viewHolder.adView.getStarRatingView().setVisibility(View.INVISIBLE);
                    if (unifiedNativeAd.getAdvertiser() != null) {
                        advertiser.setText(unifiedNativeAd.getAdvertiser());
                        viewHolder.adView.getAdvertiserView().setVisibility(View.VISIBLE);
                    } else
                        viewHolder.adView.getAdvertiserView().setVisibility(View.INVISIBLE);

                    viewHolder.adView.setNativeAd(unifiedNativeAd);
                    mediaView.addView(getAdText());
                }
            });
            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    Toast.makeText(context, "광고 불러오기 실패 : " + i, Toast.LENGTH_LONG).show();
                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
            viewHolder.groupLayout.setVisibility(View.GONE);
            viewHolder.adView.setVisibility(View.VISIBLE);
        }

        viewHolder.groupName.setText(groupItem.getName());
        Glide.with(context).load(groupItem.getImage()).transition(new DrawableTransitionOptions().crossFade(150)).into(viewHolder.groupImage);

        return convertView;
    }

    private TextView getAdText() {
        TextView adText = new TextView(context);
        adText.setText(context.getString(R.string.ad_attribution));
        adText.setTextSize(12);
        adText.setBackgroundColor(context.getResources().getColor(R.color.bg_ad_attribution));
        adText.setTextColor(context.getResources().getColor(R.color.txt_ad_attribution));
        adText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        adText.setGravity(Gravity.CENTER_VERTICAL);
        return adText;
    }

    public String getKey(int position) {
        return groupItemKeys.get(position);
    }

    public static class ViewHolder {
        private ImageView groupImage;
        private LinearLayout groupLayout;
        private TextView groupName;
        private UnifiedNativeAdView adView;

        ViewHolder(View itemView) {
            adView = itemView.findViewById(R.id.unav);
            groupLayout = itemView.findViewById(R.id.ll_group);
            groupImage = itemView.findViewById(R.id.iv_group_image);
            groupName = itemView.findViewById(R.id.tv_group_name);
        }
    }
}