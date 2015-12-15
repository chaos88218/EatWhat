package com.example.miles.eatwhat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static String DATABASE_TABLE = "list";
    private SQLiteDatabase db;
    private MyDBHelper myDBHelper;
    Random random = new Random();
    int currentRestID;

    EditText nameEdittxt;
    EditText addEdittxt;
    EditText critiEdittxt;

    TextView nameContextxt;
    TextView addContextxt;
    TextView critiContextxt;
    TextView allResulttxt;

    Button buttRand;
    Button allResult;

    Spinner eatTime, eatType, eatScore, eatPrice;

    FloatingActionButton add;
    FloatingActionButton update;
    FloatingActionButton delete;
    FloatingActionButton exit;
    FloatingActionButton save;

    private GoogleApiClient client;
    private boolean UPDATE_MODE = false;
    private String currentResult = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("State", "onCreate");

        nameEdittxt = (EditText) findViewById(R.id.nameEdit);
        addEdittxt = (EditText) findViewById(R.id.addEdit);
        critiEdittxt = (EditText) findViewById(R.id.critiEdit);

        nameContextxt = (TextView) findViewById(R.id.nameContext);
        addContextxt = (TextView) findViewById(R.id.addContext);
        critiContextxt = (TextView) findViewById(R.id.critiContext);
        allResulttxt = (TextView) findViewById(R.id.textAllResualt);
        allResulttxt.setMovementMethod(new ScrollingMovementMethod());

        buttRand = (Button) findViewById(R.id.butRand);
        allResult = (Button) findViewById(R.id.allResult);

        eatTime = (Spinner) findViewById(R.id.eat_Time);
        eatType = (Spinner) findViewById(R.id.eat_Type);
        eatScore = (Spinner) findViewById(R.id.eat_Score);
        eatPrice = (Spinner) findViewById(R.id.eat_Price);

        add = (FloatingActionButton) findViewById(R.id.add);
        update = (FloatingActionButton) findViewById(R.id.update);
        delete = (FloatingActionButton) findViewById(R.id.delete);
        exit = (FloatingActionButton) findViewById(R.id.exit);
        save = (FloatingActionButton) findViewById(R.id.save);

        buttRand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eatTime.getSelectedItemPosition() == 0 && eatType.getSelectedItemPosition() == 0 && eatPrice.getSelectedItemPosition() == 0) {
                    Toast.makeText(v.getContext(), "要選擇餐廳的分類以及評價喔! ", Toast.LENGTH_SHORT).show();
                } else {
                    String queryLogicTime = "";
                    String queryLogicType = "";
                    String queryLogicPrice = "";
                    String[] colNames = new String[]{"_id", "time", "type", "star", "price", "name", "address", "criticize"};

                    if (eatTime.getSelectedItemPosition() != 0) {
                        queryLogicTime = "time=" + eatTime.getSelectedItemPosition();
                    }
                    if (eatType.getSelectedItemPosition() != 0) {
                        queryLogicType = "type=" + eatType.getSelectedItemPosition();
                        if (eatTime.getSelectedItemPosition() != 0) {
                            queryLogicType = " AND " + queryLogicType;
                        }
                        if (eatPrice.getSelectedItemPosition() != 0) {
                            queryLogicType =  queryLogicType + " AND ";
                        }
                    }
                    if (eatPrice.getSelectedItemPosition() != 0) {
                        queryLogicPrice = "price=" + eatPrice.getSelectedItemPosition();
                    }
                    currentResult = queryLogicTime + queryLogicType + queryLogicPrice;
                    Log.e("Query: ", ".."+currentResult+"..");

                    Cursor c = db.query(DATABASE_TABLE, colNames, queryLogicTime + queryLogicType + queryLogicPrice
                            , null, null, null, null);

                    if (c.getCount() == 0) {
                        Toast.makeText(v.getContext(), "沒有符合的餐廳，新增吧!", Toast.LENGTH_SHORT).show();
                    } else {
                        int getOneResta = random.nextInt(c.getCount());
                        c.moveToPosition(getOneResta);

                        currentRestID = Integer.parseInt(c.getString(0));
                        nameContextxt.setText(c.getString(5));
                        addContextxt.setText(c.getString(6));
                        critiContextxt.setText(c.getString(7));

                        if (update.getVisibility() != View.VISIBLE) {
                            update.setVisibility(v.VISIBLE);
                        }
                        if (allResult.getVisibility() != View.VISIBLE) {
                            allResult.setVisibility(v.VISIBLE);
                            allResulttxt.setText("");
                        }
                    }
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMode(view);
                nameEdittxt.setText("");
                addEdittxt.setText("");
                critiEdittxt.setText("");
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMode(v);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdittxt.setText(nameContextxt.getText().toString());
                addEdittxt.setText(addContextxt.getText().toString());
                critiEdittxt.setText(critiContextxt.getText().toString());
                editMode(v);
                UPDATE_MODE = true;
                delete.setVisibility(View.VISIBLE);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = db.delete(DATABASE_TABLE, "_id=?", new String[]{String.valueOf(currentRestID)});
                if (count != 0) {
                    Toast.makeText(v.getContext(), "刪除一個餐廳! ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "刪除有誤喔! ", Toast.LENGTH_SHORT).show();
                }
                delete.setVisibility(View.GONE);
                viewMode(v);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();

                cv.put("time", eatTime.getSelectedItemPosition());
                cv.put("type", eatType.getSelectedItemPosition());
                cv.put("star", eatScore.getSelectedItemPosition());
                cv.put("price", eatPrice.getSelectedItemPosition());

                cv.put("name", nameEdittxt.getText().toString());
                cv.put("address", addEdittxt.getText().toString());
                cv.put("criticize", critiEdittxt.getText().toString());

                if (nameEdittxt.getText().toString().matches("") || addEdittxt.getText().toString().matches("") || critiEdittxt.getText().toString().matches("")) {
                    Toast.makeText(v.getContext(), "要填入餐廳訊息喔! ", Toast.LENGTH_SHORT).show();
                } else if (eatTime.getSelectedItemPosition() == 0 || eatType.getSelectedItemPosition() == 0
                        || eatScore.getSelectedItemPosition() == 0 || eatPrice.getSelectedItemPosition() == 0) {
                    Toast.makeText(v.getContext(), "要選擇餐廳的分類以及評價喔! ", Toast.LENGTH_SHORT).show();
                } else {
                    if (UPDATE_MODE) {
                        long new_id = db.update(DATABASE_TABLE, cv, "_id = " + currentRestID, null);
                        if (new_id != 0) {
                            Toast.makeText(v.getContext(), "編輯一個餐廳! ", Toast.LENGTH_SHORT).show();
                            UPDATE_MODE = false;
                            delete.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(v.getContext(), "編輯有誤喔! ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        long new_id = db.insert(DATABASE_TABLE, null, cv);
                        if (new_id != 0) {
                            Toast.makeText(v.getContext(), "新增一個餐廳! ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "新增有誤喔! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    viewMode(v);
                }
            }
        });

        allResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colNames = new String[]{"_id", "time", "type", "star", "price", "name", "address", "criticize"};
                String str = "";
                Cursor c = db.query(DATABASE_TABLE, colNames, currentResult
                        , null, null, null, null);

                str += "\n";

                c.moveToFirst();

                for (int i = 0; i < c.getCount(); i++) {
                    str += c.getString(5) + "\t\t";
                    str += c.getString(6) + "\t\t";
                    str += c.getString(7) + "\t\t";
                    str += c.getString(3) + "星" + "\n";
                    c.moveToNext();
                }
                allResulttxt.setText(str.toString());
                allResult.setVisibility(v.GONE);
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void editMode(View v) {
        buttRand.setClickable(false);
        nameEdittxt.setVisibility(v.VISIBLE);
        addEdittxt.setVisibility(v.VISIBLE);
        critiEdittxt.setVisibility(v.VISIBLE);

        add.setVisibility(v.GONE);
        update.setVisibility(v.GONE);
        eatScore.setVisibility(v.VISIBLE);

        save.setVisibility(v.VISIBLE);
        exit.setVisibility(v.VISIBLE);
        allResult.setVisibility(v.GONE);

        nameContextxt.setText("");
        addContextxt.setText("");
        critiContextxt.setText("");
        allResulttxt.setText("");
    }

    public void viewMode(View v) {
        buttRand.setClickable(true);
        nameEdittxt.setVisibility(v.GONE);
        addEdittxt.setVisibility(v.GONE);
        critiEdittxt.setVisibility(v.GONE);
        eatScore.setVisibility(v.GONE);

        exit.setVisibility(v.GONE);
        save.setVisibility(v.GONE);
        allResult.setVisibility(v.GONE);
        delete.setVisibility(View.GONE);

        add.setVisibility(v.VISIBLE);
        allResulttxt.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("State", "onStart");
        myDBHelper = new MyDBHelper(this);
        db = myDBHelper.getWritableDatabase();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.miles.eatwhat/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("State", "onStop");
        db.close();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.miles.eatwhat/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
