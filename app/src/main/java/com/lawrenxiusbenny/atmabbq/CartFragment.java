package com.lawrenxiusbenny.atmabbq;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.lawrenxiusbenny.atmabbq.adapter.MenuRecyclerViewAdapter;
import com.lawrenxiusbenny.atmabbq.adapter.PesananRecyclerViewAdapter;
import com.lawrenxiusbenny.atmabbq.api.MenuApi;
import com.lawrenxiusbenny.atmabbq.api.PesananApi;
import com.lawrenxiusbenny.atmabbq.model.Menu;
import com.lawrenxiusbenny.atmabbq.model.Pesanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.android.volley.Request.Method.GET;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private PesananRecyclerViewAdapter adapter;
    private List<Pesanan> listPesanan;

    private MaterialButton btnCheckout;
    private TextView txtTotalHarga;

    private ConstraintLayout layoutbawah;

    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;

    private SharedPreferences sPreferences;
    public static final String KEY_ID = "id_reservasi";
    int cekScan = 0;

    private SearchView editSearch;
    private SwipeRefreshLayout swipeRefresh;
    private View view;
    private ConstraintLayout layoutKosong, layoutScanning;
    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        editSearch = (SearchView) view.findViewById(R.id.searchViewPesanan);
        swipeRefresh = view.findViewById(R.id.refreshPesanan);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout_pesanan);
        layoutKosong = view.findViewById(R.id.layout_kosong);
        layoutScanning = view.findViewById(R.id.layout_belum_scanning);
        layoutbawah = view.findViewById(R.id.layout_bawah_cart);
        txtTotalHarga = view.findViewById(R.id.totalHarga);
        btnCheckout = view.findViewById(R.id.btnCheckOut);

        sPreferences = getActivity().getSharedPreferences("scan", Context.MODE_PRIVATE);
        cekScan = sPreferences.getInt(KEY_ID,Context.MODE_PRIVATE);

        if(cekScan != 0){
            loadPesanan();
        }else{
            layoutScanning.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void loadPesanan(){
        setAdapter();
        getPesanan();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPesanan();
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
        listPesanan = new ArrayList<Pesanan>();
        adapter = new PesananRecyclerViewAdapter(view.getContext(), listPesanan);
        recyclerView = view.findViewById(R.id.recycler_view_pesanan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
    }

    public void getPesanan(){
        RequestQueue queue = Volley.newRequestQueue(view.getContext());

        final JsonObjectRequest stringRequest = new JsonObjectRequest(GET, PesananApi.ROOT_SELECT+cekScan, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutbawah.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    JSONArray jsonArray = response.getJSONArray("data");
                    if(!listPesanan.isEmpty())
                        listPesanan.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int id_menu           = jsonObject.optInt("id_menu");
                        int id_reservasi           = jsonObject.optInt("id_reservasi");
                        int jumlah           = jsonObject.optInt("jumlah");
                        Double sub_total     = jsonObject.optDouble("sub_total");
                        int id_stok_keluar           = jsonObject.optInt("id_stok_keluar");
                        String nama_customer      = jsonObject.optString("nama_customer");
                        String nama_menu      = jsonObject.optString("nama_menu");
                        Double harga_menu     = jsonObject.optDouble("harga_menu");
                        String gambar_menu    = jsonObject.optString("gambar_menu");

                        Pesanan pesanan = new Pesanan(id_menu,id_reservasi,jumlah,sub_total,id_stok_keluar,
                                nama_customer,gambar_menu,harga_menu,nama_menu);
                        listPesanan.add(pesanan);
                    }

                    double harga = response.getDouble("total");

                    Toast.makeText(getContext(), String.valueOf(harga), Toast.LENGTH_SHORT).show();

                    NumberFormat formatter = new DecimalFormat("#,###");

                    txtTotalHarga.setText("IDR "+ formatter.format(harga));

                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                    layoutKosong.setVisibility(View.VISIBLE);
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