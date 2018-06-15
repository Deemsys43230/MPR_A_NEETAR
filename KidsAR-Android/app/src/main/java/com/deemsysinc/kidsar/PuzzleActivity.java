package com.deemsysinc.kidsar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.deemsysinc.kidsar.adapter.PuzzleAdapter;
import com.deemsysinc.kidsar.models.PuzzleModel;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;

import java.util.ArrayList;
import java.util.List;

public class PuzzleActivity extends AppCompatActivity {
    List<PuzzleModel> list;
    PuzzleAdapter adapter;
    ImageView buttonBack;

    private RecyclerView puzzlerecycler;
    private LinearLayoutManager layoutManager;
    private ImageView btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_puzzle);

        btn_back = findViewById(R.id.buttonBack);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PuzzleActivity.this,HomeActivity.class));
            }
        });

        puzzlerecycler = (RecyclerView) findViewById(R.id.puzzlerecycler);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        puzzlerecycler.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        list.add(new PuzzleModel(1, "Fruits & Vegetables", "puzzles_fruits.jpg"));
        list.add(new PuzzleModel(2, "Animals", "puzzle_animals.jpg"));

        adapter = new PuzzleAdapter(getApplicationContext(), list);
        puzzlerecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PuzzleActivity.this,HomeActivity.class));
    }
}
