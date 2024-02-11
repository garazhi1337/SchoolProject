package com.example.schoolproject.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.schoolproject.MainActivity;
import com.example.schoolproject.R;
import com.example.schoolproject.databinding.RegestrationFragmentBinding;
import com.example.schoolproject.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegistrationFragment extends Fragment {

    private RegestrationFragmentBinding binding;
    private Uri photoUri;
    private String pfpUrl;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RegestrationFragmentBinding.inflate(inflater, container, false);

        binding.alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                ft.replace(R.id.nav_host_fragment, new AlreadyHaveFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        binding.regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    uid = authResult.getUser().getUid();

                                    if (photoUri != null) {
                                        try {
                                            uploadUserToDatabaseWithPfp(photoUri);
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), getResources().getString(R.string.checkfields), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        try {
                                            uploadUserToDatabaseWithoutPfp();
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), getResources().getString(R.string.checkfields), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(getContext(), getResources().getString(R.string.checkfields), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                try {
                    startActivityForResult(photoPickerIntent, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            photoUri = data.getData();

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);
            Bitmap bmpimg = Bitmap.createScaledBitmap(bitmap, binding.profilepic.getWidth(), binding.profilepic.getHeight(), true);
            binding.profilepic.setBackground(null); //делает кнопку выбора фото прозрачной
            binding.profilepic.setImageBitmap(bmpimg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void uploadUserToDatabaseWithPfp(Uri uri) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        String filename = UUID.randomUUID().toString();
        StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        pfpUrl = uri.toString();

                                        User user = new User();
                                        user.setUsername(binding.username.getText().toString());
                                        user.setPassword(binding.password.getText().toString());
                                        user.setEmail(binding.email.getText().toString());
                                        user.setUid(uid);
                                        user.setPfpLink(pfpUrl);

                                        DatabaseReference database = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                                .getReference("/users/" + uid);
                                        database.setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Успех", Toast.LENGTH_SHORT).show();
                                                        binding.progressCircular.setVisibility(View.INVISIBLE);

                                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                                        FragmentTransaction ft = fm.beginTransaction();
                                                        ft.replace(R.id.nav_host_fragment, new EnterGameFragment());
                                                        ft.addToBackStack(null);
                                                        ft.commit();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        binding.progressCircular.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                    }
                                });
                    }
                });


    }

    public void uploadUserToDatabaseWithoutPfp() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        User user = new User();
        user.setUsername(binding.username.getText().toString());
        user.setPassword(binding.password.getText().toString());
        user.setEmail(binding.email.getText().toString());
        user.setUid(uid);
        user.setPfpLink("https://firebasestorage.googleapis.com/v0/b/schoolproject-7f38f.appspot.com/o/images%2FDefault_pfp.jpg?alt=media&token=6d84e505-807e-4307-a0e1-5685101c8f40");

        DatabaseReference database = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                .getReference("/users/" + uid);
        database.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Успех", Toast.LENGTH_SHORT).show();
                        binding.progressCircular.setVisibility(View.INVISIBLE);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.nav_host_fragment, new EnterGameFragment());
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressCircular.setVisibility(View.INVISIBLE);
                    }
                });

    }
}
