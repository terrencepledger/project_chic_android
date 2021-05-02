package org.chicdenver.data.model.Messages;

import org.chicdenver.data.model.LoggedInUser;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectionChannel {

    public ConnectionChannel() {}

    private List<ChannelMember> members = new ArrayList<>();

    private List<ChannelMessage> messages = new ArrayList<>();

    private String channelId;

    public List<ChannelMessage> getMessages() {
        return messages;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setMembers(ArrayList<ChannelMember> members) {
        this.members = members;
    }

    public void setMessages(List<ChannelMessage> messages) {
        this.messages = messages;
    }

    public List<ChannelMember> getMembers() {
        return members;
    }

    public void addMember(LoggedInUser user) {
        members.add(new ChannelMember(user.getDisplayName(0), user.getUid()));
    }
    public void addMember(String name, String uid) {
        members.add(new ChannelMember(name, uid));
    }

    public void addMessage(String name, String msg) {

        messages.add(new ChannelMessage(name, msg));

    }

    public ArrayList<String> getMemberNames() {

        ArrayList<String> ret = new ArrayList<>();

        for(ChannelMember member : members)
            ret.add(member.getName());

        return ret;

    }

    public ArrayList<String> getMemberIDs() {

        ArrayList<String> ret = new ArrayList<>();

        for(ChannelMember member : members)
            ret.add(member.getId());

        return ret;

    }

    public static String createChannelId(String s1, String s2) {

        return (s1.compareTo(s2) < 0) ?
                s1 + " & " + s2 :
                s2 + " & " + s1;

    }

    public String getChannelHeader(String name) {

        for (String member: getMemberNames()
             ) {
            if(!member.contains(name) && !name.contains(member))
                return member;
        }
        return getMemberNames().get(0);

    }

    public ChannelMessage getLastMessage(String username) {

        if(getMessages().size() == 0)
            return null;
        ChannelMessage lastMsg = getMessages().get(getMessages().size() - 1);
        for (int i = getMessages().size() - 1; i >= 0; i--) {
            if(!getMessages().get(i).getCreator().equals(username)) {
                lastMsg = getMessages().get(i);
                break;
            }
        }
        return lastMsg;

    }

//    public String getHeaderIcon(String name) {
//
//        for (ChannelMessage msg: getMessages()
//             ) {
//            if(!msg.getCreator().equals(name))
//                return msg.getIcon();
//        }
//
//    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(!(o instanceof ConnectionChannel))
            return false;
        if(this == o)
            return true;
        ConnectionChannel channel = (ConnectionChannel) o;
        return channel.getChannelId().equals(this.getChannelId());
    }

    public static class ChannelMessage {

        private String creator;
        private String message;
        private String icon;
        private String postTime;

        public ChannelMessage() {}

        public ChannelMessage(String creator, String message) {

            this.creator = creator;
            this.message = message;
            this.postTime = new Date().toString();

        }

        public String getCreator() {
            return creator;
        }

        public String getMessage() {
            return message;
        }

        public String getPostTime() {
           return postTime;
        }

        public String getPrettyPostTime() {
            StringBuffer stringBuffer = new StringBuffer();
            String now = postTime;

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
//            simpleDateFormat.format(now, stringBuffer, new FieldPosition(0));
            return now;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setPostTime(String postTime) {
            this.postTime = postTime;
        }

    }

    public static class ChannelMember {

        private String name;
        private String id;

        public ChannelMember() {}

        public ChannelMember(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

}
