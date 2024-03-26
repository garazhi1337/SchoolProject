package com.example.schoolproject.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.schoolproject.MainActivity;
import com.example.schoolproject.R;
import com.example.schoolproject.databinding.CreateQuestionFragmentBinding;
import com.example.schoolproject.models.Question;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CreateQuestionFragment extends Fragment {

    private CreateQuestionFragmentBinding binding;
    private ArrayList<Question> questions;
    private Uri photoUri = null;
    private String photoUrl = null;
    private Spinner s = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateQuestionFragmentBinding.inflate(inflater, container, false);
        Bundle data = this.getArguments();
        questions = data.getParcelableArrayList("CREATE_G");

        String[] arraySpinner = new String[] {
                "10", "15", "30", "45", "60", "90", "120"
        };
        s = binding.spinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        //я забыл уже для чего это писал но ладно пусть будет...
        if (questions != null && !questions.isEmpty()) {

        }

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                CreateGameFragment fragment = new CreateGameFragment();

                //добавление списка вопросов чтоб он не терялся
                Bundle data = new Bundle();
                data.putParcelableArrayList("CREATE_Q", questions);
                fragment.setArguments(data);

                ft.replace(R.id.nav_host_fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        binding.saveQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressCircular.setVisibility(View.VISIBLE);
                if (photoUri != null) {
                    uploadImageToDatabase(photoUri);
                } else {
                    Question question = new Question();
                    question.setQuestionText(binding.editTextQt.getText().toString());
                    question.setPhotoUrl("photoUrl");


                    HashMap<String, Boolean> map1 = new HashMap<>();
                    map1.put(binding.edittext1.getText().toString() + " ", binding.checkbox1.isChecked());
                    question.setAnswerOne(map1);

                    HashMap<String, Boolean> map2 = new HashMap<>();
                    map2.put(binding.edittext2.getText().toString() + " ", binding.checkbox2.isChecked());
                    question.setAnswerTwo(map2);

                    HashMap<String, Boolean> map3 = new HashMap<>();
                    map3.put(binding.edittext3.getText().toString() + " ", binding.checkbox3.isChecked());
                    question.setAnswerThree(map3);

                    HashMap<String, Boolean> map4 = new HashMap<>();
                    map4.put(binding.edittext4.getText().toString() + " ", binding.checkbox4.isChecked());
                    question.setAnswerFour(map4);

                    question.setSeconds(Integer.parseInt(s.getSelectedItem().toString()));

                    questions.add(question);

                    binding.progressCircular.setVisibility(View.INVISIBLE);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    CreateGameFragment fragment = new CreateGameFragment();
                    Bundle data = new Bundle();
                    data.putParcelableArrayList("CREATE_Q", questions);
                    fragment.setArguments(data);

                    ft.replace(R.id.nav_host_fragment, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
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
            Bitmap bmpimg = Bitmap.createScaledBitmap(bitmap, binding.imageButton.getWidth(), binding.imageButton.getHeight(), true);
            binding.imageButton.setBackground(null); //делает кнопку выбора фото прозрачной
            binding.imageButton.setImageBitmap(bmpimg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void uploadImageToDatabase(Uri uri) {
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
                                        photoUrl = uri.toString();
                                        Question question = new Question();
                                        question.setQuestionText(binding.editTextQt.getText() + " ".trim());
                                        question.setPhotoUrl(photoUrl);

                                        HashMap<String, Boolean> map1 = new HashMap<>();
                                        map1.put(binding.edittext1.getText().toString() + " ", binding.checkbox1.isChecked());
                                        question.setAnswerOne(map1);

                                        HashMap<String, Boolean> map2 = new HashMap<>();
                                        map2.put(binding.edittext2.getText().toString() + " ", binding.checkbox2.isChecked());
                                        question.setAnswerTwo(map2);

                                        HashMap<String, Boolean> map3 = new HashMap<>();
                                        map3.put(binding.edittext3.getText().toString() + " ", binding.checkbox3.isChecked());
                                        question.setAnswerThree(map3);

                                        HashMap<String, Boolean> map4 = new HashMap<>();
                                        map4.put(binding.edittext4.getText().toString() + " ", binding.checkbox4.isChecked());
                                        question.setAnswerFour(map4);

                                        question.setSeconds(Integer.parseInt(s.getSelectedItem().toString()));

                                        questions.add(question);

                                        binding.progressCircular.setVisibility(View.INVISIBLE);

                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();

                                        CreateGameFragment fragment = new CreateGameFragment();
                                        Bundle data = new Bundle();
                                        data.putParcelableArrayList("CREATE_Q", questions);
                                        fragment.setArguments(data);

                                        ft.replace(R.id.nav_host_fragment, fragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
                    }
                });
    }
}
