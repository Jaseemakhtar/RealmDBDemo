package appdeaddiction.jsync.realmdemojava;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnInsert, btnUpdate, btnDelete, btnFetch, btnFetchAll;
    private TextView txtResult;
    private TextView edtId, edtName, edtEmail;
    private Realm mRealm;
    private RealmResults<PersonModel> allPersons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        Realm.init(getApplicationContext());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name("test.db").schemaVersion(1).deleteRealmIfMigrationNeeded().build();
        mRealm = Realm.getInstance(realmConfiguration);
        allPersons = mRealm.where(PersonModel.class).findAllSorted("id");
        allPersons.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<PersonModel>>() {
            @Override
            public void onChange(RealmResults<PersonModel> personModels, OrderedCollectionChangeSet changeSet) {
                if(changeSet.getInsertions().length > 0)
                    toast("Inserted!");
                else if(changeSet.getChanges().length > 0)
                    toast("Updated!");
            }
        });

    }

    private void initViews(){
        btnDelete = findViewById(R.id.btnDelete);
        btnInsert = findViewById(R.id.btnInsert);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnFetch = findViewById(R.id.btnFetch);
        btnFetchAll = findViewById(R.id.btnFetchAll);

        edtId = findViewById(R.id.edtID);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);

        txtResult = findViewById(R.id.txtResult);

        btnUpdate.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnFetch.setOnClickListener(this);
        btnFetchAll.setOnClickListener(this);
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

            case R.id.btnFetchAll:

                if(allPersons.size() > 0) {
                    txtResult.setText("");
                    for (PersonModel model : allPersons) {
                        String old = txtResult.getText().toString();
                        txtResult.setText(old + "Id: " + model.id + " Name: " + model.name + " Email: " + model.email + "\n");
                    }
                }else
                    toast("DB Empty!");

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
                RealmQuery<PersonModel> query = realm.where(PersonModel.class).equalTo("id", idd);
                PersonModel personModel = query.findFirst();
                if(query.count() > 0)
                    txtResult.setText(String.format(Locale.US, "ID: %d \n Name: %s \n Email: %s", personModel.id, personModel.name, personModel.email));
                else
                    toast("Specified Id doesn't exists!");
            }
        });
    }

    private void update() {
        String id = edtId.getText().toString();
        final String name = edtName.getText().toString();
        final String email = edtEmail.getText().toString();

        if(checkEmpty(id)){
            toast("Id cannot be empty!");
            return;
        }

        final Integer idd = Integer.parseInt(id);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<PersonModel> query = realm.where(PersonModel.class).equalTo("id", idd);
                PersonModel personModel = query.findFirst();
                if(query.count() > 0) {
                    PersonModel personModel1 = new PersonModel();
                    personModel1.id = idd;

                    if(checkEmpty(name))
                        personModel1.name = personModel.name;
                    else
                        personModel1.name = name;

                    if(checkEmpty(email))
                        personModel1.email = personModel.email;
                    else
                        personModel1.email = email;

                    realm.insertOrUpdate(personModel1);
                }else{
                    toast("Specified Id doesn't exists!");
                }
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

        final Integer idd = Integer.parseInt(id);
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
            toast("Id cannot be empty!");
            return;
        }

        final Integer idd = Integer.parseInt(id);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<PersonModel> realmResults = realm.where(PersonModel.class).equalTo("id", idd).findAll();
                if(realmResults.size() > 0)
                    realmResults.deleteAllFromRealm();
                else
                    toast("Specified Id doesn't exists!");
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
