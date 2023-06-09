package com.example.qucikchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemImpl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {

    TextView txt_signIn, btn_SignUp;
    ImageView profile_image;
    EditText reg_name, reg_email, reg_pass, reg_cPassword;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        txt_signIn.findViewById(R.id.txt_signIn);
        btn_SignUp.findViewById(R.id.btn_SignUp);
        profile_image.findViewById(R.id.reg_name);
        reg_name.findViewById(R.id.reg_name);
        reg_email.findViewById(R.id.reg_email);
        reg_pass.findViewById(R.id.reg_pass);
        reg_cPassword.findViewById(R.id.reg_cPassword);




        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = reg_name.getText().toString();
                String email = reg_email.getText().toString();
                String password = reg_pass.getText().toString();
                String cPassword = reg_cPassword.getText().toString();
                
                String status = "Hey There I am Usion This application";


                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword))
                {
                    Toast.makeText(RegistrationActivity.this, "Please Enter valid data", Toast.LENGTH_SHORT).show();
                }
                else if(email.matches(emailPattern))
                {
                    reg_email.setError("Please Enter valid Email");
                    Toast.makeText(RegistrationActivity.this, "Please Enter valid email", Toast.LENGTH_SHORT).show();
                }else if(password.equals(cPassword))
                {
                    Toast.makeText(RegistrationActivity.this, "password Not matches", Toast.LENGTH_SHORT).show();
                }else if(password.length() < 6)
                {
                    Toast.makeText(RegistrationActivity.this, "Enter 6 char password", Toast.LENGTH_SHORT).show();
                }else {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                             DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
                             StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());
                             if(imageUri!=null){
                                 storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                         if(task.isSuccessful())
                                         {
                                             storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                 @Override
                                                 public void onSuccess(Uri uri) {
                                                     String imageURI = uri.toString();
                                                     User user=new User(auth.getUid(),name,email,imageURI);
                                                     reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if(task.isSuccessful())
                                                             {
                                                                 startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                             }
                                                             else{
                                                                 Toast.makeText(RegistrationActivity.this, "Error in Creating a New User", Toast.LENGTH_SHORT).show();
                                                             }

                                                         }
                                                     });
                                                     
                                                 }
                                             });
                                                
                                         }
                                         
                                     }
                                 });
                             }
                             else {
                                 MenuItemImpl uri = null;
                                 @SuppressLint("RestrictedApi") String imageURI = uri.toString();
                                 User user=new User(auth.getUid(),name,email,imageURI);
                                 reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful())
                                         {
                                             startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                         }
                                         else{
                                             Toast.makeText(RegistrationActivity.this, "Error in Creating a New User", Toast.LENGTH_SHORT).show();
                                         }

                                     }
                                 });
                             }
                             
                            }

                            else {
                                Toast.makeText(RegistrationActivity.this, "Something Wnt Wrong", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                


            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setType("");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
                

            }
        });





        txt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            if(data!=null)
            {
                imageUri= data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }

    
}
