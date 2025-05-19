package com.example.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.R;
import com.example.lib.Utils;
import com.example.model.RandomNumber;

import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RandomNumber mRandomNumber;
    private ArrayList<Integer> mNumberHistory;
    private TextInputEditText mEditTextFrom;
    private TextInputEditText mEditTextTo;
    private TextView mTextResult;
    private static final String HISTORY_KEY = "random_number_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        mEditTextFrom = findViewById(R.id.editTextFrom);
        mEditTextTo = findViewById(R.id.editTextTo);
        mTextResult = findViewById(R.id.textResult);
        mRandomNumber = new RandomNumber();
        initializeHistoryList(savedInstanceState, HISTORY_KEY);
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> generateRandomNumber());
    }

    private void initializeHistoryList(Bundle savedInstanceState, String key) {
        if (savedInstanceState != null) {
            mNumberHistory = savedInstanceState.getIntegerArrayList(MainActivity.HISTORY_KEY);
        } else {
            String history = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(MainActivity.HISTORY_KEY, null);
            mNumberHistory = history == null ?
                    new ArrayList<>() : Utils.getNumberListFromJSONString(history);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(HISTORY_KEY, mNumberHistory);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(HISTORY_KEY, Utils.getJSONStringFromNumberList(mNumberHistory)).apply();
    }

    private void generateRandomNumber() {
        String fromStr = mEditTextFrom.getText() != null ? mEditTextFrom.getText().toString() : "";
        String toStr = mEditTextTo.getText() != null ? mEditTextTo.getText().toString() : "";

        if (fromStr.isEmpty() || toStr.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int from = Integer.parseInt(fromStr);
            int to = Integer.parseInt(toStr);
            if (from < 0) {
                Toast.makeText(this, R.string.error_minimum_zero,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mRandomNumber.setFromTo(from, to);
            int generatedNumber = mRandomNumber.getCurrentRandomNumber();
            mNumberHistory.add(generatedNumber);
            mTextResult.setText(getString(R.string.result_prefix) + generatedNumber);

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_numbers, Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Toast.makeText(this, R.string.error_invalid_range,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_history) {
            Utils.showInfoDialog(MainActivity.this, getString(R.string.history_dialog_title), mNumberHistory.toString());
            return true;
        } else if (id == R.id.action_clear_history) {
            mNumberHistory.clear();
            Toast.makeText(this, R.string.history_cleared, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            Toast.makeText(this, R.string.about_text, Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 