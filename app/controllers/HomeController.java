package controllers;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.fasterxml.jackson.databind.node.ObjectNode;
        import models.ConnectionRequest;
        import models.Profile;
        import models.User;
        import play.mvc.Controller;
        import play.mvc.Result;

        import javax.inject.Inject;
        import java.util.stream.Collectors;

/**
 * Created by lubuntu on 10/23/16.
 */
public class HomeController extends Controller {


    @Inject
    ObjectMapper objectMapper;
    public Result getProfile(Long id){
        User user = User.find.byId(id);
        Profile profile = Profile.find.byId(user.profile.id);
        ObjectNode data = objectMapper.createObjectNode();
        data.put("id",user.id);
        data.put("firstName",profile.firstName);
        data.put("lastName",profile.lastName);
        data.put("company",profile.company);
        data.put("email",user.email);
        data.set("connections",objectMapper.valueToTree(user.connections.stream().map(connection->{
            ObjectNode connectionJson = objectMapper.createObjectNode();
            User connectionJsonUser = User.find.byId(connection.id);
            Profile connectionJsonProfile = Profile.find.byId(user.profile.id);
            connectionJson.put("id",connectionJsonUser.id);
            connectionJson.put("firstName",connectionJsonProfile.firstName);
            connectionJson.put("lastName",connectionJsonProfile.lastName);
            connectionJson.put("company",connectionJsonProfile.company);
            connectionJson.put("email",connectionJsonUser.email);
            return connectionJson;
        }).collect(Collectors.toList())));
        data.set("connectionRequests",objectMapper.valueToTree(user.connectionRequestsReceived.stream().filter(x -> x.status.equals(ConnectionRequest.Status.WAITING) ).map(connectionRequest->{
            ObjectNode connectionRequestsJson = objectMapper.createObjectNode();
            User connectionRequestUser = connectionRequest.sender;
            Profile connectionRequestProfile = Profile.find.byId(connectionRequestUser.profile.id);
            connectionRequestsJson.put("id",connectionRequest.id);
            connectionRequestsJson.put("firstName",connectionRequestProfile.firstName);
            return connectionRequestsJson;
        }).collect(Collectors.toList())));
        data.set("suggestions",objectMapper.valueToTree(User.find.all().stream()
                .filter(x->!user.equals(x))
                .filter(x->!user.connections.contains(x))
                .filter(x->!user.connectionRequestsReceived.stream().map(
                        y->y.sender).collect(Collectors.toList()).contains(x))
                .filter(x->!user.connectionRequestsSent.stream().map(
                        y->y.receiver).collect(Collectors.toList()).contains(x)).map(suggestions->{
                    ObjectNode suggestionJson = objectMapper.createObjectNode();
                    Profile suggestionProfile = suggestions.profile;
                    suggestionJson.put("firstName",suggestionProfile.firstName);
                    suggestionJson.put("email",suggestions.email);
                    return suggestionJson;
                }).collect(Collectors.toList())));



        return ok();
    }

}
