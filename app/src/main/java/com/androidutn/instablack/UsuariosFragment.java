package com.androidutn.instablack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.androidutn.instablack.firebase.FirebaseUtils;
import com.androidutn.instablack.model.Usuario;
import com.androidutn.instablack.viewholders.UsuarioViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsuariosFragment extends Fragment {

    @BindView(R.id.busqueda) EditText mBusqueda;
    @BindView(R.id.list) RecyclerView mList;

    private FirebaseRecyclerAdapter<Usuario, UsuarioViewHolder> adapter;

    public UsuariosFragment() {
    }

    public static UsuariosFragment newInstance() {
        UsuariosFragment fragment = new UsuariosFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);
        ButterKnife.bind(this, view);

        String authId = FirebaseAuth.getInstance().getUid();

        // query para recuperar las claves (indice)
        Query query = FirebaseDatabase.getInstance()
                .getReference("Siguiendo")
                .child(authId);

        // referencia al lugar donde estan los datos reales
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios");

        // options para adapter
        FirebaseRecyclerOptions<Usuario> options =
                new FirebaseRecyclerOptions.Builder<Usuario>()
                    .setIndexedQuery(query, usuariosRef, Usuario.class)
                    .build();
        // adapter
        adapter = new FirebaseRecyclerAdapter<Usuario, UsuarioViewHolder>(options) {
            @Override
            protected void onBindViewHolder(UsuarioViewHolder holder, int position, Usuario model) {
                holder.setModel(model);
            }

            @Override
            public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_usuario, parent, false);
                return new UsuarioViewHolder(itemView);
            }
        };

        mList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @OnClick(R.id.seguir)
    public void onSeguir() {
        if (TextUtils.isEmpty(mBusqueda.getText())) {
            return;
        }

        String email = mBusqueda.getText().toString();
        email = email.replace('@', '_').replace('.', '_');

        FirebaseDatabase.getInstance()
                .getReference("Emails")
                .child(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String uid = dataSnapshot.getValue().toString();

                            FirebaseUtils.seguirUsuario(uid, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        ((BaseActivity) getActivity()).mostrarMensaje(
                                                R.string.seguir_ok);
                                    } else {
                                        ((BaseActivity) getActivity()).mostrarMensaje(
                                                databaseError.getMessage());
                                    }
                                }
                            });
                        } else {
                            ((BaseActivity) getActivity()).mostrarMensaje(
                                    R.string.seguir_no_encontrado);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ((BaseActivity) getActivity()).mostrarMensaje(
                                databaseError.getMessage());
                    }
                });
    }

}
