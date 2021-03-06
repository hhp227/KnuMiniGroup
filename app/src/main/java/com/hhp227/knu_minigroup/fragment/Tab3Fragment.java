package com.hhp227.knu_minigroup.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.adapter.MemberGridAdapter;
import com.hhp227.knu_minigroup.app.AppController;
import com.hhp227.knu_minigroup.app.EndPoint;
import com.hhp227.knu_minigroup.dto.MemberItem;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.util.ArrayList;
import java.util.List;

public class Tab3Fragment extends Fragment {
    private static final int LIMIT = 40;
    private static final String TAG = "맴버목록";

    private boolean mHasRequestedMore;

    private int mOffSet;

    private String mGroupId;

    private List<MemberItem> mMemberItems;

    private MemberGridAdapter mAdapter;

    private ProgressBar mProgressBar;

    public Tab3Fragment() {
    }

    public static Tab3Fragment newInstance(String grpId) {
        Tab3Fragment fragment = new Tab3Fragment();
        Bundle args = new Bundle();

        args.putString("grp_id", grpId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupId = getArguments().getString("grp_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.srl_member);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        RecyclerView recyclerView = view.findViewById(R.id.rv_member);
        mProgressBar = view.findViewById(R.id.pb_member);
        mMemberItems = new ArrayList<>();
        mAdapter = new MemberGridAdapter(mMemberItems);
        mOffSet = 1;

        mAdapter.setHasStableIds(true);
        mAdapter.setOnItemClickListener(new MemberGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MemberItem memberItem = mMemberItems.get(position);
                String uid = memberItem.uid;
                String name = memberItem.name;
                String value = memberItem.value;
                Bundle args = new Bundle();
                UserFragment newFragment = UserFragment.newInstance();

                args.putString("uid", uid);
                args.putString("name", name);
                args.putString("value", value);
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "dialog");
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mHasRequestedMore && !recyclerView.canScrollVertically(1)) {
                    mHasRequestedMore = true;
                    mOffSet += LIMIT;

                    fetchMemberList();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMemberItems.clear();
                        mOffSet = 1;

                        fetchMemberList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        showProgressBar();
        fetchMemberList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            mAdapter.notifyDataSetChanged();
    }

    private void fetchMemberList() {
        String params = "?CLUB_GRP_ID=" + mGroupId + "&startM=" + mOffSet + "&displayM=" + LIMIT;

        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.GET, EndPoint.MEMBER_LIST + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Source source = new Source(response);
                    Element memberList = source.getElementById("member_list");

                    // 페이징 처리
                    String page = memberList.getFirstElementByClass("paging").getFirstElement("title", "현재 선택 목록", false).getTextExtractor().toString();
                    List<Element> inputElements = memberList.getAllElements("name", "memberIdCheck", false);
                    List<Element> imgElements = memberList.getAllElements("title", "프로필", false);
                    List<Element> spanElements = memberList.getAllElements(HTMLElementName.SPAN);

                    for (int i = 0; i < inputElements.size(); i++) {
                        String name = spanElements.get(i).getContent().toString();
                        String imageUrl = imgElements.get(i).getAttributeValue("src");
                        String value = inputElements.get(i).getAttributeValue("value");

                        mMemberItems.add(new MemberItem(imageUrl.substring(imageUrl.indexOf("id=") + "id=".length(), imageUrl.lastIndexOf("&ext")), name, value));
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                mHasRequestedMore = false;
                hideProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, error.getMessage());
                hideProgressBar();
            }
        }));
    }

    private void showProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.INVISIBLE)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE)
            mProgressBar.setVisibility(View.INVISIBLE);
    }
}
