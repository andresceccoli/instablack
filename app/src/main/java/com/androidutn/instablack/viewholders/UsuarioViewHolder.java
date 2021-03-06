package com.androidutn.instablack.viewholders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidutn.instablack.R;
import com.androidutn.instablack.model.Usuario;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by andres on 10/23/17.
 */

public class UsuarioViewHolder extends RecyclerView.ViewHolder {

    // TODO: bind views
    ImageView mImagen;
    TextView mNombre;
    TextView mEmail;

    public UsuarioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setModel(Usuario u) {
        mNombre.setText(u.getNombre());
        mEmail.setText(u.getEmail());

        if (u.getImagenUrl() != null) {
            Picasso.with(itemView.getContext()).load(Uri.parse(u.getImagenUrl())).into(mImagen);
        } else {
            mImagen.setImageResource(R.drawable.account);
        }
    }
}
