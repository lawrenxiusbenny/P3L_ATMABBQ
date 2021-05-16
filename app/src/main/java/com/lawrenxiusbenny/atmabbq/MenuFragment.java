package com.lawrenxiusbenny.atmabbq;

import android.os.Bundle;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


import static com.android.volley.Request.Method.GET;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private MenuRecyclerViewAdapter adapter;
    private List<Menu> listMenu;

    ShimmerFrameLayout shimmerFrameLayout;

    private SearchView editSearch;
    private View view;

    private SwipeRefreshLayout swipeRefresh;

    public MenuFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        editSearch = (SearchView) view.findViewById(R.id.searchView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        swipeRefresh = view.findViewById(R.id.refreshMenu);
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
        shimmerFrameLayout.startShimmer();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMenu();
            }
        });

    }

    public void setAdapter(){
        listMenu = new ArrayList<Menu>();
        recyclerView = view.findViewById(R.id.recycler_view_menu);
        adapter = new MenuRecyclerViewAdapter(view.getContext(), listMenu);
        int gridData = 2;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(),gridData);
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
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }catch (JSONException e){
                    swipeRefresh.setRefreshing(false);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefresh.setRefreshing(false);
                Log.e("error",error.getMessage());
            }
        });
        queue.add(stringRequest);
    }
}