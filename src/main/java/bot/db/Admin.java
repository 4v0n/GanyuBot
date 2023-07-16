package bot.db;

import bot.Bot;
import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.filters.Filters;
import net.dv8tion.jda.api.entities.User;

@Entity("Admin")
public class Admin {
    @Id
    private String userId;

    public Admin() {
    }

    public Admin(String userId) {
        this.userId = userId;
    }

    public static Admin getAdminListing(String userId) {
        Datastore datastore = Bot.getINSTANCE().getDatastore();
        return datastore.find(Admin.class)
                .filter(Filters.eq("userId", userId))
                .iterator().tryNext();
    }

    public static boolean isAdmin(User user) {
        return getAdminListing(user.getId()) != null;
    }

    public static boolean isAdmin(String userId) {
        return getAdminListing(userId) != null;
    }
}
