package com.lawrenxiusbenny.atmabbq;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.lawrenxiusbenny.atmabbq.adapter.PesananRecyclerViewAdapter;
import com.lawrenxiusbenny.atmabbq.api.PesananApi;
import com.lawrenxiusbenny.atmabbq.model.Pesanan;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.PUT;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private PesananRecyclerViewAdapter adapter;
    private List<Pesanan> listPesanan;

    private MaterialButton btnCheckout;
    private TextView txtTotalHarga;

    private ConstraintLayout layoutbawah;
    MeowBottomNavigation meowBottomNavigation;

    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;

    private SharedPreferences sPreferences;
    public static final String KEY_ID = "id_reservasi";
    private SharedPreferences.Editor editor;
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
        meowBottomNavigation = getActivity().findViewById(R.id.bottom_navigation);

        sPreferences = getActivity().getSharedPreferences("scan", Context.MODE_PRIVATE);
        cekScan = sPreferences.getInt(KEY_ID,Context.MODE_PRIVATE);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOut();
            }
        });

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
                    int total =0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int id_pesanan          = jsonObject.optInt("id_pesanan");
                        int id_menu           = jsonObject.optInt("id_menu");
                        int id_reservasi           = jsonObject.optInt("id_reservasi");
                        int jumlah           = jsonObject.optInt("jumlah");
                        Double sub_total     = jsonObject.optDouble("sub_total");
                        int id_stok_keluar           = jsonObject.optInt("id_stok_keluar");
                        String nama_customer      = jsonObject.optString("nama_customer");
                        String nama_menu      = jsonObject.optString("nama_menu");
                        Double harga_menu     = jsonObject.optDouble("harga_menu");
                        String gambar_menu    = jsonObject.optString("gambar_menu");
                        int serving_size           = jsonObject.optInt("serving_size");
                        int stok_bahan           = jsonObject.optInt("stok_bahan");

                        Pesanan pesanan = new Pesanan(id_pesanan,id_menu,id_reservasi,jumlah,sub_total,id_stok_keluar,
                                nama_customer,gambar_menu,harga_menu,nama_menu,serving_size,stok_bahan);
                        listPesanan.add(pesanan);
                        total = total+jumlah;
                    }

                    meowBottomNavigation.setCount(2,String.valueOf(total));

                    double harga = response.getDouble("total");

                    NumberFormat formatter = new DecimalFormat("#,###");

                    txtTotalHarga.setText("IDR "+ formatter.format(harga));

                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }catch (JSONException e){
                    e.printStackTrace();
                    swipeRefresh.setRefreshing(false);
                    layoutKosong.setVisibility(View.VISIBLE);
                    meowBottomNavigation.clearAllCounts();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefresh.setRefreshing(false);
                Log.e("error",error.getMessage());
                meowBottomNavigation.clearAllCounts();
            }
        });
        queue.add(stringRequest);
    }

    public void checkOut(){
        Dialog dialog;
        dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_checkout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Button btnClose = dialog.findViewById(R.id.closeBtnCheckout);
        Button btnProceed = dialog.findViewById(R.id.proceedBtnCheckout);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubahStatus(cekScan);
                layoutScanning.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
    }

    public void getDataToPreferences(){
        editor = sPreferences.edit();
        editor.putInt(KEY_ID,0);
        editor.commit();
    }

    public void ubahStatus(int id_reservasi){
        //Pendeklarasian queue
        RequestQueue queue = Volley.newRequestQueue(view.getContext());


        StringRequest stringRequest = new StringRequest(PUT, PesananApi.ROOT_CHECKOUT + id_reservasi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String status=  obj.getString("status");
                    if(status.equalsIgnoreCase("success")){
                        getDataToPreferences();
                        FancyToast.makeText(view.getContext(), "Checkout successful ! Please proceed to payment at cashier",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        meowBottomNavigation.clearAllCounts();
                    }else{
                        FancyToast.makeText(view.getContext(), "Checkout fail ! Please try again",FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    FancyToast.makeText(view.getContext(), "Network unstable, please try again",FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FancyToast.makeText(view.getContext(), "Network unstable, please try again",FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                return params;
            }
        };
        queue.add(stringRequest);
    }
}