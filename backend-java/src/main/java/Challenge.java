import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class Challenge extends Application<ChallengeConfiguration> {
    @Override
    public void run(ChallengeConfiguration conf, Environment env) {
        DBIFactory dbiFactory = new DBIFactory();
        DBI dbi = dbiFactory.build(env, conf.database, "mysql");
        env.jersey().register(new TestResource(dbi));
        env.jersey().register(new UserCtrl(dbi));
        env.jersey().register(new MessageCtrl(dbi));
    }

    public static void main(String[] args) throws Exception {
        new Challenge().run(args);
    }
}
