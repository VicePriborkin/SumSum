package com.android.pribo.vice.sumsum;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pribo.vice.sumsum.Modules.Gate;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class GateListFragment extends Fragment {

    @BindView(R.id.btnAddPrivetGate)
    Button btnAddPrivetGate;
    @BindView(R.id.rvGateList)
    RecyclerView rvGateList;
    Unbinder unbinder;
    @BindView(R.id.fabToHome)
    FloatingActionButton fabToHome;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.searchFab)
    FloatingActionButton searchFab;


    public GateListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gate_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("SumSum", "Null user");
            return view;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Gates");
        //TODO: Handle nulls
        rvGateList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGateList.setAdapter(new GateListAdapter(ref, this));


        fabToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0) {
                    String s1 = String.valueOf(charSequence.charAt(0)).toUpperCase();
                    String s = charSequence.subSequence(1, charSequence.length()).toString().toLowerCase();
                    Query query = FirebaseDatabase.getInstance().getReference("Gates").orderByChild("name").startAt(s1 + s);
                    rvGateList.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvGateList.setAdapter(new GateListAdapter(query, getParentFragment()));
//
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnAddPrivetGate)
    public void onAddPrivateGateClicked() {
        Intent intent = new Intent(getContext(), MapsActivity.class);

        startActivity(intent);
    }

    @OnClick(R.id.fabToHome)
    public void onViewClicked() {

    }


    public static class GateListAdapter extends FirebaseRecyclerAdapter<Gate, GateListAdapter.GateListViewHolder> {
        Fragment fragment;
        Context context;
        private String gateName = null;
        AlertDialog n;


        public GateListAdapter(Query query, Fragment fragment) {
            super(Gate.class, R.layout.gate_list_item, GateListViewHolder.class, query);
            this.fragment = fragment;
        }


        @Override
        public GateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GateListViewHolder vh = super.onCreateViewHolder(parent, viewType);
            vh.gateListFragment = fragment;
            context = parent.getContext();
            return vh;
        }


        @Override
        protected void populateViewHolder(final GateListViewHolder viewHolder, final Gate model, final int position) {
            viewHolder.tvGateName.setText(model.getName());
            viewHolder.btnAddGate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gateName = viewHolder.tvGateName.getText().toString();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userList = FirebaseDatabase.getInstance().getReference("UserGatesList").child(uid).child(gateName);
                    userList.setValue(model);
                    n = new AlertDialog.Builder(context)
                            .setTitle("Gate added")
                            .setMessage("You have added gate" + gateName + " to your list ")
                            .setPositiveButton("Add another..", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    n.dismiss();
                                }
                            }).setCancelable(false)
                            .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);

                                }
                            })
                            .create();
                    n.show();
                }
            });
            viewHolder.model = model;


        }

        private void showMyList() {

        }

        //1)ViewHolder
        public static class GateListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            //Properties:
            ImageView ivGateProfile;
            TextView tvGateName;
            Button btnAddGate;
            Fragment gateListFragment;
            Gate model;

            //Constructor:
            public GateListViewHolder(View itemView) {
                super(itemView);
                ivGateProfile = (ImageView) this.itemView.findViewById(R.id.ivGateProfile);
                tvGateName = (TextView) this.itemView.findViewById(R.id.tvGateName);
                btnAddGate = (Button) this.itemView.findViewById(R.id.btnAddGate);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v == btnAddGate) {
                    try {
                        int adapterPosition = getAdapterPosition();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
