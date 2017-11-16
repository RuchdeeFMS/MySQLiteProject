package com.example.teach.mysqliteproject;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MyDBHandler dbHandler;
    ArrayList<String> listAllMovies;
    ArrayList<Integer> listAllMoviesID;
    ArrayAdapter<String> movieAdapter = null;
    ListView movieListView;
    int selMovie_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new MyDBHandler(MainActivity.this, null, null, 1);
        listAllMovies = dbHandler.getAllMovies();
        listAllMoviesID = dbHandler.getAllMoviesID();

        //set adapter to ListView
        movieListView = (ListView) findViewById(R.id.lvMovies);
        movieAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listAllMovies);
        movieListView.setAdapter(movieAdapter);

        //action for floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater mLayoutInflater = getLayoutInflater();
                final View add_dialog = mLayoutInflater.inflate(R.layout.add_movie_dialog, null);
                mBuilder.setView(add_dialog);

                final CoordinatorLayout mLayout = (CoordinatorLayout) findViewById(R.id.ctLayout);

                mBuilder.setPositiveButton("Add Movie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText mname = (EditText) add_dialog.findViewById(R.id.txtMovie_Name);
                        EditText myear = (EditText) add_dialog.findViewById(R.id.txtRelease_Year);
                        //insert new movie
                        Movie add_movie = new Movie(mname.getText().toString(), myear.getText().toString());
                        dbHandler.addNewMovie(add_movie);

                        //update ArrayList
                        final int new_RowID = dbHandler.getLastRowID();
                        listAllMoviesID.add(new_RowID);
                        listAllMovies.add(mname.getText().toString() + " " + myear.getText().toString());
                        //update ArrayAdapter
                        movieAdapter.notifyDataSetChanged();

                        //setup Snackbar
                        Snackbar mSnackbar = Snackbar.make(mLayout, mname.getText() + " is added.", Snackbar.LENGTH_LONG);
                        mSnackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbHandler.deleteMovie(new_RowID);
                                listAllMovies.remove(listAllMovies.size()-1);
                                listAllMoviesID.remove(listAllMoviesID.size()-1);
                                movieAdapter.notifyDataSetChanged();
                            }
                        });
                        mSnackbar.show();
                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
            }
        });

        //register context menu to ListView
        registerForContextMenu(movieListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_movie, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //get current position
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        selMovie_ID = listAllMoviesID.get(info.position);

        switch (item.getItemId()) {
            case R.id.action_update:
                AlertDialog.Builder uBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater uLayoutInflater = getLayoutInflater();
                View update_dialog = uLayoutInflater.inflate(R.layout.update_movie_dialog, null);
                uBuilder.setView(update_dialog);

                //display selected movie's data
                final EditText mname_update = (EditText) update_dialog.findViewById(R.id.txtMovie_Name_Update);
                final EditText myear_update = (EditText) update_dialog.findViewById(R.id.txtRelease_Year_Update);

                Cursor searchResult = dbHandler.getMoviebyID(selMovie_ID);
                searchResult.moveToFirst();
                mname_update.setText(searchResult.getString(searchResult.getColumnIndex(dbHandler.COLUMN_MOVIE_NAME)));
                myear_update.setText(searchResult.getString(searchResult.getColumnIndex(dbHandler.COLUMN_RELEASE_YEAR)));

                uBuilder.setPositiveButton("Update Movie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Movie update_movie = new Movie(selMovie_ID, mname_update.getText().toString(), myear_update.getText().toString());
                        dbHandler.updateMovie(update_movie);
                        //update ArrayList
                        listAllMovies.set(info.position, mname_update.getText().toString() + " " + myear_update.getText().toString());
                        //update ArrayAdapter
                        movieAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                uBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog uAlertDialog = uBuilder.create();
                uAlertDialog.show();
                break;
            case R.id.action_delete:
                final String mname = listAllMovies.get(info.position);
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(MainActivity.this);
                dBuilder.setTitle("Confirm Delete");
                dBuilder.setMessage("Are you sure to delete " + mname + "?");
                dBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dbHandler.deleteMovie(selMovie_ID)) {
                            listAllMovies.remove(info.position);
                            listAllMoviesID.remove(info.position);
                            movieAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,
                                    mname + " is removed.", Toast.LENGTH_SHORT).show();
                        } else {
                            //throw exception
                        }
                        dialogInterface.dismiss();
                    }
                });
                dBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dAlertDialog = dBuilder.create();
                dAlertDialog.show();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
