package com.cubosystems.findme.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubosystems.findme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 7/24/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private HashMap<String,LostPerson> dataHashMap;
    private ArrayList<LostPerson> dataset;
    private Context context;

    public void delete(int position){
        dataset.remove(position);
        notifyItemRemoved(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1, textView2, textView3;
        ImageView btnFound, btnNotFound;

        public ViewHolder(View itemView){
            super(itemView);
            textView1 = itemView.findViewById(R.id.txt1);
            textView2 = itemView.findViewById(R.id.txt2);
            textView3 = itemView.findViewById(R.id.txtPersonID);
            btnFound = itemView.findViewById(R.id.btnFound);
            btnNotFound = itemView.findViewById(R.id.btnNotFound);

            btnFound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uniqueID = textView3.getText().toString();
                    //Toast.makeText(context,uniqueID, Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();


                    final DatabaseReference ref = database.getReference("LostPeople").child(uniqueID);


                    ref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LostPerson lp = dataSnapshot.getValue(LostPerson.class);
                            lp.setStatus("found");
                            ref.setValue(lp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    delete(getAdapterPosition());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });


            btnNotFound.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    /*View v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_not_found_alert_dialog,null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                    alertDialog.setView(v);
                    alertDialog.show();*/

                    String uniqueID = textView3.getText().toString();
                    //Toast.makeText(context,uniqueID, Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();


                    final DatabaseReference ref = database.getReference("LostPeople").child(uniqueID);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LostPerson lp = dataSnapshot.getValue(LostPerson.class);
                            lp.setStatus("not found");
                            ref.setValue(lp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_person_record,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView1.setText(dataset.get(position).getPersonName());
        holder.textView2.setText(dataset.get(position).getStationName());
        holder.textView3.setText(dataset.get(position).getId());
    }

    public MyAdapter(ArrayList<LostPerson> myDataset, Context c){
        dataset = myDataset;
        context = c;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
