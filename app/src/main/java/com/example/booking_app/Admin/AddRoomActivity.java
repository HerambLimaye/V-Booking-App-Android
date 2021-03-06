package com.example.booking_app.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booking_app.DialogBoxRB;
import com.example.booking_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddRoomActivity extends AppCompatActivity implements View.OnClickListener {


    enum ClickedTextView{
        CategoryT , BlockT
    }

    private EditText roomET;
    private TextView categoryT, blockT;
    private Button addBtn;
    private ClickedTextView clickedStatus ;
    ProgressDialog progressDialog;

    String blockS , roomS , categoryS;

    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        referenceUI();

        fireStore = FirebaseFirestore.getInstance();


        DialogBoxRB.setOnCLickBlockOptn(new DialogBoxRB.onClickBlocks() {
            @Override
            public void setBlockName(String name) {
                if(clickedStatus == ClickedTextView.BlockT)
                    blockT.setText(name);
                    else
                    categoryT.setText(name);
            }
        });
    }

    private void referenceUI() {
        blockT = findViewById(R.id.BlockName);
        roomET = findViewById(R.id.NewRoomName);
        addBtn = findViewById(R.id.Add);
        categoryT = findViewById(R.id.CategoryName);

        blockT.setOnClickListener(this);
        categoryT.setOnClickListener(this);
        addBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.CategoryName : onClickCategoryT(); break;
            case R.id.BlockName : onClickBlockT(); break;
            case R.id.Add :addRoom(); break;
        }
    }


    private void onClickBlockT() {

        //open dialog box
        clickedStatus = ClickedTextView.BlockT;
        String[] list = getResources().getStringArray(R.array.Blocks);
        DialogBoxRB dialogBoxRB = new DialogBoxRB(list);
        dialogBoxRB.show(getSupportFragmentManager() , "open");
    }

    private void onClickCategoryT() {
        clickedStatus = ClickedTextView.CategoryT;
        String[] list = getResources().getStringArray(R.array.RoomType);
        DialogBoxRB dialogBoxRB = new DialogBoxRB(list);
        dialogBoxRB.show(getSupportFragmentManager() , "open");
    }
    //on click add button
    public void addRoom(){
        progressDialog = new ProgressDialog(AddRoomActivity.this);
        progressDialog.setTitle("Add New Room");
        progressDialog.setMessage("Adding new Room");
        progressDialog.show();

        categoryS = categoryT.getText().toString();
        blockS = blockT.getText().toString();
        roomS = roomET.getText().toString();

        if(categoryS.equals("Category")
                || blockS.equals("Block")
                || TextUtils.isEmpty(roomS)
               || roomS.length()<3){
            Toast.makeText(this, "Enter all fields\nand room should contain 3 character number only", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        blockS = blockS.substring(0 , blockS.indexOf('-'));


        HashMap<String , Object> map = new HashMap<>();
       map.put("roomNo" , roomS);
       map.put("slots" , "000000000");


        DocumentReference reference =  fireStore.collection(categoryS)
                .document("block")
                .collection(blockS)
                .document(roomS);

        reference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(AddRoomActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }else Toast.makeText(AddRoomActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}