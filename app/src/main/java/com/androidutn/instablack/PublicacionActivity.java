package com.androidutn.instablack;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidutn.instablack.firebase.FirebaseUtils;
import com.androidutn.instablack.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicacionActivity extends BaseActivity {

    public static final String EXTRA_URI = "uri";

    @BindView(R.id.publicacion_imagen) ImageView mImagen;
    @BindView(R.id.publicacion_comentario) EditText mComentario;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        ButterKnife.bind(this);

        uri = getIntent().getParcelableExtra(EXTRA_URI);
        mImagen.setImageURI(uri);
    }

    @OnClick(R.id.publicacion_ok)
    public void onPublicar() {
        blockUI();

        FirebaseStorage.getInstance().getReference("imagenes")
            .child(UUID.randomUUID().toString())
            .putFile(uri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Post post = new Post();
                    post.setFotoUrl(downloadUrl.toString());
                    post.setTexto(mComentario.getText().toString());
                    post.setFecha(System.currentTimeMillis());
                    post.setFechaRev(-post.getFecha());
                    post.setAutorUid(FirebaseAuth.getInstance().getUid());

                    FirebaseUtils.guardarPost(post, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            unblockUI();
                            if (databaseError == null) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                mostrarMensaje(databaseError.getMessage());
                            }
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    unblockUI();
                    mostrarMensaje(e);
                }
            });
    }
}
