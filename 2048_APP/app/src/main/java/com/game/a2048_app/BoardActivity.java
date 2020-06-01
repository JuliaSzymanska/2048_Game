package com.game.a2048_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.module.Field;
import com.game.module.Game;

public class BoardActivity extends AppCompatActivity {

    private Game game = Game.getInstance();
    GridView gridView;
    // TODO: 01.06.2020 narazie mamy zwykla tablice fieldow
    private Field[] fields = game.getCopyOfTheBoard().toArray(new Field[0]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        gridView = (GridView) findViewById(R.id.gridView);
        // TODO: 01.06.2020 cos mi sie obilo o uszy ze to moze byc listener ale i'm not sure to trzeba bedzie sprawdzic
        ArrayAdapter<Field> adapter = new ArrayAdapter<Field>(this,
                android.R.layout.simple_list_item_1, fields);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO: 01.06.2020 to jest po prostu to ze jak klikniesz to na dole sie pojawia co wybrales
                //  dodalalm to zeby po prostu poprobowac cos trzeba to bedzie jakos madrze zrobic
                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setListOfFields(){

    }

}
