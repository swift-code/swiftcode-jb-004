package controllers;

import models.ConnectionRequest;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by lubuntu on 10/23/16.
 */
public class RequestController extends Controller{
    public Result sendRequest(Long senderId, Long receiverId){
        if(senderId==null | receiverId==null | User.find.byId(senderId)==null | User.find.byId(receiverId)==null){
            return ok();
        }
        else {
            ConnectionRequest request = new ConnectionRequest();
            request.sender.id = senderId;
            request.receiver.id = receiverId;
            request.status = ConnectionRequest.Status.WAITING;

            ConnectionRequest.db().insert(request);
        }
        return ok();
    }
    public Result acceptRequest(Long requestId){
        if(requestId==null | ConnectionRequest.find.byId(requestId)==null){
            return ok();
        }
        else{
            ConnectionRequest request=ConnectionRequest.find.byId(requestId);
            request.sender.connections.add(request.receiver);
            request.receiver.connections.add(request.sender);
            request.status= ConnectionRequest.Status.ACCEPTED;
            User.db().save(request.sender);
            User.db().save(request.receiver);

        }
        return ok();
    }
}
