package com.example.zaikafoodyville;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import android.graphics.Color;

import androidx.appcompat.widget.Toolbar;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText inputSearch;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Restaurant> options;
    FirebaseRecyclerAdapter<Restaurant, MyViewHolder> adapter;
    DatabaseReference databaseReference;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    // String _Name, _Phone, _Email, _Password, imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("restaurantDetails");

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Zaika On Wheel");

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        navigationView.setCheckedItem(R.id.nav_home);


        inputSearch = findViewById(R.id.inputSearch);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        LoadData("");

        //Search bar working
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null) {
                    LoadData(editable.toString());
                } else
                    LoadData("");

            }
        });

        /*
          Intent intent = getIntent();
        _Name = intent.getStringExtra("name");
        _Phone = intent.getStringExtra("phoneNo");
        _Email = intent.getStringExtra("email");
        _Password = intent.getStringExtra("password");
        imageUrl = intent.getStringExtra("imageUri");
         */


    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }

    }

    private void LoadData(String data) {

        Query query = databaseReference.orderByChild("restaurantName").startAt(data).endAt(data + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Restaurant>().setQuery(query, Restaurant.class).build();
        adapter = new FirebaseRecyclerAdapter<Restaurant, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Restaurant model) {

                float a = Float.parseFloat(model.getRating());
                holder.restaurantName.setText(model.getRestaurantName());
                holder.description.setText(model.getDescription());
                holder.rating.setText(model.getRating());
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);

                if (a < 4.0) {
                    holder.rating.setBackgroundColor(Color.parseColor("#FFC400"));
                }

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), Demo.class));
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_singleview, parent, false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(this, UserProfile.class);
                /*
                 intent.putExtra("name", _Name);
                intent.putExtra("email", _Email);
                intent.putExtra("phoneNo", _Phone);
                intent.putExtra("password", _Password);
                intent.putExtra("imageUri", imageUrl);
                 */
                startActivity(intent);
                break;

            case R.id.logOut:
                SessionManager manager = new SessionManager(this);
                manager.logOutUserFromSession();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;

            case R.id.contactUs:
                Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                intent1.setData(Uri.parse("mailto:"));
                intent1.putExtra(Intent.EXTRA_EMAIL,new String[]{"maayankraj@gmail.com"});
                startActivity(intent1);
                break;




        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}