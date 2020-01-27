package appdeaddiction.jsync.realmdemojava;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnInsert, btnUpdate, btnDelete, btnFetch;
    private TextView txtResult;
    private TextView edtId, edtName, edtEmail;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name("test.db").schemaVersion(1).deleteRealmIfMigrationNeeded().build();
        mRealm = Realm.getInstance(realmConfiguration);
    }

    private void initViews(){
        btnDelete = findViewById(R.id.btnDelete);
        btnInsert = findViewById(R.id.btnInsert);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnFetch = findViewById(R.id.btnFetch);

        edtId = findViewById(R.id.edtID);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);

        txtResult = findViewById(R.id.txtResult);

        btnUpdate.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnFetch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDelete:
                delete();
                break;

            case R.id.btnInsert:
                insert();
                break;

            case R.id.btnUpdate:
                update();
                break;

            case R.id.btnFetch:
                fetch();
                break;
        }
    }

    private void fetch() {
        String id = edtId.getText().toString();

        if(checkEmpty(id)){
            toast("Fields cannot be empty!");
            return;
        }

        final Integer idd = Integer.parseInt(id);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonModel personModel = realm.where(PersonModel.class).equalTo("id", idd).findFirst();
                txtResult.setText(String.format(Locale.US, "ID: %d \n Name: %s \n Email: %s", personModel.id, personModel.name, personModel.email));
            }
        });
    }

    private void update() {
        String id = edtId.getText().toString();
        final String name = edtName.getText().toString();
        final String email = edtEmail.getText().toString();

        if(checkEmpty(id, name, email)){
            toast("Fields cannot be empty!");
            return;
        }

        final Number idd = Integer.parseInt(id);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonModel personModel = new PersonModel();
                personModel.id = idd;
                personModel.name = name;
                personModel.email = email;

                realm.insertOrUpdate(personModel);
            }
        });

    }

    private void insert() {
        String id = edtId.getText().toString();
        final String name = edtName.getText().toString();
        final String email = edtEmail.getText().toString();

        if(checkEmpty(id, name, email)){
            toast("Fields cannot be empty!");
            return;
        }

        final Number idd = Integer.parseInt(id);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonModel p = new PersonModel();
                p.id = idd;
                p.name = name;
                p.email = email;

                realm.insert(p);
            }
        });
    }

    private void delete() {
        String id = edtId.getText().toString();

        if(checkEmpty(id)){
            toast("Insert id first!");
            return;
        }

        final Integer idd = Integer.parseInt(id);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<PersonModel> realmResults = realm.where(PersonModel.class).equalTo("id", idd).findAll();
                realmResults.deleteAllFromRealm();
            }
        });
    }

    boolean checkEmpty(Object... objects){
        String string;
        for(Object o: objects){
            string = String.valueOf(o).trim();
            if(string.isEmpty())
                return true;
        }
        return false;
    }

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
