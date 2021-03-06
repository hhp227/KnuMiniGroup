package com.hhp227.knu_minigroup;

import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.hhp227.knu_minigroup.adapter.YouTubeListAdapter;
import com.hhp227.knu_minigroup.app.AppController;
import com.hhp227.knu_minigroup.app.EndPoint;
import com.hhp227.knu_minigroup.dto.YouTubeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YouTubeSearchActivity extends AppCompatActivity {
    public static final String API_KEY = "AIzaSyBxQb9CaA01lU5AkXnPGf3s8QjoiV-3Vys";
    private static final int LIMIT = 50;

    private int mType;

    private YouTubeListAdapter mAdapter;

    private List<YouTubeItem> mYouTubeItemList;

    private ProgressBar mProgressBar;

    private ShimmerFrameLayout mShimmerFrameLayout;

    private String mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.srl_list);
        mProgressBar = findViewById(R.id.pb_group);
        mShimmerFrameLayout = findViewById(R.id.sfl_group);
        mYouTubeItemList = new ArrayList<>();
        mAdapter = new YouTubeListAdapter(mYouTubeItemList);
        mSearchText = "";
        mType = getIntent().getIntExtra("type", 0);

        setSupportActionBar(toolbar);
        mAdapter.setOnItemClickListener(new YouTubeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                YouTubeItem youTubeItem = mYouTubeItemList.get(position);
                Intent intent = new Intent(getApplicationContext(), mType == 0 ? WriteActivity.class : ModifyActivity.class);

                intent.putExtra("youtube", youTubeItem);
                setResult(RESULT_OK, intent);
                finish();
            }
        });//
        mAdapter.setHasStableIds(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mYouTubeItemList.clear();
                        mAdapter.notifyDataSetChanged();
                        fetchDataTask();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        showProgressBar();
        fetchDataTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShimmerFrameLayout.clearAnimation();
        mShimmerFrameLayout.removeAllViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        MenuItem search = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) search.getActionView();

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        searchView.setQueryHint("검색어를 입력하세요.");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showProgressBar();
                mShimmerFrameLayout.setVisibility(View.VISIBLE);
                mYouTubeItemList.clear();
                mAdapter.notifyDataSetChanged();
                mSearchText = query;
                fetchDataTask();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchDataTask() {
        AppController.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.GET, EndPoint.URL_YOUTUBE_API + "?part=snippet&key=" + API_KEY + "&q=" + mSearchText + "&maxResults=" + LIMIT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {
                    JSONArray items = response.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject jsonObject = items.getJSONObject(i);
                        String id = jsonObject.getJSONObject("id").getString("videoId");
                        JSONObject snippet = jsonObject.getJSONObject("snippet");
                        String publishedAt = snippet.getString("publishedAt");
                        String title = snippet.getString("title");
                        String thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                        String channelTitle = snippet.getString("channelTitle");

                        YouTubeItem youTubeItem = new YouTubeItem(id, publishedAt, title, thumbnail, channelTitle);
                        mYouTubeItemList.add(youTubeItem);
                        mAdapter.notifyItemInserted(mYouTubeItemList.size() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(error.getMessage());
                hideProgressBar();
            }
        }));
    }

    private void showProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE)
            mProgressBar.setVisibility(View.VISIBLE);
        if (!mShimmerFrameLayout.isShimmerStarted())
            mShimmerFrameLayout.startShimmer();
        if (!mShimmerFrameLayout.isShimmerVisible())
            mShimmerFrameLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE)
            mProgressBar.setVisibility(View.GONE);
        if (mShimmerFrameLayout.isShimmerStarted())
            mShimmerFrameLayout.stopShimmer();
        if (mShimmerFrameLayout.isShimmerVisible())
            mShimmerFrameLayout.setVisibility(View.GONE);
    }
}
