package com.example.schoolproject.views;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.schoolproject.databinding.MyGamesResultsFragmentBinding;
import com.example.schoolproject.models.Game;
import com.example.schoolproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
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

public class MyGamesResultsFragment extends Fragment {

    private MyGamesResultsFragmentBinding binding;
    private GroupAdapter<GroupieViewHolder> adapter = new GroupAdapter<>();
    private ArrayList<Game> games = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MyGamesResultsFragmentBinding.inflate(inflater, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                    .getReference("/results/");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Game game = snap.getValue(Game.class);
                        if (game.getAuthor().equals(MainActivity.currentUser.getUsername())) {
                            games.add(game);
                            refreshAdapter();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, new RegistrationFragment());
            ft.addToBackStack(null);
            ft.commit();
        }



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void refreshAdapter() {
        adapter.clear();

        for (Game g : games) {
            adapter.add(new MyResultsItem(g));
        }

        binding.myresrecycler.setAdapter(adapter);
    }

    class MyResultsItem extends Item<GroupieViewHolder> {

        Game game;
        private ArrayList<User> users = new ArrayList<>();
        GroupAdapter<GroupieViewHolder> adapter1 = new GroupAdapter<>();

        public MyResultsItem (Game game) {
            this.game = game;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView textViewTitle = viewHolder.itemView.findViewById(R.id.game_title);
            TextView textViewId = viewHolder.itemView.findViewById(R.id.game_id);

            textViewTitle.setText(getResources().getString(R.string.title) + ": " + game.getTitle());
            textViewId.setText(getResources().getString(R.string.pin) + " " + game.getPin());

            DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                    .getReference("/results/" + game.getPin() + "/players/");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User user = snap.getValue(User.class);

                        users.add(user);

                        adapter1.clear();

                        for (User u : users) {
                            adapter1.add(new UserScoreItem(u));
                        }

                        RecyclerView recyclerView = viewHolder.itemView.findViewById(R.id.game_recycler_view);
                        recyclerView.setAdapter(adapter1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public int getLayout() {
            return R.layout.my_results_item;
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

                ImageView imageView = (ImageView) viewHolder.itemView.findViewById(R.id.score_image_view);
                TextView username = (TextView) viewHolder.itemView.findViewById(R.id.score_username);
                TextView userScore = (TextView) viewHolder.itemView.findViewById(R.id.score_score);

                Picasso.get()
                        .load(user.getPfpLink())
                        .into(imageView);
                username.setText(user.getUsername());

                DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                        .getReference("/results/" + game.getPin() + "/scores/" + user.getUsername() + "/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long score = (Long) snapshot.getValue();
                        //Toast.makeText(getContext(), "" + snapshot, Toast.LENGTH_SHORT).show();
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
}
