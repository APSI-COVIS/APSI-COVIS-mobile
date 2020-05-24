package com.main.covis.covid_map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.main.covis.R;
import com.main.covis.covid_plot.CovidPlotFragment;
import com.main.covis.main.MainActivity;
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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
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
        super.onViewCreated(view, savedInstanceState);

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
                                51.919438, 19.145136), 3));

                        addClusteredGeoJsonSource(style);
                        style.addImage(
                                "cross-icon-id",
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


    private void addClusteredGeoJsonSource(@NonNull Style loadedMapStyle) {

        try {
            loadedMapStyle.addSource(
                    new GeoJsonSource(geoJsonSourceId,
                            new URI("asset://earthquakes.geojson")
//                            new GeoJsonOptions()
//                                    .withCluster(false)
//                                    .withClusterMaxZoom(100)
//                                    .withClusterRadius(1)
                    )
            );
        } catch (URISyntaxException uriSyntaxException) {
            Timber.e("Check the URL %s", uriSyntaxException.getMessage());
        }

        SymbolLayer unclustered = new SymbolLayer("unclustered-points", geoJsonSourceId);

        unclustered.setProperties(
                iconImage("cross-icon-id"),
                iconSize(
                        interpolate(exponential(1), get("cases"),
                                stop(10000.0, 1),
                                stop(50000.0, 2),
                                stop(100000.0, 3)
                        )
                ),
                iconColor(
                        interpolate(exponential(1), get("cases"),
                                stop(10000.0, rgb(0, 255, 0)),
                                stop(50000.0, rgb(0, 0, 255)),
                                stop(100000.0, rgb(255, 0, 0))
                        )
                )
        );
        unclustered.setFilter(has("cases"));
        loadedMapStyle.addLayer(unclustered);

        SymbolLayer count = new SymbolLayer("count", "earthquakes");
        count.setProperties(
                textField(Expression.toString(get("cases"))),
                textSize(12f),
                textColor(Color.WHITE),
                textIgnorePlacement(true),
                textAllowOverlap(true)
        );
        loadedMapStyle.addLayer(count);
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
        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
        List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, geoJsonLayerId);
        if (featureList.size() > 0) {
            for (Feature feature : featureList) {
                //Timber.d("Feature found with %1$s", feature.toJson());
                Toast.makeText(activity, feature.getStringProperty("country"),
                        Toast.LENGTH_SHORT).show();

                FragmentTransaction transection=getFragmentManager().beginTransaction();
                CovidPlotFragment mfragment=new CovidPlotFragment();
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
