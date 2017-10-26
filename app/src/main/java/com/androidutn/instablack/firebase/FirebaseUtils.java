package com.androidutn.instablack.firebase;

import android.support.annotation.Nullable;

import com.androidutn.instablack.model.Comentario;
import com.androidutn.instablack.model.Post;
import com.androidutn.instablack.model.PostRef;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by andres on 10/23/17.
 */

public class FirebaseUtils {

    public static void insertarUsuario(@Nullable DatabaseReference.CompletionListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // TODO: implementar
        }
    }

    public static void insertarEmail(@Nullable DatabaseReference.CompletionListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // TODO: implementar
        }
    }

    public static void seguirUsuario(final String uid, @Nullable final DatabaseReference.CompletionListener listener) {
        final String authUid = FirebaseAuth.getInstance().getUid();
        if (authUid != null) {
            // TODO: implementar
        }
    }

    public static void guardarPost(final Post post, @Nullable final DatabaseReference.CompletionListener listener) {
        final String authUid = FirebaseAuth.getInstance().getUid();
        if (authUid != null) {
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");
            final String key = postsRef.push().getKey();
            post.setId(key);

            postsRef.child(key).setValue(post, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        guardarUserPost(post, listener);
                    } else {
                        if (listener != null) {
                            listener.onComplete(databaseError, databaseReference);
                        }
                    }
                }
            });
        }
    }

    private static void guardarUserPost(final Post post, final DatabaseReference.CompletionListener listener) {
        final PostRef ref = new PostRef();
        ref.setFechaRev(post.getFechaRev());

        FirebaseDatabase.getInstance().getReference("UserPosts")
                .child(post.getAutorUid()).child(post.getId()).setValue(ref, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // TODO: actualizar feed del autor

                    actualizarFeedSeguidores(post, ref, listener);
                } else {
                    if (listener != null) {
                        listener.onComplete(databaseError, databaseReference);
                    }
                }
            }
        });
    }

    private static void actualizarFeedSeguidores(final Post post, final PostRef ref, final DatabaseReference.CompletionListener listener) {
        FirebaseDatabase.getInstance().getReference("Seguidores")
                .child(post.getAutorUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uid = ds.getKey();

                    // TODO: actualizar feed seguidor
                }

                if (listener != null) {
                    listener.onComplete(null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onComplete(databaseError, null);
            }
        });
    }

    public static void buscarUsuario(String uid, ValueEventListener listener) {
        // TODO: implementar
    }

    public static void buscarPost(String id, ValueEventListener listener) {
        // TODO: implementar
    }

    public static void guardarComentario(String postId, Comentario comentario, DatabaseReference.CompletionListener listener) {
        final String authUid = FirebaseAuth.getInstance().getUid();
        if (authUid != null) {
            // TODO: implementar
        }
    }
}
