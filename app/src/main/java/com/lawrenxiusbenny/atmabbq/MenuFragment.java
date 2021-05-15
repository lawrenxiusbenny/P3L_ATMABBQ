package com.lawrenxiusbenny.atmabbq;

import android.content.res.Configuration;
import android.os.Bundle;

import android.app.SearchManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import androidx.annotation.MenuRes;
//import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.lawrenxiusbenny.atmabbq.adapter.MenuRecyclerViewAdapter;
import com.lawrenxiusbenny.atmabbq.api.MenuApi;
import com.lawrenxiusbenny.atmabbq.model.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.android.volley.Request.Method.GET;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private MenuRecyclerViewAdapter adapter;
    private List<Menu> listMenu;

    ProgressBar progressBar;
    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;
//    int page=1, limit=10;

    private SearchView editSearch;
    private SwipeRefreshLayout swipeRefresh;
    private View view;

    public MenuFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        editSearch = (SearchView) view.findViewById(R.id.searchView);
        swipeRefresh = view.findViewById(R.id.refreshMenu);
        nestedScrollView = view.findViewById(R.id.scrollView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        loadMenu();

        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return view;
    }

    public void loadMenu(){
        setAdapter();
        getMenu();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMenu();
            }
        });

        shimmerFrameLayout.startShimmer();

//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
//                    page++;
//                    progressBar.setVisibility(View.VISIBLE);
//
//                    getMenu();
//                }
//            }
//        });
    }

    public void setAdapter(){
        listMenu = new ArrayList<Menu>();
        recyclerView = view.findViewById(R.id.recycler_view_menu);
        adapter = new MenuRecyclerViewAdapter(view.getContext(), listMenu);
        int gridData = 2;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(),gridData);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void getMenu(){
        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        final JsonObjectRequest stringRequest = new JsonObjectRequest(GET, MenuApi.ROOT_SELECT_ALL, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    JSONArray jsonArray = response.getJSONArray("data");
                    if(!listMenu.isEmpty())
                        listMenu.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int id_menu           = jsonObject.optInt("id_menu");
                        String nama_menu      = jsonObject.optString("nama_menu");
                        Double harga_menu     = jsonObject.optDouble("harga_menu");
                        String deskripsi_menu = jsonObject.optString("deskripsi_menu");
                        String gambar_menu    = jsonObject.optString("gambar_menu");
                        int serving_size    = jsonObject.optInt("serving_size");
                        int stok_bahan    = jsonObject.optInt("stok_bahan");
                        Menu menu = new Menu(id_menu,nama_menu,harga_menu,deskripsi_menu,gambar_menu,serving_size,stok_bahan);
                        listMenu.add(menu);
                    }
                    int jumlah = listMenu.size();


                    adapter.notifyDataSetChanged();
//                    swipeRefresh.setRefreshing(false);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                swipeRefresh.setRefreshing(false);
//                Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("error",error.getMessage());
            }
        });
        queue.add(stringRequest);
    }
}