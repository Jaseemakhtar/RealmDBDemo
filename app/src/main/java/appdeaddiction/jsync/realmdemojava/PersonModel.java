package appdeaddiction.jsync.realmdemojava;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class PersonModel extends RealmObject {
    @PrimaryKey
    @Required
    public Number id;

    @Required
    public String name;

    @Required
    public String email;
}
