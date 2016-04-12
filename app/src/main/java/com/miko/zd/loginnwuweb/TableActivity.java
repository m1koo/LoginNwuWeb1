package com.miko.zd.loginnwuweb;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        addWegit();
    }

    public void addWegit() {
        TableLayout table = (TableLayout) findViewById(R.id.tablelayout);
        table.setStretchAllColumns(true);
        for (int i = 0; i < 3; i++) {
            TableRow tablerow = new TableRow(TableActivity.this);
            tablerow.setBackgroundColor(Color.rgb(222, 220, 210));
            for (int j = 0; j < 10; j++) {
                TextView testview = new TextView(TableActivity.this);
                testview.setBackgroundColor(Color.WHITE);
                testview.setText(MainActivity.score[i][j]);
                //还原初始化空值
                MainActivity.score[i][j]="";
                testview.setGravity(Gravity.CENTER);

                TableRow.LayoutParams p = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(1, 1, 1, 1);
                testview.setLayoutParams(p);
                tablerow.addView(testview);
            }
            table.addView(tablerow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}
