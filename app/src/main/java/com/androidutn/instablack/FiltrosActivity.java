package com.androidutn.instablack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidutn.instablack.model.Thumbnail;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FiltrosActivity extends BaseActivity {

    private static final int MAX_SIZE = 1280;
    public static final String EXTRA_URI = "uri";
    private static final int REQUEST_PUBLICAR = 300;

    private Uri uriOriginal;
    private Bitmap bitmapBase;
    private List<Thumbnail> thumbnails;
    private Filter filtroSeleccionado;

    @BindView(R.id.filtros_imagen) ImageView mImagen;
    @BindView(R.id.filtros_nombre) TextView mNombre;
    @BindViews({R.id.filtro_original, R.id.filtro1, R.id.filtro2,
        R.id.filtro3, R.id.filtro4, R.id.filtro5})
    List<ImageView> mMiniaturas;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        ButterKnife.bind(this);

        uriOriginal = getIntent().getParcelableExtra(EXTRA_URI);

        cargarFiltros();
        seleccionarFiltro(0);
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

        for (ImageView miniatura : mMiniaturas) {
            Thumbnail t = thumbnails.get(mMiniaturas.indexOf(miniatura));
            miniatura.setImageBitmap(t.bitmap);
        }
    }

    private void seleccionarFiltro(int indice) {
        Thumbnail thumbnail = thumbnails.get(indice);
        mNombre.setText(thumbnail.nombre);

        Bitmap bitmap = bitmapBase;
        if (thumbnail.filtro != null) {
            bitmap = Bitmap.createBitmap(bitmap);
            bitmap = thumbnail.filtro.processFilter(bitmap);
        }
        mImagen.setImageBitmap(bitmap);

        filtroSeleccionado = thumbnail.filtro;
    }

    @OnClick({R.id.filtro_original, R.id.filtro1, R.id.filtro2,
            R.id.filtro3, R.id.filtro4, R.id.filtro5})
    public void onFiltro(View v) {
        int indice = mMiniaturas.indexOf(v);
        seleccionarFiltro(indice);
    }

    @OnClick(R.id.filtros_continuar)
    public void onContinuar() {
        Bitmap bitmap = getBitmapOriginal();

        if (filtroSeleccionado != null) {
            bitmap = filtroSeleccionado.processFilter(bitmap);
        }

        try {
            File temp = new File(getExternalCacheDir(), UUID.randomUUID().toString());
            FileOutputStream out = new FileOutputStream(temp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.close();

            Uri uri = Uri.fromFile(temp);
            Intent i = new Intent(this, PublicacionActivity.class);
            i.putExtra(PublicacionActivity.EXTRA_URI, uri);
            startActivityForResult(i, REQUEST_PUBLICAR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapOriginal() {
        Bitmap bitmap = ImageUtils.cargarBitmap(this, uriOriginal, MAX_SIZE);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = ImageUtils.convertirGrayscale(bitmap);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PUBLICAR) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
