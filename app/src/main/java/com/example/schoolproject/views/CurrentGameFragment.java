package com.example.schoolproject.views;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.database.DatabaseUtilsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproject.MainActivity;
import com.example.schoolproject.R;
import com.example.schoolproject.databinding.CurrentGameFragmentBinding;
import com.example.schoolproject.models.Game;
import com.example.schoolproject.models.Question;
import com.example.schoolproject.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.StreamSupport;

public class CurrentGameFragment extends Fragment {

    //все что происходит в текущей игре

    private User currentUser;

    private Game currentGame;
    private GroupAdapter<GroupieViewHolder> adapter = new GroupAdapter<>();
    private ArrayList<User> totalPlayers = new ArrayList<>();
    private ArrayList<Question> questions = new ArrayList<>();
    private Question currentQuestion;
    private int k = 0;
    private long timeLeft = 1337;
    private CurrentGameFragmentBinding binding;
    private CountDownTimer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CurrentGameFragmentBinding.inflate(inflater, container, false);
        MainActivity.bottomNavigationView.getMenu().findItem(R.id.entergame).setChecked(true);
        //после установки пользователя задается опрос в методе ниже и заполняется адаптер с очками пользователей
        setCurrentUser();

        binding.answer1Tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //проверяет правильно ли ответил участник
                DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/games/" + currentGame.getPin() + "/questions/ID" + currentQuestion.getId() + "/answers/" + currentUser.getUsername() + "/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long userAnswer = (Long) snapshot.getValue();

                        if (userAnswer == null) {
                            DatabaseReference ref2 = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                    .getReference("/games/" + currentGame.getPin() + "/scores/" + currentUser.getUsername() + "/");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Long score = (Long) snapshot.getValue();
                                    System.out.println(currentQuestion.getAnswerOne().containsValue(true) + currentQuestion.getQuestionText() + score);
                                    if (currentQuestion.getAnswerOne().containsValue(true)) {
                                        if (score == null) {
                                            ref2.setValue(150);
                                            ref.setValue(1);
                                        } else {
                                            ref2.setValue(score + 150);
                                            ref.setValue(1);
                                        }
                                    } else {
                                        ref.setValue(1);
                                    }
                                    ref2.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.answer2Tw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //проверяет правильно ли ответил участник
                DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/games/" + currentGame.getPin() + "/questions/ID" + currentQuestion.getId() + "/answers/" + currentUser.getUsername() + "/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long userAnswer = (Long) snapshot.getValue();

                        if (userAnswer == null) {
                            DatabaseReference ref2 = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                    .getReference("/games/" + currentGame.getPin() + "/scores/" + currentUser.getUsername() + "/");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Long score = (Long) snapshot.getValue();
                                    if (currentQuestion.getAnswerTwo().containsValue(true)) {
                                        if (score == null) {
                                            ref2.setValue(150);
                                            ref.setValue(1);
                                        } else {
                                            ref2.setValue(score + 150);
                                            ref.setValue(1);
                                        }
                                    } else {
                                        ref.setValue(1);
                                    }
                                    ref2.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.answer3Tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //проверяет правильно ли ответил участник
                DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/games/" + currentGame.getPin() + "/questions/ID" + currentQuestion.getId() + "/answers/" + currentUser.getUsername() + "/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long userAnswer = (Long) snapshot.getValue();

                        if (userAnswer == null) {
                            DatabaseReference ref2 = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                    .getReference("/games/" + currentGame.getPin() + "/scores/" + currentUser.getUsername() + "/");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Long score = (Long) snapshot.getValue();
                                    if (currentQuestion.getAnswerThree().containsValue(true)) {
                                        if (score == null) {
                                            ref2.setValue(150);
                                            ref.setValue(1);
                                        } else {
                                            ref2.setValue(score + 150);
                                            ref.setValue(1);
                                        }
                                    } else {
                                        ref.setValue(1);
                                    }
                                    ref2.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.answer4Tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //проверяет правильно ли ответил участник
                DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/games/" + currentGame.getPin() + "/questions/ID" + currentQuestion.getId() + "/answers/" + currentUser.getUsername() + "/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long userAnswer = (Long) snapshot.getValue();

                        if (userAnswer == null) {
                            DatabaseReference ref2 = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                    .getReference("/games/" + currentGame.getPin() + "/scores/" + currentUser.getUsername() + "/");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Long score = (Long) snapshot.getValue();
                                    if (currentQuestion.getAnswerFour().containsValue(true)) {
                                        if (score == null) {
                                            ref2.setValue(150);
                                            ref.setValue(1);
                                        } else {
                                            ref2.setValue(score + 150);
                                            ref.setValue(1);
                                        }
                                    } else {
                                        ref.setValue(1);
                                    }
                                    ref2.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        //поток нужен чтобы обновить ui после 2 секунд после захода на фрагмент, но похоже он не понаобится
        /**
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        System.out.println(secondsLeft.get(Integer.parseInt(currentQuestion.getId())-1));
                        setCurrentQuestion(currentGame);
                    } catch (InterruptedException | NullPointerException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
        thread.start();
        try {
            thread.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
         **/

        timer = new CountDownTimer(1000000000, 1000) {
            @Override
            public void onTick(long l) {
                try {
                    if (currentGame.getStarted()) {
                        System.out.println(timeLeft);
                        setCurrentQuestion(currentGame);

                    }
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onFinish() {
                timer.start();
            }
        };

        timer.start();


        return binding.getRoot();


    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("OnStop");
        timer.cancel();
    }

    public void setCurrentUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH).getReference("/users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);

                if (user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    currentUser = user;
                    setCurrentGame();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setCurrentGame() {
        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH).getReference("/games/");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Game game = snapshot.getValue(Game.class);
                DatabaseReference ref2 = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/games/" + game.getPin() + "/players/");
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                if (user.getUsername().equals(currentUser.getUsername())) {
                                    currentGame = game;

                                    DatabaseReference isStartedRef = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                            .getReference("/games/" + currentGame.getPin());
                                    isStartedRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            HashMap<String, Boolean> isStarted = (HashMap<String, Boolean>) snapshot.getValue();
                                            if (isStarted != null) {
                                                if (!isStarted.containsValue(Boolean.TRUE)) {
                                                    binding.upperLayout.setVisibility(View.INVISIBLE);
                                                    binding.scrollView2.setVisibility(View.INVISIBLE);

                                                    binding.innerlayout.setVisibility(View.VISIBLE);
                                                } else {
                                                    binding.upperLayout.setVisibility(View.VISIBLE);
                                                    binding.scrollView2.setVisibility(View.VISIBLE);

                                                    binding.innerlayout.setVisibility(View.INVISIBLE);
                                                }
                                            } else {
                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                ft.replace(R.id.nav_host_fragment, new EnterGameFragment());
                                                ft.addToBackStack(null);
                                                ft.commit();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    fillAdapter();
                                    binding.currentPin.setText("ID: " + currentGame.getPin());
                                    getCurrentGameQuestions(currentGame);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void fillAdapter() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                    .getReference("/games/" + currentGame.getPin() + "/players/");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    totalPlayers.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User player = data.getValue(User.class);
                        totalPlayers.add(player);
                        refreshAdapter();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {

        }
    }

    public void refreshAdapter() {
        adapter.clear();

        /**
        for (User u : totalPlayers) {
            adapter.add(new UserScoreItem(u));
        }
         */

        sortScores();

        binding.currentGameRecyclerView.setAdapter(adapter);

    }


    public void sortScores() {

        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                .getReference("/games/" + currentGame.getPin() + "/scores/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Long> score = new ArrayList<>();
                ArrayList<String> username = new ArrayList<>();
                //Long[] score = new Long[(int) snapshot.getChildrenCount()];
                for (DataSnapshot snap : snapshot.getChildren()) {
                    score.add((Long) snap.getValue());
                    username.add(snap.getKey());
                }

                boolean fl = true;
                while (fl) {
                    fl = false;
                    for (int i = 0; i < score.size()-1; i++) {
                        if (score.get(i) < score.get(i+1)) {
                            fl = true;
                            String tempUsername = username.get(i);
                            Long tempScore = score.get(i);

                            username.set(i, username.get(i+1));
                            username.set(i+1, tempUsername);

                            score.set(i, score.get(i+1));
                            score.set(i+1, tempScore);
                        }

                    }
                }

                for (String u : username) {

                    if (adapter.getItemCount() < username.size()) {
                        adapter.add(new UserScoreItem(findUserByUsername(u)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public User findUserByUsername(String username) {
        User u = null;
        for (User user : totalPlayers) {
            if (user.getUsername().equals(username)) {
                u = user;
            }
        }
        return u;
    }

    public void setCurrentQuestion(Game game) {
        /**
         * если колво ответов на вопрос i в цикле меньше колва пользователей,
         * то этот вопрос устанавливается как вопрос в данный момент (i-1) у всех пользователей
         * Если колво ответов на вопрос совпадает с колвом пользователей то вопросом в данный момент
         * будет вопрос i (отнимаю единицу потому что цикл начитается с 1)
         */

        for (int i = 1; i < questions.size()+1; i++) {
            DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                    .getReference("/games/" + game.getPin() + "/questions/ID" + i + "/answers/");
            binding.answers.setText(getResources().getString(R.string.answers) + " " + 0);
            int finalI = i;
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long answersCount = 0;

                    try {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Long k = (Long) snap.getValue();
                            if (k != null) {
                                answersCount += k;
                            }

                            if (answersCount == totalPlayers.size()) {
                                binding.answers.setText(getResources().getString(R.string.answers) + " " + 0);
                            } else {
                                binding.answers.setText(getResources().getString(R.string.answers) + " " + answersCount);
                            }

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    //просто проверка что правильно колво ответов считает
                    //Toast.makeText(getContext(), totalPlayers.size() + " " + answersCount + " " + questions.size(), Toast.LENGTH_SHORT).show();

                    if ((answersCount == 0 || answersCount < totalPlayers.size()) && (finalI < questions.size()) && (timeLeft > 0)) {

                        if (currentQuestion == null) {
                            currentQuestion = questions.get(finalI-1);
                            setCurrentQuestionTimestamp(System.currentTimeMillis()/1000, game);
                            refreshUi(currentQuestion);
                        } else {
                            setCurrentQuestionTimestamp(System.currentTimeMillis()/1000, game);
                            refreshUi(currentQuestion);
                        }

                    } else if (((answersCount != 0 && answersCount >= totalPlayers.size()) || (timeLeft <= 0)) && finalI < questions.size()) {
                        //тут переход на следующий вопрос
                        if ((currentQuestion != null) && (!currentQuestion.equals(questions.get(finalI)))) {
                            timeLeft = 1337;
                            currentQuestion = questions.get(finalI);
                            setCurrentQuestionTimestamp(System.currentTimeMillis()/1000, game);
                            refreshUi(currentQuestion);
                        }

                        currentQuestion = questions.get(finalI);

                    } else if (((answersCount != 0 && answersCount >= totalPlayers.size()) || (timeLeft <= 0)) && finalI == questions.size()) {
                        currentQuestion = questions.get(questions.size()-1);
                        //setCurrentQuestionTimestamp(System.currentTimeMillis()/1000, game);
                        refreshUi(currentQuestion);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ResultsFragment fragment = new ResultsFragment();

                        Bundle data = new Bundle();
                        data.putParcelable("CURR_GAME", currentGame);
                        data.putParcelableArrayList("CURR_MEMBERS", totalPlayers);
                        fragment.setArguments(data);

                        ft.replace(R.id.nav_host_fragment, fragment);
                        ft.addToBackStack(null);
                        ft.commit();

                    } else {
                        try {
                            //setCurrentQuestionTimestamp(System.currentTimeMillis()/1000, game);
                            refreshUi(currentQuestion);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void refreshUi(Question question) {
        try {
            binding.currentQuestionText.setText(question.getQuestionText());
            binding.quizTitle.setText(currentGame.getTitle());
            Picasso.get()
                    .load(question.getPhotoUrl())
                    .into(binding.imageView);
            binding.answer1Tw.setText(question.getAnswerOne().keySet().toArray()[0].toString());
            binding.answer2Tw.setText(question.getAnswerTwo().keySet().toArray()[0].toString());
            binding.answer3Tw.setText(question.getAnswerThree().keySet().toArray()[0].toString());
            binding.answer4Tw.setText(question.getAnswerFour().keySet().toArray()[0].toString());
            binding.timestamp.setText(getResources().getString(R.string.ostalos) + " " + timeLeft);

            binding.currentQuestionNum.setText(question.getId() + " " + getResources().getString(R.string.of) + " " + questions.size());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    //обавляет вопросы в массив
    public void getCurrentGameQuestions(Game game) {
        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                .getReference("/games/" + game.getPin() + "/questions/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questions.clear();

                long qsize = StreamSupport.stream(snapshot.getChildren().spliterator(), false).count();

                HashMap<Integer, Question> qsorted = new HashMap<>();


                for (DataSnapshot data : snapshot.getChildren()) {
                    Question question = data.getValue(Question.class);
                    qsorted.put(Integer.parseInt(question.getId()), question);
                }

                for (int i = 1; i <= qsize; i++) {

                    questions.add(qsorted.get(i));
                    System.out.println(qsorted.get(i).getId());
                }


                //сделал таймер который будет обновляться каждую секунду поэтому закомментил тут
                /**
                try {
                    setCurrentQuestion(game);
                } catch (Exception e) {

                }
                */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //что этот метод делает понятно из названия
    public void setCurrentQuestionTimestamp(long timestamp, Game game) {
        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                .getReference("/games/" + game.getPin() + "/questions/ID" + currentQuestion.getId() + "/timestamp/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() == null) {
                        ref.setValue(timestamp);
                        timeLeft = currentQuestion.getSeconds();
                        ref.removeEventListener(this);
                    } else {
                        timeLeft = Long.parseLong(snapshot.getValue().toString()) + currentQuestion.getSeconds() - System.currentTimeMillis()/1000;
                        ref.removeEventListener(this);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    class UserScoreItem extends Item<GroupieViewHolder> {

        private User user;
        public Long score = 0L;

        public UserScoreItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {

            if (user.getUsername().equals(currentUser.getUsername())) {
                viewHolder.itemView.findViewById(R.id.userScoreLayout)
                        .setBackground(getResources().getDrawable(R.drawable.shape6));
            }

            ImageView imageView = viewHolder.itemView.findViewById(R.id.score_image_view);
            TextView username = viewHolder.itemView.findViewById(R.id.score_username);
            TextView userScore = viewHolder.itemView.findViewById(R.id.score_score);

            Picasso.get()
                    .load(user.getPfpLink())
                    .into(imageView);
            username.setText(user.getUsername());

            DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                    .getReference("/games/" + currentGame.getPin() + "/scores/" + user.getUsername() + "/");


            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getValue() != null) {
                        score = (Long) snapshot.getValue();
                        userScore.setText(Long.toString(score));
                    } else {
                        userScore.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public int getLayout() {
            return R.layout.user_score_item;
        }
    }
}