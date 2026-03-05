package com.example.vocab;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class VocabDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_detail);

        Intent intent = getIntent();
        ArrayList<Vocab> vocabList = (ArrayList<Vocab>) intent.getSerializableExtra("vocab_list");
        int position = intent.getIntExtra("position", 0);

        if (savedInstanceState == null && vocabList != null) {

            Vocab vocab = vocabList.get(position);

            VocabFragment vocabFragment = VocabFragment.newInstance(vocab);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, vocabFragment);
            fragmentTransaction.commit();
        }
    }
}
