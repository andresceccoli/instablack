package com.androidutn.instablack;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.androidutn.instablack.model.Thumbnail;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;
import java.util.List;

public class FiltrosActivity extends BaseActivity {

    private static final int MAX_SIZE = 1280;
    public static final String EXTRA_URI = "uri";

    private Uri uriOriginal;
    private Bitmap bitmapBase;
    private List<Thumbnail> thumbnails;
    private Filter filtroSeleccionado;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        uriOriginal = getIntent().getParcelableExtra(EXTRA_URI);
    }

    private void cargarFiltros() {
        Bitmap bm = ImageUtils.cargarBitmap(this, uriOriginal, MAX_SIZE);
        bitmapBase = ImageUtils.generarThumbnail(bm, 600);
        bitmapBase = ImageUtils.convertirGrayscale(bitmapBase);

        thumbnails = new ArrayList<>();

        Bitmap thumb = ImageUtils.generarThumbnail(this, bitmapBase);
        thumbnails.add(Thumbnail.getBase(this, thumb));
        thumbnails.add(Thumbnail.getVintage(this, thumb));
        thumbnails.add(Thumbnail.getContraste(this, thumb));
        thumbnails.add(Thumbnail.getBrillo(this, thumb));
        thumbnails.add(Thumbnail.getTinta(this, thumb));
        thumbnails.add(Thumbnail.getHardLight(this, thumb));

        // TODO: mostrar miniaturas
    }

    private Bitmap getBitmapOriginal() {
        Bitmap bitmap = ImageUtils.cargarBitmap(this, uriOriginal, MAX_SIZE);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = ImageUtils.convertirGrayscale(bitmap);
        return bitmap;
    }
}
