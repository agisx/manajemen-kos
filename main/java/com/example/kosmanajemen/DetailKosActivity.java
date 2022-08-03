package com.example.kosmanajemen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKosActivity extends AppCompatActivity {

    private boolean stop = false;
    private EditText etNama;
    private EditText etHarga;
    private EditText etAlamat;

    private WebView mvView = null;

    private Button btnSimpan;
    private ImageButton btnReload;
    private Button btnMaps;
    private Button btnDelete;
    private String geotagging = "";

    String url = "https://www.google.com/maps/search/?api=1&query=" + geotagging;
    public static ModelKos.Kos kos = new ModelKos.Kos();

    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude;
    private double longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsHandler();
        setContentView(R.layout.activity_detail_kos);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mvView = findViewById(R.id.webview);
        btnSimpan = findViewById(R.id.btnKirim);
        btnDelete = findViewById(R.id.btnDelete);
        btnReload = findViewById(R.id.btnReload);
        btnMaps = findViewById(R.id.btnMaps);

        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        etAlamat = findViewById(R.id.etAlamat);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(kos.getGeotagging().isEmpty()){
            // GPS HANDLER
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
                return;
            }
            btnSimpan.setText("Tambah");
        }else{
            btnDelete.setVisibility(View.VISIBLE);
        }
        etNama.setText(String.valueOf(kos.getNama()));
        etHarga.setText(String.valueOf(kos.getHarga()));
        etAlamat.setText(String.valueOf(kos.getAlamat()));
        geotagging = kos.getGeotagging();
        url = "https://www.google.com/maps/search/?api=1&query=" + geotagging;

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder edit = new AlertDialog.Builder(DetailKosActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen
                );

                LayoutInflater layoutInflater = getLayoutInflater();
                View viewDialog = layoutInflater.inflate(R.layout.fullscreen_maps, null);

                WebView map = viewDialog.findViewById(R.id.map);
                map.getSettings().setJavaScriptEnabled(true);
                map.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                map.getSettings().setGeolocationEnabled(true);
                map.setWebChromeClient(new WebChromeClient(){
                    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                        // callback.invoke(String origin, boolean allow, boolean remember);
                        callback.invoke(origin, true, false);
                    }
                });
                map.getSettings().setAppCacheEnabled(true);
                map.getSettings().setDatabaseEnabled(true);
                map.getSettings().setDomStorageEnabled(true);
                map.loadUrl(url);
                TextView tvCancel = viewDialog.findViewById(R.id.tvCancel);

                edit.setView(viewDialog);

                AlertDialog alertDialog = edit.create();

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setCanceledOnTouchOutside(false);

                alertDialog.show();
            }
        });

        loadMaps();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DetailKosActivity.this)
                        .setTitle("HAPUS DATA")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete();
                            }
                        })
                        .setNeutralButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLocation();
            }
        });
    }

    private void delete() {
        ApiClient.getKaretEndPoint().deleteOne(kos.getId()).enqueue(new Callback<ModelKos>() {
            @Override
            public void onResponse(Call<ModelKos> call, Response<ModelKos> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<ModelKos> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void insert(){
        if(
                etNama.getText().toString().isEmpty()
                || etHarga.getText().toString().isEmpty()
                || etAlamat.getText().toString().isEmpty()
        ){
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("ERROR")
                    .setMessage("Terdapat data yang kosong")
                    .setPositiveButton("Ya", (CharSequence, listener)->{});
            return;
        }
        if(geotagging.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("ERROR")
                    .setMessage("Data lokasi kosong")
                    .setPositiveButton("Ya", (CharSequence, listener)->{});
            return;
        }

        Call<ModelKos> action = null;
        ApiEndPoint apiEndPoint = ApiClient.getKaretEndPoint();

        if(kos.getId() == 0){
            apiEndPoint.insertOne(
                    etNama.getText().toString(),
                    Integer.valueOf(etHarga.getText().toString()),
                    etAlamat.getText().toString(),
                    geotagging
            ).enqueue(new Callback<ModelKos>() {
                @Override
                public void onResponse(Call<ModelKos> call, Response<ModelKos> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<ModelKos> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }else{
            apiEndPoint.updateOne(
                    kos.getId(),
                    etNama.getText().toString(),
                    Integer.valueOf(etHarga.getText().toString()),
                    etAlamat.getText().toString(),
                    geotagging
            ).enqueue(new Callback<ModelKos>() {
                @Override
                public void onResponse(Call<ModelKos> call, Response<ModelKos> response) {
                    if (response.isSuccessful() && response.body() != null  && response.body().isSuccess()) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<ModelKos> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private void loadMaps() {
        url = "https://www.google.com/maps/search/?api=1&query=" + geotagging;
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // callback.invoke(String origin, boolean allow, boolean remember);
                callback.invoke(origin, true, false);
            }
        });
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    private void OnGPS() {
        new AlertDialog.Builder(this).setMessage("Menyalakan GPS ?")
                .setPositiveButton("Ya", new  DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    }
                }).setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                .show();
    }

    private void gpsHandler(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onSuccess(Location location) {
                if(location == null){
                    LocationRequest.Builder builder = null;
                    LocationRequest locationRequest = null;
                    LocationCallback locationCallback = null;
                    try{
                        builder = new LocationRequest.Builder(LocationRequest.QUALITY_HIGH_ACCURACY);
                        builder.setIntervalMillis(20 * 1000);
                        locationRequest = builder.build();
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                for (Location location : locationResult.getLocations()) {
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }

                            @Override
                            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                                super.onLocationAvailability(locationAvailability);
                            }
                        };
                    }catch (NoClassDefFoundError e){
                    }catch (Exception e){
                        new androidx.appcompat.app.AlertDialog.Builder(getApplicationContext())
                                .setMessage("Error")
                                .setPositiveButton("Gagal menemukan lokasi", new  DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        gpsHandler();
                                    }
                                })
                                .show();
                    }
                    return;
                }

                // Got last known location. In some rare situations this can be null.
                // Logic to handle location object
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                geotagging = latitude + ", " + longitude;
            }
        });
        // Camera HANDLER
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void updateLocation() {
        gpsHandler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMaps();
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kos = new ModelKos.Kos();
        stop = true;
    }
}