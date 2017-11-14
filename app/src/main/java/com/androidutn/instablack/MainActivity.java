package com.androidutn.instablack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.androidutn.instablack.firebase.FirebaseUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // no hay usuario
            Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAllowNewEmailAccounts(true)
                    .setTheme(R.style.AppTheme)
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                    .build();
            startActivityForResult(intent, REQUEST_SIGN_IN);
        } else {
            // hay usuario
            irAHome();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                insertarUsuario();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No hay conexion", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Error desconocido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    private void insertarUsuario() {
        blockUI();
        FirebaseUtils.insertarUsuario(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    insertarEmail();
                } else {
                    unblockUI();
                    mostrarMensaje(databaseError.getMessage());
                }
            }
        });
    }

    private void insertarEmail() {
        FirebaseUtils.insertarEmail(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                unblockUI();
                if (databaseError == null) {
                    irAHome();
                } else {
                    mostrarMensaje(databaseError.getMessage());
                }
            }
        });
    }

    private void irAHome() {
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }
}
