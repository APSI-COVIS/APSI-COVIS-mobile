package com.main.covis.covid_map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.gson.JsonObject;
import com.main.covis.R;
import com.main.covis.covid_plot.CovidPlotFragment;
import com.main.covis.main.MainActivity;
import com.main.covis.network.ApiClient;
import com.main.covis.network.ApiService;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.layers.TransitionOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Retrofit;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.expressions.Expression.distance;
import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;


public class CovidMapFragment extends Fragment implements CovidMapContract.View, MapboxMap.OnMapClickListener {


    private MapView mapView;
    private MapboxMap mapboxMap;
    private MainActivity activity;
    private CovidMapPresenter presenter;
    private static final String geoJsonSourceId = "earthquakes";
    private static final String geoJsonLayerId = "polygonFillLayer";
    JsonObject geoJson;
    private Retrofit retrofit;
    private ApiService apiService;
    private Call<JsonObject> call;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            super.onViewCreated(view, savedInstanceState);

            retrofit = ApiClient.getClient();
            apiService = retrofit.create(ApiService.class);
            call = apiService.getCovidData("2020-06-01", "ACTIVE");//formattedDate
            try {
                geoJson= call.execute().body();
                System.out.println("test" + geoJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HorizontalPicker picker = (HorizontalPicker) activity.findViewById(R.id.datePicker);
            picker.setListener(new DatePickerListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDateSelected(DateTime dateSelected) {
                    int month = dateSelected.getMonthOfYear();
                    int day = dateSelected.getDayOfMonth();
                    String formattedDate = String.valueOf(dateSelected.getYear()) +
                            '-' +
                            ( month >10 ? String.valueOf(month) :('0'+String.valueOf(month))) +
                            '-' +
                            ( day >10 ? String.valueOf(day) :('0'+String.valueOf(day)));
                    Toast.makeText(activity, formattedDate,
                            Toast.LENGTH_SHORT).show();
                    System.out.println("formatteddata" + formattedDate);
                    call = apiService.getCovidData((String)formattedDate, "ACTIVE");

                    try {
                        geoJson= call.execute().body();
                        System.out.println("test" + geoJson);
                        GeoJsonSource geoJsonSource = mapboxMap.getStyle().getSourceAs(geoJsonSourceId);
                        if(geoJsonSource !=null ){
                            geoJsonSource.setGeoJson(String.valueOf(geoJson));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).setDays(190)
                    .setOffset(180)
                    .init();
            picker.setDate(new DateTime());



    //        call.enqueue(new Callback() {
    //            @Override
    //            public void onResponse(Call call, Response response) {
    //                if( response.body() !=null){
    //                    geoJson = response;
    //                    System.out.println("siema" + response.body());
    //                }
    //                else
    //                    System.out.println("nara" + response.toString());
    //            }
    //
    //            @Override
    //            public void onFailure(Call call, Throwable t) {
    //                System.out.println("ERROR");
    //            }
    //        });

            mapView = activity.findViewById(R.id.mapView);

            mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull MapboxMap map) {

                    mapboxMap = map;

                    map.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {


                            style.setTransition(new TransitionOptions(0, 0, false));

                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                    51.919438, 19.145136), 2.6));
//                                    30.5,
//                                    -40.5), 3));

                            addClusteredGeoJsonSource(style);
                            style.addImage(
                                    "circle-icon",
                                    Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_circle))),
                                    true
                            );
                            if (style != null) {
                                style.addLayer(new FillLayer(geoJsonLayerId, geoJsonSourceId)
                                        .withProperties(fillOpacity(0.5f)));
                            }

                            Toast.makeText(activity, R.string.app_name,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    map.addOnMapClickListener(CovidMapFragment.this);
                }
            });
        }

    }


    private void addClusteredGeoJsonSource(@NonNull Style loadedMapStyle) {

//        try {
            loadedMapStyle.addSource(
                    new GeoJsonSource(geoJsonSourceId, String.valueOf(geoJson),
//                    new GeoJsonSource(geoJsonSourceId,
//                            new URI("asset://earthquakes.geojson"),
                            new GeoJsonOptions()
                                    .withCluster(false)
//                                    .withClusterMaxZoom(2)
//                                    .withClusterRadius(1)
                    )
            );
//        } catch (URISyntaxException uriSyntaxException) {
//            Timber.e("Check the URL %s", uriSyntaxException.getMessage());
//        }

        SymbolLayer unclustered = new SymbolLayer("unclustered-points", geoJsonSourceId);
        unclustered.setProperties(
                iconImage("circle-icon"),
                iconSize(
//                        interpolate(exponential(1), get("cases"),
//                                stop(10000, 1),
//                                stop(50000, 2),
//                                stop(100000, 3)
                        interpolate(exponential(1),zoom(),
                                stop(1f, 0.5),
                                stop(2f, 1.2),
                                stop(3f, 2.2)
                        )
                ),
                iconColor(
                        interpolate(exponential(1), get("cases"),
                                stop(10000.0, rgb(0, 255, 0)),
                                stop(50000.0, rgb(0, 0, 255)),
                                stop(100000.0, rgb(255, 0, 0))
                        )
                ),
                iconAllowOverlap(true),
                iconIgnorePlacement(true),
                iconOpacity((float) 0.9),
                textField(Expression.toString(get("cases"))),
                textSize(12f),
                textColor(Color.WHITE),
                textIgnorePlacement(true),
                textAllowOverlap(true)
        );

        loadedMapStyle.addLayer(unclustered);
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void showMessage(String message) {
        System.out.println("Fragment");
    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
        RectF rectF = new RectF(pointf.x - 20, pointf.y - 20, pointf.x + 20, pointf.y + 20);
        List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, geoJsonLayerId);
        if (featureList.size() > 0) {
            for (Feature feature : featureList) {
                //Timber.d("Feature found with %1$s", feature.toJson());
                Toast.makeText(activity, feature.getStringProperty("country-slug"),
                        Toast.LENGTH_SHORT).show();

                FragmentTransaction transection=getFragmentManager().beginTransaction();
                CovidPlotFragment mfragment=new CovidPlotFragment();
                CovidPlotFragment.country = feature.getStringProperty("country");
                Bundle bundle=new Bundle();
                bundle.putString("country",feature.getStringProperty("country"));
                mfragment.setArguments(bundle); //data being send to SecondFragment
                transection.replace(R.id.container, mfragment);
                transection.commit();
            }
            return true;
        }
        return false;
    }

}
