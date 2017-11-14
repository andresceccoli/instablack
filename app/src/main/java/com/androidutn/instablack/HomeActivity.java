package com.androidutn.instablack;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends BaseActivity {

    private static final int REQUEST_IMAGEN = 201;
    private Uri mArchivoUri;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    mostrarFeed();
                    return true;
                case R.id.navigation_usuarios:
                    mostrarUsuarios();
                    return true;
                case R.id.navigation_nuevo_post:
                    seleccionarImagen();
                    return true;
                case R.id.navigation_cuenta:
                    mostrarPerfil();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void mostrarFeed() {

    }

    private void mostrarUsuarios() {
        UsuariosFragment fragment = (UsuariosFragment)
                getSupportFragmentManager().findFragmentByTag("usuarios");
        if (fragment == null) {
            fragment = UsuariosFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment, "usuarios")
                .commit();
    }

    private void seleccionarImagen() {
        // TODO: Chequear permisos

        File archivoTemp = new File(getExternalCacheDir(), UUID.randomUUID().toString());
        mArchivoUri = Uri.fromFile(archivoTemp);

        // camara
        List<Intent> camaraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> camaraApps = getPackageManager().queryIntentActivities(captureIntent, 0);
        for (ResolveInfo info : camaraApps) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
            intent.setPackage(info.activityInfo.packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mArchivoUri);
            camaraIntents.add(intent);
        }

        // galeria
        Intent picker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(picker, getString(R.string.title_picker));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, camaraIntents.toArray(new Parcelable[camaraIntents.size()]));
        startActivityForResult(chooser, REQUEST_IMAGEN);
    }

    private void mostrarPerfil() {
        // TODO: Reemplazar por acceso a perfil
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGEN) {
            if (resultCode == RESULT_OK) {
                boolean camara = false;
                if (data == null || data.getData() == null) {
                    camara = true;
                } else if (data.getAction() != null) {
                    camara = data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }

                if (!camara) {
                    mArchivoUri = data.getData();
                }

                if (mArchivoUri != null) {
                    // TODO: Abrir Filtros
                }
            }
        }
    }

}
