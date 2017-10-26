package com.androidutn.instablack.viewholders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidutn.instablack.R;
import com.androidutn.instablack.firebase.FirebaseUtils;
import com.androidutn.instablack.model.Comentario;
import com.androidutn.instablack.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by andres on 10/24/17.
 */

public class ComentarioViewHolder extends RecyclerView.ViewHolder {

    // TODO: bind views
    ImageView mAutorImagen;
    TextView mAutorNombre;
    TextView mTexto;
    TextView mFecha;

    public ComentarioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setModel(Comentario c) {
        FirebaseUtils.buscarUsuario(c.getAutorUid(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                if (usuario != null) {
                    mAutorNombre.setText(usuario.getNombre());
                    if (usuario.getImagenUrl() != null)
                        Picasso.with(itemView.getContext()).load(Uri.parse(usuario.getImagenUrl())).into(mAutorImagen);
                    else
                        mAutorImagen.setImageResource(R.drawable.account);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTexto.setText(c.getTexto());

        mFecha.setText(DateUtils.getRelativeTimeSpanString(c.getFecha(), System.currentTimeMillis(), 0));
    }
}
