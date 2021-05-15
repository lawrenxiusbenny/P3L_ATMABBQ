package com.lawrenxiusbenny.atmabbq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.lawrenxiusbenny.atmabbq.api.PesananApi;
import com.lawrenxiusbenny.atmabbq.model.Pesanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.android.volley.Request.Method.GET;

public class MainActivity extends AppCompatActivity {

    MeowBottomNavigation bottomNavigation;
    Fragment fragment = null;

    private SharedPreferences sPreferences;
    public static final String KEY_ID = "id_reservasi";
    int id_reservasi = 0;
    int jumlahPesanan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = new MenuFragment();
        loadFragment(fragment);

        sPreferences = getSharedPreferences("scan", Context.MODE_PRIVATE);
        id_reservasi = sPreferences.getInt(KEY_ID,Context.MODE_PRIVATE);
        getPesanan();

        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_restaurant_menu_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_shopping_cart_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_qr_code_scanner_24));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {


                switch (item.getId()){
                    case 1:
                        fragment = new MenuFragment();
                        break;

                    case 2:
                        fragment = new CartFragment();
                        break;

                    case 3:
                        fragment = new QrFragment();
                        break;
                }

            }
        });

        bottomNavigation.show(1,true);



        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                loadFragment(fragment);
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,fragment)
                .commit();
    }

    public void getPesanan(){
        RequestQueue queue = Volley.newRequestQueue(this);

        final JsonObjectRequest stringRequest = new JsonObjectRequest(GET, PesananApi.ROOT_SELECT+id_reservasi, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int jumlah           = jsonObject.optInt("jumlah");
                        jumlahPesanan = jumlahPesanan+jumlah;
                    }
                    bottomNavigation.setCount(2,String.valueOf(jumlahPesanan));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
            }
        });
        queue.add(stringRequest);
    }
}