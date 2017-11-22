package com.androidutn.instablack.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidutn.instablack.R;
import com.androidutn.instablack.firebase.FirebaseUtils;
import com.androidutn.instablack.model.Post;
import com.androidutn.instablack.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andres on 10/24/17.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.post_user_imagen) ImageView mAutorImagen;
    @BindView(R.id.post_user_nombre) TextView mAutorNombre;
    @BindView(R.id.post_imagen) ImageView mImagen;
    @BindView(R.id.post_like) ImageView mLike;
    @BindView(R.id.post_like_count) TextView mLikeCount;
    @BindView(R.id.post_texto) TextView mTexto;
    @BindView(R.id.post_fecha) TextView mFecha;
    @BindView(R.id.post_comentario_count) TextView mComentariosCount;

    private Post post;

    public PostViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void onClick(View v) {
        // TODO: ver post
    }

    public void onLike() {
        // TODO: implementar like
    }

    public void onComentariosCount() {
        // TODO: ver comentarios
    }

    public void setModel(Post p) {
        this.post = p;

        FirebaseUtils.buscarUsuario(p.getAutorUid(), new ValueEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {}
        });

        Picasso.with(itemView.getContext()).load(Uri.parse(p.getFotoUrl())).into(mImagen);
        mTexto.setText(p.getTexto());

        mFecha.setText(DateUtils.getRelativeTimeSpanString(p.getFecha(), System.currentTimeMillis(), 0));

        if (p.getLikes() == 1) {
            mLikeCount.setText(itemView.getContext().getString(R.string.like_count_1));
            mLikeCount.setVisibility(View.VISIBLE);
        } else if (p.getLikes() > 1) {
            mLikeCount.setText(itemView.getContext().getString(R.string.like_count, p.getLikes()));
            mLikeCount.setVisibility(View.VISIBLE);
        } else {
            mLikeCount.setVisibility(View.GONE);
        }

        if (p.getComentarios() == 1) {
            mComentariosCount.setText(itemView.getContext().getString(R.string.comentarios_count_1));
            mComentariosCount.setVisibility(View.VISIBLE);
        } else  if (p.getComentarios() > 1) {
            mComentariosCount.setText(itemView.getContext().getString(R.string.comentarios_count, p.getComentarios()));
            mComentariosCount.setVisibility(View.VISIBLE);
        } else {
            mComentariosCount.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference("Likes")
                .child(post.getId())
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean userLike = (Boolean) dataSnapshot.getValue();
                        if (userLike != null && userLike) {
                            mLike.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
                        } else {
                            mLike.setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.primary_text_dark));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

}
