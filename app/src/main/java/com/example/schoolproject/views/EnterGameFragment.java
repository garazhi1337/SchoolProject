package com.example.schoolproject.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.schoolproject.MainActivity;
import com.example.schoolproject.R;
import com.example.schoolproject.databinding.EnterGameFragmentBinding;
import com.example.schoolproject.models.Game;
import com.example.schoolproject.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnterGameFragment extends Fragment {

    //вход в игру

    private EnterGameFragmentBinding binding;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EnterGameFragmentBinding.inflate(inflater, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            setCurrentUser();

            binding.enterGameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH).getReference("/games/");
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            try {
                                Game game = snapshot.getValue(Game.class);
                                if (game.getPin().equals(binding.enterPin.getText().toString().trim())
                                        && game.getStarted() == false && !MainActivity.currentUser.getUsername().equals(game.getAuthor())) {
                                    DatabaseReference ref2 = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH)
                                            .getReference("/games/" + game.getPin() + "/players/" + MainActivity.currentUser.getUsername() + "/");
                                    ref2.setValue(MainActivity.currentUser)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    binding.progressCircular.setVisibility(View.INVISIBLE);
                                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                                    FragmentTransaction ft = fm.beginTransaction();
                                                    ft.replace(R.id.nav_host_fragment, new CurrentGameFragment());
                                                    ft.addToBackStack(null);
                                                    ft.commit();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    binding.progressCircular.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    binding.progressCircular.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), getResources().getString(R.string.cantaccess), Toast.LENGTH_SHORT).show();
                                    System.out.println(game.getPin() + " " + binding.enterPin.getText() + " " + game.getStarted() + " " + !MainActivity.currentUser.getUsername().equals(game.getAuthor()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
            });

            //если пошльзователь уже есть в какойто игре , то при заходе на заход его перекидывает в ту игру
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
                                    try {
                                        if (user.getUsername().equals(currentUser.getUsername())) {
                                            try {
                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                ft.replace(R.id.nav_host_fragment, new CurrentGameFragment());
                                                ft.addToBackStack(null);
                                                ft.commit();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        } else {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, new RegistrationFragment());
            ft.addToBackStack(null);
            ft.commit();
        }

        return binding.getRoot();
    }

    public void setCurrentUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance(MainActivity.DATABASE_PATH).getReference("/users/");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                //Toast.makeText(MainActivity.this, user.getUsername(), Toast.LENGTH_SHORT).show();

                if (user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    currentUser = user;
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
}