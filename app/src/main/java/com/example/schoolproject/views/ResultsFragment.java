package com.example.schoolproject.views;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schoolproject.MainActivity;
import com.example.schoolproject.R;
import com.example.schoolproject.databinding.ResultsFragmentBinding;
import com.example.schoolproject.models.Game;
import com.example.schoolproject.models.User;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResultsFragment extends Fragment {

    private ResultsFragmentBinding binding;
    private Game currentGame;
    private ArrayList<User> players;
    private GroupAdapter<GroupieViewHolder> adapter = new GroupAdapter<>();
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResultsFragmentBinding.inflate(inflater, container, false);



        currentGame = getArguments().getParcelable("CURR_GAME");
        players = getArguments().getParcelableArrayList("CURR_MEMBERS");
        setCurrentUser();

        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.deletingInfo.setText(getResources()
                        .getString(R.string.deletingin) +
                        " " + (millisUntilFinished / 1000) + " " +
                        getResources().getString(R.string.xseconds));
            }

            public void onFinish() {
                binding.deletingInfo.setText(getResources()
                        .getString(R.string.deletingin) +
                        " " + 0 + " " +
                        getResources().getString(R.string.xseconds));
                DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/games/" + currentGame.getPin() + "/");

                DatabaseReference newRef = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/results/" + currentGame.getPin() + "/");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            newRef.setValue(snapshot.getValue());
                            ref.removeValue();
                            ref.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }.start();

        return binding.getRoot();
    }

    public void setCurrentUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH).getReference("/users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);

                if (user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    currentUser = user;
                    for (User u : players) {
                        adapter.add(new UserScoreItem(u));
                    }
                    binding.resultsRecycler.setAdapter(adapter);
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

    private void sortScores() {

    }

    class UserScoreItem extends Item<GroupieViewHolder> {

        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public UserScoreItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {

            if (user.getUsername().equals(currentUser.getUsername())) {
                viewHolder.itemView.findViewById(R.id.userScoreLayout).setBackground(getResources()
                        .getDrawable(R.drawable.shape6));
            }

            ImageView imageView = (ImageView) viewHolder.itemView.findViewById(R.id.score_image_view);
            TextView username = (TextView) viewHolder.itemView.findViewById(R.id.score_username);
            TextView userScore = (TextView) viewHolder.itemView.findViewById(R.id.score_score);

            Picasso.get()
                    .load(user.getPfpLink())
                    .into(imageView);
            username.setText(user.getUsername());

            DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                    .getReference("/games/" + currentGame.getPin() + "/scores/" + user.getUsername() + "/");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long score = (Long) snapshot.getValue();
                    if (score != null) {
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
