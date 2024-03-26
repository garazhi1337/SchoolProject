package com.example.schoolproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproject.MainActivity;
import com.example.schoolproject.R;
import com.example.schoolproject.databinding.CreateGameFragmentBinding;
import com.example.schoolproject.models.Game;
import com.example.schoolproject.models.Question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateGameFragment extends Fragment {

    //класс для создания викторины

    private CreateGameFragmentBinding binding;
    private ArrayList<Question> questions;
    private GroupAdapter<GroupieViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateGameFragmentBinding.inflate(inflater, container, false);

        //проверка зарегестрирован ли пользователь
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            adapter = new GroupAdapter<>();

            //получение списка вопросов из creategamefragment
            if (this.getArguments() != null) {
                Bundle data = this.getArguments();
                questions = data.getParcelableArrayList("CREATE_Q");
            } else {
                questions = new ArrayList<>();
            }

            for (Question q : questions) {
                adapter.add(new QuestionItem(q));
            }

            binding.recyclerView.setAdapter(adapter);

            binding.addQuestionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    CreateQuestionFragment fragment = new CreateQuestionFragment();
                    Bundle data = new Bundle();
                    data.putParcelableArrayList("CREATE_G", questions);
                    fragment.setArguments(data);
                    ft.replace(R.id.nav_host_fragment, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            binding.saveGameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (questions.size() > 1) {
                        saveGame();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.addquestion), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            //если пользователь не зарегестрирован перенаправляет во фрагмент с регистрацией/входом
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, new RegistrationFragment());
            ft.addToBackStack(null);
            ft.commit();
        }


        return binding.getRoot();
    }

    //сохрянает игру в бд
    public void saveGame() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Game game = new Game();
        if (!binding.editText.getText().equals(null)) {
            game.setTitle(binding.editText.getText().toString());
        } else {
            game.setTitle("Без названия");
        }
        game.setPin(getRandomNumberString());
        game.setStarted(false);
        game.setQuestions(convertArrayListToHashMap(questions));
        game.setAuthor(MainActivity.currentUser.getUsername());

        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH).getReference("games/" + game.getPin() + "/");
        try {
            ref.setValue(game)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.INVISIBLE);
        }

    }

    public String getRandomNumberString() {
        //генерирует шести значное число
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%06d", number);
    }



    public HashMap<String, Question> convertArrayListToHashMap(ArrayList<Question> arrayList) {
        HashMap<String, Question> hashMap = new HashMap<>();
        int i = 1;
        for (Question question : arrayList) {
            //добавляю строку, чтобы не переводилось в
            // инт и нормально записывалось как map а не arraylist
            question.setId(Integer.toString(i));
            hashMap.put("ID" + i, question);
            i++;
        }

        return hashMap;
    }

    class QuestionItem extends Item<GroupieViewHolder> {

        Question question;

        public QuestionItem(Question question) {
            this.question = question;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            ImageView imageView = viewHolder.itemView.findViewById(R.id.questionImageView);
            TextView textView = viewHolder.itemView.findViewById(R.id.questionTitle);
            ImageButton imageButton = viewHolder.itemView.findViewById(R.id.deleteQuestion);

            Picasso.get()
                    .load(question.getPhotoUrl())
                    .into(imageView);

            textView.setText(question.getQuestionText());

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    questions.remove(question);
                    adapter.clear();
                    for (Question q : questions) {
                        adapter.add(new QuestionItem(q));
                    }
                    binding.recyclerView.setAdapter(adapter);
                }
            });
        }

        @Override
        public int getLayout() {
            return R.layout.question_item;
        }
    }
}
